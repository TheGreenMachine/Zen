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
import com.team1816.lib.util.team254.SwerveDriveSignal;
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

//        speeds_request.Speeds = chassisSpeed;
        // TODO: Elena suggested that this maybe would work.
        // Explanation: Pythagorean theorem.
        speeds_request.Speeds = new ChassisSpeeds(forward, strafe, rotation);

        request = speeds_request;
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
