package com.team1816.lib.autopath;

import com.team1816.lib.subsystems.drive.Drive;
import edu.wpi.first.math.geometry.Pose2d;
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

        while(bestGuessTrajectory == null || !Autopath.testTrajectory(bestGuessTrajectory)){
            System.out.println("Robot is at "+Autopath.robotState.fieldToVehicle);
            System.out.println("Target is at "+autopathTargetPosition);


            TrajectoryConfig config = new TrajectoryConfig(Drive.kPathFollowingMaxVelMeters, Drive.kPathFollowingMaxAccelMeters);

            bestGuessTrajectory = TrajectoryGenerator.generateTrajectory(
                    Autopath.robotState.fieldToVehicle,
                    new ArrayList<>(),
                    autopathTargetPosition,
                    config
            );

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
