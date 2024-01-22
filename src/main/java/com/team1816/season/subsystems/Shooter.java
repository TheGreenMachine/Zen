package com.team1816.season.subsystems;

import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.hardware.factory.SensorFactory;
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
    private final IGreenMotor shootMotor;
    private final DigitalInput noteSensor;

    /**
     * Properties
     */
    public final double shootPower;

    /**
     * States
     */
    private ROLLER_STATE desiredShootState = ROLLER_STATE.STOP;
    private boolean shootOutputsChanged = false;

    /**
     * Base constructor needed to instantiate a shooter
     *
     * @param inf Infrastructure
     * @param rs  RobotState
     */
    public Shooter(Infrastructure inf, RobotState rs) {
        super(NAME, inf, rs);
        shootMotor = factory.getMotor(NAME, "shootMotor");
        noteSensor = new DigitalInput((int) factory.getConstant(NAME, "noteSensorChannel", 0));

        shootPower = factory.getConstant(NAME, "shootPower", 0.70);
    }

    /**
     * Sets the desired state of the shooter
     *
     * @param desiredShootState SHOOT_STATE
     */
    public void setDesiredShootState(ROLLER_STATE desiredShootState) {
        this.desiredShootState = desiredShootState;
        shootOutputsChanged = true;
    }

    /**
     * Reads actual outputs from shoot motor
     *
     * @see Subsystem#readFromHardware()
     */
    @Override
    public void readFromHardware() {
    }

    /**
     * Writes outputs to shoot motor
     *
     * @see Subsystem#writeToHardware()
     */
    @Override
    public void writeToHardware() {
        if (shootOutputsChanged) {
            shootOutputsChanged = false;
            switch (desiredShootState) {
                case STOP -> {
                    shootMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
                }
                case SHOOT_SPEAKER -> {
                    shootMotor.set(GreenControlMode.PERCENT_OUTPUT, shootPower);
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

    @Override
    public boolean testSubsystem() {
        return false;
    }

    /**
     * Returns the desired shoot state
     *
     * @return desired shoot state
     */
    public ROLLER_STATE getDesiredShootState() {
        return desiredShootState;
    }

    /**
     * Shooter enum
     */
    public enum ROLLER_STATE {
        STOP(0),
        SHOOT_SPEAKER(factory.getConstant(NAME, "speakerShootSpeed", 0.70)),
        SHOOT_AMP(factory.getConstant(NAME, "ampShootSpeed", 0.40));
        final double velocity;
        ROLLER_STATE (double velocity) {
            this.velocity = velocity;
        }
        public boolean inDesiredSpeedRange (double actualVelocity) {
            return actualVelocity < 1.1 * velocity && actualVelocity > 0.9 * velocity;
        }
    }
    public enum FEEDER_STATE {
        STOP,
        SHOOT_SPEAKER,
        SHOOT_AMP,
        REV_SPEAKER,
        REV_AMP
    }
}
