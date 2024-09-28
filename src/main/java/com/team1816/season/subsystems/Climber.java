package com.team1816.season.subsystems;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.Injector;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.input_handler.InputHandler;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.core.configuration.Constants;
import com.team1816.core.states.Orchestrator;
import com.team1816.core.states.RobotState;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;

@Singleton
public class Climber extends Subsystem {

    /**
     * Name
     */
    private static final String NAME = "climber";


    /**
     * Components
     */
    private final IGreenMotor climbMotor;
    private final Orchestrator orchestrator;
    /**
     * Properties
     */
    public final double slowClimbPower;
    public final double fastClimbPower;
    public final double reSpoolPower;
    public final double climbPosition;
    public final double climbThreshold;

    /**
     * Logging
     */
    private DoubleLogEntry climberCurrentDrawLogger;
    private StringLogEntry controlModeLogger;

    /**
     * States
     */
    private Climber.CLIMBER_STATE desiredState = Climber.CLIMBER_STATE.STOP;
    private double actualClimberOutput = 0;
    private double desiredClimberOutput = 0;
    private double climberCurrentDraw = 0;
    private GreenControlMode desiredControlMode = GreenControlMode.PERCENT_OUTPUT;
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
        orchestrator = Injector.get(Orchestrator.class);
        climbMotor = factory.getMotor(NAME, "climbMotor");

        slowClimbPower = factory.getConstant(NAME, "slowClimbPower", 0.3);
        fastClimbPower = factory.getConstant(NAME, "fastClimbPower", 0.8);
        reSpoolPower = factory.getConstant(NAME, "reSpoolPower", -0.5);
        climbPosition = factory.getConstant(NAME, "climbPosition", 20);
        climbThreshold = factory.getConstant(NAME, "climbThreshold", 15);


        if (Constants.kLoggingRobot) {
            desStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Climber/desiredClimberOutput");
            actStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Climber/actualClimberOutput");
            controlModeLogger = new StringLogEntry(DataLogManager.getLog(), "Climber/controlMode");
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Climber/climberCurrentDraw"), climbMotor::getMotorOutputCurrent);
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
        actualClimberOutput = desiredControlMode == GreenControlMode.PERCENT_OUTPUT ? climbMotor.getMotorOutputPercent() : climbMotor.getSensorPosition();
        climberCurrentDraw = climbMotor.getMotorOutputCurrent();

        if (robotState.actualClimberState != desiredState) {
            robotState.actualClimberState = desiredState;
        }

        if (desiredState == CLIMBER_STATE.PRECISE_TOP) {
            if (actualClimberOutput >= climbThreshold) {
                setDesiredState(CLIMBER_STATE.STOP);
                orchestrator.setControllerRumble(InputHandler.ControllerRole.OPERATOR, InputHandler.RumbleDirection.UNIFORM, 0.75);
            } else {
                orchestrator.setControllerRumble(InputHandler.ControllerRole.OPERATOR, InputHandler.RumbleDirection.UNIFORM, 0.2);
            }
        } else {
            orchestrator.stopRumble(InputHandler.ControllerRole.OPERATOR);
        }


        if (Constants.kLoggingRobot && isImplemented()) {
            ((DoubleLogEntry) actStatesLogger).append(actualClimberOutput);
            ((DoubleLogEntry) desStatesLogger).append(desiredClimberOutput);
            climberCurrentDrawLogger.append(climberCurrentDraw);
            controlModeLogger.append(desiredControlMode.name());
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
            desiredControlMode = desiredState == CLIMBER_STATE.PRECISE_TOP ? GreenControlMode.POSITION_CONTROL : GreenControlMode.PERCENT_OUTPUT;
            switch (desiredState) {
                case STOP -> {
                    desiredClimberOutput = 0;
                }
                case CLIMB_SLOW -> {
                        desiredClimberOutput = slowClimbPower;
                }
                case CLIMB_FAST -> {
                        desiredClimberOutput = fastClimbPower;
                }
                case RE_SPOOL -> {
                    if (!DriverStation.isFMSAttached()) {
                        desiredClimberOutput = reSpoolPower;
                    }
                }
                case PRECISE_TOP -> {
                    desiredClimberOutput = climbPosition;
                }
            }
            climbMotor.set(desiredControlMode, desiredClimberOutput);
        }
    }

    @Override
    public void zeroSensors() {
        climbMotor.setSensorPosition(0);
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
        return actualClimberOutput;
    }

    /**
     * Base enum for climber
     */
    public enum CLIMBER_STATE {
        STOP,
        CLIMB_SLOW,
        CLIMB_FAST,
        RE_SPOOL,

        PRECISE_TOP
    }
}
