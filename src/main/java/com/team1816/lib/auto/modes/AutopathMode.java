package com.team1816.lib.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
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
     Translation2d startTranslation = new Translation2d(14, 4);

    @Override
    protected void routine() throws AutoModeEndedException {
        Trajectory autopathTrajectory;

        double beforeTime = System.nanoTime();

        Autopath.robotState.autopathWaypointsSuccess.clear();
        Autopath.robotState.autopathWaypointsFail.clear();

        int i3 = 0;
        long totalTime = 0;
        long highestTime = -1;
        for(double i = 16.51; i >= 0; i-=16.51/200.){
            for(double i2 = 0; i2 <= 8.21; i2+=8.21/100.){
                long holdStartTime = System.nanoTime()/1000000;
                try{
                    i3++;
                    Trajectory test = AutopathAlgorithm.calculateAutopath(new Pose2d(startTranslation, new Rotation2d()), new Pose2d(new Translation2d(i, i2), new Rotation2d(0)));
                    if(test != null && test.getStates().size() > 1)
                        Autopath.robotState.autopathWaypointsSuccess.add(new Pose2d(new Translation2d(i, i2), new Rotation2d()));
                    else
                        Autopath.robotState.autopathWaypointsFail.add(new Pose2d(new Translation2d(i, i2), new Rotation2d()));
                } catch (Exception e){
                    Autopath.robotState.autopathWaypointsFail.add(new Pose2d(new Translation2d(i, i2), new Rotation2d()));
                }
                long holdEndTime = System.nanoTime()/1000000;
                totalTime += holdEndTime-holdStartTime;
                if(holdEndTime-holdStartTime > highestTime)
                    highestTime = holdEndTime-holdStartTime;
                System.out.println(highestTime + ", " + totalTime/i3);
            }
        }

        autopathTrajectory = AutopathAlgorithm.calculateAutopath(new Pose2d(new Translation2d(14, 4), new Rotation2d(0)));


        System.out.println("Time taken "+(System.nanoTime()-beforeTime)/1000000000);

        List<Rotation2d> autopathHeadings = new ArrayList<>();
        //TODO create headings
        // for now I'll make it use the current robot rotation
        autopathHeadings.add(robotState.fieldToVehicle.getRotation());

        //Here's where your trajectory gets checked against the field
        System.out.println("And survey says: "+Autopath.testTrajectory(autopathTrajectory));

        TrajectoryAction autopathTrajectoryAction = new TrajectoryAction(autopathTrajectory, autopathHeadings);

        runAction(autopathTrajectoryAction);
    }

    public Pose2d getInitialPose() {
        return new Pose2d(startTranslation, robotState.allianceColor == Color.BLUE ? Rotation2d.fromDegrees(0) : Rotation2d.fromDegrees(180));
    }
}
