package com.team1816.lib.subsystems.drive;

import com.ctre.phoenix6.StatusSignal;
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
import com.team1816.lib.hardware.components.gyro.CTREPigeonWrapper;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.util.team254.DriveSignal;
import com.team1816.lib.util.team254.SwerveDriveHelper;
import com.team1816.lib.util.team254.SwerveDriveSignal;
import com.team1816.season.Robot;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CTRESwerveDrive extends Drive {
    private final ArrayList<StatusSignal<Double>> motorTemperatures = new ArrayList<>();
    /**
     * Components
     */
    private SwerveDrivetrain train;
    private SwerveModuleConstants[] swerveModules;

    /**
     * Control
     */
    private SwerveRequest request;
    private SwerveRequest.FieldCentric fieldCentricRequest;
    private SwerveRequest.PointWheelsAt pointWheelsAt;

    /**
     * Properties
     */
    private SwerveDriveKinematics swerveKinematics;

    /**
     * Logging
     */
    private DoubleLogEntry temperatureLogger;

    // module indices
    public static final int kFrontLeft = 0;
    public static final int kFrontRight = 1;
    public static final int kBackLeft = 2;
    public static final int kBackRight = 3;

    @Inject
    public CTRESwerveDrive(LedManager lm, Infrastructure inf, RobotState rs) {
        super(lm, inf, rs);
        temperatureLogger = new DoubleLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/moduleTemps");

        swerveModules = new SwerveModuleConstants[4];

        swerveModules[kFrontLeft] = factory.getCTRESwerveModule(NAME, "frontLeft");
        swerveModules[kFrontRight] = factory.getCTRESwerveModule(NAME, "frontRight");
        swerveModules[kBackLeft] = factory.getCTRESwerveModule(NAME, "backLeft");
        swerveModules[kBackRight] = factory.getCTRESwerveModule(NAME, "backRight");


        SwerveDrivetrainConstants constants = new SwerveDrivetrainConstants()
                .withCANbusName("highSpeed")
                .withPigeon2Id(32); // TODO: Make more flexible.


        train = new SwerveDrivetrain(constants, swerveModules);
        request = new SwerveRequest.Idle();

        Translation2d[] moduleLocations = new Translation2d[4];
        for (int i = 0; i < 4; i++) {
            moduleLocations[i] = new Translation2d(swerveModules[i].LocationX, swerveModules[i].LocationY);
        }

        swerveKinematics = new SwerveDriveKinematics(moduleLocations);

        for (int i = 0; i < 4; i++) {
            System.out.println("setting " + i + " to " + train.getModule(i).getDriveMotor().getDeviceTemp());
            motorTemperatures.add(train.getModule(i).getDriveMotor().getDeviceTemp());
        }

        fieldCentricRequest = new SwerveRequest.FieldCentric()
                .withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage)
                .withSteerRequestType(SwerveModule.SteerRequestType.MotionMagic)
                .withDeadband(0.1 * kMaxVelOpenLoopMeters)
                .withRotationalDeadband(0.05 * kMaxAngularSpeed);

        pointWheelsAt = new SwerveRequest.PointWheelsAt()
                .withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage)
                .withSteerRequestType(SwerveModule.SteerRequestType.MotionMagic)
                .withModuleDirection(Rotation2d.fromDegrees(0));

        if (Constants.kLoggingRobot) {
            gyroPitchLogger = new DoubleLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/Pitch");
            gyroRollLogger = new DoubleLogEntry(DataLogManager.getLog(), "Drivetrain/Swerve/Roll");
        }
    }

    @Override
    public synchronized void writeToHardware() {
        if (controlState == ControlState.OPEN_LOOP) {
//            train.setControl(pointWheelsAt);
            train.setControl(request);
        }
    }

    @Override
    public synchronized void readFromHardware() {
        super.readFromHardware();

        chassisSpeed = swerveKinematics.toChassisSpeeds(train.getState().ModuleStates);

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

        temperatureLogger.append(motorTemperatures.get(0).getValueAsDouble());
        robotState.drivetrainTemp = motorTemperatures.get(0).getValueAsDouble();

        robotState.vehicleToFloorProximityCentimeters = infrastructure.getMaximumProximity();

//        swerveOdometry.update(Rotation2d.fromDegrees(train.getPigeon2().getAngle()));

        if (Constants.kLoggingDrivetrain) {
            drivetrainPoseLogger.append(new double[]{robotState.fieldToVehicle.getX(), robotState.fieldToVehicle.getY(), robotState.fieldToVehicle.getRotation().getDegrees()});
            drivetrainChassisSpeedsLogger.append(new double[]{robotState.deltaVehicle.vxMetersPerSecond, robotState.deltaVehicle.vyMetersPerSecond, robotState.deltaVehicle.omegaRadiansPerSecond});
            gyroPitchLogger.append(pigeon.getPitchValue());
            gyroRollLogger.append(pigeon.getRollValue());
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
        // TODO: Figure out what would be considered stop with the swervedrivetrain.
    }

    @Override
    public boolean testSubsystem() {
        // TODO: Figure out what would be considered testing system.
        return true;
    }

    @Override
    public void setOpenLoop(DriveSignal signal) {
        if (controlState != ControlState.OPEN_LOOP) {
            GreenLogger.log("Switching to open loop.");
            controlState = ControlState.OPEN_LOOP;
            fieldCentricRequest.withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage);
        }
    }

    @Override
    public void setTeleopInputs(double forward, double strafe, double rotation) {
//        SwerveRequest.ApplyChassisSpeeds speeds_request = new SwerveRequest.ApplyChassisSpeeds();
//
////        speeds_request.Speeds = chassisSpeed;
//        // TODO: Elena suggested that this maybe would work.
//        // Explanation: Pythagorean theorem.
//        speeds_request.Speeds = new ChassisSpeeds(forward, strafe, rotation);
//
//        request = speeds_request;

        request = fieldCentricRequest
                .withVelocityY(strafe * kMaxVelOpenLoopMeters)
                .withVelocityX(forward * kMaxVelOpenLoopMeters)
                .withRotationalRate(rotation * kMaxAngularSpeed);

        setOpenLoop(null);
    }

    @Override
    public synchronized void setBraking(boolean braking) {
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
        super.pigeon = new CTREPigeonWrapper(train.getPigeon2());
    }

}
