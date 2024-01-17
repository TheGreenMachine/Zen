package com.team1816.lib.subsystems.drive;

import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.PIDSlotConfiguration;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.util.team254.DriveSignal;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.geometry.Pose2d;

@Singleton
public class CTRESwerveDrive extends Drive {
    private SwerveDrivetrain train;

    @Inject
    public CTRESwerveDrive(LedManager lm, Infrastructure inf, RobotState rs) {
        super(lm, inf, rs);

        SwerveDrivetrainConstants constants = new SwerveDrivetrainConstants();
        constants.CANbusName = "highSpeed"; // TODO: Make more flexible.
        constants.Pigeon2Id = 32;

        train = new SwerveDrivetrain(constants);
    }

    @Override
    public synchronized void writeToHardware() {

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

    }

    @Override
    public void setBraking(boolean srtBrake) {

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
