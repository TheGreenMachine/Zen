package com.team1816.season.subsystems;

import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.season.states.RobotState;
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
     * Reads actual outputs from shooter motors
     *
     * @see Subsystem#readFromHardware()
     */
    @Override
    public void readFromHardware() {
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
            switch (desiredRollerState) { // Everything in here is totally wrong right now
                case STOP -> {
                    rollerMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
                }
                case SHOOT_SPEAKER -> {
                    rollerMotor.set(GreenControlMode.PERCENT_OUTPUT, 0.70);
                }
            }
        }
        if (feederOutputsChanged) {
            feederOutputsChanged = false;
            switch (desiredFeederState) {

            }
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
        SHOOT_SPEAKER(factory.getConstant(NAME, "rollerSpeakerShootSpeed", 0.70)),
        SHOOT_AMP(factory.getConstant(NAME, "rollerAmpShootSpeed", 0.40));
        final double velocity;
        ROLLER_STATE (double velocity) {
            this.velocity = velocity;
        }
        public boolean inDesiredSpeedRange (double actualVelocity) {
            return actualVelocity < 1.1 * velocity && actualVelocity > 0.9 * velocity;
        }
    }

    /**
     * Feeder enum
     */
    public enum FEEDER_STATE {
        STOP,
        SHOOT_SPEAKER,
        SHOOT_AMP,
        REV_SPEAKER,
        REV_AMP
    }
}
