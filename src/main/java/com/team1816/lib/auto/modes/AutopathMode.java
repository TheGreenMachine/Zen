package com.team1816.lib.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.autopath.Autopath;
import com.team1816.lib.autopath.AutopathAlgorithm;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.spline.SplineParameterizer;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryParameterizer;

import java.util.*;

public class AutopathMode extends AutoMode{
     Translation2d startTranslation = new Translation2d(2.6, 5.5);
//    Translation2d startTranslation = new Translation2d(14, 4);
    //total avg: -0.15704970708570218 pos avg: 0.18805418060928844 ratio: 6.142624593037816


    @Override
    protected void routine() throws AutoModeEndedException {
        Trajectory autopathTrajectory;

        double beforeTime = System.nanoTime();

        Autopath.robotState.autopathWaypointsSuccess.clear();
        Autopath.robotState.autopathWaypointsFail.clear();

//        int i3 = 0;
//        long totalTime = 0;
//        long highestTime = -1;
//        Autopath.robotState.printAutopathFieldTest = true;
//        for(double i = 16.51; i >= 0; i-=16.51/200.){
//            double lastTime = 0;
//            for(double i2 = 0; i2 <= 8.21; i2+=8.21/100.){
//                if(lastTime < 25) {
//                    try {
//                        Thread.sleep((long) (25 - lastTime));
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//                long holdStartTime = System.nanoTime()/1000000;
//                try{
//                    i3++;
//                    Trajectory test = AutopathAlgorithm.calculateAutopath(new Pose2d(startTranslation, new Rotation2d()), new Pose2d(new Translation2d(i, i2), new Rotation2d(0)));
////                    Trajectory test = AutopathAlgorithm.calculateAutopath(new Pose2d(new Translation2d(i, i2), new Rotation2d(0)), new Pose2d(startTranslation, new Rotation2d()));
//                    if(test != null && test.getStates().size() > 1)
//                        Autopath.robotState.autopathWaypointsSuccess.add(new Pose2d(new Translation2d(i, i2), new Rotation2d()));
//                    else
//                        Autopath.robotState.autopathWaypointsFail.add(new Pose2d(new Translation2d(i, i2), new Rotation2d()));
//                } catch (SplineParameterizer.MalformedSplineException |
//                         TrajectoryParameterizer.TrajectoryGenerationException e){
//                    Autopath.robotState.autopathWaypointsFail.add(new Pose2d(new Translation2d(i, i2), new Rotation2d()));
//                }
//                long holdEndTime = System.nanoTime()/1000000;
//                totalTime += holdEndTime-holdStartTime;
//                if(holdEndTime-holdStartTime > highestTime)
//                    highestTime = holdEndTime-holdStartTime;
//                lastTime = holdEndTime-holdStartTime;
//                System.out.println(highestTime + ", " + totalTime/i3);
////                11661, 25 both
////                1383, 7 backwards
//            }
//        }

        autopathTrajectory = AutopathAlgorithm.calculateAutopath(new Pose2d(new Translation2d(1.6, 5.5), Rotation2d.fromDegrees(90)));
//        autopathTrajectory = AutopathAlgorithm.calculateAutopath(new Pose2d(new Translation2d(1.3, 1.25), new Rotation2d(0)));


        System.out.println("Time taken "+(System.nanoTime()-beforeTime)/1000000000);

        List<Rotation2d> autopathHeadings = new ArrayList<>();
        double autopathTrajectoryTime = autopathTrajectory.getTotalTimeSeconds();
        for(int i = 0; i < autopathTrajectory.getStates().size(); i++){
            autopathHeadings.add(Rotation2d.fromDegrees(
                    robotState.fieldToVehicle.getRotation().getDegrees() * ((autopathTrajectoryTime - autopathTrajectory.getStates().get(i).timeSeconds) / autopathTrajectoryTime) +
                            90 * (autopathTrajectory.getStates().get(i).timeSeconds / autopathTrajectoryTime)
            ));
        }
//        List<Rotation2d> autopathHeadings = List.of(
//                Rotation2d.fromRadians(0),
//                Rotation2d.fromRadians(0.3),
//                Rotation2d.fromRadians(0.43),
//                Rotation2d.fromRadians(0.52),
//                Rotation2d.fromRadians(0.59),
//                Rotation2d.fromRadians(0.70),
//                Rotation2d.fromRadians(0.79),
//                Rotation2d.fromRadians(0.87),
//                Rotation2d.fromRadians(0.98),
//                Rotation2d.fromRadians(1.06),
//                Rotation2d.fromRadians(1.15),
//                Rotation2d.fromRadians(1.27),
//                Rotation2d.fromRadians(1.57)
//        );

        System.out.println(autopathHeadings);

        //Here's where your trajectory gets checked against the field
        System.out.println("And survey says: "+Autopath.testTrajectory(autopathTrajectory));

        TrajectoryAction autopathTrajectoryAction = new TrajectoryAction(autopathTrajectory, autopathHeadings);

        runAction(autopathTrajectoryAction);
    }

    public Pose2d getInitialPose() {
        return new Pose2d(startTranslation, robotState.allianceColor == Color.BLUE ? Rotation2d.fromDegrees(0) : Rotation2d.fromDegrees(180));
    }
}
