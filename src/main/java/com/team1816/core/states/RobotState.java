package com.team1816.core.states;

import com.google.inject.Singleton;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.PathFinder;
import com.team1816.lib.autopath.Autopath;
import com.team1816.lib.subsystems.drive.SwerveDrive;
import com.team1816.lib.util.visionUtil.VisionPoint;
import com.team1816.core.configuration.Constants;
import com.team1816.core.configuration.FieldConfig;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.*;
import org.photonvision.EstimatedRobotPose;

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
     * Current Drive inputs
     */
    public double throttleInput = 0;
    public double strafeInput = 0;
    public double rotationInput = 0;

    /**
     * Rotating closed loop
     */

    public boolean rotatingClosedLoop = false;
    public double targetRotationRadians = 0;

    /**
     * Orchestrator states
     */

    //TODO add new subystem states here

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
     * Autopathing state
     */
    public boolean autopathing = false;
    public boolean printAutopathing = false;
    public boolean printAutopathFieldTest = false;
    public Trajectory autopathTrajectory = null;
    public ArrayList<Trajectory> autopathTrajectoryPossibilities = new ArrayList<>();
    public boolean autopathTrajectoryChanged = false;
    public boolean autopathTrajectoryPossibilitiesChanged = false;
    public ArrayList<Pose2d> autopathCollisionStarts = new ArrayList<>();
    public ArrayList<Pose2d> autopathCollisionEnds = new ArrayList<>();
    public ArrayList<Pose2d> autopathWaypoints = new ArrayList<>();
    public ArrayList<Pose2d> autopathWaypointsSuccess = new ArrayList<>();
    public ArrayList<Pose2d> autopathWaypointsFail = new ArrayList<>();
    public int autopathMaxBranches = 0;

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
     * Vision Pose Stuff
     */
    public double lastEstTimestamp = 0;
    public final Matrix<N3, N1> kSingleTagStdDevs = VecBuilder.fill(4, 4, 8);
    public final Matrix<N3, N1> kMultiTagStdDevs = VecBuilder.fill(0.5, 0.5, 1);
    public EstimatedRobotPose currentVisionEstimatedPose;
    public boolean currentCamFind;

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

        // TODO: Insert any subsystem state set up here.

        isPoseUpdated = true;
        superlativeTarget = new VisionPoint();
        visibleTargets = new ArrayList<>();
        drivetrainTemp = 0;
        vehicleToFloorProximityCentimeters = 0;
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
     * Locks robot rotation to a specific angle, then terminates rotation once angle is reached
     *
     * @param targetRotationRadians
     * @return
     */
    public boolean setRobotRotatingClosedLoop(double targetRotationRadians){
        this.targetRotationRadians = targetRotationRadians;
        rotatingClosedLoop = true;

        return fieldToVehicle.getRotation().getRadians() == targetRotationRadians;
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
     * Outputs real-time telemetry data to Shuffleboard / SmartDashboard
     */
    public synchronized void outputToSmartDashboard() {
        field.setRobotPose(fieldToVehicle);

        if (printAutopathing) {
            if (Autopath.fieldMap != null && Autopath.fieldMap.outputToSmartDashboardChanged) {
                ArrayList<Pose2d> obstaclesExpanded = new ArrayList<>();

                for (int i = 0; i < Autopath.fieldMap.getCurrentMap().getMapX(); i++) {
                    for (int i2 = 0; i2 < Autopath.fieldMap.getCurrentMap().getMapY(); i2++) {
                        if (Autopath.fieldMap.getCurrentMap().checkPixelHasObjectOrOffMap(i, i2)) {
                            obstaclesExpanded.add(new Pose2d(new Translation2d(i * .01, i2 * .01), new Rotation2d()));
                        }
                    }
                }

                field.getObject("ExpandedObstacles").setPoses(obstaclesExpanded);

                ArrayList<Pose2d> obstacles = new ArrayList<>();

                for (int i = 0; i < Autopath.fieldMap.getCurrentMap().getMapX(); i++) {
                    for (int i2 = 0; i2 < Autopath.fieldMap.getCurrentMap().getMapY(); i2++) {
                        if (Autopath.fieldMap.getStableMapCheckPixelHasObjectOrOffMap(i, i2)) {
                            obstacles.add(new Pose2d(new Translation2d(i * .01, i2 * .01), new Rotation2d()));
                        }
                    }
                }

                field.getObject("Obstacles").setPoses(obstacles);

                Autopath.fieldMap.outputToSmartDashboardChanged = false;
            }

            if(autopathTrajectoryPossibilitiesChanged) {
                for (int i = 0; i < autopathTrajectoryPossibilities.size(); i++) {
                    if (autopathTrajectoryPossibilities.get(i) != null) {
                        field.getObject("AutopathTrajectory: " + i).setTrajectory(autopathTrajectoryPossibilities.get(i));
                    }
                }
                autopathMaxBranches = Math.max(autopathTrajectoryPossibilities.size(), autopathMaxBranches);
                autopathTrajectoryPossibilitiesChanged = false;
            }

            field.getObject("StartCollisionPoints").setPoses(autopathCollisionStarts);
            field.getObject("EndCollisionPoints").setPoses(autopathCollisionEnds);
            field.getObject("AutopathWaypoints").setPoses(autopathWaypoints);
        }

        if (autopathTrajectoryChanged) {
            if(autopathTrajectory != null){
                for (int i = 0; i < autopathMaxBranches; i++) {
                    field.getObject("AutopathTrajectory: " + i).close();
                }
                field.getObject("AutopathTrajectory").setTrajectory(autopathTrajectory);
            } else
                field.getObject("AutopathTrajectory").setPoses(List.of(new Pose2d(new Translation2d(-1, -1), new Rotation2d())));
            autopathTrajectoryChanged = false;
        }

        if(printAutopathFieldTest) {
            field.getObject("AutopathSuccessfulPoints").setPoses(autopathWaypointsSuccess);
            field.getObject("AutopathFailPoints").setPoses(autopathWaypointsFail);
        }

        SmartDashboard.putData("Mech2d", mechCanvas);

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
