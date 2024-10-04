package com.team1816.lib.auto.paths;

import com.team1816.lib.Injector;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.PathFinder;
import com.team1816.core.configuration.Constants;
import com.team1816.core.states.RobotState;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;

import java.util.ArrayList;
import java.util.List;

/**
 * Path that delivers the robot to a specified target with active obstacle avoidance
 */
public class TargetTrajectoryPath extends AutoPath {

    public static RobotState robotState;
    private static Pose2d target;
    private PathFinder pathFinder;

    public TargetTrajectoryPath(Pose2d pose) {
        robotState = Injector.get(RobotState.class);
        target = pose;

        pathFinder = robotState.pathFinder;
        pathFinder.setRobot(robotState.fieldToVehicle);
        pathFinder.setTarget(target);
    }

    public TargetTrajectoryPath() {
        robotState = Injector.get(RobotState.class);
        new TargetTrajectoryPath(robotState.target);

//        pathFinder = robotState.pathFinder;
//        pathFinder.setRobot(robotState.fieldToVehicle);
//        pathFinder.setTarget(target);
    }

    @Override
    protected List<Pose2d> getWaypoints() { // A* accelerated path routing
        List<Pose2d> waypoints = new ArrayList<>();

        try {
            waypoints = pathFinder.getWaypoints();
            if (waypoints.size() > 1) {
                return waypoints;
            } else {
                waypoints.clear();
            }
        } catch (Exception ignored) {
        }

        Rotation2d angle = new Translation2d(target.getX() - robotState.fieldToVehicle.getX(), target.getY() - robotState.fieldToVehicle.getY()).getAngle();
        waypoints.add(new Pose2d(robotState.fieldToVehicle.getTranslation(), target.getRotation()));
        waypoints.add(target);

        return waypoints;
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        List<Rotation2d> headings = new ArrayList<>();
        headings.add(robotState.fieldToVehicle.getRotation());
        if (robotState.allianceColor == Color.BLUE && robotState.fieldToVehicle.getRotation().getDegrees() < 0) {
            for (int i = 1; i < getWaypoints().size(); i++) {
                headings.add(target.getRotation().times(-1)); // optimizes blue side wraparound
            }
        } else {
            for (int i = 1; i < getWaypoints().size(); i++) {
                headings.add(target.getRotation());
            }
        }
        return headings;
    }

    @Override
    public Trajectory getAsTrajectory() {
        var translatedVelocity = new Translation2d(
            robotState.deltaVehicle.vxMetersPerSecond,
            robotState.deltaVehicle.vyMetersPerSecond).rotateBy(robotState.fieldToVehicle.getRotation().unaryMinus()
        );
        var translatedChassisSpeeds = new ChassisSpeeds(
            translatedVelocity.getX(),
            translatedVelocity.getY(),
            robotState.deltaVehicle.omegaRadiansPerSecond
        );
        return PathUtil.generateTrajectory(usingApp(), translatedChassisSpeeds, getWaypoints());
    }

    @Override
    public List<Rotation2d> getAsTrajectoryHeadings() {
        var translatedVelocity = new Translation2d(
            robotState.deltaVehicle.vxMetersPerSecond,
            robotState.deltaVehicle.vyMetersPerSecond).rotateBy(robotState.fieldToVehicle.getRotation().unaryMinus()
        );
        var translatedChassisSpeeds = new ChassisSpeeds(
            translatedVelocity.getX(),
            translatedVelocity.getY(),
            robotState.deltaVehicle.omegaRadiansPerSecond
        );
        return PathUtil.generateHeadings(
            usingApp(),
            getWaypoints(),
            getWaypointHeadings(),
            translatedChassisSpeeds
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
