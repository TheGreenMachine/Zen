package com.team1816.season.subsystems;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;

@Singleton
public class Collector extends Subsystem {

    /**
     * Name
     */
    private static final String NAME = "collector";

    /**
     * Components
     */
    private final IGreenMotor intakeMotor;

    /**
     * Properties
     */
    public final double intakeSpeed;
    public final double outtakeSpeed;

    /**
     * Logging
     */
    private DoubleLogEntry intakeVelocityLogger;
    //TODO Current draw logger!

    /**
     * States
     */
    private COLLECTOR_STATE desiredState = COLLECTOR_STATE.STOP;
    private double intakeVelocity = 0;
    private boolean outputsChanged = false;

    /**
     * Base parameters needed to instantiate a subsystem
     *
     * @param inf  Infrastructure
     * @param rs   RobotState
     */
    @Inject
    public Collector(Infrastructure inf, RobotState rs) {
        super(NAME, inf, rs);
        intakeMotor = factory.getMotor(NAME, "intakeMotor");

        intakeSpeed = factory.getConstant(NAME, "intakeSpeed", -0.5);
        outtakeSpeed = factory.getConstant(NAME, "outtakeSpeed", 0.25);

        if (Constants.kLoggingRobot) {
            //TODO initialize desStatesLogger and actStatesLogger (from Subsystem super class)
            intakeVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Collector/intakeVelocity");
        }
    }

    /**
     * Sets the desired state of the collector
     *
     * @param desiredState COLLECTOR_STATE
     */
    public void setDesiredState(COLLECTOR_STATE desiredState) {
        this.desiredState = desiredState;
        outputsChanged = true;
    }

    /**
     * Reads actual outputs from intake motor
     *
     * @see Subsystem#readFromHardware()
     */
    @Override
    public void readFromHardware() {
        intakeVelocity = intakeMotor.getSensorVelocity(0);

        if (robotState.actualCollectorState != desiredState) {
            robotState.actualCollectorState = desiredState;
        }

        if (Constants.kLoggingRobot) {
            intakeVelocityLogger.append(intakeVelocity);
        }
    }

    /**
     * Writes outputs to intake motor
     *
     * @see Subsystem#writeToHardware()
     */
    @Override
    public void writeToHardware() {
        if (outputsChanged) {
            outputsChanged = false;
            switch (desiredState) {
                //TODO change to variable assignment with a set() after the switch - reduces points of failure
                case STOP -> {
                    intakeMotor.set(GreenControlMode.VELOCITY_CONTROL, 0);
                }
                case INTAKE -> {
                    intakeMotor.set(GreenControlMode.VELOCITY_CONTROL, intakeSpeed);
                }
                case OUTTAKE -> {
                    intakeMotor.set(GreenControlMode.VELOCITY_CONTROL, outtakeSpeed);
                }
            }
        }
    }

    @Override
    public void zeroSensors() {
        //No implementation
    }

    @Override
    public void stop() {
        //TODO make this.
    }

    /**
     * Tests the collector subsystem, returns true if tests passed
     *
     * @return true if tests passed
     */
    @Override
    public boolean testSubsystem() {
        //TODO make this once the rest of the subsystem is done and tested
        return false;
    }

    /**
     * Returns the desired collector state
     *
     * @return desired collector state
     */
    public COLLECTOR_STATE getDesiredCollectorState() {
        return desiredState;
    }

    /**
     * Returns the intake motor of the collector velocity
     *
     * @return intake velocity
     */
    public double getIntakeVelocity() {
        return intakeVelocity;
    }

    /**
     * Base enum for collector
     */
    public enum COLLECTOR_STATE {
        STOP,
        INTAKE,
        OUTTAKE
    }
}