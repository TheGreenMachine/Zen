package com.team1816.core.configuration;

import com.google.inject.Singleton;
import com.team1816.lib.Injector;
import com.team1816.lib.auto.Symmetry;
import com.team1816.lib.hardware.factory.RobotFactory;
import com.team1816.lib.variableInputs.Numeric;
import com.team1816.lib.variableInputs.VariableInput;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * This class contains all constants pertinent to robot-specific aspects.
 * Only fields that are necessary and generalizable across systems belong in this class.
 */
@Singleton
public class Constants {
    /**
     * Factory & Stem
     */
    private static final RobotFactory factory = Injector.get(RobotFactory.class);

    public static final Pose2d EmptyPose2d = new Pose2d();
    public static final Rotation2d EmptyRotation2d = new Rotation2d();
    public static final Transform2d EmptyTransform2d = new Transform2d();

    public static final Pose3d EmptyPose3d = new Pose3d();
    public static final Rotation3d EmptyRotation3d = new Rotation3d();
    public static final Transform3d EmptyTransform3d = new Transform3d();
    public static final Quaternion EmptyQuaternion = new Quaternion();

    public static final double kLooperDt = 100;  //factory.getConstant("kLooperDt", .020);

    /**
     * Git Hash
     */
    public static final String kGitHash = RobotFactory.getGitHash();

    /**
     * CANBus Characterization
     */
    public static final boolean kHasCANivore = factory.getConstant("hasCanivore", 0) > 0;
    public static final String kCANivoreName = factory.getCanBusName();
    public static final String kLowSpeedBusName = "rio";

    /**
     * CAN Timeouts
     */
    public static final int kCANTimeoutMs = 10; //FIXME // utility: on the fly updates
    public static final int kLongCANTimeoutMs = 100; // utility: constructors


    /**
     * Field characterization
     */
    public static final Symmetry fieldSymmetry = Symmetry.AXIS;
    public static final double fieldCenterY = 8.211 / 2.0; //FIXME
    public static final double fieldCenterX = 16.542 / 2.0; //FIXME
    public static final Pose2d fieldCenterPose = new Pose2d(
        fieldCenterX,
        fieldCenterY,
        EmptyRotation2d
    );
    public static final Pose2d kDefaultZeroingPose = new Pose2d(
        0.5,
        fieldCenterY,
        EmptyRotation2d
    );

    public static final Pose2d kFlippedZeroingPose = new Pose2d(
        fieldCenterX * 2 - .5,
        fieldCenterY,
        Rotation2d.fromDegrees(180)
    );


    public static final double kCameraHeightMeters = 0.601; //FIXME

    public static final Pose2d kCameraMountingOffset = new Pose2d( //FIXME
            -0.369,
            0,
            Rotation2d.fromRadians(Math.PI)
    );

    public static final Transform3d kCameraMountingOffset3D = new Transform3d( //FIXME
            -0.369,
            0,
            Constants.kCameraHeightMeters,
            new Rotation3d(Math.PI,-0.44,Math.PI)
    );

    /**
     * Drivetrain characterization
     */
    public static double kClosedLoopRotationTolerance = factory.getConstant("rotationToleranceClosedLoop", 1);

    public static final boolean kSoundOnConfig = factory.getConstant("soundOnConfig", 1) > 0;
    public static final boolean kMusicEnabled = factory.getConstant("enableMusic", 0) > 0;


    /**
     * Camera characterization
     */
    public static final double kCameraMountingAngleY = 0; // degrees

    public static final double kLoggingDiskPartitionRatio = 0.25; // percent of storage space allotted for logging
    public static final boolean kLoggingRobot = factory.getConstant("logRobot", 1) > 0;
    public static final boolean kLoggingDrivetrain = factory.getConstant("logDrivetrain", 1) > 0 && kLoggingRobot;

    public static final boolean kUseVision = factory.getConstant("usingVision", 0) > 0;

    /**
     * Autonomous
     */
    public static final double kPTranslational = 10;
    public static final double kPRotational = 10;

    /**
     * Simulation
     */
    public static final ShuffleboardTab kSimWindow = Shuffleboard.getTab("Simulation");

    /**
     * AutoAiming stuff
     */
    //TODO set to meters, (currently in inches)
    //Positive
    public static final double lengthOfArm = Units.inchesToMeters(7.94);
    //0-180
    public static final double angleBetweenArmAndShooterDegrees = 180-116.52;
    //Positive
    public static final double outputVelocityPerSecond = 13;
    //10.2 maybe if it don't work right away
    //Negative
    public static final double gravityPerSecond = -9.80665;
    //Nonzero
    public static final double errorPerUnitDistance = 0.1;
    //the robot looks down the positive y axis, and the right side is the positive x axis while the left is the negative x axis
    public static final double axlePositionOffsetX = 0;
    public static final double axlePositionOffsetY = Units.inchesToMeters(12);
    public static final double axlePositionOffsetZ = Units.inchesToMeters(24.5);
    public static final double heightOfTarget = Units.inchesToMeters(80.4375);
    //2.0431125
    public static final double blueSpeakerX = Units.inchesToMeters(7);
    public static final double redSpeakerX = Units.inchesToMeters(642.25+3);
    //16.27
    //TODO find out this measurement
    public static final double speakerY = 5.6597042;
    //5.6597042
    public static final double axleRadiansDisplacement = 0;
}
