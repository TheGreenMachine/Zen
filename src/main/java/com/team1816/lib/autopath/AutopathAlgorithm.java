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
        return calculateAutopath(Autopath.robotState.fieldToVehicle, autopathTargetPosition);
    }

    public static Trajectory calculateAutopath(Pose2d autopathStartPosition, Pose2d autopathTargetPosition){
        Trajectory bestGuessTrajectory = null;

        Pose2d startPos = autopathStartPosition;

        TrajectoryConfig config = new TrajectoryConfig(Drive.kPathFollowingMaxVelMeters, Drive.kPathFollowingMaxAccelMeters);

        List<Translation2d> waypoints = new ArrayList<>();

        double beforeTime = System.nanoTime();

        while(!Autopath.testTrajectory(bestGuessTrajectory)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Translation2d firstWaypoint = waypoints.size() > 0 ? waypoints.get(1) : autopathTargetPosition.getTranslation();
            Translation2d lastWaypoint = waypoints.size() > 0 ? waypoints.get(waypoints.size()-2) : startPos.getTranslation();

            bestGuessTrajectory = TrajectoryGenerator.generateTrajectory(
                    new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(firstWaypoint.getY()-startPos.getY(), firstWaypoint.getX()-startPos.getX()))),
//                    new Pose2d(startPos.getTranslation(), new Rotation2d()),

                    waypoints,
                    new Pose2d(autopathTargetPosition.getTranslation(), Rotation2d.fromRadians(Math.atan2(autopathTargetPosition.getY()-lastWaypoint.getY(), autopathTargetPosition.getX()-lastWaypoint.getX()))),
//                    new Pose2d(autopathTargetPosition.getTranslation(), new Rotation2d()),

                    config
            );

            if(Autopath.robotState.autopathTrajectory != bestGuessTrajectory) {
                Autopath.robotState.autopathTrajectoryChanged = true;
                Autopath.robotState.autopathTrajectory = bestGuessTrajectory;
            } else{
                System.out.println("Path is not right and looping");
            }

            if (Autopath.testTrajectory(bestGuessTrajectory)) {
                System.out.println("found with waypoints: " + waypoints);
                Autopath.robotState.autopathTrajectory = bestGuessTrajectory;
                return bestGuessTrajectory;

            } else {
                Autopath.TimestampTranslation2d startCollision = Autopath.returnCollisionStart(bestGuessTrajectory);

                Autopath.TimestampTranslation2d endCollision = Autopath.returnCollisionEnd(bestGuessTrajectory, startCollision);



                System.out.println("Colliding at " + startCollision.getTranslation2d() + " to " + endCollision.getTranslation2d());
            }
        }

        System.out.println("Autopath Idek");
        System.out.println("tried with waypoints " + waypoints);

        return null;
    }
}
