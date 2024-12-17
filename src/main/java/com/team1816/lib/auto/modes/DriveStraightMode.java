package com.team1816.lib.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.DriveOpenLoopAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.paths.DriveStraightPath;
import com.team1816.lib.util.logUtil.GreenLogger;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class DriveStraightMode extends AutoMode {

    @Override
    protected void routine() throws AutoModeEndedException {
        GreenLogger.log("Running Drive Straight Mode");
        runAction(new WaitAction(.5));
        runAction(new TrajectoryAction(new DriveStraightPath()));
    }

    public Pose2d getInitialPose() {
        return new Pose2d(2.6,5.5, robotState.allianceColor == Color.BLUE ? Rotation2d.fromDegrees(0) : Rotation2d.fromDegrees(180));
    }
}
