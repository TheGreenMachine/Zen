package com.team1816.season.subsystems;

import com.ctre.phoenix.Util;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.hardware.components.motor.GhostMotor;
import com.team1816.lib.hardware.components.motor.IGreenMotor;
import com.team1816.lib.hardware.components.motor.LazyTalonFX;
import com.team1816.lib.hardware.components.motor.configurations.GreenControlMode;
import com.team1816.lib.subsystems.Subsystem;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.Robot;
import com.team1816.season.autoaim.AutoAimUtil;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;

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

//    private final CANcoder pivotCancoder;

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

    private boolean correctingAutoAim = false;
    private double autoAimTargetDegrees = 0;
    private double autoAimCorrectionRotations = 0;

    private double actualRollerVelocity = 0;
    private double actualFeederVelocity = 0;

    private double rollerCurrentDraw;
    private double feederCurrentDraw;
    private double pivotCurrentDraw;

    private double desiredPivotPosition = 0;
    private double actualPivotPosition = 0;
    private double actualPivotDegrees = 0;



    /**
     * Constants
     */
    private static final double velocityErrorMargin = factory.getConstant(NAME, "velocityErrorMargin", 0.1);
    private static final double velocityErrorMarginAutoAim = factory.getConstant(NAME, "velocityErrorMargin", 0.02);
    private static final double autoAimDegreeTolerance = factory.getConstant(NAME, "autoAimDegreeTolerance", 2);
    private static final double rollerSpeakerShootSpeed = factory.getConstant(NAME, "rollerSpeakerShootSpeed", 0.70);
    private static final double rollerEjectShootSpeed = factory.getConstant(NAME, "rollerEjectShootSpeed", 0.3);

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

//        pivotCancoder = factory.getCanCoder(NAME, "Pivot", factory.getConstant(NAME, "canCoderOffset", -0.5));

        noteSensor = new DigitalInput((int) factory.getConstant(NAME, "noteSensorChannel", 9));

        rollerMotor.selectPIDSlot(1);
        pivotMotor.selectPIDSlot(2);

        robotState.pivotArm.setColor(new Color8Bit(Color.kDarkBlue));

        if (RobotBase.isSimulation()) {
            pivotMotor.setMotionProfileMaxVelocity(12 / 0.05);
            pivotMotor.setMotionProfileMaxAcceleration(12 / 0.08);
            ((GhostMotor) pivotMotor).setMaxVelRotationsPerSec(240);
        }

        if (Constants.kLoggingRobot) {
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/actualPivotPosition"), pivotMotor::getSensorPosition);
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/actualRollerVelocity"), rollerMotor::getSensorVelocity);
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/actualFeederVelocity"), feederMotor::getSensorVelocity);
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/rollerMotorCurrentDraw"), rollerMotor::getMotorOutputCurrent);
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/feederMotorCurrentDraw"), feederMotor::getMotorOutputCurrent);
            GreenLogger.addPeriodicLog(new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/pivotMotorCurrentDraw"), pivotMotor::getMotorOutputCurrent);

            desStatesLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Pivot/desiredPivotPosition");
            desiredRollerVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Roller/desiredRollerVelocity");
            desiredFeederVelocityLogger = new DoubleLogEntry(DataLogManager.getLog(), "Shooter/Feeder/desiredFeederVelocity");

            beamBreakLogger = new BooleanLogEntry(DataLogManager.getLog(), "Shooter/Feeder/beamBreakTriggered");
        }
    }

    public void enableStatorCurrentLimit() {
        if(RobotBase.isReal()) {
            ((LazyTalonFX) rollerMotor).enableStatorCurrentLimit(true);
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
        SmartDashboard.putNumber("ActualDegrees", actualPivotDegrees);
        SmartDashboard.putNumber("TargetDegrees", autoAimTargetDegrees);
        SmartDashboard.putNumber("correctionDegrees", autoAimCorrectionRotations / Constants.motorRotationsPerDegree);

        actualPivotPosition = pivotMotor.getSensorPosition();
//        actualPivotDegrees = (pivotCancoder.getPosition().getValueAsDouble() / Constants.cancoderRotationsPerDegree);

        actualRollerVelocity = rollerMotor.getSensorVelocity();
        actualFeederVelocity = feederMotor.getSensorVelocity();

        rollerCurrentDraw = rollerMotor.getMotorOutputCurrent();
        feederCurrentDraw = feederMotor.getMotorOutputCurrent();
        pivotCurrentDraw = pivotMotor.getMotorOutputCurrent();

        if (robotState.actualPivotState == PIVOT_STATE.AUTO_AIM) {
            if (correctingAutoAim) {
                if (!MathUtil.isNear(autoAimTargetDegrees, actualPivotDegrees, autoAimDegreeTolerance)) { //This tolerance needs to be calc'd in auto aim util
                    autoAimCorrectionRotations =
                            (autoAimTargetDegrees - actualPivotDegrees) * Constants.motorRotationsPerDegree;

                    autoAimCorrectionRotations = MathUtil.inputModulus(autoAimCorrectionRotations, 0, 1);

                    robotState.readyToShoot = autoAimCorrectionRotations < 0.01;
                } else {
                    robotState.readyToShoot = true;
                }
            } else {
                robotState.readyToShoot = false;
            }
            if (RobotBase.isReal()) {
//                correctingAutoAim = pivotMotor.get_ClosedLoopOutput() <= 0.02 + robotState.pivotLoopIncrement && !pivotCancoder.getFault_BadMagnet().getValue(); //Under 6%, TODO put into yaml later
            }
        }

        if (robotState.actualRollerState != desiredRollerState) {
            //jank
            if ((desiredRollerState != ROLLER_STATE.STOP) && desiredRollerState.inDesiredSpeedRange(actualRollerVelocity)) {
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

        double angleToApply = robotState.pivotBaseAngle - (pivotMotor.getSensorPosition() * degreesPerMotorRotations);
        robotState.pivotArm.setAngle(Rotation2d.fromDegrees(angleToApply));

        if (Constants.kLoggingRobot) {
            ((DoubleLogEntry) desStatesLogger).append(desiredPivotPosition);

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
                    // Ternary, is shuttling, need to get shuttle speed
                    desiredRollerVelocity = rollerSpeakerShootSpeed;
                }
                case EJECT -> {
                    desiredRollerVelocity = rollerEjectShootSpeed;
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
                case MANUAL_TRANSFER -> {
                    desiredFeederVelocity = feederIntakeSpeed;
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
                    Optional<Double> shooterAngle = AutoAimUtil.getShooterAngle(new Translation2d(robotState.allianceColor == com.team1816.lib.auto.Color.BLUE ? Constants.blueSpeakerX : Constants.redSpeakerX, Constants.speakerY).getDistance(robotState.fieldToVehicle.getTranslation()));
                    if(shooterAngle.isPresent()){
                        desiredPivotPosition =
                                (Math.PI-shooterAngle.get())
                                * Constants.motorRotationsPerRadians
                                - pivotNeutralPosition;
                        autoAimTargetDegrees = -(desiredPivotPosition / Constants.motorRotationsPerDegree);

                        if (correctingAutoAim) {
                            desiredPivotPosition += autoAimCorrectionRotations;
                        }

                    } else {
                        desiredPivotPosition = pivotNeutralPosition;
                        correctingAutoAim = false;
                    }
                }
                case LASER -> {
                    desiredPivotPosition = (50) * Constants.motorRotationsPerDegree - pivotNeutralPosition;
                }
            }
            //System.out.println(desiredPivotPosition);
            pivotMotor.set(GreenControlMode.MOTION_MAGIC_EXPO, MathUtil.clamp(desiredPivotPosition, 1.5, 35));
        }
    }

    public double getActualPivotPosition () {
        return pivotMotor.getSensorPosition();
    }

    @Override
    public void zeroSensors() {
        pivotMotor.setSensorPosition(0, 50);
//        pivotCancoder.setPosition(0, 50);
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

        EJECT(rollerEjectShootSpeed),
        SHOOT_DISTANCE(90),
        SHOOT_AMP(rollerAmpShootSpeed);


        final double velocity;

        ROLLER_STATE (double velocity) {
            this.velocity = velocity;
        }

        public boolean inDesiredSpeedRange (double actualVelocity) {
            if (this == SHOOT_DISTANCE)
                return actualVelocity < (1+velocityErrorMarginAutoAim) * velocity && actualVelocity > (1-velocityErrorMarginAutoAim) * velocity;
            else
                return actualVelocity < (1+velocityErrorMargin) * velocity && actualVelocity > (1-velocityErrorMargin) * velocity;
        }
    }

    /**
     * Feeder enum
     */
    public enum FEEDER_STATE {
        STOP,
        SHOOT,
        TRANSFER,
        MANUAL_TRANSFER
    }

    /**
     * Pivot enum
     */
    public enum PIVOT_STATE {
        STOW,
        SHOOT_AMP,
        SHOOT_DISTANCE,
        AUTO_AIM,
        LASER
    }
}
