package com.team1816.season.subsystems;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    private DoubleLogEntry intakeCurrentDrawLogger;

    /**
     * States
     */
    private COLLECTOR_STATE desiredState = COLLECTOR_STATE.STOP;
    private double actualIntakePower = 0;
    private double desiredIntakePower = 0;
    private double intakeCurrentDraw = 0;
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

        SmartDashboard.putBoolean("Collector", intakeMotor.getMotorTemperature() < 55);

        if (Constants.kLoggingRobot) {

            desStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Collector/desiredIntakePower");
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Collector/actualIntakePower"), intakeMotor::getMotorOutputPercent);
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Collector/intakeCurrentDraw"), intakeMotor::getMotorOutputCurrent);
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
        actualIntakePower = intakeMotor.getMotorOutputPercent();
        intakeCurrentDraw = intakeMotor.getMotorOutputCurrent();

        if (robotState.actualCollectorState != desiredState) {
            robotState.actualCollectorState = desiredState;
        }

        if (intakeMotor.getMotorTemperature() >= 55) {
            SmartDashboard.putBoolean("Collector", false);
        }
        if (Constants.kLoggingRobot) {
            ((DoubleLogEntry) desStatesLogger).append(desiredIntakePower);
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
                    desiredIntakePower = 0;
                }
                case INTAKE -> {
                    desiredIntakePower = intakeSpeed;
                }
                case OUTTAKE -> {
                    desiredIntakePower = outtakeSpeed;
                }
            }
            intakeMotor.set(GreenControlMode.PERCENT_OUTPUT, desiredIntakePower);
        }
    }

    @Override
    public void zeroSensors() {
        //No implementation
    }

    @Override
    public void stop() {
        desiredState = COLLECTOR_STATE.STOP;
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
        return actualIntakePower;
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