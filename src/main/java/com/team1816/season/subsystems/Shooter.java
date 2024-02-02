package com.team1816.season.subsystems;

import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.LazyTalonFX;
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
    private final IGreenMotor pivotMotor;
    private final IGreenMotor pivotFollowMotor;

    private final DigitalInput noteSensor; // BeamBreak


    /**
     * States
     */
    private ROLLER_STATE desiredRollerState = ROLLER_STATE.STOP;
    private FEEDER_STATE desiredFeederState = FEEDER_STATE.STOP;
    private PIVOT_STATE desiredPivotState = PIVOT_STATE.STOW;

    private boolean rollerOutputsChanged = false;
    private boolean feederOutputsChanged = false;
    private boolean pivotOutputsChanged = false;

    private double actualRollerVelocity = 0;
    private double actualFeederVelocity = 0;

    private double rollerCurrentDraw;
    private double feederCurrentDraw;
    private double pivotCurrentDraw;

    private double desiredPivotPosition = 0;
    private double actualPivotPosition = 0;


    /**
     * Constants
     */
    private static final double velocityErrorMargin = factory.getConstant(NAME, "velocityErrorMargin", 0.1);
    private static final double rollerSpeakerShootSpeed = factory.getConstant(NAME, "rollerSpeakerShootSpeed", 0.70);
    private static final double rollerAmpShootSpeed = factory.getConstant(NAME, "rollerAmpShootSpeed", 0.40);

    private final double feederSpeakerShootSpeed = factory.getConstant(NAME, "feederSpeakerShootSpeed", 0.70);
    private final double feederAmpShootSpeed = factory.getConstant(NAME, "feederAmpShootSpeed", 0.40);
    private final double feederIntakeSpeed = factory.getConstant(NAME, "feederIntakeSpeed", 0.20);

    private final double pivotSpeakerShootPosition = factory.getConstant(NAME, "pivotSpeakerShootPosition", 0.5);
    private final double pivotAmpShootPosition = factory.getConstant(NAME, "pivotAmpShootPosition", 1.0);


    /**
     * Logging
     */
    private DoubleLogEntry actualRollerVelocityLogger;
    private DoubleLogEntry actualFeederVelocityLogger;

    private DoubleLogEntry rollerCurrentDrawLogger;
    private DoubleLogEntry feederCurrentDrawLogger;
    private DoubleLogEntry pivotCurrentDrawLogger;

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
        pivotMotor = factory.getMotor(NAME, "pivotMotor");

        // TODO We want the main and follower to oppose each other (Go opposite directions)
        // TODO Currently, the main and followers' positive power is opposite, so opposeMasterDirection will be false
        // TODO I added "opposeLeaderDirection" as a parameter of getFollowerMotor
        // TODO You guys will need to grab the result from YAML.
        pivotFollowMotor = factory.getFollowerMotor(NAME, "pivotFollowMotor", pivotMotor, false);
        noteSensor = new DigitalInput((int) factory.getConstant(NAME, "noteSensorChannel", 0));

        // shootPower = factory.getConstant(NAME, "shootPower", 0.70);

        if (Constants.kLoggingRobot) {
            desStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/desiredPivotPosition");
            actStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/actualPivotPosition");

            actualRollerVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/actualRollerVelocity");
            actualFeederVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/actualFeederVelocity");

            rollerCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/rollerMotorCurrentDraw");
            feederCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/feederMotorCurrentDraw");
            pivotCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/pivotMotorCurrentDraw");

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
     * Sets the desired state of the pivot
     *
     * @param desiredPivotState PIVOT_STATE
     */
    public void setDesiredPivotState(PIVOT_STATE desiredPivotState) {
        this.desiredPivotState = desiredPivotState;
        pivotOutputsChanged = true;
    }

    /**
     * Sets the desired state of the roller, feeder, and pivot
     *
     * @param desiredRollerState ROLLER_STATE
     * @param desiredFeederState FEEDER_STATE
     * @param desiredPivotState PIVOT_STATE
     */
    public void setDesiredState(ROLLER_STATE desiredRollerState, FEEDER_STATE desiredFeederState, PIVOT_STATE desiredPivotState) {
        this.desiredRollerState = desiredRollerState;
        this.desiredFeederState = desiredFeederState;
        this.desiredPivotState = desiredPivotState;

        rollerOutputsChanged = true;
        feederOutputsChanged = true;
        pivotOutputsChanged = true;
    }

    /**
     * Reads actual outputs from shooter motors
     *
     * @see Subsystem#readFromHardware()
     */
    @Override
    public void readFromHardware() {
        actualPivotPosition = pivotMotor.getSensorPosition(0);

        actualRollerVelocity = rollerMotor.getSensorVelocity(0);
        actualFeederVelocity = feederMotor.getSensorVelocity(0);

        rollerCurrentDraw = rollerMotor.getMotorOutputCurrent();
        feederCurrentDraw = feederMotor.getMotorOutputCurrent();
        pivotCurrentDraw = pivotMotor.getMotorOutputCurrent();

        if (robotState.actualRollerState != desiredRollerState) {
            robotState.actualRollerState = desiredRollerState;
        }

        if (robotState.actualFeederState != desiredFeederState) {
            robotState.actualFeederState = desiredFeederState;
        }

        if (robotState.actualPivotState != desiredPivotState) {
            robotState.actualPivotState = desiredPivotState;
        }

        if (robotState.isBeamBreakTriggered != isBeamBreakTriggered()) {
            robotState.isBeamBreakTriggered = isBeamBreakTriggered();
        }

        if (Constants.kLoggingRobot) {
            ((DoubleLogEntry) desStatesLogger).append(desiredPivotPosition);
            ((DoubleLogEntry) actStatesLogger).append(actualPivotPosition);

            actualRollerVelocityLogger.append(actualRollerVelocity);
            actualFeederVelocityLogger.append(actualFeederVelocity);

            rollerCurrentDrawLogger.append(rollerCurrentDraw);
            feederCurrentDrawLogger.append(feederCurrentDraw);
            pivotCurrentDrawLogger.append(pivotCurrentDraw);
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
            //TODO transfer state, if trying to transfer but the sensor is triggered STOP
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
                case INTAKE -> {
                    desiredFeederVelocity = feederIntakeSpeed;
                }
            }
            feederMotor.set(GreenControlMode.PERCENT_OUTPUT, desiredFeederVelocity);
            desiredFeederVelocityLogger.append(desiredFeederVelocity);
        }
        if (pivotOutputsChanged) {
            pivotOutputsChanged = false;
            switch (desiredPivotState) {
                case STOW -> {
                    desiredPivotPosition = 0;
                }
                case SHOOT_SPEAKER -> {
                    desiredPivotPosition = pivotSpeakerShootPosition;
                }
                case SHOOT_AMP -> {
                    desiredPivotPosition = pivotAmpShootPosition;
                }
            }
            pivotMotor.set(GreenControlMode.POSITION_CONTROL, desiredPivotPosition);
        }
    }

    @Override
    public void zeroSensors() {
        //TODO set pivot sensor pos to 0
    }

    @Override
    public void stop() {
        desiredRollerState = ROLLER_STATE.STOP;
        desiredFeederState = FEEDER_STATE.STOP;
    }

    @Override
    public boolean testSubsystem() {
        //TODO eventually.
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
     * Returns the desired pivot state
     *
     * @return desired pivot state
     */
    public PIVOT_STATE getDesiredPivotState() {
        return desiredPivotState;
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
        SHOOT_AMP,
        INTAKE
    }

    /**
     * Pivot enum
     */
    public enum PIVOT_STATE {
        STOW,
        SHOOT_SPEAKER,
        SHOOT_AMP
    }
}
