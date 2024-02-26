package com.team1816.season.states;

import com.google.inject.Singleton;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.PathFinder;
import com.team1816.lib.subsystems.drive.SwerveDrive;
import com.team1816.lib.util.visionUtil.VisionPoint;
import com.team1816.season.configuration.Constants;
import com.team1816.season.configuration.FieldConfig;
import com.team1816.season.subsystems.Climber;
import com.team1816.season.subsystems.Shooter;
import com.team1816.season.subsystems.Collector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.util.Color8Bit;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for logging the robot's actual states and estimated states.
 * Including superstructure and subsystem states.
 */

@Singleton
public class RobotState {

    /**
     * Odometry and field characterization
     */
    public final Field2d field = new Field2d();
    public Color allianceColor = Color.BLUE;
    public Pose2d fieldToVehicle = Constants.EmptyPose2d;
    public Pose2d driverRelativeFieldToVehicle = Constants.EmptyPose2d;
    public Pose2d extrapolatedFieldToVehicle = Constants.EmptyPose2d;
    public Pose2d target = Constants.fieldCenterPose;
    public Rotation2d vehicleToTurret = Constants.EmptyRotation2d;
    public Pose2d fieldToTurret = Constants.EmptyPose2d;
    public ChassisSpeeds deltaVehicle = new ChassisSpeeds(); // velocities of vehicle
    public ChassisSpeeds calculatedVehicleAccel = new ChassisSpeeds(); // calculated acceleration of vehicle
    public Double[] triAxialAcceleration = new Double[]{0d, 0d, 0d};
    public boolean isPoseUpdated = true;
    public double vehicleToFloorProximityCentimeters = 0;
    public double drivetrainTemp = 0;
    public SwerveDrivePoseEstimator swerveEstimator =
            new SwerveDrivePoseEstimator(
                    SwerveDrive.swerveKinematics,
                    Constants.EmptyRotation2d,
                    new SwerveModulePosition[]{
                            new SwerveModulePosition(),
                            new SwerveModulePosition(),
                            new SwerveModulePosition(),
                            new SwerveModulePosition()
                    },
                    new Pose2d() //TODO figure out what to initialize this to
            );

    /**
     * Inertial characterization
     */
    public Pose3d fieldToCG = Constants.EmptyPose3d;
    public Rotation3d inertialOrientationState = Constants.EmptyRotation3d;
    public Quaternion inertialReferenceOrientationState = Constants.EmptyQuaternion; // utilizes active multiplication

    /**
     * Snapping Mode
     */
    public enum SnappingDirection {
        FRONT(0),
        BACK(180),
        LEFT(90),
        RIGHT(-90),
        NO_SNAP(-360); // Some magic value.

        public double value;

        SnappingDirection(double value) {
            this.value = value;
        }
    }

    public SnappingDirection snapDirection = SnappingDirection.NO_SNAP;

    /**
     * Orchestrator states
     */

    public Collector.COLLECTOR_STATE actualCollectorState = Collector.COLLECTOR_STATE.STOP;

    public Shooter.ROLLER_STATE actualRollerState = Shooter.ROLLER_STATE.STOP;
    public Shooter.FEEDER_STATE actualFeederState = Shooter.FEEDER_STATE.STOP;
    public Shooter.PIVOT_STATE actualPivotState = Shooter.PIVOT_STATE.STOW;
    public boolean isShooting = false;
    public boolean isBeamBreakTriggered = false;

    public Climber.CLIMBER_STATE actualClimberState = Climber.CLIMBER_STATE.STOP;

    public VisionPoint superlativeTarget = new VisionPoint();
    public List<VisionPoint> visibleTargets = new ArrayList<>();


    public final Mechanism2d mechCanvas = new Mechanism2d(3, 3);
    public final MechanismRoot2d root = mechCanvas.getRoot("root", 2.25, 0);

    public final MechanismLigament2d pivotStand = root.append(new MechanismLigament2d("stand", 2.5, 90));
    public final double pivotBaseAngle = 150;
    public final MechanismLigament2d pivotArm = pivotStand.append(new MechanismLigament2d("pivot", 2, pivotBaseAngle));

    /**
     * Functional pathing states
     */
    public PathFinder pathFinder = new PathFinder();


    /**
     * Pigeon state
     */

    public double[] gyroPos = new double[3];

    /**
     * Initializes RobotState and field
     */
    public RobotState() {
        resetPosition();
        FieldConfig.setupField(field);
    }

    /**
     * Resets drivetrain and turret position to a specified pose of drivetrain and rotation of turret
     *
     * @param initial_field_to_vehicle
     * @param initial_vehicle_to_turret
     */
    public synchronized void resetPosition(
        Pose2d initial_field_to_vehicle,
        Rotation2d initial_vehicle_to_turret
    ) {
        resetPosition(initial_field_to_vehicle);
        vehicleToTurret = initial_vehicle_to_turret;
    }

    /**
     * Resets drivetrain position to a specified pose of drivetrain
     *
     * @param initial_field_to_vehicle
     */
    public synchronized void resetPosition(Pose2d initial_field_to_vehicle) {
        fieldToVehicle = initial_field_to_vehicle;
    }

    /**
     * Resets the drivetrain to its default "zero" pose
     *
     * @see Constants
     */
    public synchronized void resetPosition() {
        resetPosition(Constants.kDefaultZeroingPose);
    }

    /**
     * Resets all values stored in RobotState
     */
    public synchronized void resetAllStates() {
        deltaVehicle = new ChassisSpeeds();
        calculatedVehicleAccel = new ChassisSpeeds();
        triAxialAcceleration = new Double[]{0d, 0d, 0d};

        // TODO: Insert any state set up here.
        actualPivotState = Shooter.PIVOT_STATE.STOW;
        actualCollectorState = Collector.COLLECTOR_STATE.STOP;
        actualFeederState = Shooter.FEEDER_STATE.STOP;
        actualRollerState = Shooter.ROLLER_STATE.STOP;
        actualClimberState = Climber.CLIMBER_STATE.STOP;

        isPoseUpdated = true;
        superlativeTarget = new VisionPoint();
        visibleTargets = new ArrayList<>();
        drivetrainTemp = 0;
        vehicleToFloorProximityCentimeters = 0;
    }

    /**
     * Returns rotation of the turret with respect to the field
     *
     * @return Rotation2d
     */
    public Rotation2d getLatestFieldToTurret() {
        return fieldToTurret.getRotation();
    }

    /**
     * Returns rotation of the camera with respect to the field
     *
     * @return Rotation2d
     * @see Orchestrator#calculateSingleTargetTranslation(VisionPoint) ()
     */
    public Rotation2d getLatestFieldToCamera() {
        return fieldToVehicle.getRotation().plus(Constants.kCameraMountingOffset.getRotation());
    }

    /**
     * Returns pose of the turret with respect ot the field
     *
     * @return Pose2d
     */
    public synchronized Pose2d getFieldToTurretPos() {
        return fieldToTurret;
    }

    /**
     * Returns the estimated pose of the turret with respect to the field based on a look-ahead time
     *
     * @return Pose2d
     */
    public synchronized Pose2d getEstimatedFieldToTurretPos() {
        return new Pose2d(
            extrapolatedFieldToVehicle
                .transformBy(
                    new Transform2d(
                        Constants.kTurretMountingOffset,
                        Constants.EmptyRotation2d
                    )
                )
                .getTranslation(),
            getLatestFieldToTurret()
        );
    }

    /**
     * Returns the estimated / calculated acceleration of the robot based on sensor readings
     *
     * @return ChassisSpeeds
     */
    public synchronized ChassisSpeeds getCalculatedAccel() {
        return calculatedVehicleAccel;
    }

    /**
     * Returns the distance from the goal based on the pose of the robot
     *
     * @return distance (meters)
     */
    public double getDistanceToGoal() {
        double estimatedDistanceToGoalMeters = fieldToVehicle
            .getTranslation()
            .getDistance(Constants.targetPos.getTranslation());
        return estimatedDistanceToGoalMeters;
    }

    /**
     * Outputs real-time telemetry data to Shuffleboard / SmartDashboard
     */
    public synchronized void outputToSmartDashboard() {
        field.setRobotPose(fieldToVehicle);

        SmartDashboard.putData("Mech2d", mechCanvas);
        SmartDashboard.putBoolean("BeamBreak", isBeamBreakTriggered);

        if (RobotBase.isSimulation()) {
            // TODO: Display any stats here

            // e.g.
            SmartDashboard.putNumber(
                    "Path_to_Subsystem/Value",
                    02390293.23
            );
        }
    }
}
