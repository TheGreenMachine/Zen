package com.team1816.lib.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Autonomous mode to do absolutely nothing
 */
public class DoNothingMode extends AutoMode {

    public DoNothingMode () {}

    public DoNothingMode(AutoModeManager.ShootPos start, Color color) {
        double poseX;
        switch (start) {
            case TOP_SPEAKER -> {
                initialPose = new Pose2d(color == Color.BLUE ? 0.721 : (2 * Constants.fieldCenterX) - 0.721,
                        6.762,
                        Rotation2d.fromDegrees(color == Color.BLUE ? 60 : 120));
            }
            case MIDDLE_SPEAKER -> {
                initialPose = new Pose2d(color == Color.BLUE ? 1.40 : (2 * Constants.fieldCenterX) - 1.40,
                        5.55,
                        Rotation2d.fromDegrees(color == Color.BLUE ? 0 : 180));
            }
            case BOTTOM_SPEAKER -> {
                initialPose = new Pose2d(color == Color.BLUE ? 0.752 : (2 * Constants.fieldCenterX) - 0.752,
                        4.364,
                        Rotation2d.fromDegrees(color == Color.BLUE ? -60 : 240));
            }
            default -> {
                initialPose = new Pose2d(color == Color.BLUE ? 1.47 : (2 * Constants.fieldCenterX) - 1.47,
                        7.3,
                        Rotation2d.fromDegrees(color == Color.BLUE ? 0 : 180));
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
