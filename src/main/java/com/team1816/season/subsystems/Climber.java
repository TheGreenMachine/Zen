package com.team1816.season.subsystems;

import com.google.inject.Inject;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;

public class Climber extends Subsystem {

    /**
     * Name
     */
    private static final String NAME = "climber";

    /**
     * Components
     */
    private final IGreenMotor climbMotor;

    /**
     * Properties
     */
    public final double slowClimbSpeed;
    public final double fastClimbSpeed;

    /**
     * Logging
     */
    private DoubleLogEntry climberCurrentDrawLogger;

    /**
     * States
     */
    private Climber.CLIMBER_STATE desiredState = Climber.CLIMBER_STATE.STOP;
    private double actualClimberVelocity = 0;
    private double desiredClimberVelocity = 0;
    private double climberCurrentDraw = 0;
    private boolean outputsChanged = false;

    /**
     * Base parameters needed to instantiate a subsystem
     *
     * @param inf  Infrastructure
     * @param rs   RobotState
     */
    @Inject
    public Climber(Infrastructure inf, RobotState rs) {
        super(NAME, inf, rs);
        climbMotor = factory.getMotor(NAME, "climbMotor");

        slowClimbSpeed = factory.getConstant(NAME, "slowClimbSpeed", 0.3);
        fastClimbSpeed = factory.getConstant(NAME, "fastClimbSpeed", 0.8);

        if (Constants.kLoggingRobot) {
            desStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Climber/desiredClimberVelocity");
            actStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Climber/actualClimberVelocity");
            climberCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Climber/climberCurrentDraw");
        }
    }

    /**
     * Sets the desired state of the climber
     *
     * @param desiredState CLIMBER_STATE
     */
    public void setDesiredState(Climber.CLIMBER_STATE desiredState) {
        this.desiredState = desiredState;
        outputsChanged = true;
    }

    /**
     * Reads actual outputs from climb motor
     *
     * @see Subsystem#readFromHardware()
     */
    @Override
    public void readFromHardware() {
        actualClimberVelocity = climbMotor.getSensorVelocity(0);
        climberCurrentDraw = climbMotor.getMotorOutputCurrent();

        if (robotState.actualClimberState != desiredState) {
            robotState.actualClimberState = desiredState;
        }

        if (Constants.kLoggingRobot) {
            ((DoubleLogEntry) actStatesLogger).append(actualClimberVelocity);
            ((DoubleLogEntry) desStatesLogger).append(desiredClimberVelocity);
            climberCurrentDrawLogger.append(climberCurrentDraw);
        }
    }

    /**
     * Writes outputs to climb motor
     *
     * @see Subsystem#writeToHardware()
     */
    @Override
    public void writeToHardware() {
        if (outputsChanged) {
            outputsChanged = false;
            switch (desiredState) {
                case STOP -> {
                    desiredClimberVelocity = 0;
                }
                case CLIMB_SLOW -> {
                    if (!DriverStation.isFMSAttached() || DriverStation.getMatchTime() < 20) {
                        desiredClimberVelocity = slowClimbSpeed;
                    }
                }
                case CLIMB_FAST -> {
                    if (!DriverStation.isFMSAttached() || DriverStation.getMatchTime() < 20) {
                        desiredClimberVelocity = fastClimbSpeed;
                    }
                }
            }
            climbMotor.set(GreenControlMode.PERCENT_OUTPUT, desiredClimberVelocity);
        }
    }

    @Override
    public void zeroSensors() {

    }

    @Override
    public void stop() {desiredState = Climber.CLIMBER_STATE.STOP;}

    /**
     * Tests the climber subsystem, returns true if tests passed
     *
     * @return true if tests passed
     */
    @Override
    public boolean testSubsystem() {
        //TODO make this once the rest of the subsystem is done and tested
        return false;
    }

    /**
     * Returns the desired climber state
     *
     * @return desired climber state
     */
    public Climber.CLIMBER_STATE getDesiredClimberState() {
        return desiredState;
    }

    /**
     * Returns the climb motor velocity
     *
     * @return climber velocity
     */
    public double getClimberVelocity() {
        return actualClimberVelocity;
    }

    /**
     * Base enum for climber
     */
    public enum CLIMBER_STATE {
        STOP,
        CLIMB_SLOW,
        CLIMB_FAST
    }
}
