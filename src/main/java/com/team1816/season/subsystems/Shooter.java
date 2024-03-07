package com.team1816.season.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.GhostMotor;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.season.autoaim.AutoAimUtil;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
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

    private final double feederShootSpeed = factory.getConstant(NAME, "feederSpeakerShootSpeed", 0.70);
    private final double feederIntakeSpeed = factory.getConstant(NAME, "feederIntakeSpeed", 0.20);

    private final double pivotAmpShootPosition = factory.getConstant(NAME, "pivotAmpShootPosition", 1.0);
    private final double pivotNeutralPosition = factory.getConstant(NAME, "pivotNeutralPosition", 1.0);
    private final double pivotDistanceShootPosition = factory.getConstant(NAME, "pivotDistanceShootPosition", 1.0);

    private final boolean opposeLeaderDirection = ((int) factory.getConstant(NAME, "invertFollowerMotor", 0)) == 1;

    private final double degreesPerMotorRotations = Constants.degreesPerMotorRotations;


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

    private BooleanLogEntry beamBreakLogger;


    /**
     * Base constructor needed to instantiate a shooter
     *
     * @param inf Infrastructure
     * @param rs  RobotState
     */
    @Inject
    public Shooter(Infrastructure inf, RobotState rs) {
        super(NAME, inf, rs);
        rollerMotor = factory.getMotor(NAME, "rollerMotor");
        feederMotor = factory.getMotor(NAME, "feederMotor");
        pivotMotor = factory.getMotor(NAME, "pivotMotor");
        pivotFollowMotor = factory.getFollowerMotor(NAME, "pivotFollowMotor", pivotMotor, opposeLeaderDirection);

        noteSensor = new DigitalInput((int) factory.getConstant(NAME, "noteSensorChannel", 0));

        rollerMotor.selectPIDSlot(1);
        pivotMotor.selectPIDSlot(2);

        robotState.pivotArm.setColor(new Color8Bit(Color.kDarkBlue));

        if (RobotBase.isSimulation()) {
            pivotMotor.setMotionProfileMaxVelocity(12 / 0.05);
            pivotMotor.setMotionProfileMaxAcceleration(12 / 0.08);
            ((GhostMotor) pivotMotor).setMaxVelRotationsPerSec(240);
        }

        if (Constants.kLoggingRobot) {
            desStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/desiredPivotPosition");
            actStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/actualPivotPosition");

            actualRollerVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/actualRollerVelocity");
            actualFeederVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/actualFeederVelocity");

            rollerCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/rollerMotorCurrentDraw");
            feederCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/feederMotorCurrentDraw");
            pivotCurrentDrawLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/pivotMotorCurrentDraw");

            desiredRollerVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/desiredRollerVelocity");
            desiredFeederVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/desiredFeederVelocity");

            beamBreakLogger = new BooleanLogEntry(DataLogManager.getLog(), "Shooter/Feeder/beamBreakTriggered");
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
     */
    public void setDesiredState(ROLLER_STATE desiredRollerState, FEEDER_STATE desiredFeederState) {
        setDesiredState(desiredRollerState, desiredFeederState, this.desiredPivotState);
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
            if (desiredRollerState != ROLLER_STATE.STOP && desiredRollerState.inDesiredSpeedRange(actualRollerVelocity)) {
                robotState.actualRollerState = desiredRollerState;
            } else if (desiredRollerState == ROLLER_STATE.STOP) {
                robotState.actualRollerState = desiredRollerState;
            }
        }

        if (robotState.actualFeederState != desiredFeederState) {
            robotState.actualFeederState = desiredFeederState;
        }

        if (robotState.actualPivotState != desiredPivotState) {
            robotState.actualPivotState = desiredPivotState;
        }

        if (robotState.isBeamBreakTriggered != isBeamBreakTriggered()) {
            robotState.isBeamBreakTriggered = isBeamBreakTriggered();
            feederOutputsChanged = true;
        }

        double angleToApply = robotState.pivotBaseAngle - (pivotMotor.getSensorPosition(0) * degreesPerMotorRotations);
        robotState.pivotArm.setAngle(Rotation2d.fromDegrees(angleToApply));

        if (Constants.kLoggingRobot) {
            ((DoubleLogEntry) desStatesLogger).append(desiredPivotPosition);
            ((DoubleLogEntry) actStatesLogger).append(actualPivotPosition);

            actualRollerVelocityLogger.append(actualRollerVelocity);
            actualFeederVelocityLogger.append(actualFeederVelocity);

            rollerCurrentDrawLogger.append(rollerCurrentDraw);
            feederCurrentDrawLogger.append(feederCurrentDraw);
            pivotCurrentDrawLogger.append(pivotCurrentDraw);

            beamBreakLogger.append(isBeamBreakTriggered());
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
                case SHOOT_DISTANCE -> {
                    desiredRollerVelocity = desiredRollerState.velocity;
                }
            }
            rollerMotor.set(GreenControlMode.VELOCITY_CONTROL, desiredRollerVelocity);
            desiredRollerVelocityLogger.append(desiredRollerVelocity);
        }
        if (feederOutputsChanged) {
            feederOutputsChanged = false;
            double desiredFeederVelocity = 0;
            switch (desiredFeederState) {
                case STOP -> {
                    desiredFeederVelocity = 0;
                }
                case SHOOT -> {
                    if (desiredRollerState.inDesiredSpeedRange(actualRollerVelocity)) {
                        desiredFeederVelocity = feederShootSpeed;
                    } else {
                        feederOutputsChanged = true; //Jank
                    }
                }
                case TRANSFER -> {
                    if (!isBeamBreakTriggered()) {
                        desiredFeederVelocity = feederIntakeSpeed;
                    }
                }
            }
            feederMotor.set(GreenControlMode.VELOCITY_CONTROL, desiredFeederVelocity);
            desiredFeederVelocityLogger.append(desiredFeederVelocity);
        }
        if (pivotOutputsChanged) {
            pivotOutputsChanged = false;
            switch (desiredPivotState) {
                case STOW -> {
                    desiredPivotPosition = pivotNeutralPosition;
                }
                case SHOOT_AMP -> {
                    desiredPivotPosition = pivotAmpShootPosition;
                }
                case SHOOT_DISTANCE -> {
                    desiredPivotPosition = pivotDistanceShootPosition; //Lil bit over because of possibility for overshoot
                }
                case AUTO_AIM -> {
                    pivotOutputsChanged = true;
                    if(AutoAimUtil.getShooterAngle(new Translation2d(Constants.blueSpeakerX, Constants.speakerY).getDistance(robotState.fieldToVehicle.getTranslation())).isPresent()){
                        desiredPivotPosition = AutoAimUtil.getShooterAngle(
                                new Translation2d(Constants.blueSpeakerX, Constants.speakerY).getDistance(robotState.fieldToVehicle.getTranslation())
                                    ).get()
                                * Constants.motorRotationsPerRadians
                                - pivotNeutralPosition;
                    } else {
                        desiredPivotPosition = pivotNeutralPosition;
                    }
                }

            }
            pivotMotor.set(GreenControlMode.MOTION_MAGIC_EXPO, desiredPivotPosition);
        }
    }

    @Override
    public void zeroSensors() {
        pivotMotor.setSensorPosition(0, 50);
    }

    public void setBraking(boolean braking) {
        pivotMotor.setNeutralMode(braking ? NeutralMode.Brake : NeutralMode.Coast);
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
        SHOOT_DISTANCE(68),
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
        SHOOT,
        TRANSFER
    }

    /**
     * Pivot enum
     */
    public enum PIVOT_STATE {
        STOW,
        SHOOT_AMP,
        SHOOT_DISTANCE,
        AUTO_AIM
    }
}
