package com.team1816.season.subsystems;

import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.season.states.RobotState;
import edu.wpi.first.wpilibj.DigitalInput;

public class Shooter extends Subsystem {

    private static final String NAME = "shooter";

    /**
     * Components
     */
    private final IGreenMotor bridgeMotor;
    private final IGreenMotor shootMotor;
    private final DigitalInput noteSensor;

    /**
     * Constants
     */
    private final double bridgeIntakeSpeed = factory.getConstant(NAME, "bridgeIntakeSpeed", -0.5);
    private final double bridgeSpeakerSpeed = factory.getConstant(NAME, "bridgeSpeakerSpeed", 0.7);
    private final double bridgeAmpSpeed = factory.getConstant(NAME, "bridgeAmpSpeed", 0.4);
    private final double speakerSpeed = factory.getConstant(NAME, "speakerSpeed", 1);
    private final double ampSpeed = factory.getConstant(NAME, "ampSpeed", 0.2);

    /**
     * States
     */
    private SHOOTER_STATE desiredShooterState = SHOOTER_STATE.STOW;
    private SHOOTER_STATE actualShooterState = SHOOTER_STATE.STOW;
    private boolean outputsChanged = false;

    /**
     * Base parameters needed to instantiate a subsystem
     *
     * @param inf  Infrastructure
     * @param rs   RobotState
     */
    public Shooter(Infrastructure inf, RobotState rs) {
        super(NAME, inf, rs);
        bridgeMotor = factory.getMotor(NAME, "bridgeMotor");
        shootMotor = factory.getMotor(NAME, "shootMotor");
        noteSensor = new DigitalInput((int) factory.getConstant(NAME, "noteSensorID", -1));
    }

    @Override
    public void readFromHardware() {

    }

    @Override
    public void writeToHardware() {
        switch(desiredShooterState){
            case STOW -> {
                bridgeMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
                shootMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
            }
            case BRIDGE_INTAKE -> {
                bridgeMotor.set(GreenControlMode.PERCENT_OUTPUT, bridgeIntakeSpeed);
                shootMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
            }
            case SHOOT_SPEAKER -> {
                if(shootMotor.getSensorVelocity(0) >= speakerSpeed){
                    bridgeMotor.set(GreenControlMode.PERCENT_OUTPUT, bridgeSpeakerSpeed);
                    shootMotor.set(GreenControlMode.PERCENT_OUTPUT, speakerSpeed);
                } else {
                    bridgeMotor.set(GreenControlMode.PERCENT_OUTPUT, 0);
                    shootMotor.set(GreenControlMode.PERCENT_OUTPUT, speakerSpeed);
                }
            }
            case SHOOT_AMP -> {
                bridgeMotor.set(GreenControlMode.PERCENT_OUTPUT, bridgeAmpSpeed);
                shootMotor.set(GreenControlMode.PERCENT_OUTPUT, ampSpeed);
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

    public enum SHOOTER_STATE {
        STOW,
        BRIDGE_INTAKE,
        SHOOT_SPEAKER,
        SHOOT_AMP
    }

    public enum REV_STATE {
        IDLE,
        AMP,
        SPEAKER
    }
}