package com.team1816.lib.autopath;

import com.team1816.lib.subsystems.drive.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutopathAlgorithmNodeTree {
    public static Trajectory calculateAutopath(Pose2d autopathTargetPosition){
        return calculateAutopath(Autopath.robotState.fieldToVehicle, autopathTargetPosition);
    }

    public static Trajectory calculateAutopath(Pose2d autopathStartPosition, Pose2d autopathTargetPosition){
        Autopath.robotState.autopathWaypoints.clear();
        Autopath.robotState.autopathTrajectory = null;
        Autopath.robotState.autopathTrajectoryPossibilities.clear();
        Autopath.robotState.autopathCollisionStarts.clear();
        Autopath.robotState.autopathCollisionEnds.clear();

        ArrayList<WaypointTreeNode> pathTree = new ArrayList<>();

        Pose2d startPos = autopathStartPosition;

        TrajectoryConfig config = new TrajectoryConfig(Drive.kPathFollowingMaxVelMeters, Drive.kPathFollowingMaxAccelMeters);

//        try {
//            Thread.sleep(2500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        pathTree.add(new WaypointTreeNode(
                new Pose2d(startPos.getTranslation(), Rotation2d.fromRadians(Math.atan2(autopathTargetPosition.getY()-startPos.getY(), autopathTargetPosition.getX()-startPos.getX()))),
                new ArrayList<>(),
                new Pose2d(autopathTargetPosition.getTranslation(), Rotation2d.fromRadians(Math.atan2(autopathTargetPosition.getY()-startPos.getY(), autopathTargetPosition.getX()-startPos.getX()))),
                config
        ));

        boolean validPathFound = false;

        do{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Autopath.robotState.autopathTrajectoryPossibilities.clear();
            for(WaypointTreeNode branch : pathTree){
                Autopath.robotState.autopathTrajectoryPossibilities.add(branch.getTrajectory());
            }
            Autopath.robotState.autopathTrajectoryPossibilitiesChanged = true;

            int originalWaypointsSize = pathTree.get(0).getWaypoints().size();

            for(int branchIndex = 0; branchIndex < pathTree.size(); branchIndex++) {
                if((validPathFound && branchIndex == pathTree.size()-1) || originalWaypointsSize != pathTree.get(branchIndex).getWaypoints().size())
                    continue;

                WaypointTreeNode currentNode = pathTree.get(branchIndex);
                Trajectory currentPathBranch = currentNode.getTrajectory();

                ArrayList<Translation2d> waypointsPos = (ArrayList<Translation2d>) currentNode.getWaypoints().clone();
                ArrayList<Translation2d> waypointsNeg = (ArrayList<Translation2d>) currentNode.getWaypoints().clone();
                Translation2d firstWaypoint = waypointsNeg.size() > 0 ? waypointsNeg.get(0) : autopathTargetPosition.getTranslation();
                Translation2d lastWaypoint = waypointsNeg.size() > 0 ? waypointsNeg.get(waypointsNeg.size()-1) : startPos.getTranslation();
//
//                if (Autopath.robotState.autopathTrajectory.equals(currentPathBranch)) {
//                    System.out.println("Path is not right and looping");
//                }

                if (Autopath.testTrajectory(currentPathBranch)) { //TODO it's kinda inefficient to test this now, should test it right after creating new path
                    System.out.println("found with waypoints: " + waypointsNeg);
                    Autopath.robotState.autopathTrajectory = currentPathBranch;
                    Autopath.robotState.autopathTrajectoryChanged = true;
                    for(int i = branchIndex+1; i < pathTree.size();) {
                        pathTree.remove(i);
                    }
                    validPathFound = true;
                } else {
                    Autopath.TimestampTranslation2d startCollision = Autopath.returnCollisionStart(currentPathBranch);
                    Autopath.TimestampTranslation2d endCollision = Autopath.returnCollisionEnd(currentPathBranch, startCollision);

                    Autopath.robotState.autopathCollisionStarts.add(new Pose2d(startCollision.getTranslation2d().times(.01), new Rotation2d()));
                    Autopath.robotState.autopathCollisionEnds.add(new Pose2d(endCollision.getTranslation2d().times(.01), new Rotation2d()));

                    Translation2d startToEndTranspose = endCollision.getTranslation2d().minus(startCollision.getTranslation2d());

                    int[] newWaypointPositive;

                    int[] startNewCollisionPositive = new int[]{(int) startCollision.getTranslation2d().getX(), (int) startCollision.getTranslation2d().getY()};
                    int[] endNewCollisionPositive = new int[]{(int) endCollision.getTranslation2d().getX(), (int) endCollision.getTranslation2d().getY()};
                    int[] lastCollisionPointPositive = null;

                    while (true) { //TODO fix this having the possibility to break algorithm by infinitely looping
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }

                        int[] collisionPointPositive =
                                Bresenham.drawPerpLineMinusOnePixelPositive(
                                        Autopath.fieldMap.getCurrentMap(),
                                        startNewCollisionPositive[0],
                                        startNewCollisionPositive[1],
                                        endNewCollisionPositive[0],
                                        endNewCollisionPositive[1]
                                ); //TODO fix the fact that im only perping "negatively"

                        Autopath.robotState.autopathWaypointsPos.add(new Pose2d(new Translation2d(collisionPointPositive[0] / 100., collisionPointPositive[1] / 100.), new Rotation2d()));

                        int[] possibleStartNewCollisionPositive =
                                Bresenham.lineReturnCollisionInverted(
                                        Autopath.fieldMap.getCurrentMap(),
                                        collisionPointPositive[0],
                                        collisionPointPositive[1],
                                        collisionPointPositive[0] - (int) (10*startToEndTranspose.getX()),
                                        collisionPointPositive[1] - (int) (10*startToEndTranspose.getY()),
                                        true
                                );
                        int[] possibleEndNewCollisionPositive =
                                Bresenham.lineReturnCollisionInverted(
                                        Autopath.fieldMap.getCurrentMap(),
                                        collisionPointPositive[0],
                                        collisionPointPositive[1],
                                        collisionPointPositive[0] + (int) (10*startToEndTranspose.getX()),
                                        collisionPointPositive[1] + (int) (10*startToEndTranspose.getY()),
                                        true
                                );

                        if (Arrays.equals(startNewCollisionPositive, endNewCollisionPositive)) {
                            newWaypointPositive = startNewCollisionPositive;
                            break;
                        } else if (Arrays.equals(startNewCollisionPositive, possibleStartNewCollisionPositive) && Arrays.equals(endNewCollisionPositive, possibleEndNewCollisionPositive)) {
                            newWaypointPositive = startNewCollisionPositive;
                            break;
                        }
//                        else if (lastCollisionPointPositive != null && (collisionPointPositive[0] < lastCollisionPointPositive[0] || collisionPointPositive[1] < lastCollisionPointPositive[1])) {
//                            newWaypointPositive = startNewCollisionPositive;
//                            System.out.println("AAAAHHHHHHH AutopathAlgorithm NOT DOING GOOD P");
//                            break;
//                        } //TODO if this ever actually triggers we need to revamp the system so that it...doesn't, this is basically just a botch solution to a really bad problem we may or may not have
                        else {
                            startNewCollisionPositive = possibleStartNewCollisionPositive;
                            endNewCollisionPositive = possibleEndNewCollisionPositive;
                        }

                        lastCollisionPointPositive = collisionPointPositive;
                    }

                    int[] newWaypointNegative;

                    int[] startNewCollisionNegative = new int[]{(int) startCollision.getTranslation2d().getX(), (int) startCollision.getTranslation2d().getY()};
                    int[] endNewCollisionNegative = new int[]{(int) endCollision.getTranslation2d().getX(), (int) endCollision.getTranslation2d().getY()};
                    int[] lastCollisionPointNegative = null;

                    while (true) { //TODO fix this having the possibility to break algorithm by infinitely looping
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }

                        int[] collisionPointNegative =
                                Bresenham.drawPerpLineMinusOnePixelNegative(
                                        Autopath.fieldMap.getCurrentMap(),
                                        startNewCollisionNegative[0],
                                        startNewCollisionNegative[1],
                                        endNewCollisionNegative[0],
                                        endNewCollisionNegative[1]
                                ); //TODO fix the fact that im only perping "negatively"

                        Autopath.robotState.autopathWaypointsNeg.add(new Pose2d(new Translation2d(collisionPointNegative[0] / 100., collisionPointNegative[1] / 100.), new Rotation2d()));

                        int[] possibleStartNewCollisionNegative =
                                Bresenham.lineReturnCollisionInverted(
                                        Autopath.fieldMap.getCurrentMap(),
                                        collisionPointNegative[0],
                                        collisionPointNegative[1],
                                        collisionPointNegative[0] - (int) (10*startToEndTranspose.getX()),
                                        collisionPointNegative[1] - (int) (10*startToEndTranspose.getY()),
                                        true
                                );
                        int[] possibleEndNewCollisionNegative =
                                Bresenham.lineReturnCollisionInverted(
                                        Autopath.fieldMap.getCurrentMap(),
                                        collisionPointNegative[0],
                                        collisionPointNegative[1],
                                        collisionPointNegative[0] + (int) (10*startToEndTranspose.getX()),
                                        collisionPointNegative[1] + (int) (10*startToEndTranspose.getY()),
                                        true
                                );

                        if (Arrays.equals(startNewCollisionNegative, endNewCollisionNegative)) {
                            newWaypointNegative = startNewCollisionNegative;
                            break;
                        } else if (Arrays.equals(startNewCollisionNegative, possibleStartNewCollisionNegative) && Arrays.equals(endNewCollisionNegative, possibleEndNewCollisionNegative)) {
                            newWaypointNegative = startNewCollisionNegative;
                            break;
                        }
//                        else if (lastCollisionPointNegative != null && (collisionPointNegative[0] < lastCollisionPointNegative[0] || collisionPointNegative[1] > lastCollisionPointNegative[1])) {
//                            newWaypointNegative = startNewCollisionNegative;
//                            System.out.println("AAAAHHHHHHH AutopathAlgorithm NOT DOING GOOD N");
//                            break;
//                        } //TODO if this ever actually triggers we need to revamp the system so that it...doesn't, this is basically just a botch solution to a really bad problem we may or may not have
                        else {
                            startNewCollisionNegative = possibleStartNewCollisionNegative;
                            endNewCollisionNegative = possibleEndNewCollisionNegative;
                        }

                        lastCollisionPointNegative = collisionPointNegative;
                    }

                    if (Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) < Math.PI / 2 || Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) > 3 * Math.PI / 2) {
                        newWaypointPositive[0] -= 5;
                        newWaypointNegative[0] += 5;
                    } else {
                        newWaypointPositive[0] += 5;
                        newWaypointNegative[0] -= 5;
                    }

                    if (Math.atan2(startToEndTranspose.getY(), startToEndTranspose.getX()) < Math.PI) {
                        newWaypointPositive[1] += 5;
                        newWaypointNegative[1] -= 5;
                    } else {
                        newWaypointPositive[1] -= 5;
                        newWaypointNegative[1] += 5;
                    }

                    int waypointsBeforeSize = waypointsPos.size();

                    double distPos = Bresenham.dist(newWaypointPositive[0], newWaypointPositive[1], startPos.getX() * 100, startPos.getY() * 100);
                    //TODO might have to make this a binary rather than just iterative insertion sort
                    for (int i = 0; i < waypointsPos.size(); i++) {
                        if (distPos == waypointsPos.get(i).getDistance(startPos.getTranslation()) * 100) {
                            System.out.println("algorithm's being bad");
                        } else if (distPos < waypointsPos.get(i).getDistance(startPos.getTranslation()) * 100) {
                            waypointsPos.add(i, new Translation2d(newWaypointPositive[0] / 100., newWaypointPositive[1] / 100.));
                            break;
                        }
                    }
                    if (waypointsPos.size() == waypointsBeforeSize)
                        waypointsPos.add(new Translation2d(newWaypointPositive[0] / 100., newWaypointPositive[1] / 100.));

                    double distNeg = Bresenham.dist(newWaypointNegative[0], newWaypointNegative[1], startPos.getX() * 100, startPos.getY() * 100);
                    //TODO might have to make this a binary rather than just iterative insertion sort
                    for (int i = 0; i < waypointsNeg.size(); i++) {
                        if (distNeg == waypointsNeg.get(i).getDistance(startPos.getTranslation()) * 100) {
                            System.out.println("algorithm's being bad");
                        } else if (distNeg < waypointsNeg.get(i).getDistance(startPos.getTranslation()) * 100) {
                            waypointsNeg.add(i, new Translation2d(newWaypointNegative[0] / 100., newWaypointNegative[1] / 100.));
                            break;
                        }
                    }
                    if (waypointsNeg.size() == waypointsBeforeSize)
                        waypointsNeg.add(new Translation2d(newWaypointPositive[0] / 100., newWaypointPositive[1] / 100.));

                    Autopath.robotState.autopathWaypoints.add(new Pose2d(new Translation2d(newWaypointPositive[0] / 100., newWaypointPositive[1] / 100.), new Rotation2d()));
                    Autopath.robotState.autopathWaypoints.add(new Pose2d(new Translation2d(newWaypointNegative[0] / 100., newWaypointNegative[1] / 100.), new Rotation2d()));
                }

                boolean addedPos = false;
                boolean addedNeg = false;

                WaypointTreeNode nodePos = new WaypointTreeNode(
                        startPos,
                        waypointsPos,
                        autopathTargetPosition,
                        config
                );
                WaypointTreeNode nodeNeg = new WaypointTreeNode(
                        startPos,
                        waypointsNeg,
                        autopathTargetPosition,
                        config
                );

                for(int i = 0; i < pathTree.size(); i++){
                    if(!addedPos && pathTree.get(i).getTrajectoryTime() < nodePos.getTrajectoryTime()) {
                        pathTree.add(i, nodePos);
                        addedPos = true;
                    } else if(!addedPos && i == pathTree.size()-1){
                        pathTree.add(nodePos);
                        addedPos = true;
                    }
                    if(!addedNeg && pathTree.get(i).getTrajectoryTime() < nodeNeg.getTrajectoryTime()) {
                        pathTree.add(i, nodeNeg);
                        addedNeg = true;
                    } else if(!addedNeg && i == pathTree.size()-1){
                        pathTree.add(nodeNeg);
                        addedNeg = true;
                    }
                }

                if(!addedPos || !addedNeg)
                    System.out.println("SOMETHING VERY WRONG IDK AUTOPATH ALGORITHM NODE TREE");
            }
        } while(/*!pathTree.isEmpty()*/ !(validPathFound && pathTree.size() == 1));

        System.out.println("Autopath Idek");

        return null;
    }
}

class WaypointTreeNode{
    private final ArrayList<Translation2d> waypoints;
    private final Trajectory trajectory;
    private final double trajectoryTime;

    WaypointTreeNode(Pose2d startPos, ArrayList<Translation2d> waypoints, Pose2d endPos, TrajectoryConfig config){
        this.waypoints = waypoints;
        trajectory = TrajectoryGenerator.generateTrajectory(
                startPos,
                waypoints,
                endPos,
                config
        );
        trajectoryTime = trajectory.getTotalTimeSeconds();
    }

    public boolean equals(WaypointTreeNode otherNode){
        if(waypoints.equals(otherNode.getWaypoints())){
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