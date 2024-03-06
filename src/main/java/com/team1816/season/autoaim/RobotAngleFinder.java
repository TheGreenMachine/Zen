package com.team1816.season.autoaim;

//Based on TransformedRobotRotationForShot
//https://www.desmos.com/calculator/jerkgh3adb
public class RobotAngleFinder extends ArmAngleFinder{
    public static double getTransformedAxleX(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double targetX, double targetY){
        return distance(axleOffsetX, axleOffsetY) * Math.cos(getTransformedAngleOfAxle(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY));
    }

    public static double getTransformedAxleY(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double targetX, double targetY){
        return distance(axleOffsetX, axleOffsetY) * Math.sin(getTransformedAngleOfAxle(axleOffsetX, axleOffsetY, radiansDisplacement, targetX, targetY));
    }

    public static double getTransformedAngleOfAxle(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double targetX, double targetY){
        return (getUntransformedAngleOfAxle(axleOffsetX, axleOffsetY, radiansDisplacement, distance(targetX, targetY)) + getRadiansOf(targetX, targetY)) % (2*Math.PI);
    }

    public static double getUntransformedAngleOfAxle(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double centerOfRobotXYPlaneDistanceToTarget){
        double untransformedAngleOfAxle = 0;

        untransformedAngleOfAxle = radiansDisplacement-(Math.PI/2)+(2*Math.PI)-getRadiansOf(axleOffsetX, axleOffsetY);
        untransformedAngleOfAxle = Math.sin(untransformedAngleOfAxle);
        untransformedAngleOfAxle /= centerOfRobotXYPlaneDistanceToTarget;
        untransformedAngleOfAxle *= distance(axleOffsetX, axleOffsetY);
        untransformedAngleOfAxle = Math.asin(untransformedAngleOfAxle);
        untransformedAngleOfAxle = 2*Math.PI-getRadiansOf(axleOffsetX, axleOffsetY)-untransformedAngleOfAxle;
        untransformedAngleOfAxle -= Math.PI/2+radiansDisplacement;
        untransformedAngleOfAxle %= 2*Math.PI;

        return untransformedAngleOfAxle;
    }

    /**
     * Assumes the center of the robot is at the origin
     * @param axleOffsetX sideways offset, right side of robot is positive x
     * @param axleOffsetY front-back offset, forward is positive y
     * @param radiansDisplacement direction displacement from normal robot direction, moves counterclockwise
     * @param targetX assuming the robot is at the origin, literally the x position of the target
     * @param targetY same as targetX except the y position
     * @return angle theta in radians that the robot's direction should be
     */
    public static double getTransformedAngleOfRobot(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double targetX, double targetY){
        return (getUntransformedAngleOfRobot(axleOffsetX, axleOffsetY, radiansDisplacement, distance(targetX, targetY)) + getRadiansOf(targetX, targetY)) % (2*Math.PI);
    }

    /**
     * Assumes the center of the robot is at the origin
     * @param axleOffsetX sideways offset, right side of robot is positive x
     * @param axleOffsetY front-back offset, forward is positive y
     * @param radiansDisplacement direction displacement from normal robot direction, moves counterclockwise
     * @param centerOfRobotXYPlaneDistanceToTarget how far away the center of rotation is from the target ignoring height different(because that's part of ballistic trajectories)
     * @return angle theta in radians that the robot's direction should be
     */
    protected static double getUntransformedAngleOfRobot(double axleOffsetX, double axleOffsetY, double radiansDisplacement, double centerOfRobotXYPlaneDistanceToTarget){
        radiansDisplacement %= 2*Math.PI;

        double untransformedAngleOfRobot = 0;

        untransformedAngleOfRobot = radiansDisplacement-(Math.PI/2)+(2*Math.PI)-getRadiansOf(axleOffsetX, axleOffsetY);
        untransformedAngleOfRobot = Math.sin(untransformedAngleOfRobot);
        untransformedAngleOfRobot /= centerOfRobotXYPlaneDistanceToTarget;
        untransformedAngleOfRobot *= distance(axleOffsetX, axleOffsetY);
        untransformedAngleOfRobot = -Math.asin(untransformedAngleOfRobot);
        untransformedAngleOfRobot -= radiansDisplacement;
        untransformedAngleOfRobot %= 2*Math.PI;

        return untransformedAngleOfRobot;
    }

    /**
     * Just a method to get the theta of a cartesian coordinate
     * @param coordX
     * @param coordY
     * @return
     */
    public static double getRadiansOf(double coordX, double coordY){
        double radians = Math.acos(coordX/distance(coordX, coordY));
        if(coordY < 0)
            radians = 2*Math.PI-radians;
        radians %= 2*Math.PI;
        return radians;
    }
}
