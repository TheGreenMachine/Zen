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
import jakarta.inject.Singleton;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class Autopath {

    public static RobotState robotState;

    private final long looperDtInMS = (long) (Constants.kLooperDt * 1000);

    private Pose2d autopathTargetPosition = new Pose2d(0,0,new Rotation2d(0));

    private static FieldMap fieldMap = new FieldMap(1654, 821);

    private Pose2d autopathStartPosition = null;

    /**
     * State: if path needs to be stopped
     */
    private boolean needsStop;

    /**
     * Initializes Autopath
     */
    public Autopath() {
        robotState = Injector.get(RobotState.class);
        fieldMap.drawPolygon(new double[]{1100, 1100, 900}, new double[]{210, 630, 420}, true);
    }

    /**
     * Tests a trajectory against the fieldMap to see whether a robot of (whatever) width can path successfully
     *
     * @param trajectory
     * @return
     */
    public static boolean testTrajectory(Trajectory trajectory){
        for(int t = 0; t*.1 < trajectory.getTotalTimeSeconds(); t++){
            if(fieldMap.checkPixelHasObjectOrOffMap((int)(trajectory.sample(t).poseMeters.getX()*100), (int)(trajectory.sample(t).poseMeters.getY()*100)))
                return false;
        }
        return true;
    }

    /**
     * Runs the Autopath routine actions
     *
     * @see #routine()
     */
    public void run(Pose2d autopathTargetPosition) {
        this.autopathTargetPosition = autopathTargetPosition;

        System.out.println("You told me to do something!");

        start();

        try {
            routine();
        } catch (Exception e){
            GreenLogger.log("Autopathing ended early");
        }

        done();
    }

    /**
     * Runs the Autopath routine actions
     *
     * @see #routine()
     */
    private void run(Translation2d autopathTargetPosition) {
        run(new Pose2d(autopathTargetPosition, robotState.fieldToVehicle.getRotation()));
    }

    /**
     * Starts the Autopath and relevant actions
     */
    private void start() {
        robotState.autopathing = true;

        autopathStartPosition = robotState.fieldToVehicle;

        GreenLogger.log("Starting Autopath");
        needsStop = false;
    }

    /**
     * Called every loop
     */
    protected void routine() throws AutoModeEndedException {
        GreenLogger.log("Autopathing Running");

        Trajectory autopathTrajectory = new Trajectory();

        autopathTrajectory = AutopathAlgorithm.calculateAutopath(autopathTargetPosition);

        List<Rotation2d> autopathHeadings = new ArrayList<>();
        //TODO create headings
        // for now I'll make it use the current robot rotation
        for(int i = 0; i < autopathTrajectory.getStates().size(); i++)
            autopathHeadings.add(robotState.fieldToVehicle.getRotation());

        //Here's where your trajectory gets checked against the field
        System.out.println("And survey says: "+testTrajectory(autopathTrajectory));

        TrajectoryAction autopathTrajectoryAction = new TrajectoryAction(autopathTrajectory, autopathHeadings);

        // Run actions here:
        // e.g. runAction(new SeriesAction(new WaitAction(0.5), ...))
        runAction(
                autopathTrajectoryAction
        );
    }

    /**
     * Standard cleanup end-procedure
     */
    protected void done() {
        robotState.autopathing = false;

        System.out.println("Started at "+autopathStartPosition);
        System.out.println("Hopefully ended at "+autopathTargetPosition);
        System.out.println("And it thinks it's at "+robotState.fieldToVehicle);

        GreenLogger.log("Autopath Done");
    }

    /**
     * Stops the auto path
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
