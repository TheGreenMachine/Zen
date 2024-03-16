package com.team1816.lib.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Autonomous mode to do absolutely nothing
 */
public class DoNothingMode extends AutoMode {

    public DoNothingMode () {}

    public DoNothingMode(AutoModeManager.ShootPos start) {
        switch (start) {
            case TOP_SPEAKER -> {
                initialPose = new Pose2d(0.721, 6.762, Rotation2d.fromDegrees(60));
            }
            case MIDDLE_SPEAKER -> {
                initialPose = new Pose2d(1.40, 5.55, Rotation2d.fromDegrees(0));
            }
            case BOTTOM_SPEAKER -> {
                initialPose = new Pose2d(0.752, 4.364, Rotation2d.fromDegrees(-60));
            }
            default -> {
                initialPose = new Pose2d(1.47, 7.3, Rotation2d.fromDegrees(0));
            }
        };
    }

    /**
     * Routine. Does nothing.
     *
     * @throws AutoModeEndedException
     * @see AutoMode#routine()
     */
    @Override
    protected void routine() throws AutoModeEndedException {
        GreenLogger.log("doing nothing");
    }
}
