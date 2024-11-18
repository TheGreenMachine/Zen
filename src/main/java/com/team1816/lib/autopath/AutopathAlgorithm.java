package com.team1816.lib.autopath;

import com.team1816.lib.subsystems.drive.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutopathAlgorithm {
    public static Trajectory calculateAutopath(Pose2d autopathTargetPosition){
        return calculateAutopath(Autopath.robotState.fieldToVehicle, autopathTargetPosition);
    }

    public static Trajectory calculateAutopath(Pose2d autopathStartPosition, Pose2d autopathTargetPosition){
        Autopath.robotState.autopathWaypoints.clear();
        Autopath.robotState.autopathTrajectory = null;
        Autopath.robotState.autopathCollisionStarts.clear();
        Autopath.robotState.autopathCollisionEnds.clear();

        Trajectory bestGuessTrajectory = null;

        Pose2d startPos = autopathStartPosition;

        TrajectoryConfig config = new TrajectoryConfig(Drive.kPathFollowingMaxVelMeters, Drive.kPathFollowingMaxAccelMeters);

        List<Translation2d> waypoints = new ArrayList<>();

        double beforeTime = System.nanoTime();

        while(!Autopath.testTrajectory(bestGuessTrajectory)) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Translation2d firstWaypoint = waypoints.size() > 0 ? waypoints.get(0) : autopathTargetPosition.getTranslation();
            Translation2d lastWaypoint = waypoints.size() > 0 ? waypoints.get(waypoints.size()-1) : startPos.getTranslation();

            bestGuessTrajectory = TrajectoryGenerator.generateTrajectory(
                    new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(firstWaypoint.getY()-startPos.getY(), firstWaypoint.getX()-startPos.getX()))),
                    waypoints,
                    new Pose2d(autopathTargetPosition.getTranslation(), Rotation2d.fromRadians(Math.atan2(autopathTargetPosition.getY()-lastWaypoint.getY(), autopathTargetPosition.getX()-lastWaypoint.getX()))),
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
                int[] tempNewWaypoint = getWaypoint(bestGuessTrajectory, true);
                double[] newWaypoint = new double[]{tempNewWaypoint[0]/100., tempNewWaypoint[1]/100.};

                addNewWaypoint(newWaypoint, waypoints, startPos, autopathTargetPosition, config);

                Autopath.robotState.autopathWaypoints.add(new Pose2d(new Translation2d(newWaypoint[0], newWaypoint[1]), new Rotation2d()));

//                System.out.println("Colliding at " + startCollision.getTranslation2d() + " to " + endCollision.getTranslation2d());
                System.out.println("Solved with waypoint: " + newWaypoint[0] + ", " + newWaypoint[1]);
            }
        }

        System.out.println("Autopath Idek");
        System.out.println("tried with waypoints " + waypoints);

        return null;
    }

    private static void addNewWaypoint(double[] newWaypoint, List<Translation2d> waypoints, Pose2d startPos, Pose2d endPos, TrajectoryConfig config){
        int waypointsBeforeSize = waypoints.size();
        Translation2d newFirstWaypoint = new Translation2d(newWaypoint[0], newWaypoint[1]);
        Translation2d newLastWaypoint = waypoints.size() > 0 ? waypoints.get(waypoints.size()-1) : new Translation2d(newWaypoint[0], newWaypoint[1]);
        waypoints.add(0, new Translation2d(newWaypoint[0], newWaypoint[1]));
        double bestTime = TrajectoryGenerator.generateTrajectory(
                new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(newFirstWaypoint.getY()-startPos.getY(), newFirstWaypoint.getX()-startPos.getX()))),
                waypoints,
                new Pose2d(endPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(endPos.getY()-newLastWaypoint.getY(), endPos.getX()-newLastWaypoint.getX()))),
                config
        ).getTotalTimeSeconds();
        waypoints.remove(0);
        int bestIndex = 0;

        for(int i = 0; i < waypoints.size(); i++){
            newFirstWaypoint = waypoints.get(0);
            newLastWaypoint = waypoints.get(waypoints.size()-1);

            double newTime;

            if(i == waypoints.size()-1) {
                newLastWaypoint = new Translation2d(newWaypoint[0], newWaypoint[1]);
                waypoints.add(new Translation2d(newWaypoint[0], newWaypoint[1]));
                newTime = TrajectoryGenerator.generateTrajectory(
                        new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(newFirstWaypoint.getY() - startPos.getY(), newFirstWaypoint.getX() - startPos.getX()))),
                        waypoints,
                        new Pose2d(endPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(endPos.getY() - newLastWaypoint.getY(), endPos.getX() - newLastWaypoint.getX()))),
                        config
                ).getTotalTimeSeconds();
                waypoints.remove(waypoints.size() - 1);
            }
            else {
                waypoints.add(i+1, new Translation2d(newWaypoint[0], newWaypoint[1]));
                newTime = TrajectoryGenerator.generateTrajectory(
                        new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(newFirstWaypoint.getY() - startPos.getY(), newFirstWaypoint.getX() - startPos.getX()))),
                        waypoints,
                        new Pose2d(endPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(endPos.getY() - newLastWaypoint.getY(), endPos.getX() - newLastWaypoint.getX()))),
                        config
                ).getTotalTimeSeconds();
                waypoints.remove(i+1);
            }

            if(bestTime > newTime){
                bestIndex = i+1;
                bestTime = newTime;
            }
        }

        waypoints.add(bestIndex, new Translation2d(newWaypoint[0], newWaypoint[1]));
    }

    private static int[] getWaypoint(Trajectory bestGuessTrajectory, boolean makeNegative) {
        Autopath.TimestampTranslation2d startCollision = Autopath.returnCollisionStart(bestGuessTrajectory);
        Autopath.TimestampTranslation2d endCollision = Autopath.returnCollisionEnd(bestGuessTrajectory, startCollision);

        Autopath.robotState.autopathCollisionStarts.add(new Pose2d(startCollision.getTranslation2d().times(.01), new Rotation2d()));
        Autopath.robotState.autopathCollisionEnds.add(new Pose2d(endCollision.getTranslation2d().times(.01), new Rotation2d()));

        Translation2d startToEndTranspose = endCollision.getTranslation2d().minus(startCollision.getTranslation2d());

        int[] newWaypoint;

        int[] startNewCollision = new int[]{(int) startCollision.getTranslation2d().getX(), (int) startCollision.getTranslation2d().getY()};
        int[] endNewCollision = new int[]{(int) endCollision.getTranslation2d().getX(), (int) endCollision.getTranslation2d().getY()};

        int[] lastCollisionPoint = null;

        int buffer = makeNegative ? -5 : 5;

        int iterationNum = 0;

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            int[] collisionPoint;

            if (makeNegative)
                collisionPoint =
                        Bresenham.drawPerpLineMinusOnePixelNegative(
                                Autopath.fieldMap.getCurrentMap(),
                                startNewCollision[0],
                                startNewCollision[1],
                                endNewCollision[0],
                                endNewCollision[1]
                        ); //TODO fix the fact that im only perping "negatively"
            else
                collisionPoint =
                    Bresenham.drawPerpLineMinusOnePixelPositive(
                            Autopath.fieldMap.getCurrentMap(),
                            startNewCollision[0],
                            startNewCollision[1],
                            endNewCollision[0],
                            endNewCollision[1]
                    ); //TODO fix the fact that im only perping "negatively"

            Autopath.robotState.autopathWaypoints.add(new Pose2d(new Translation2d(collisionPoint[0] / 100., collisionPoint[1] / 100.), new Rotation2d()));

            int[] possibleStartNewCollision =
                    Bresenham.lineReturnCollisionInverted(
                            Autopath.fieldMap.getCurrentMap(),
                            collisionPoint[0],
                            collisionPoint[1],
                            collisionPoint[0] - (int) (startToEndTranspose.getX()),
                            collisionPoint[1] - (int) (startToEndTranspose.getY()),
                            true
                    );
            int[] possibleEndNewCollision =
                    Bresenham.lineReturnCollisionInverted(
                            Autopath.fieldMap.getCurrentMap(),
                            collisionPoint[0],
                            collisionPoint[1],
                            collisionPoint[0] + (int) (100 * startToEndTranspose.getX()),
                            collisionPoint[1] + (int) (100 * startToEndTranspose.getY()),
                            true
                    );

            System.out.println("Original Start: " + startCollision.getTranslation2d().getX() + ", " + startCollision.getTranslation2d().getY());
            System.out.println("Original End: " + endCollision.getTranslation2d().getX() + ", " + endCollision.getTranslation2d().getY());
            System.out.println(Autopath.fieldMap.getCurrentMap().checkPixelHasObject(collisionPoint[0], collisionPoint[1]));
            System.out.println("Colliding: " + collisionPoint[0] + ", " + collisionPoint[1]);
            System.out.println("Start: " + possibleStartNewCollision[0] + ", " + possibleStartNewCollision[1]);
            System.out.println("End: " + possibleEndNewCollision[0] + ", " + possibleEndNewCollision[1]);

            if (Arrays.equals(startNewCollision, endNewCollision)) {
                newWaypoint = startNewCollision;
                break;
            } else if (Arrays.equals(startNewCollision, possibleStartNewCollision) && Arrays.equals(endNewCollision, possibleEndNewCollision)) {
                newWaypoint = startNewCollision;
                break;
            } else if (lastCollisionPoint != null && (collisionPoint[0] < lastCollisionPoint[0] || (makeNegative ? collisionPoint[1] > lastCollisionPoint[1] : collisionPoint[1] < lastCollisionPoint[1]))) {
                newWaypoint = startNewCollision;
                System.out.println("AAAAHHHHHHH AutopathAlgorithm NOT DOING GOOD");
                break;
            } //TODO if this ever actually triggers we need to revamp the system so that it...doesn't, this is basically just a botch solution to a really bad problem we may or may not have
            else {
                startNewCollision = possibleStartNewCollision;
                endNewCollision = possibleEndNewCollision;
            }

            lastCollisionPoint = collisionPoint;

            iterationNum++;
        }

        if (Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) < Math.PI / 2 || Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) > 3 * Math.PI / 2) {
            newWaypoint[0] -= buffer;
        } else {
            newWaypoint[0] += buffer;
        }

        if (Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) < Math.PI) {
            newWaypoint[1] += buffer;
        } else {
            newWaypoint[1] -= buffer;
        }
        return newWaypoint;
    }
}
