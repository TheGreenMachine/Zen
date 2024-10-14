package com.team1816.lib.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.autopath.Autopath;
import com.team1816.lib.autopath.AutopathAlgorithm;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;

import java.util.ArrayList;
import java.util.List;

public class AutopathMode extends AutoMode{
    @Override
    protected void routine() throws AutoModeEndedException {
        Trajectory autopathTrajectory = new Trajectory();

        autopathTrajectory = AutopathAlgorithm.calculateAutopath(new Pose2d(new Translation2d(robotState.fieldToVehicle.getX()+10, robotState.fieldToVehicle.getY()+5), new Rotation2d(0)));

        List<Rotation2d> autopathHeadings = new ArrayList<>();
        //TODO create headings
        // for now I'll make it use the current robot rotation
        for(int i = 0; i < autopathTrajectory.getStates().size(); i++)
            autopathHeadings.add(robotState.fieldToVehicle.getRotation());

        //Here's where your trajectory gets checked against the field
        System.out.println("And survey says: "+Autopath.testTrajectory(autopathTrajectory));

        TrajectoryAction autopathTrajectoryAction = new TrajectoryAction(autopathTrajectory, autopathHeadings);

        runAction(autopathTrajectoryAction);
    }
}
