package com.team1816.season.autoaim;

import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Translation2d;

import java.util.Optional;

/**
 * Organized from top down in the order that you should execute the methods(though they could technically be simultaneous)
 */
public class AutoAimUtil {
    private static double axleOffsetX = Constants.axlePositionOffsetX;
    private static double axleOffsetY = Constants.axlePositionOffsetY;
    private static double radiansDisplacement = Constants.axleRadiansDisplacement;

    public static boolean robotInRange(double xyPlaneEuclideanDistanceToTarget){
        return ArmAngleFinder.robotIsRoughlyInRange(xyPlaneEuclideanDistanceToTarget);
    }

    public static double getRobotRotation(double targetX, double targetY){
        return RobotAngleFinder.getTransformedAngleOfRobot(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY);
    }

    public static double getRobotRotation(Translation2d target){
        return getRobotRotation(target.getX(), target.getY());
    }

    public static Optional<Double> getShooterAngle(double targetEuclidDist){
        return RobotAngleFinder.getBallisticAngleOfArm(targetEuclidDist-axleOffsetY);
    }
//    public static Optional<Double> getShooterAngle(double targetX, double targetY){
//        return RobotAngleFinder.getBallisticAngleOfArm(ArmAngleFinder.distance(targetX- RobotAngleFinder.getTransformedAxleX(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY), targetY- RobotAngleFinder.getTransformedAxleY(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY)));
//    }
//
//    public static Optional<Double> getShooterAngle(Translation2d target){
//        return getShooterAngle(target.getX(), target.getY());
//    }
}
