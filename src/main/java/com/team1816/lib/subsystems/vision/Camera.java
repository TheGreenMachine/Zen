package com.team1816.lib.subsystems.vision;

import com.team1816.lib.Infrastructure;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.RobotBase;
import jakarta.inject.Inject;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.simulation.PhotonCameraSim;
import org.photonvision.simulation.VisionSystemSim;
import org.photonvision.targeting.PhotonPipelineResult;

import java.util.Optional;

public class Camera extends Subsystem{
    /**
     * Properties
     */
    private static final String NAME = "camera";
    private static final String CAM = "Arducam_OV9281_USB_Camera";
    public static final AprilTagFieldLayout kTagLayout =
            AprilTagFields.kDefaultField.loadAprilTagLayoutField();
    private static final Transform3d robotToCam = Constants.kCameraMountingOffset3D;

    /**
     * Components
     */
    private VisionSystemSim visionSim;
    private PhotonCamera cam;
    private PhotonCameraSim cameraSim;
    private final PhotonPoseEstimator photonEstimator;

    @Inject
    public Camera(Infrastructure inf, RobotState rs){
        super(NAME, inf, rs);
        cam = new PhotonCamera(CAM);
        cam.setDriverMode(true);

        if (RobotBase.isSimulation()) {
            cameraSim = new PhotonCameraSim(cam);
            cameraSim.enableDrawWireframe(true);
            cameraSim.setWireframeResolution(1);
            visionSim = new VisionSystemSim("SimVision");
            visionSim.addCamera(cameraSim, Constants.kCameraMountingOffset3D);
            visionSim.addAprilTags(kTagLayout);
        }
        photonEstimator = new PhotonPoseEstimator(kTagLayout, PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cam, robotToCam);
        photonEstimator.setMultiTagFallbackStrategy(PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY);
    }

    public PhotonPipelineResult getLatestResult() {
        return cam.getLatestResult();
    }

    public void setDriverMode(boolean driverMode){
        cam.setDriverMode(driverMode);
    }

    /**
     * The latest estimated robot pose on the field from vision data. This may be empty. This should
     * only be called once per loop.
     *
     * @return An {@link EstimatedRobotPose} with an estimated pose, estimate timestamp, and targets
     *     used for estimation.
     */
    public boolean updateEstimatedGlobalPose() {
        Optional<EstimatedRobotPose> visionEst = photonEstimator.update();
        if(visionEst.isEmpty())
            return false;

        robotState.currentVisionEstimatedPose = visionEst.get();
        double latestTimestamp = cam.getLatestResult().getTimestampSeconds();
        boolean newResult = Math.abs(latestTimestamp - robotState.lastEstTimestamp) > 1e-5;

        if (newResult) robotState.lastEstTimestamp = latestTimestamp;

        return true;
    }

    public EstimatedRobotPose getEstimatedGlobalPose() {
        return robotState.currentVisionEstimatedPose;
    }

    /**
     * The standard deviations of the estimated pose from {@link #getEstimatedGlobalPose()}, for use
     * with {@link edu.wpi.first.math.estimator.SwerveDrivePoseEstimator SwerveDrivePoseEstimator}.
     * This should only be used when there are targets visible.
     *
     * @param estimatedPose The estimated pose to guess standard deviations for.
     */
    public Matrix<N3, N1> getEstimationStdDevs(Pose2d estimatedPose) {
        var estStdDevs = robotState.kSingleTagStdDevs;
        var targets = getLatestResult().getTargets();
        int numTags = 0;
        double avgDist = 0;
        for (var tgt : targets) {
            var tagPose = photonEstimator.getFieldTags().getTagPose(tgt.getFiducialId());
            if (tagPose.isEmpty()) continue;
            numTags++;
            avgDist +=
                    tagPose.get().toPose2d().getTranslation().getDistance(estimatedPose.getTranslation());
        }
        if (numTags == 0) return estStdDevs;
        avgDist /= numTags;
        // Decrease std devs if multiple targets are visible
        if (numTags > 1) estStdDevs = robotState.kMultiTagStdDevs;
        // Increase std devs based on (average) distance
        if (numTags == 1 && avgDist > 4)
            estStdDevs = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        else estStdDevs = estStdDevs.times(1 + (avgDist * avgDist / 30));

        return estStdDevs;
    }

    @Override
    public void readFromHardware() {
        if (RobotBase.isSimulation()) {
            visionSim.update(robotState.fieldToVehicle);
        } else {
            robotState.currentCamFind = updateEstimatedGlobalPose();
        }
    }

    @Override
    public void writeToHardware() {

    }

    @Override
    public void zeroSensors() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean testSubsystem() {
        return false;
    }
}