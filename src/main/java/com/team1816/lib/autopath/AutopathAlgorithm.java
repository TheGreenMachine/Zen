package com.team1816.lib.autopath;

import com.team1816.lib.subsystems.drive.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;

import java.util.ArrayList;
import java.util.List;

public class AutopathAlgorithm {
    public static Trajectory calculateAutopath(Pose2d autopathTargetPosition){
        Trajectory bestGuessTrajectory = null;
        int loops = 0;

//     Example of 2d array looping
        int[][] badArray = new int[100][100];
        int[] array = new int[100];

        for(int arrayElementIndex = 0; arrayElementIndex < array.length; arrayElementIndex++) {
            for (int arrayElementIndex2 = 0; arrayElementIndex2 < array.length; arrayElementIndex2++){
                badArray[arrayElementIndex2][arrayElementIndex] = 1;
            }
        }

        for(int arrayElementIndex = 0; arrayElementIndex < array.length; arrayElementIndex++) {
            array[arrayElementIndex] = 1;
        }

        while(bestGuessTrajectory == null || !Autopath.testTrajectory((bestGuessTrajectory))){
            TrajectoryConfig config = new TrajectoryConfig(Drive.kPathFollowingMaxVelMeters, Drive.kPathFollowingMaxAccelMeters);

            List<Translation2d> waypoints = List.of(
            );

            for(int loops2 = 0; loops2 < 360; loops2++) {
                bestGuessTrajectory = TrajectoryGenerator.generateTrajectory(

//                    Autopath.robotState.fieldToVehicle,
                        new Pose2d(new Translation2d(0, 0), Rotation2d.fromDegrees(loops)),
                        waypoints,
                        new Pose2d(autopathTargetPosition.getTranslation(), Rotation2d.fromDegrees(loops2)),
                        config
                );
            }

            /**
             *    Create your autopathing method here
             *
             *    ...hint(use the TrajectoryGenerator class)
             */

            loops++;
        }

        System.out.println("Autopath looped "+loops+" times");

        return bestGuessTrajectory;
    }
}