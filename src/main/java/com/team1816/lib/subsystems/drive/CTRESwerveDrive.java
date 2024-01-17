package com.team1816.lib.subsystems.drive;

import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.PIDSlotConfiguration;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.util.team254.DriveSignal;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

@Singleton
public class CTRESwerveDrive extends Drive {
    /** Constants */

    /**
     * Module Characterization
     */
    private static final double moduleDeltaX = kDriveWheelbaseLengthMeters / 2.0;
    private static final double moduleDeltaY = kDriveWheelTrackWidthMeters / 2.0;

    // module indices
    public static final int kFrontLeft = 0;
    public static final int kFrontRight = 1;
    public static final int kBackLeft = 2;
    public static final int kBackRight = 3;

    // module positions
    public static final Translation2d kFrontLeftModulePosition = new Translation2d(
            moduleDeltaX,
            moduleDeltaY
    );
    public static final Translation2d kFrontRightModulePosition = new Translation2d(
            moduleDeltaX,
            -moduleDeltaY
    );
    public static final Translation2d kBackLeftModulePosition = new Translation2d(
            -moduleDeltaX,
            moduleDeltaY
    );
    public static final Translation2d kBackRightModulePosition = new Translation2d(
            -moduleDeltaX,
            -moduleDeltaY
    );

    public static final Translation2d[] kModulePositions = {
            kFrontLeftModulePosition,
            kFrontRightModulePosition,
            kBackRightModulePosition,
            kBackLeftModulePosition,
    };

    // Kinematics (https://docs.wpilib.org/en/stable/docs/software/kinematics-and-odometry/swerve-drive-kinematics.html)
    public static final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
            kFrontLeftModulePosition,
            kFrontRightModulePosition,
            kBackLeftModulePosition,
            kBackRightModulePosition
    );

    /**
     * Components
     */
    public SwerveModule[] swerveModules;

    /**
     * States
     */
    public SwerveModuleState[] desiredModuleStates = new SwerveModuleState[4];
    SwerveModuleState[] actualModuleStates = new SwerveModuleState[4];
    SwerveModulePosition[] actualModulePositions = new SwerveModulePosition[4];
    public double[] motorTemperatures = new double[4];

    private SwerveDrivetrain train;

    private SwerveRequest request;

    @Inject
    public CTRESwerveDrive(LedManager lm, Infrastructure inf, RobotState rs) {
        super(lm, inf, rs);

        SwerveDrivetrainConstants constants = new SwerveDrivetrainConstants();
        constants.CANbusName = "highSpeed"; // TODO: Make more flexible.
        constants.Pigeon2Id = 32;

        train = new SwerveDrivetrain(constants);
        request = new SwerveRequest.Idle();
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
        double[] actualStates = new double[8];
        double[] desiredStates = new double[8];
        for (int i = 0; i < 4; i++) {
            // logging actual angle and velocity of swerve motors (azimuth & drive)
            swerveModules[i].update();
            actualModuleStates[i] = swerveModules[i].getActualState();
            actualModulePositions[i] = swerveModules[i].getActualPosition();
            // logging current temperatures of each module's drive motor
            motorTemperatures[i] = swerveModules[i].getMotorTemp();

            if (Constants.kLoggingDrivetrain) {
                // populating double list with actState angles and speeds
                actualStates[i * 2] = actualModuleStates[i].angle.getRadians();
                actualStates[i * 2 + 1] = actualModuleStates[i].speedMetersPerSecond;

                // populating double list with desState angles and speeds
                desiredStates[i * 2] = desiredModuleStates[i].angle.getRadians();
                desiredStates[i * 2 + 1] = desiredModuleStates[i].speedMetersPerSecond;
            }
        }
        chassisSpeed = swerveKinematics.toChassisSpeeds(actualModuleStates);

        updateRobotState();
    }

    @Override
    public void updateRobotState() {

    }

    @Override
    public void resetOdometry(Pose2d pose) {

    }

    @Override
    public void zeroSensors(Pose2d pose) {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean testSubsystem() {
        return true;
    }

    @Override
    public void setOpenLoop(DriveSignal signal) {
        if (controlState != ControlState.OPEN_LOOP) {
            GreenLogger.log("Switching to open loop.");
            controlState = ControlState.OPEN_LOOP;
        }
    }

    @Override
    public void setTeleopInputs(double forward, double strafe, double rotation) {
        SwerveRequest.ApplyChassisSpeeds speeds_request = new SwerveRequest.ApplyChassisSpeeds();

        speeds_request.Speeds = chassisSpeed;

    }

    @Override
    public synchronized void setBraking(boolean braking) {
        isBraking = braking;

        if (braking) {
            request = new SwerveRequest.SwerveDriveBrake();
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


}
