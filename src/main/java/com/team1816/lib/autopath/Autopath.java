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

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class Autopath {

    public static RobotState robotState;

    private final long looperDtInMS = (long) (Constants.kLooperDt * 1000);

    private Pose2d autopathTargetPosition = new Pose2d(0,0,new Rotation2d(0));

    private static FieldMap stableFieldMap = new FieldMap(1651, 821);

    public static UpdatableAndExpandableFieldMap fieldMap;

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

//        Ethan's Code
        stableFieldMap.drawPolygon(new int[]{0, 0, 175}, new int[]{0, 125, 0}, true);
        stableFieldMap.drawPolygon(new int[]{1651, 1651, 1476}, new int[]{0, 125, 0}, true);

//        Nora's Code
        stableFieldMap.drawPolygon(new int[]{0, 0, 100, 100}, new int[]{450, 650, 600, 500}, true);
        stableFieldMap.drawPolygon(new int[]{1651, 1651, 1551, 1551}, new int[]{450, 650, 600, 500}, true);

//        Noah's Code
        stableFieldMap.drawPolygon(new int[]{310, 310, 370, 370}, new int[]{380, 440, 440, 380}, true);
        stableFieldMap.drawPolygon(new int[]{520, 550, 600, 570}, new int[]{290, 240, 270, 320}, true);
        stableFieldMap.drawPolygon(new int[]{520, 550, 600, 570}, new int[]{531, 581, 551, 501}, true);

//        John Jerney's Code
        stableFieldMap.drawPolygon(new int[]{1651-310, 1651-310, 1651-370, 1651-370}, new int[]{380, 440, 440, 380}, true);
        stableFieldMap.drawPolygon(new int[]{1651-520, 1651-550, 1651-600, 1651-570}, new int[]{290, 240, 270, 320}, true);
        stableFieldMap.drawPolygon(new int[]{1651-520, 1651-550, 1651-600, 1651-570}, new int[]{531, 581, 551, 501}, true);

        fieldMap = new UpdatableAndExpandableFieldMap(stableFieldMap.getMapX(), stableFieldMap.getMapY(), stableFieldMap, new FieldMap(stableFieldMap.getMapX(), stableFieldMap.getMapY()), 40);
    }

    /**
     * Tests a trajectory against the fieldMap to see whether a robot of (whatever) width can path successfully
     *
     * @param trajectory
     * @return
     */
    public static boolean testTrajectory(Trajectory trajectory){
        if(trajectory == null)
            return false;

        Pose2d prevState = trajectory.sample(0).poseMeters;

        for(int t = 1; t*.02 < trajectory.getTotalTimeSeconds() + .02; t++){
            Pose2d currentState = trajectory.sample(t*.02).poseMeters;
//            if(Bresenham.drawLine(fieldMap.getCurrentMap(), (int)(prevState.getX()*100), (int)(prevState.getY()*100), (int)(currentState.getX()*100), (int)(currentState.getY()*100), false))
            if(fieldMap.getCurrentMap().checkPixelHasObjectOrOffMap((int)(currentState.getX()*100), (int)(currentState.getY()*100)))
                return false;

            prevState = currentState;
        }

        return true;
    }

    public static TimestampTranslation2d returnCollisionStart(Trajectory trajectory){
        Pose2d prevState = trajectory.sample(0).poseMeters;

        for(int t = 1; t*.02 < trajectory.getTotalTimeSeconds() + .02; t++){
            Pose2d currentState = trajectory.sample(t*.02).poseMeters;
            Translation2d result = Bresenham.lineReturnCollision(fieldMap.getCurrentMap(), (int)(prevState.getX()*100), (int)(prevState.getY()*100), (int)(currentState.getX()*100), (int)(currentState.getY()*100));

            if(result != null)
                return new TimestampTranslation2d(t*.02, result);

            prevState = currentState;
        }
        return null;
    }

    public static TimestampTranslation2d returnCollisionEnd(Trajectory trajectory, TimestampTranslation2d timestampTranslation2d){
//        System.out.println("Testing position: "+timestampTranslation2d.getTranslation2d()+" at time: "+timestampTranslation2d.getTimestamp());

        Pose2d prevState = trajectory.sample(timestampTranslation2d.getTimestamp()).poseMeters;

        for(int t = (int)(timestampTranslation2d.getTimestamp()*50) + 1; t*.02 < trajectory.getTotalTimeSeconds() + .02; t++){
            Pose2d currentState = trajectory.sample(t*.02).poseMeters;

//            System.out.println("Testing line: "+prevState+" to: "+currentState);

            int[] result =
                    Bresenham.lineReturnCollisionInverted(
                            fieldMap.getCurrentMap(),
                            (int)(prevState.getX()*100),
                            (int)(prevState.getY()*100),
                            (int)(currentState.getX()*100),
                            (int)(currentState.getY()*100),
                            false
                    );

//            System.out.println(result);

            if(result != null)
                return new TimestampTranslation2d(t*.02, new Translation2d(result[0], result[1]));

            prevState = currentState;
        }

        return timestampTranslation2d;
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

        double beforeTime = System.nanoTime();

        autopathTrajectory = AutopathAlgorithm.calculateAutopath(autopathTargetPosition);

        System.out.println("Time taken "+(System.nanoTime()-beforeTime)/1000000000);

        List<Rotation2d> autopathHeadings = new ArrayList<>();
        //TODO create headings
        // for now I'll make it use the current robot rotation
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


    public static class TimestampTranslation2d{
        private double timestamp;
        private Translation2d translation2d;

        public TimestampTranslation2d(double timestamp, Translation2d translation2d){
            this.timestamp = timestamp;
            this.translation2d = translation2d;
        }

        public double getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(double timestamp) {
            this.timestamp = timestamp;
        }

        public Translation2d getTranslation2d() {
            return translation2d;
        }

        public void setTranslation2d(Translation2d translation2d) {
            this.translation2d = translation2d;
        }
    }
}