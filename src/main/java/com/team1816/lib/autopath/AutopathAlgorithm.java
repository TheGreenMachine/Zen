package com.team1816.lib.autopath;

import com.team1816.lib.subsystems.drive.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.spline.SplineParameterizer;
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

        if(Autopath.fieldMap.getCurrentMap().checkPixelHasObjectOrOffMap((int)(autopathTargetPosition.getX()*100), (int)(autopathTargetPosition.getY()*100)))
            return null;

        TrajectoryConfig config = new TrajectoryConfig(Drive.kPathFollowingMaxVelMeters, Drive.kPathFollowingMaxAccelMeters);

        ArrayList<WaypointTreeNode> branches = new ArrayList<>();
        branches.add(
                new WaypointTreeNode(
                        new Pose2d(autopathStartPosition.getTranslation(), Rotation2d.fromRadians(Math.atan2(autopathTargetPosition.getY() - autopathStartPosition.getY(), autopathTargetPosition.getX() - autopathStartPosition.getX()))),
                        new ArrayList<>(),
                        new Pose2d(autopathTargetPosition.getTranslation(), Rotation2d.fromRadians(Math.atan2(autopathTargetPosition.getY() - autopathStartPosition.getY(), autopathTargetPosition.getX() - autopathStartPosition.getX()))),
                        config
                )
        );

        while(!branches.get(0).trajectoryCheck){
            for(int i = 1; i < branches.size(); i++)
                if(branches.get(i-1).trajectoryCheck) {
                    branches.remove(i);
                    i--;
                }

            int currentBranchIndex = 0;

            Autopath.robotState.autopathTrajectoryPossibilities.clear();
            if(!branches.get(currentBranchIndex).trajectoryCheck) {
                Autopath.robotState.autopathTrajectoryPossibilities.add(branches.get(currentBranchIndex).getTrajectory());
            }
            Autopath.robotState.autopathTrajectoryPossibilitiesChanged = true;

            ArrayList<Translation2d> waypoints = branches.get(currentBranchIndex).getWaypoints();
            Trajectory bestGuessTrajectory = branches.get(currentBranchIndex).getTrajectory();

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            branches.remove(currentBranchIndex);

            int[] tempNewWaypointNegative = getWaypoint(bestGuessTrajectory, true);
            if(tempNewWaypointNegative != null) {
                double[] newWaypointNegative = new double[]{tempNewWaypointNegative[0] / 100., tempNewWaypointNegative[1] / 100.};
                ArrayList<Translation2d> newWaypointsNegative = (ArrayList<Translation2d>) waypoints.clone();
                addNewWaypoint(newWaypointNegative, newWaypointsNegative, autopathStartPosition, autopathTargetPosition, config);

                WaypointTreeNode newNodeNeg =
                        new WaypointTreeNode(
                                new Pose2d(
                                        autopathStartPosition.getTranslation(),
                                        Rotation2d.fromRadians(
                                                Math.atan2(
                                                        newWaypointsNegative.get(0).getY()
                                                        - autopathStartPosition.getY(),
                                                        newWaypointsNegative.get(0).getX()
                                                        - autopathStartPosition.getX()
                                                )
                                        )
                                ),
                                newWaypointsNegative,
                                new Pose2d(
                                        autopathTargetPosition.getTranslation(),
                                        Rotation2d.fromRadians(
                                                Math.atan2(
                                                        autopathTargetPosition.getY()
                                                                - newWaypointsNegative.get(newWaypointsNegative.size() - 1).getY(),
                                                        autopathTargetPosition.getX()
                                                                - newWaypointsNegative.get(newWaypointsNegative.size() - 1).getX()
                                                )
                                        )
                                ),
                                config
                        );

                for (int i = 0; i <= branches.size(); i++)
                    if (i == branches.size()) {
                        branches.add(newNodeNeg);
                        break;
                    } else if (newNodeNeg.getTrajectoryTime() < branches.get(i).getTrajectoryTime()) {
                        branches.add(i, newNodeNeg);
                        break;
                    }

                Autopath.robotState.autopathWaypoints.add(new Pose2d(new Translation2d(newWaypointNegative[0], newWaypointNegative[1]), new Rotation2d()));
            }

            int[] tempNewWaypointPositive = getWaypoint(bestGuessTrajectory, false);
            if(tempNewWaypointPositive != null) {
                double[] newWaypointPositive = new double[]{tempNewWaypointPositive[0] / 100., tempNewWaypointPositive[1] / 100.};
                ArrayList<Translation2d> newWaypointsPositive = (ArrayList<Translation2d>) waypoints.clone();
                addNewWaypoint(newWaypointPositive, newWaypointsPositive, autopathStartPosition, autopathTargetPosition, config);

                WaypointTreeNode newNodePos =
                        new WaypointTreeNode(
                                new Pose2d(
                                        autopathStartPosition.getTranslation(),
                                        Rotation2d.fromRadians(
                                                Math.atan2(
                                                        newWaypointsPositive.get(0).getY()
                                                                - autopathStartPosition.getY(),
                                                        newWaypointsPositive.get(0).getX()
                                                                - autopathStartPosition.getX()
                                                )
                                        )
                                ),
                                newWaypointsPositive,
                                new Pose2d(
                                        autopathTargetPosition.getTranslation(),
                                        Rotation2d.fromRadians(
                                                Math.atan2(
                                                        autopathTargetPosition.getY()
                                                                - newWaypointsPositive.get(newWaypointsPositive.size() - 1).getY(),
                                                        autopathTargetPosition.getX()
                                                                - newWaypointsPositive.get(newWaypointsPositive.size() - 1).getX()
                                                )
                                        )
                                ),
                                config
                        );

                for (int i = 0; i <= branches.size(); i++)
                    if (i == branches.size()) {
                        branches.add(newNodePos);
                        break;
                    } else if (newNodePos.getTrajectoryTime() < branches.get(i).getTrajectoryTime()) {
                        branches.add(i, newNodePos);
                        break;
                    }
                Autopath.robotState.autopathWaypoints.add(new Pose2d(new Translation2d(newWaypointPositive[0], newWaypointPositive[1]), new Rotation2d()));
            }
        }

        return branches.get(0).getTrajectory();
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

            if (i == waypoints.size() - 1) {
                newLastWaypoint = new Translation2d(newWaypoint[0], newWaypoint[1]);
                waypoints.add(new Translation2d(newWaypoint[0], newWaypoint[1]));
                newTime = TrajectoryGenerator.generateTrajectory(
                        new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(newFirstWaypoint.getY() - startPos.getY(), newFirstWaypoint.getX() - startPos.getX()))),
                        waypoints,
                        new Pose2d(endPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(endPos.getY() - newLastWaypoint.getY(), endPos.getX() - newLastWaypoint.getX()))),
                        config
                ).getTotalTimeSeconds();
                waypoints.remove(waypoints.size() - 1);
            } else {
                waypoints.add(i + 1, new Translation2d(newWaypoint[0], newWaypoint[1]));
                newTime = TrajectoryGenerator.generateTrajectory(
                        new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(newFirstWaypoint.getY() - startPos.getY(), newFirstWaypoint.getX() - startPos.getX()))),
                        waypoints,
                        new Pose2d(endPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(endPos.getY() - newLastWaypoint.getY(), endPos.getX() - newLastWaypoint.getX()))),
                        config
                ).getTotalTimeSeconds();
                waypoints.remove(i + 1);
            }

                if (bestTime > newTime) {
                    bestIndex = i + 1;
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

        ArrayList<Integer> pastCollisionPointHashes = new ArrayList<>();

        int buffer = 5;

        while (true) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

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
                            collisionPoint[0] - (int) (startToEndTranspose.getX()*(2000/startToEndTranspose.getNorm())),
                            collisionPoint[1] - (int) (startToEndTranspose.getY()*(2000/startToEndTranspose.getNorm())),
                            true
                    );
            int[] possibleEndNewCollision =
                    Bresenham.lineReturnCollisionInverted(
                            Autopath.fieldMap.getCurrentMap(),
                            collisionPoint[0],
                            collisionPoint[1],
                            collisionPoint[0] + (int) (startToEndTranspose.getX()*(2000/startToEndTranspose.getNorm())),
                            collisionPoint[1] + (int) (startToEndTranspose.getY()*(2000/startToEndTranspose.getNorm())),
                            true
                    );

            if (Arrays.equals(startNewCollision, endNewCollision)) {
                newWaypoint = startNewCollision;
                break;
            } else if (Arrays.equals(startNewCollision, possibleStartNewCollision) && Arrays.equals(endNewCollision, possibleEndNewCollision)) {
                newWaypoint = startNewCollision;
                break;
            } else if (checkCollisions(pastCollisionPointHashes, collisionPoint)) {
                newWaypoint = startNewCollision;
//                System.out.println("AAAAHHHHHHH AutopathAlgorithm NOT DOING GOOD");
                break;
            } //TODO if this ever actually triggers we need to revamp the system so that it...doesn't, this is basically just a botch solution to a really bad problem we may or may not have
            //TODO UPDATE: well now its used a lot and works soooo...ig it's a feature???
            else {
                startNewCollision = possibleStartNewCollision;
                endNewCollision = possibleEndNewCollision;
            }

            pastCollisionPointHashes.add(Arrays.hashCode(collisionPoint));
        }

        if(makeNegative){
            newWaypoint[0] -= (int)(buffer*Math.cos(startToEndTranspose.getAngle().getRadians()+(Math.PI/2)));
            newWaypoint[1] -= (int)(buffer*Math.sin(startToEndTranspose.getAngle().getRadians()+(Math.PI/2)));
        } else{
            newWaypoint[0] -= (int)(buffer*Math.cos(startToEndTranspose.getAngle().getRadians()-(Math.PI/2)));
            newWaypoint[1] -= (int)(buffer*Math.sin(startToEndTranspose.getAngle().getRadians()-(Math.PI/2)));
        }

        return newWaypoint;
    }

    private static boolean checkCollisions(List<Integer> pastCollisionPointHashes, int[] collisionPoint){
        int collisionPointHash = Arrays.hashCode(collisionPoint);
        for(int currentCollisionPointHash : pastCollisionPointHashes)
            if(collisionPointHash == currentCollisionPointHash)
                return true;
        return false;
    }

    static class WaypointTreeNode {
        private final ArrayList<Translation2d> waypoints;
        private final Trajectory trajectory;
        private final double trajectoryTime;
        private final boolean trajectoryCheck;

        WaypointTreeNode(Pose2d startPos, ArrayList<Translation2d> waypoints, Pose2d endPos, TrajectoryConfig config) {
            this.waypoints = waypoints;
            trajectory = TrajectoryGenerator.generateTrajectory(
                    startPos,
                    waypoints,
                    endPos,
                    config
            );
            trajectoryTime = trajectory.getTotalTimeSeconds();
            trajectoryCheck = Autopath.testTrajectory(trajectory);
        }

        public boolean equals(WaypointTreeNode otherNode) {
            if (waypoints.equals(otherNode.getWaypoints())) {
                return true;
            }
            return false;
        }

        public ArrayList<Translation2d> getWaypoints() {
            return waypoints;
        }

        public Trajectory getTrajectory() {
            return trajectory;
        }

        public double getTrajectoryTime() {
            return trajectoryTime;
        }
    }
}
