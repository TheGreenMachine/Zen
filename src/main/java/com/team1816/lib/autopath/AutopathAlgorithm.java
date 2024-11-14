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
                Autopath.TimestampTranslation2d startCollision = Autopath.returnCollisionStart(bestGuessTrajectory);
                Autopath.TimestampTranslation2d endCollision = Autopath.returnCollisionEnd(bestGuessTrajectory, startCollision);

                Autopath.robotState.autopathCollisionStarts.add(new Pose2d(startCollision.getTranslation2d().times(.01), new Rotation2d()));
                Autopath.robotState.autopathCollisionEnds.add(new Pose2d(endCollision.getTranslation2d().times(.01), new Rotation2d()));

                Translation2d startToEndTranspose = endCollision.getTranslation2d().minus(startCollision.getTranslation2d());

                int[] newWaypoint = new int[]{(int)startCollision.getTranslation2d().getX(), (int)startCollision.getTranslation2d().getY()};

                int[] startNewCollision = new int[]{(int)startCollision.getTranslation2d().getX(), (int)startCollision.getTranslation2d().getY()};
                int[] endNewCollision = new int[]{(int)endCollision.getTranslation2d().getX(), (int)endCollision.getTranslation2d().getY()};

                int[] lastCollisionPoint = null;

                int iterationNum = 0;

                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    int[] collisionPointPositive =
                            Bresenham.drawPerpLineMinusOnePixelPositive(
                                    Autopath.fieldMap.getCurrentMap(),
                                    startNewCollision[0],
                                    startNewCollision[1],
                                    endNewCollision[0],
                                    endNewCollision[1]
                            );

                    int[] collisionPointNegative =
                            Bresenham.drawPerpLineMinusOnePixelNegative(
                                    Autopath.fieldMap.getCurrentMap(),
                                    startNewCollision[0],
                                    startNewCollision[1],
                                    endNewCollision[0],
                                    endNewCollision[1]
                            ); //TODO fix the fact that im only perping "negatively"

                    Autopath.robotState.autopathWaypointsPos.add(new Pose2d(new Translation2d(collisionPointPositive[0]/100., collisionPointPositive[1]/100.), new Rotation2d()));
                    Autopath.robotState.autopathWaypointsNeg.add(new Pose2d(new Translation2d(collisionPointNegative[0]/100., collisionPointNegative[1]/100.), new Rotation2d()));

                    boolean isCollisionPointOnMap = Autopath.fieldMap.getCurrentMap().checkPixelHasObjectOrOffMap(collisionPointNegative[0], collisionPointNegative[1]);
                    boolean isStartPointOnMap = Autopath.fieldMap.getCurrentMap().checkPixelOnMap(autopathStartPosition.getX(), autopathStartPosition.getY());

                    int[] possibleStartNewCollision =
                            Bresenham.lineReturnCollisionInverted(
                                    Autopath.fieldMap.getCurrentMap(),
                                    collisionPointNegative[0],
                                    collisionPointNegative[1],
                                    collisionPointNegative[0]-(int)(startToEndTranspose.getX()),
                                    collisionPointNegative[1]-(int)(startToEndTranspose.getY()),
                                    true
                            );
                    int[] possibleEndNewCollision =
                            Bresenham.lineReturnCollisionInverted(
                                    Autopath.fieldMap.getCurrentMap(),
                                    collisionPointNegative[0],
                                    collisionPointNegative[1],
                                    collisionPointNegative[0]+(int)(100*startToEndTranspose.getX()),
                                    collisionPointNegative[1]+(int)(100*startToEndTranspose.getY()),
                                    true
                            );

//                    System.out.println("Original Start: "+startCollision.getTranslation2d().getX()+", "+startCollision.getTranslation2d().getY());
//                    System.out.println("Original End: "+endCollision.getTranslation2d().getX()+", "+endCollision.getTranslation2d().getY());
//                    System.out.println(Autopath.fieldMap.getCurrentMap().checkPixelHasObject(collisionPointNegative[0], collisionPointNegative[1]));
//                    System.out.println("Colliding: "+collisionPointNegative[0]+", "+collisionPointNegative[1]);
//                    System.out.println("Start: "+possibleStartNewCollision[0]+", "+possibleStartNewCollision[1]);
//                    System.out.println("End: "+possibleEndNewCollision[0]+", "+possibleEndNewCollision[1]);

                    if(Arrays.equals(startNewCollision, endNewCollision)) {
                        newWaypoint = startNewCollision;
                        break;
                    }
                    else if(Arrays.equals(startNewCollision, possibleStartNewCollision) && Arrays.equals(endNewCollision, possibleEndNewCollision)) {
                        newWaypoint = startNewCollision;
                        break;
                    }
                    else if(lastCollisionPoint != null && (collisionPointNegative[0] < lastCollisionPoint[0] || collisionPointNegative[1] > lastCollisionPoint[1])){
                        newWaypoint = startNewCollision;
                        System.out.println("AAAAHHHHHHH AutopathAlgorithm NOT DOING GOOD");
                        break;
                    } //TODO if this ever actually triggers we need to revamp the system so that it...doesn't, this is basically just a botch solution to a really bad problem we may or may not have
                    else{
                        startNewCollision = possibleStartNewCollision;
                        endNewCollision = possibleEndNewCollision;
                    }

                    lastCollisionPoint = collisionPointNegative;

                    iterationNum++;
                }

                if(Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) < Math.PI/2 || Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) > 3*Math.PI/2){
                    newWaypoint[0] += 5;
                } else{
                    newWaypoint[0] -= 5;
                }

                if(Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) < Math.PI){
                    newWaypoint[1] -= 5;
                } else {
                    newWaypoint[1] += 5;
                }

                double dist = Bresenham.dist(newWaypoint[0], newWaypoint[1], startPos.getX()*100, startPos.getY()*100);
                int waypointsBeforeSize = waypoints.size();
                //TODO might have to make this a binary rather than just iterative insertion sort
                for(int i = 0; i < waypoints.size(); i++){
                    if(dist == waypoints.get(i).getDistance(startPos.getTranslation())*100){
                        System.out.println("algorithm's being bad");
                    }
                    else if(dist < waypoints.get(i).getDistance(startPos.getTranslation())*100){
                        waypoints.add(i, new Translation2d(newWaypoint[0]/100., newWaypoint[1]/100.));
                        break;
                    }
                }
                if(waypoints.size() == waypointsBeforeSize)
                    waypoints.add(new Translation2d(newWaypoint[0]/100., newWaypoint[1]/100.));

                Autopath.robotState.autopathWaypoints.add(new Pose2d(new Translation2d(newWaypoint[0]/100., newWaypoint[1]/100.), new Rotation2d()));

                System.out.println("Colliding at " + startCollision.getTranslation2d() + " to " + endCollision.getTranslation2d());
                System.out.println("Solved with waypoint: " + newWaypoint[0] + ", " + newWaypoint[1]);
            }
        }

        System.out.println("Autopath Idek");
        System.out.println("tried with waypoints " + waypoints);

        return null;
    }
}
