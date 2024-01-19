package com.team1816.season.autoaim;

import java.util.Optional;

/**
 * Organized from top down in the order that you should execute the methods(though they could technically be simultaneous)
 */
public class UseableMethods {
    public boolean robotInRange(double xyPlaneEuclideanDistanceToTarget){
        return ArmAngleFinder.robotIsRoughlyInRange(xyPlaneEuclideanDistanceToTarget);
    }

    public double getRobotRotation(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double targetX, double targetY){
        return RobotAngleFinder.getTransformedAngleOfRobot(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY);
    }

    public Optional<Double> getShooterAngle(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double targetX, double targetY){
        return RobotAngleFinder.getBallisticAngleOfArm(ArmAngleFinder.distance(targetX- RobotAngleFinder.getTransformedAxleX(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY), targetY- RobotAngleFinder.getTransformedAxleY(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY)));
    }
}
