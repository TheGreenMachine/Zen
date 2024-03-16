package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.RotateSwerveAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.actions.*;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;

public class ScoreAndSitMode extends AutoMode {

    public ScoreAndSitMode(AutoModeManager.ShootPos start) {
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

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new ShootSpeakerAction()
        );
    }

}
