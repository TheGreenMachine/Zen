package com.team1816.lib.autopath;

import com.team1816.lib.DriveFactory;
import com.team1816.lib.Injector;
import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.AutoAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.subsystems.drive.EnhancedSwerveDrive;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.core.auto.AutoModeManager;
import com.team1816.core.configuration.Constants;
import com.team1816.core.states.RobotState;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.DriverStation;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import java.util.ArrayList;
import java.util.List;

public class Autopath {

    public static RobotState robotState;

    private final long looperDtInMS = (long) (Constants.kLooperDt * 1000);

    private Pose2d autopathTargetPosition = new Pose2d(0,0,new Rotation2d(0));

    /**
     * State: if mode needs to be stopped
     */
    private boolean needsStop;

    /**
     * Instantiates an AutoMode from a list of trajectory actions
     *
     * @see TrajectoryAction
     */
    protected Autopath() {
        robotState = Injector.get(RobotState.class);
    }

    /**
     * Runs the autoMode routine actions
     *
     * @see #routine()
     */
    public void run(Pose2d autopathTargetPosition) {
        this.autopathTargetPosition = autopathTargetPosition;

        start();

        try {
            routine();
        } catch (Exception e){
            GreenLogger.log("Autopathing ended early");
        }

        done();
    }

    /**
     * Runs the autoMode routine actions
     *
     * @see #routine()
     */
    private void run(Translation2d autopathTargetPosition) {
        run(new Pose2d(autopathTargetPosition, robotState.fieldToVehicle.getRotation()));
    }

    /**
     * Starts the AutoMode and relevant actions
     */
    private void start() {
        GreenLogger.log("Starting Autopath");
        needsStop = false;
    }

    /**
     * Routine register of actions that will be run in the mode
     *
     */
    protected void routine() throws AutoModeEndedException {
        GreenLogger.log("Autopathing Running");

        Trajectory autopathTrajectory = new Trajectory();
        //TODO create your trajectory
        List<Rotation2d> autopathHeadings = new ArrayList<>();
        //TODO create headings

        TrajectoryAction autopathTrajectoryAction = new TrajectoryAction(autopathTrajectory, autopathHeadings);

        // Run actions here:
        // e.g. runAction(new SeriesAction(new WaitAction(0.5), ...))
        runAction(
                new SeriesAction(
                        autopathTrajectoryAction

                )
        );
    }

    /**
     * Standard cleanup end-procedure
     */
    protected void done() {
        GreenLogger.log("Autopath Done");
    }

    /**
     * Stops the auto mode
     */
    public void stop() {
        needsStop = true;
    }

    /**
     * Runs a given action, typically placed in routine()
     *
     * @param action
     * @throws AutoModeEndedException
     * @see AutoAction
     */
    protected void runAction(AutoAction action) throws AutoModeEndedException {
        action.start();

        // Run action, stop action on interrupt or done
        while (!action.isFinished()) {
            if (needsStop) {
                throw new AutoModeEndedException();
            }

            action.update();

            try {
                Thread.sleep(looperDtInMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        action.done();
    }
}
