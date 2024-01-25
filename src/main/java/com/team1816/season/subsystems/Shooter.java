package com.team1816.season.subsystems;

import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DigitalInput;

public class Shooter extends Subsystem {

    /**
     * Name
     */
    private static final String NAME = "shooter";

    /**
     * Components
     */
    private final IGreenMotor rollerMotor;
    private final IGreenMotor feederMotor;
    private final DigitalInput noteSensor;

    /**
     * Properties
     */
    // public final double shootPower;

    /**
     * States
     */
    private ROLLER_STATE desiredRollerState = ROLLER_STATE.STOP;
    private FEEDER_STATE desiredFeederState = FEEDER_STATE.STOP;
    private boolean rollerOutputsChanged = false;
    private boolean feederOutputsChanged = false;
    private double actualRollerVelocity = 0;
    private double actualFeederVelocity = 0;
    private double rollerCurrentDraw;
    private double feederCurrentDraw;

    /**
     * Constants
     */
    private static final double velocityErrorMargin = factory.getConstant(NAME, "velocityErrorMargin", 0.1);
    private static final double rollerSpeakerShootSpeed = factory.getConstant(NAME, "rollerSpeakerShootSpeed", 0.70);
    private static final double rollerAmpShootSpeed = factory.getConstant(NAME, "rollerAmpShootSpeed", 0.40);
    private final double feederSpeakerShootSpeed = factory.getConstant(NAME, "feederSpeakerShootSpeed", 0.70);
    private final double feederAmpShootSpeed = factory.getConstant(NAME, "feederAmpShootSpeed", 0.40);

    /**
     * Logging
     */
    private DoubleLogEntry actualRollerVelocityLogger;
    private DoubleLogEntry actualFeederVelocityLogger;
    private DoubleLogEntry rollerCurrentDrawLogger;
    private DoubleLogEntry feederCurrentDrawLogger;
    private DoubleLogEntry desiredRollerVelocityLogger;
    private DoubleLogEntry desiredFeederVelocityLogger;

    /**
     * Base constructor needed to instantiate a shooter
     *
     * @param inf Infrastructure
     * @param rs  RobotState
     */
    public Shooter(Infrastructure inf, RobotState rs) {
        super(NAME, inf, rs);
        rollerMotor = factory.getMotor(NAME, "rollerMotor");
        feederMotor = factory.getMotor(NAME, "feederMotor");
        noteSensor = new DigitalInput((int) factory.getConstant(NAME, "noteSensorChannel", 0));

        // shootPower = factory.getConstant(NAME, "shootPower", 0.70);

        if (Constants.kLoggingRobot) {
            actualRollerVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/actualRollerVelocity");
            actualFeederVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/actualFeederVelocity");
            rollerCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/rollerMotorCurrentDraw");
            feederCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/feederMotorCurrentDraw");
            desiredRollerVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/desiredRollerVelocity");
            desiredFeederVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/desiredFeederVelocity");
        }
    }

    /**
     * Sets the desired state of the roller
     *
     * @param desiredRollerState ROLLER_STATE
     */
    public void setDesiredRollerState(ROLLER_STATE desiredRollerState) {
        this.desiredRollerState = desiredRollerState;
        rollerOutputsChanged = true;
    }

    /**
     * Sets the desired state of the feeder
     *
     * @param desiredFeederState FEEDER_STATE
     */
    public void setDesiredFeederState(FEEDER_STATE desiredFeederState) {
        this.desiredFeederState = desiredFeederState;
        feederOutputsChanged = true;
    }

    /**
     * Sets the desired state of the roller and feeder
     *
     * @param desiredRollerState ROLLER_STATE
     * @param desiredFeederState FEEDER_STATE
     */
    public void setDesiredState(ROLLER_STATE desiredRollerState, FEEDER_STATE desiredFeederState) {
        this.desiredRollerState = desiredRollerState;
        this.desiredFeederState = desiredFeederState;

        rollerOutputsChanged = true;
        feederOutputsChanged = true;
    }

    /**
     * Reads actual outputs from shooter motors
     *
     * @see Subsystem#readFromHardware()
     */
    @Override
    public void readFromHardware() {
        actualRollerVelocity = rollerMotor.getSensorVelocity(0);
        actualFeederVelocity = feederMotor.getSensorVelocity(0);
        rollerCurrentDraw = rollerMotor.getMotorOutputCurrent();
        feederCurrentDraw = feederMotor.getMotorOutputCurrent();

        if (robotState.actualRollerState != desiredRollerState) {
            robotState.actualRollerState = desiredRollerState;
        }

        if (robotState.actualFeederState != desiredFeederState) {
            robotState.actualFeederState = desiredFeederState;
        }

        if (robotState.isBeamBreakTriggered != isBeamBreakTriggered()) {
            robotState.isBeamBreakTriggered = isBeamBreakTriggered();
        }

        if (Constants.kLoggingRobot) {
            actualRollerVelocityLogger.append(actualRollerVelocity);
            actualFeederVelocityLogger.append(actualFeederVelocity);
            rollerCurrentDrawLogger.append(rollerCurrentDraw);
            feederCurrentDrawLogger.append(feederCurrentDraw);
        }
    }

    /**
     * Writes outputs to shooter motors
     *
     * @see Subsystem#writeToHardware()
     */
    @Override
    public void writeToHardware() {
        if (rollerOutputsChanged) {
            rollerOutputsChanged = false;
            double desiredRollerVelocity = 0;
            switch (desiredRollerState) {
                case STOP -> {
                    desiredRollerVelocity = 0;
                }
                case SHOOT_SPEAKER -> {
                    desiredRollerVelocity = rollerSpeakerShootSpeed;
                }
                case SHOOT_AMP -> {
                    desiredRollerVelocity = rollerAmpShootSpeed;
                }
            }
            rollerMotor.set(GreenControlMode.PERCENT_OUTPUT, desiredRollerVelocity);
            desiredRollerVelocityLogger.append(desiredRollerVelocity);
        }
        if (feederOutputsChanged) {
            feederOutputsChanged = false;
            double desiredFeederVelocity = 0;
            switch (desiredFeederState) {
                case STOP -> {
                    desiredFeederVelocity = 0;
                }
                case SHOOT_SPEAKER -> {
                    desiredFeederVelocity = feederSpeakerShootSpeed;
                }
                case SHOOT_AMP -> {
                    desiredFeederVelocity = feederAmpShootSpeed;
                }
            }
            feederMotor.set(GreenControlMode.PERCENT_OUTPUT, desiredFeederVelocity);
            desiredFeederVelocityLogger.append(desiredFeederVelocity);
        }
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

    /**
     * Returns whether the beam break is triggered
     *
     * @return whether the beam break is triggered
     */
    public boolean isBeamBreakTriggered() {
        return !noteSensor.get();
    }

    /**
     * Returns the desired roller state
     *
     * @return desired roller state
     */
    public ROLLER_STATE getDesiredRollerState() {
        return desiredRollerState;
    }

    /**
     * Returns the desired feeder state
     *
     * @return desired feeder state
     */
    public FEEDER_STATE getDesiredFeederState() {
        return desiredFeederState;
    }

    /**
     * Roller enum
     */
    public enum ROLLER_STATE {
        STOP(0),
        SHOOT_SPEAKER(rollerSpeakerShootSpeed),
        SHOOT_AMP(rollerAmpShootSpeed);
        final double velocity;
        ROLLER_STATE (double velocity) {
            this.velocity = velocity;
        }
        public boolean inDesiredSpeedRange (double actualVelocity) {
            return actualVelocity < (1+velocityErrorMargin) * velocity && actualVelocity > (1-velocityErrorMargin) * velocity;
        }
    }

    /**
     * Feeder enum
     */
    public enum FEEDER_STATE {
        STOP,
        SHOOT_SPEAKER,
        SHOOT_AMP
    }
}
