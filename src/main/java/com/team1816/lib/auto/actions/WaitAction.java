package com.team1816.lib.auto.actions;

import com.team1816.lib.variableInputs.Numeric;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Action to wait for a given amount of time To use this Action, call runAction(new WaitAction(your_time))
 *
 * @see WaitAction
 */
public class WaitAction implements AutoAction {

    /**
     * State: duration of action
     */
    private double mTimeToWait;
    /**
     * State: start time of action
     */
    private double mStartTime;

    private final String mSmartDashboardTextViewLabel;
    private final Numeric mSmartDashboardNumeric;

    /**
     * Instantiates a WaitAction with a time to stop
     *
     * @param timeToWait
     */
    public WaitAction(double timeToWait) {
        mTimeToWait = timeToWait;
        mSmartDashboardTextViewLabel = null;
        mSmartDashboardNumeric = null;
    }

    /**
     * Instantiates a WaitAction with a smart dashboard text view label.
     *
     * This constructor allows the action to update whenever the drive
     * team decides to while running the robot.
     *
     * @param smartDashboardTextViewLabel
     */
    public WaitAction(String smartDashboardTextViewLabel) {
        mSmartDashboardTextViewLabel = smartDashboardTextViewLabel;
        mSmartDashboardNumeric = null;
    }

    /**
     * Instantiates a WaitAction with a numeric input system.
     *
     * This constructor allows the action to pull updated numeric data
     * from the smartdashboard.
     *
     * @param smartDashboardNumeric
     */
    public WaitAction(Numeric smartDashboardNumeric) {
        mSmartDashboardNumeric = smartDashboardNumeric;
        mSmartDashboardTextViewLabel = null;
    }

    /**
     * Records start time and begins the action
     *
     * @see AutoAction#start()
     */
    @Override
    public void start() {
        if (mSmartDashboardTextViewLabel != null) {
            mTimeToWait = SmartDashboard.getNumber(mSmartDashboardTextViewLabel, 0.0);
        }

        if (mSmartDashboardNumeric != null) {
            mTimeToWait = mSmartDashboardNumeric.toNumber();
        }

        mStartTime = Timer.getFPGATimestamp();
    }

    /**
     * Nonexistant
     *
     * @see AutoAction#update()
     */
    @Override
    public void update() {
    }

    /**
     * Checks if the duration to wait has passed
     *
     * @return isFinished
     * @see AutoAction#isFinished()
     */
    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - mStartTime >= mTimeToWait;
    }

    /**
     * Standard verification cleanup for the series action
     *
     * @see AutoAction#done()
     */
    @Override
    public void done() {
    }
}
