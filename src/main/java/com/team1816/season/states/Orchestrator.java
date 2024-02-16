package com.team1816.season.states;

import com.ctre.phoenix6.StatusCode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.Injector;
import com.team1816.lib.PlaylistManager;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.subsystems.drive.Drive;
import com.team1816.lib.subsystems.vision.Camera;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.util.visionUtil.VisionPoint;
import com.team1816.season.configuration.Constants;
import com.team1816.season.configuration.FieldConfig;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.Objects;

import static com.team1816.lib.subsystems.Subsystem.robotState;

/**
 * Main superstructure-style class and logical operator for handling and delegating subsystem tasks. Consists of an integrated
 * drivetrain with other subsystems and utilizes closed loop state dependent control via {@link RobotState}.
 *
 * @see RobotState
 */
@Singleton
public class Orchestrator {

    /**
     * Subsystems
     */
    private static Drive drive;
    private static Camera camera;
    private static LedManager ledManager;
    private static Collector collector;
    private static Shooter shooter;

    /**
     * Properties
     */

    // Place threads here.
    // e.g. private Thread [ThreadName]Thread;

    public static boolean runningAutoTarget = false;

    // Place appropriate running booleans here.
    // e.g. public static boolean running[ThreadName] = false;

    /**
     * Instantiates an Orchestrator with all its subsystems
     *
     * @param df  Drive.Factory (derives drivetrain)
     * @param led LedManager
     */
    @Inject
    public Orchestrator(Drive.Factory df, Camera cam, LedManager led) {
        /**
         * Insert any other parameters into the constructor if you need to
         * manage them.
         *
         * e.g. a Subsystem of some kind like the LEDManager.
         */

        drive = df.getInstance();
        camera = cam;
        ledManager = led;
        collector = Injector.get(Collector.class);
        shooter = Injector.get(Shooter.class);
    }

    /**
     * Actions
     */

    // Place any actions here.

    /**
     * Music Control
     */

    //TODO Move orchestra object out of drive and add wrapper - LOW LOW PRIORITY

    /**
     * Plays/Pauses the selected song for the Orchestra
     * @see com.ctre.phoenix6.Orchestra
     * @param playing If the song should play or pause
     */
    public void playSong(boolean playing) {
        if (playing) {
            if (!drive.orchestra.isPlaying()) {
                drive.orchestra.play();
            }
        } else {
            drive.orchestra.pause();
        }
    }

    /**
     * Stops the Orchestra from playing a song
     * @see com.ctre.phoenix6.Orchestra
     */
    public void stopSong() {
        drive.orchestra.stop();
    }

    /**
     * Loads the entered song's filepath into the Orchestra
     *
     * @param song The selected song
     * @see com.ctre.phoenix6.Orchestra
     */
    public StatusCode loadSong(PlaylistManager.Playlist song) {
        return loadSong(song.getFilePath());
    }

    /**
     * Loads the entered filepath into the Orchestra
     *
     * @param filepath
     * @see com.ctre.phoenix6.Orchestra
     */
    private StatusCode loadSong(String filepath) {
        return drive.orchestra.loadMusic(filepath);
    }

    /**
     * Clears executable threads
     */
    public void clearThreads() {
        /**
            For clearing a thread, here is the general pattern we follow:

            if (thread != null && thread.isAlive()) {
                thread.stop();
            }

            Make sure to use the pattern above to avoid causing exceptions
            and any errors, when stopping the work on a thread.
         */
    }

    /** Superseded Odometry Handling */

    /**
     * Calculates the absolute pose of the drivetrain based on a single target
     *
     * @param target VisionPoint
     * @return Pose2d
     * @see VisionPoint
     */
    public Pose2d calculateSingleTargetTranslation(VisionPoint target) {
        if (FieldConfig.fiducialTargets.containsKey(target.id)) {
            Pose2d targetPos = FieldConfig.fiducialTargets.get(target.id).toPose2d();
            double X = target.getX(), Y = target.getY();

            Translation2d cameraToTarget = new Translation2d(X, Y).rotateBy(robotState.getLatestFieldToCamera());
            Translation2d robotToTarget = cameraToTarget.plus(
                Constants.kCameraMountingOffset.getTranslation().rotateBy(
                    robotState.fieldToVehicle.getRotation()
                )
            );
            Translation2d targetToRobot = robotToTarget.unaryMinus();

            Translation2d targetTranslation = targetToRobot.rotateBy(targetPos.getRotation());
            Pose2d p = targetPos.plus(
                new Transform2d(
                    targetTranslation,
                    targetPos.getRotation().rotateBy(Rotation2d.fromDegrees(180))
                )
            ); // inverse axis angle

            GreenLogger.log("Updated Pose: " + p);
            return p;
        } else {
            return robotState.fieldToVehicle;
        }
    }

    /**
     * Calculates the absolute pose of the drivetrain based on a single target using PhotonVision's library
     *
     * @param target VisionPoint
     * @return Pose2d
     * @see org.photonvision.targeting.PhotonTrackedTarget
     */
    public Pose2d photonCalculateSingleTargetTranslation(PhotonTrackedTarget target) {
        Pose2d targetPos = new Pose2d(
            FieldConfig.fiducialTargets.get(target.getFiducialId()).getX(),
            FieldConfig.fiducialTargets.get(target.getFiducialId()).getY(),
            new Rotation2d()
        );
        Translation2d targetTranslation = target.getBestCameraToTarget().getTranslation().toTranslation2d();
        Transform2d targetTransform = new Transform2d(targetTranslation, robotState.getLatestFieldToCamera());
        return PhotonUtils.estimateFieldToCamera(targetTransform, targetPos);
    }

    public void autoSetCollectorState(){
        if (!robotState.isShooting) {
            if (!robotState.isBeamBreakTriggered && robotState.actualPivotState == Shooter.PIVOT_STATE.STOW) {
                collector.setDesiredState(Collector.COLLECTOR_STATE.INTAKE);
                shooter.setDesiredFeederState(Shooter.FEEDER_STATE.TRANSFER);
            } else {
                collector.setDesiredState(Collector.COLLECTOR_STATE.OUTTAKE);
                shooter.setDesiredFeederState(Shooter.FEEDER_STATE.STOP);
            }
        }
    }

    public enum CONTROL_MODE {
        ALEPH_0,
        ALEPH_1
    }
}
