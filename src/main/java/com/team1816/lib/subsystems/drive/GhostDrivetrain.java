package com.team1816.lib.subsystems.drive;

import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.PIDSlotConfiguration;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.util.team254.DriveSignal;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.geometry.Pose2d;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * A Drivetrain with nothing in it to allow for Drive to be unimplemented
 * @see Drive
 */
@Singleton
public class GhostDrivetrain extends Drive {

    //TODO this class is a STOPGAP, find the root of this needing to exist after duluth
    
    /**
     * Instantiates the Drive with base subsystem parameters and accounts for DemoMode
     *
     * @param lm  LEDManager
     * @param inf Infrastructure
     * @param rs  RobotState
     */
    @Inject
    public GhostDrivetrain(LedManager lm, Infrastructure inf, RobotState rs) {
        super(lm, inf, rs);
    }

    @Override
    public void writeToHardware() {
        
    }

    @Override
    protected void updateRobotState() {

    }

    @Override
    public void setOpenLoop(DriveSignal signal) {

    }

    @Override
    public void setTeleopInputs(double forward, double strafe, double rotation) {

    }

    @Override
    public PIDSlotConfiguration getPIDConfig() {
        return null;
    }

    @Override
    public void setBraking(boolean on) {

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
        return false;
    }

    @Override
    public void configureOrchestra() {

    }
}
