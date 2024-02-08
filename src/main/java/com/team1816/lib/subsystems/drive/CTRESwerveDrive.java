package com.team1816.lib.subsystems.drive;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.Symmetry;
import com.team1816.lib.hardware.PIDSlotConfiguration;
import com.team1816.lib.hardware.components.gyro.Pigeon2Wrapper;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.util.driveUtil.DriveConversions;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.util.team254.DriveSignal;
import com.team1816.season.Robot;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.util.datalog.StructArrayLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CTRESwerveDrive extends Drive implements com.team1816.lib.subsystems.drive.SwerveDrivetrain {

    private final ArrayList<StatusSignal<Double>> motorTemperatures = new ArrayList<>();
    /**
     * Components
     */
    private SwerveDrivetrain train;
    private SwerveModuleConstants[] swerveModules;

    /**
     * Trajectory
     */
    protected List<Rotation2d> headingsList;
    protected int trajectoryIndex = 0;

    /**
     * Control
     */
    private SwerveRequest request;
    private SwerveRequest.FieldCentric fieldCentricRequest;
    private ModuleRequest autoRequest;

    /**
     * Properties
     */
    private SwerveDriveKinematics swerveKinematics;

    private double maxVel12MPS = factory.getConstant(NAME,"maxVel12VMPS");
    private double driveScalar = kMaxVelOpenLoopMeters / maxVel12MPS;

    /**
     * Logging
     */
    private DoubleLogEntry temperatureLogger;
    private DoubleArrayLogEntry inputLogger; //X, Y, Rotation - raw -1 to 1 from setTeleopInputs
    private StringLogEntry controlRequestLogger;

    private ArrayList<DoubleLogEntry> desiredModuleStatesLogger;
    private ArrayList<DoubleLogEntry> actualModuleStatesLogger;

    private StructArrayLogEntry<SwerveModuleState> desiredModuleStructLogger;
    private StructArrayLogEntry<SwerveModuleState> actualModuleStructLogger;

    // module indices
    public static final int kFrontLeft = 0;
    public static final int kFrontRight = 1;
    public static final int kBackLeft = 2;
    public static final int kBackRight = 3;

    @Inject
    public CTRESwerveDrive(LedManager lm, Infrastructure inf, RobotState rs) {
        super(lm, inf, rs);

        swerveModules = new SwerveModuleConstants[4];

        swerveModules[kFrontLeft] = factory.getCTRESwerveModule(NAME, "frontLeft");
        swerveModules[kFrontRight] = factory.getCTRESwerveModule(NAME, "frontRight");
        swerveModules[kBackLeft] = factory.getCTRESwerveModule(NAME, "backLeft");
        swerveModules[kBackRight] = factory.getCTRESwerveModule(NAME, "backRight");


        SwerveDrivetrainConstants constants = new SwerveDrivetrainConstants()
                .withCANbusName(factory.getCanBusName())
                .withPigeon2Id(factory.getPigeonID());


        train = new SwerveDrivetrain(constants, swerveModules);

        train.getDaqThread().setThreadPriority(99);

        Translation2d[] moduleLocations = new Translation2d[4];
        for (int i = 0; i < 4; i++) {
            moduleLocations[i] = new Translation2d(swerveModules[i].LocationX, swerveModules[i].LocationY);
        }

        swerveKinematics = new SwerveDriveKinematics(moduleLocations);

        for (int i = 0; i < 4; i++) {
            motorTemperatures.add(train.getModule(i).getDriveMotor().getDeviceTemp());
        }

        fieldCentricRequest = new SwerveRequest.FieldCentric()
                .withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage)
                .withSteerRequestType(SwerveModule.SteerRequestType.MotionMagic)
                .withDeadband(0.15 * kMaxVelOpenLoopMeters)
                .withRotationalDeadband(0.1 * kMaxAngularSpeed);

        autoRequest = new ModuleRequest()
                .withModuleStates(new SwerveModuleState[4]);

        request = fieldCentricRequest;

        configMotors();
        if (Constants.kLoggingRobot) {
            temperatureLogger = new DoubleLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/moduleTemps");
            desStatesLogger = new DoubleArrayLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/DesiredSpeeds");
            inputLogger = new DoubleArrayLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/Inputs");
            gyroPitchLogger = new DoubleLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/Pitch");
            gyroRollLogger = new DoubleLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/Roll");
            controlRequestLogger = new StringLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/ControlRequest");

            desiredModuleStructLogger = StructArrayLogEntry.create(DataLogManager.getLog(), "Drivetrain/Swerve/DesiredStateStruct", SwerveModuleState.struct);
            actualModuleStructLogger = StructArrayLogEntry.create(DataLogManager.getLog(), "Drivetrain/Swerve/ActualStateStruct", SwerveModuleState.struct);

            desiredModuleStatesLogger = new ArrayList<>();
            actualModuleStatesLogger = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                desiredModuleStatesLogger.add(new DoubleLogEntry(DataLogManager.getLog(),
                        "Drivetrain/Swerve/DesiredModuleStates/" + toModuleName(i) +"/speedMPS"));
                desiredModuleStatesLogger.add(new DoubleLogEntry(DataLogManager.getLog(),
                        "Drivetrain/Swerve/DesiredModuleStates/" + toModuleName(i) +"/angleDegrees"));
                actualModuleStatesLogger.add(new DoubleLogEntry(DataLogManager.getLog(),
                        "Drivetrain/Swerve/ActualModuleStates/" + toModuleName(i) +"/speedMPS"));
                actualModuleStatesLogger.add(new DoubleLogEntry(DataLogManager.getLog(),
                        "Drivetrain/Swerve/ActualModuleStates/" + toModuleName(i) +"/angleDegrees"));
            }
        }
    }

    @Override
    public synchronized void writeToHardware() {
        if (controlState == ControlState.OPEN_LOOP) {
            train.setControl(request);
        }
    }

    @Override
    public synchronized void readFromHardware() {
        super.readFromHardware();

        if (train.getState().ModuleStates != null) {
            chassisSpeed = swerveKinematics.toChassisSpeeds(train.getState().ModuleStates);
        }

        for (int i = 0; i < 4; i++) {
            motorTemperatures.get(i).refresh();
        }

        updateRobotState();
    }

    @Override
    public void configureOrchestra() {

    }

    @Override
    public void updateRobotState() {
        if (RobotBase.isSimulation()) {
            train.updateSimState(Robot.looperDt / 1000, RobotController.getBatteryVoltage());
        }
        robotState.fieldToVehicle = train.getState().Pose;
        robotState.driverRelativeFieldToVehicle = new Pose2d( // for inputs ONLY
                robotState.fieldToVehicle.getTranslation(),
                (robotState.allianceColor == Color.BLUE && Constants.fieldSymmetry == Symmetry.AXIS) ? robotState.fieldToVehicle.getRotation() : robotState.fieldToVehicle.getRotation().rotateBy(Rotation2d.fromDegrees(180))
        );

        var cs = new ChassisSpeeds(
                chassisSpeed.vxMetersPerSecond,
                chassisSpeed.vyMetersPerSecond,
                chassisSpeed.omegaRadiansPerSecond
        );
        robotState.calculatedVehicleAccel =
                new ChassisSpeeds(
                        (cs.vxMetersPerSecond - robotState.deltaVehicle.vxMetersPerSecond) /
                                Robot.looperDt,
                        (cs.vyMetersPerSecond - robotState.deltaVehicle.vyMetersPerSecond) /
                                Robot.looperDt,
                        -9.80
                );
        robotState.deltaVehicle = cs;

        robotState.drivetrainTemp = motorTemperatures.get(0).getValueAsDouble();

        robotState.vehicleToFloorProximityCentimeters = infrastructure.getMaximumProximity();

//        swerveOdometry.update(Rotation2d.fromDegrees(train.getPigeon2().getAngle()));


        if (Constants.kLoggingDrivetrain) {
            double[] desiredSpeeds = getDesiredSpeeds();

            ((DoubleArrayLogEntry) desStatesLogger).append(desiredSpeeds);
            temperatureLogger.append(motorTemperatures.get(0).getValueAsDouble());
            drivetrainPoseLogger.append(new double[]{robotState.fieldToVehicle.getX(), robotState.fieldToVehicle.getY(), robotState.fieldToVehicle.getRotation().getDegrees()});
            drivetrainChassisSpeedsLogger.append(new double[]{robotState.deltaVehicle.vxMetersPerSecond, robotState.deltaVehicle.vyMetersPerSecond, robotState.deltaVehicle.omegaRadiansPerSecond});
            gyroPitchLogger.append(pigeon.getPitchValue());
            gyroRollLogger.append(pigeon.getRollValue());
            controlRequestLogger.append(request.getClass().getSimpleName());


            var desiredModuleStates = swerveKinematics.toSwerveModuleStates(ChassisSpeeds.fromFieldRelativeSpeeds(
                    desiredSpeeds[0],
                    desiredSpeeds[1],
                    desiredSpeeds[2],
                    robotState.fieldToVehicle.getRotation()
            ));
            SwerveDriveKinematics.desaturateWheelSpeeds(
                    desiredModuleStates,
                    kMaxVelOpenLoopMeters
            );
            var actualModuleStates = train.getState().ModuleStates;

            for (int i = 0; i < 4; i++) {
                desiredModuleStates[i] = SwerveModuleState.optimize(desiredModuleStates[i], robotState.fieldToVehicle.getRotation());
                desiredModuleStatesLogger.get(i * 2).append(desiredModuleStates[i].speedMetersPerSecond);
                desiredModuleStatesLogger.get(i * 2 + 1).append(desiredModuleStates[i].angle.getDegrees());

                if (actualModuleStates!= null) {
                    actualModuleStatesLogger.get(i * 2).append(actualModuleStates[i].speedMetersPerSecond);
                    actualModuleStatesLogger.get(i * 2 + 1).append(actualModuleStates[i].angle.getDegrees());
                }
            }

            desiredModuleStructLogger.append(desiredModuleStates);
            if (actualModuleStates != null) {
                actualModuleStructLogger.append(actualModuleStates);
            }

        }
    }

    @Override
    public void resetOdometry(Pose2d pose) {
        train.seedFieldRelative(pose);
        updateRobotState();
    }

    @Override
    public void zeroSensors(Pose2d pose) {
        resetOdometry(pose);
        startingPose = pose;
        chassisSpeed = new ChassisSpeeds();
        isBraking = false;
    }

    @Override
    public void stop() {
        train.setControl(new SwerveRequest.FieldCentric());
    }

    @Override
    public boolean testSubsystem() {
        //TODO
        return true;
    }

    @Override
    public void setOpenLoop(DriveSignal signal) {
        if (controlState != ControlState.OPEN_LOOP) {
            GreenLogger.log("Switching to open loop.");
            controlState = ControlState.OPEN_LOOP;
//            fieldCentricRequest.withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage);
        }
    }

    @Override
    public void setTeleopInputs(double throttle, double strafe, double rotation) {
        double inputScale = new Translation2d(throttle, strafe).getNorm();

        request = fieldCentricRequest
                .withVelocityX(throttle * inputScale * maxVel12MPS * driveScalar)
                .withVelocityY(strafe * inputScale * maxVel12MPS * driveScalar)
                .withRotationalRate(rotation * kMaxAngularSpeed * Math.PI); //These will need to be multiplied, but i want to test first

        if (Constants.kLoggingDrivetrain) {
            inputLogger.append(new double[] {throttle, strafe, rotation});
        }
        setOpenLoop(null);
    }

    @Override
    public synchronized void setBraking(boolean braking) { //TODO
        isBraking = braking;

        if (braking) {
            isBraking = true;
            setOpenLoop(DriveSignal.BRAKE);
        }
    }

    @Override
    public PIDSlotConfiguration getPIDConfig() {
        // NOTE: Stolen code from ServeDrive. Not sure if it works.
        PIDSlotConfiguration defaultPIDConfig = new PIDSlotConfiguration();
        defaultPIDConfig.kP = 0.0;
        defaultPIDConfig.kI = 0.0;
        defaultPIDConfig.kD = 0.0;
        defaultPIDConfig.kF = 0.0;
        return (factory.getSubsystem(NAME).implemented)
                ? factory
                .getSubsystem(NAME)
                .swerveModules.drivePID.getOrDefault("slot0", defaultPIDConfig)
                : defaultPIDConfig;
    }

    @Override
    public void createPigeon() {
        super.pigeon = new Pigeon2Wrapper(train.getPigeon2());
    }

    /**
     * Returns the list of headings for following a path that are transposed onto a path
     *
     * @return trajectoryHeadings
     */
    public Rotation2d getTrajectoryHeadings() {
        if (headingsList == null) {
            return Constants.EmptyRotation2d;
        } else if (trajectoryIndex > headingsList.size() - 1) {
            return headingsList.get(headingsList.size() - 1);
        }
        if (
                getTrajectoryTimestamp() >
                        trajectory.getStates().get(trajectoryIndex).timeSeconds ||
                        trajectoryIndex == 0
        ) trajectoryIndex++;
        if (trajectoryIndex >= headingsList.size()) {
            GreenLogger.log(headingsList.get(headingsList.size() - 1) + " = max");
            return headingsList.get(headingsList.size() - 1);
        }
        double timeBetweenPoints =
                (
                        trajectory.getStates().get(trajectoryIndex).timeSeconds -
                                trajectory.getStates().get(trajectoryIndex - 1).timeSeconds
                );
        Rotation2d heading;
        heading =
                headingsList
                        .get(trajectoryIndex - 1)
                        .interpolate(
                                headingsList.get(trajectoryIndex),
                                getTrajectoryTimestamp() / timeBetweenPoints
                        );
        return heading;
    }

    /**
     * Starts a trajectory to be followed with headings (rotate while moving)
     *
     * @param trajectory Trajectory
     * @param headings   Headings (for swerve)
     * @see Drive#startTrajectory(Trajectory, List)
     */
    @Override
    public void startTrajectory(Trajectory trajectory, List<Rotation2d> headings) {
        super.startTrajectory(trajectory, headings);
        headingsList = headings;
        trajectoryIndex = 0;
    }


    @Override
    public void setModuleStates(SwerveModuleState... desiredStates) {
        for (int i = 0; i < 4; i++) {
            desiredStates[i].speedMetersPerSecond =
                    DriveConversions.metersToRotations(desiredStates[i].speedMetersPerSecond);
        }

        request = autoRequest.withModuleStates(desiredStates);

        train.setControl(request);
    }

    public double[] getDesiredSpeeds() {
        if (request instanceof SwerveRequest.FieldCentric) {
           return new double[]{
                ((SwerveRequest.FieldCentric) request).VelocityX,
                ((SwerveRequest.FieldCentric) request).VelocityY,
                ((SwerveRequest.FieldCentric) request).RotationalRate
           };
        } else if (request instanceof ModuleRequest) {
            ChassisSpeeds moduleSpeeds = swerveKinematics.toChassisSpeeds(((ModuleRequest) request).moduleStates);
            return new double[] {
                    moduleSpeeds.vxMetersPerSecond,
                    moduleSpeeds.vyMetersPerSecond,
                    moduleSpeeds.omegaRadiansPerSecond
            };
        } else {
            return new double[3];
        }
    }
    public String toModuleName(int moduleIndex) {
        return switch (moduleIndex) {
            case 0 -> "frontLeft";
            case 1 -> "frontRight";
            case 2 -> "backLeft";
            case 3 -> "backRight";
            default -> "unknown";
        };
    }

    private void configMotors() {
        TalonFXConfiguration motorConfig = new TalonFXConfiguration();
        TalonFXConfigurator motorConfigurator;

        for (int i = 0; i < 4; i++) {
            var swerveModule = train.getModule(i);
            //Drive Configs
            motorConfigurator = swerveModule.getDriveMotor().getConfigurator();
            motorConfigurator.refresh(motorConfig);
            motorConfigurator.apply(motorConfig.withOpenLoopRamps(new OpenLoopRampsConfigs().withDutyCycleOpenLoopRampPeriod(factory.getConstant(NAME, "openLoopRampRate"))));

            //Steer configs
            motorConfigurator = swerveModule.getSteerMotor().getConfigurator();
            motorConfigurator.refresh(motorConfig);
            motorConfigurator.apply(motorConfig.withClosedLoopRamps(new ClosedLoopRampsConfigs().withVoltageClosedLoopRampPeriod(factory.getConstant(NAME, "openLoopRampRate"))));
        }
    }
}
