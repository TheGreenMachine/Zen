package com.team1816.season.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
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
    private final IGreenMotor bridgeMotor;

    /**
     * Properties
     */
    public final double intakeSpeed;
    public final double bridgeSpeed;

    /**
     * Logging
     */
    private DoubleLogEntry intakeVelocityLogger;
    private DoubleLogEntry bridgeVelocityLogger;

    /**
     * States
     */
    private COLLECTOR_STATE desiredState = COLLECTOR_STATE.STOP;
    private double intakeVelocity = 0;
    private double bridgeVelocity = 0;
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
        bridgeMotor = factory.getMotor(NAME, "bridgeMotor");

        intakeSpeed = factory.getConstant(NAME, "intakeSpeed", -0.5);
        bridgeSpeed = factory.getConstant(NAME, "bridgeSpeed", -0.5);

        if (Constants.kLoggingRobot) {
            intakeVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Collector/intakeVelocity");
            bridgeVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Collector/bridgeVelocity");
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
        bridgeVelocity = bridgeMotor.getSensorVelocity(0);

        if (robotState.actualCollectorState != desiredState) {
            robotState.actualCollectorState = desiredState;
        }

        if (Constants.kLoggingRobot) {
            intakeVelocityLogger.append(intakeVelocity);
            bridgeVelocityLogger.append(bridgeVelocity);
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
                case STOP -> {
                    intakeMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
                    bridgeMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
                }
                case INTAKE -> {
                    intakeMotor.set(GreenControlMode.PERCENT_OUTPUT, intakeSpeed);
                    bridgeMotor.set(GreenControlMode.PERCENT_OUTPUT, bridgeSpeed);
                }
            }
        }
    }

    @Override
    public void zeroSensors() {

    }

    @Override
    public void stop() {

    }

    /**
     * Tests the collector subsystem, returns true if tests passed
     *
     * @return true if tests passed
     */
    @Override
    public boolean testSubsystem() {
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
     * Returns the bridge motor of the collector velocity
     *
     * @return bridge velocity
     */
    public double getBridgeVelocity() {
        return bridgeVelocity;
    }

    /**
     * Base enum for collector
     */
    public enum COLLECTOR_STATE {
        STOP,
        INTAKE,
    }
}