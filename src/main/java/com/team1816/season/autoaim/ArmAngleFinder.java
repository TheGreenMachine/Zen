package com.team1816.season.autoaim;

import com.team1816.lib.Injector;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.util.datalog.DoubleLogEntry;

import java.util.Optional;

//Based on EvenMoreGravCalcs
//https://www.desmos.com/3d/fc156ffe8f
public class ArmAngleFinder {

    static RobotState robotState = Injector.get(RobotState.class);
    /**
     * Constants
     */
    //Positive
    private static final double lengthOfArm = Constants.lengthOfArm;
    //0-180
    private static double angleBetweenArmAndShooterDegrees = Constants.angleBetweenArmAndShooterDegrees + robotState.angleAdjustment;
    public static void setAngleBetweenArmAndShooterDegrees(double increment){
        robotState.angleAdjustment += increment;
        angleBetweenArmAndShooterDegrees = Constants.angleBetweenArmAndShooterDegrees + robotState.angleAdjustment;
        angleBetweenArmAndShooterRadians = (Math.PI/180)*angleBetweenArmAndShooterDegrees;
    }
    private static double angleBetweenArmAndShooterRadians = (Math.PI/180)*angleBetweenArmAndShooterDegrees;
    //Positive
    private static double outputVelocityPerSecond = Constants.outputVelocityPerSecond;
    public static void setOutputVelocityPerSecond(double increment){
        robotState.speedAdjustment += increment;
        outputVelocityPerSecond = Constants.outputVelocityPerSecond + robotState.speedAdjustment;
    }
    //Negative
    private static final double gravityPerSecond = Constants.gravityPerSecond;
    //Nonzero
    private static final double errorPerUnitDistance = Constants.errorPerUnitDistance;
    //the robot looks down the positive y axis, and the right side is the positive x axis while the left is the negative x axis
    private static final double axlePositionOffsetX = Constants.axlePositionOffsetX;
    private static final double axlePositionOffsetY = Constants.axlePositionOffsetY;
    private static final double axlePositionOffsetZ = Constants.axlePositionOffsetZ;
    private static final double heightOfTarget = Constants.heightOfTarget - axlePositionOffsetZ;

    //TODO account for how if the robot isnt pointed at the target, turning the robot to point to the target will offset the shooter position

    public static boolean robotIsDefinitelyInRange(double xyPlaneEuclideanDistanceToTarget){
        Optional<Double> ballisticAngleOfArm = getBallisticAngleOfArm(xyPlaneEuclideanDistanceToTarget+distance(axlePositionOffsetX, axlePositionOffsetY));
        if(ballisticAngleOfArm.isPresent())
            return true;
        return false;
    }

    public static boolean robotIsRoughlyInRange(double xyPlaneEuclideanDistanceToTarget){
        Optional<Double> ballisticAngleOfArm = getBallisticAngleOfArm(xyPlaneEuclideanDistanceToTarget-distance(axlePositionOffsetX, axlePositionOffsetY));
        if(ballisticAngleOfArm.isPresent())
            return true;
        return false;
    }

    //Approximates the arclength of the ballistic path
    public static double getError(double xyPlaneEuclideanDistanceToTarget,double armAngle, double shotAngle, int numSegments){
        double shotOriginX = getShotOriginX(armAngle);
        double shotOriginY = getShotOriginY(armAngle);

        double velocityX = outputVelocityPerSecond*Math.cos(shotAngle);
        double velocityY = outputVelocityPerSecond*Math.sin(shotAngle);

        double shotPositionX = getShotOriginX(armAngle);
        double shotPositionY = getShotOriginY(armAngle);

        double shotTime = (xyPlaneEuclideanDistanceToTarget-shotOriginX)/(outputVelocityPerSecond*Math.cos(shotAngle));

        double shotDistance = 0;

        for(double t = 0; t <= shotTime+1E-6; t += shotTime/numSegments){
            double nextShotVectorX = velocityX*t - shotPositionX;
            double nextShotVectorY = velocityY*t - gravityPerSecond*Math.pow(t, 2)/2 - shotPositionY;

            shotDistance += distance(nextShotVectorX, nextShotVectorY);

            shotPositionX += nextShotVectorX;
            shotPositionY += nextShotVectorY;
        }

        return shotDistance*errorPerUnitDistance;
    }

    public static double getError(double xyPlaneEuclideanDistanceToTarget,double armAngle, double shotAngle){
        return getError(
                xyPlaneEuclideanDistanceToTarget,
                armAngle,
                shotAngle,
                Math.abs((int)(outputVelocityPerSecond * (xyPlaneEuclideanDistanceToTarget-getShotOriginX(armAngle))/(outputVelocityPerSecond*Math.cos(shotAngle))))
        );
    }

    public static double getError(double xyPlaneEuclideanDistanceToTarget,double armAngle){
        double shotAngle = getBallisticAngleOfShooter(armAngle);
        return getError(xyPlaneEuclideanDistanceToTarget, armAngle, shotAngle);
    }

    public static Optional<Double> getError(double xyPlaneEuclideanDistanceToTarget){
        Optional<Double> optionalArmAngle = getBallisticAngleOfArm(xyPlaneEuclideanDistanceToTarget);

        if (optionalArmAngle.isEmpty()){
//            System.out.println("Target not in range");
            return Optional.empty();
        }

        double armAngle = optionalArmAngle.get();

        return Optional.of(getError(xyPlaneEuclideanDistanceToTarget, armAngle));
    }

    public static double getShotOriginX(double armAngle){
        return lengthOfArm*Math.cos(armAngle);
    }

    public static double getShotOriginY(double armAngle){
        return lengthOfArm*Math.sin(armAngle);
    }

    /**
     *
     * @param armAngle
     * @return gives the angle of the projectile shot
     */
    public static double getBallisticAngleOfShooter(double armAngle){
        double shooterAngle = Math.PI+armAngle+angleBetweenArmAndShooterRadians;
        if(shooterAngle > 2*Math.PI)
            shooterAngle -= 2*Math.PI;
        return shooterAngle;
    }

    /**
     *
     * @param xyPlaneEuclideanDistanceToTarget this should always be positive, think of it like rho in spherical but ignore the z dimension(so basically like r but can't be negative)
     * @return gives the angle that the shooter arm should be set at
     */
    public static Optional<Double> getBallisticAngleOfArm(double xyPlaneEuclideanDistanceToTarget){
        double armAngle = getStraightShotArmAngleRadians(xyPlaneEuclideanDistanceToTarget);
        int currentDivisor = 10;
        int runLoops = 0;

        if(heightOfTarget+1E-6 >=
                0.5*(
                    (gravityPerSecond
                        /Math.pow(outputVelocityPerSecond,2)
                        )*Math.pow(xyPlaneEuclideanDistanceToTarget,2)
                    -(Math.pow(outputVelocityPerSecond,2)/gravityPerSecond))
        ){
//            System.out.println("Target out of range");
            return Optional.empty();
        }

        while(Math.abs(getUnreliableError(xyPlaneEuclideanDistanceToTarget, armAngle)) > 1E-6){
            runLoops++;

            while(getUnreliableError(xyPlaneEuclideanDistanceToTarget, armAngle) < 0)
                armAngle += 1.0/currentDivisor;
            currentDivisor *= -10;

            while(getUnreliableError(xyPlaneEuclideanDistanceToTarget, armAngle) > 0)
                armAngle += 1.0/currentDivisor;
            currentDivisor *= -10;

            if(currentDivisor == 0){
//                System.out.println("Something went wrong");
//                System.out.println("Loop count: "+runLoops);
//                System.out.println(armAngle);
                return Optional.empty();
            }
        }

        if(xyPlaneEuclideanDistanceToTarget > (Math.pow(outputVelocityPerSecond,2)/gravityPerSecond)*Math.cos(armAngle)*Math.sin(armAngle)){
//            System.out.println("Target out of range message 2");
            return Optional.empty();
        }

//        System.out.println("Loop count: "+runLoops);

        return Optional.of(armAngle);
    }

    //Eunreliableerror
    public static double getUnreliableError(double xyPlaneEuclideanDistanceToTarget, double armAngle){
        double placeholder = (xyPlaneEuclideanDistanceToTarget-lengthOfArm*Math.cos(armAngle))/(outputVelocityPerSecond*Math.cos(armAngle+angleBetweenArmAndShooterRadians));
        double returnable = lengthOfArm*Math.sin(armAngle)
                +outputVelocityPerSecond
                    *Math.sin(armAngle+angleBetweenArmAndShooterRadians)
                    * placeholder
                +(gravityPerSecond/2)*Math.pow(placeholder,2)
                -heightOfTarget;
        return returnable;
    }

    //Cuseableangle
    public static double getStraightShotArmAngleRadians(double xyPlaneEuclideanDistanceToTarget){
        double returnable = (Math.PI/2)
                +Math.acos(-heightOfTarget/distance(xyPlaneEuclideanDistanceToTarget, heightOfTarget))
                -Math.asin(lengthOfArm*(Math.sin(angleBetweenArmAndShooterRadians)/distance(xyPlaneEuclideanDistanceToTarget, heightOfTarget)))
                -angleBetweenArmAndShooterRadians;
        return returnable;
    }

    protected static double distance(double... values){
        double currentDist = 0;
        for(double a : values)
            currentDist = Math.sqrt(Math.pow(currentDist,2)+Math.pow(a,2));
        return currentDist;
    }
}