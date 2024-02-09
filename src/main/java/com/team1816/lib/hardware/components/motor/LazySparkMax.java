package com.team1816.lib.hardware.components.motor;

import com.ctre.phoenix.motorcontrol.*;
import com.revrobotics.*;
import com.team1816.lib.hardware.components.motor.configurations.*;
import com.team1816.lib.util.ConfigurationTranslator;
import com.team1816.lib.util.logUtil.GreenLogger;

public class LazySparkMax extends CANSparkMax implements IGreenMotor {
    private SparkPIDController pidController;
    private RelativeEncoder encoder;

    protected String name = "";
    protected GreenControlMode currentControlMode = GreenControlMode.PERCENT_OUTPUT;
    protected GreenControlMode lastControlMode = null;
    protected double lastSet = Double.NaN;
    protected int currentPIDSlot = 0;

    protected SparkLimitSwitch forwardLimitSwitch, reverseLimitSwitch = null;

    protected double peakOutputForward, peakOutputBackward = -0;

    protected double voltageForCompensation = 0;
    protected boolean voltageCompensationEnabled = false;

    /**
     * Create a new object to control a SPARK MAX motor Controller
     *
     * @param deviceNumber The device ID.
     * @param motorName The name of the motor
     */
    public LazySparkMax(int deviceNumber, String motorName) {
        super(deviceNumber, CANSparkLowLevel.MotorType.kBrushless);
        pidController = super.getPIDController();
        encoder = configureRelativeEncoder(FeedbackDeviceType.HALL_SENSOR);
        name = motorName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IGreenMotor.MotorType get_MotorType() {
        return IGreenMotor.MotorType.SparkMax;
    }

    @Override
    public void selectFeedbackSensor(FeedbackDeviceType deviceType) {
        encoder = configureRelativeEncoder(deviceType);
    }

    @Override
    public void selectFeedbackSensor(FeedbackDeviceType deviceType, int id) {
        selectFeedbackSensor(deviceType);
    }

    private RelativeEncoder configureRelativeEncoder(FeedbackDeviceType deviceType) {
        return super.getEncoder(
            ConfigurationTranslator.toSparkRelativeEncoderType(deviceType),
            42
        );
    }

    @Override
    public void configCurrentLimit(SupplyCurrentLimitConfiguration configuration) {
        configCurrentLimit((int) configuration.currentLimit);
    }

    @Override
    public void configCurrentLimit(int current) {
        super.setSmartCurrentLimit(current);
    }

    @Override
    public void set(GreenControlMode controlMode, double demand) {
        currentControlMode = controlMode;
        if (demand != lastSet || currentControlMode != lastControlMode) {
            lastSet = demand;
            lastControlMode = currentControlMode;
            pidController.setReference(
                demand,
                ConfigurationTranslator.toSparkMaxControlType(controlMode),
                currentPIDSlot
            );
        }
    }

    @Override
    public void configForwardLimitSwitch(boolean normallyOpen) {
        forwardLimitSwitch = super.getForwardLimitSwitch(normallyOpen ? SparkLimitSwitch.Type.kNormallyOpen : SparkLimitSwitch.Type.kNormallyClosed);
    }

    @Override
    public void configReverseLimitSwitch(boolean normallyOpen) {
        reverseLimitSwitch = super.getReverseLimitSwitch(normallyOpen ? SparkLimitSwitch.Type.kNormallyOpen : SparkLimitSwitch.Type.kNormallyClosed);
    }

    @Override
    public boolean isLimitSwitchClosed(LimitSwitchDirection direction) {
        return direction == LimitSwitchDirection.FORWARD ? forwardLimitSwitch.isPressed() : reverseLimitSwitch.isPressed();
    }


    @Override
    public void neutralOutput() {
        super.stopMotor();
    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {
        super.setIdleMode(neutralMode == NeutralMode.Brake ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast);
    }

    @Override
    public void setSensorPhase(boolean isInverted) {
        GreenLogger.log("Cannot invert sensor phase of a Spark in brushless mode");
        //If we ever have a spark controlling a brushed motor, the next line can be uncommented.
            //encoder.setInverted(isInverted); // This is NOT the same as a call to super.getInverted().
    }

    @Override
    public void configOpenLoopRampRate(double secondsNeutralToFull) {
        super.setOpenLoopRampRate(secondsNeutralToFull);
    }

    @Override
    public void configOpenLoopRampRate(double secondsNeutralToFull, int timeoutMs) {
        configOpenLoopRampRate(secondsNeutralToFull);
    }

    @Override
    public void configClosedLoopRampRate(double secondsNeutralToFull) {
        super.setClosedLoopRampRate(secondsNeutralToFull);
    }

    @Override
    public void config_PeakOutputForward(double percentOut) {
        peakOutputForward = percentOut;
        pidController.setOutputRange(peakOutputBackward, peakOutputForward, currentPIDSlot);
    }

    @Override
    public void config_PeakOutputForward(double percentOut, int timeoutMs) {
        config_PeakOutputForward(percentOut);
    }


    @Override
    public void config_PeakOutputReverse(double percentOut) {
        //Use negative values for backwards range
        peakOutputBackward = percentOut;
        pidController.setOutputRange(peakOutputBackward, peakOutputForward, currentPIDSlot);
    }

    @Override
    public void config_PeakOutputReverse(double percentOut, int timeoutMs) {
        config_PeakOutputReverse(percentOut);
    }


    /**
     * @see <a href="https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces">Documentation</a>
     */
    @Override
    public void config_NeutralDeadband(double deadbandPercent) {
        GreenLogger.log("Neutral deadband is only configurable through USB for Spark Max. Factory default is ±5%");

    }

    @Override
    public void enableClearPositionOnLimitF(boolean clearPosition, int timeoutMs) {
        //No functionality
    }

    @Override
    public void enableClearPositionOnLimitR(boolean clearPosition, int timeoutMs) {
        //No functionality
    }

    @Override
    public double getMotorOutputPercent() {
        return super.getAppliedOutput(); // We don't use get() because that is only supplied with set() and we skip over that for setReference()
    }

    @Override
    public double getMotorOutputVoltage() {
        return getMotorOutputPercent() * getBusVoltage(); //hate this but it's literally how BaseMotorController does it
    }

    @Override
    public double get_SupplyCurrent() {
        return super.getOutputCurrent();
    }

    @Override
    public double getSensorPosition(int closedLoopSlotID) {
        return encoder.getPosition();
    }

    @Override
    public double getSensorVelocity(int closedLoopSlotID) {
        return encoder.getVelocity();
    }

    @Override
    public void setSensorPosition(double sensorPosition) {
        encoder.setPosition(sensorPosition);
    }

    @Override
    public void setSensorPosition(double sensorPosition, int timeoutMs) {
        setSensorPosition(sensorPosition);
    }

    @Override
    public void enableLimitSwitches(boolean isEnabled) {
        //WHY DO LIMIT SWITCHES HAVE A TOGGLE PARAMETER BUT VOLTAGE COMPENSATION DOESNT
        if (forwardLimitSwitch == null || reverseLimitSwitch == null) {
            configForwardLimitSwitch(true);
            configReverseLimitSwitch(true);
        }

        forwardLimitSwitch.enableLimitSwitch(isEnabled);
        reverseLimitSwitch.enableLimitSwitch(isEnabled);
    }

    @Override
    public void configForwardSoftLimit(double forwardSoftLimit) {
        //free me from this torture. why does this use a float when every other rev method uses doubles?
        super.setSoftLimit(SoftLimitDirection.kForward, (float) forwardSoftLimit);
    }

    @Override
    public void configReverseSoftLimit(double reverseSoftLimit) {
        super.setSoftLimit(SoftLimitDirection.kReverse, (float) reverseSoftLimit);
    }

    @Override
    public void enableForwardSoftLimit(boolean isEnabled) {
        super.enableSoftLimit(SoftLimitDirection.kForward, isEnabled);
    }

    @Override
    public void enableReverseSoftLimit(boolean isEnabled) {
        super.enableSoftLimit(SoftLimitDirection.kReverse, isEnabled);
    }

    @Override
    public void enableSoftLimits(boolean isEnabled) {
        super.enableSoftLimit(SoftLimitDirection.kForward, isEnabled);
        super.enableSoftLimit(SoftLimitDirection.kReverse, isEnabled);
    }

    @Override
    public void set_kP(int pidSlotID, double kP) {
        pidController.setP(kP, pidSlotID);
    }

    @Override
    public void set_kI(int pidSlotID, double kI) {
        pidController.setI(kI, pidSlotID);
    }

    @Override
    public void set_kD(int pidSlotID, double kD) {
        pidController.setD(kD, pidSlotID);
    }

    @Override
    public void set_kF(int pidSlotID, double kF) {
        pidController.setFF(kF, pidSlotID);
    }

    @Override
    public void selectPIDSlot(int pidSlotID) {
        currentPIDSlot = pidSlotID;
    }

    @Override
    public void set_iZone(int pidSlotID, double iZone) {
        pidController.setIZone(iZone, pidSlotID);
    }

    @Override
    public void configAllowableErrorClosedLoop(int pidSlotID, double allowableError) {
        GreenLogger.log("Allowable error is only configurable through USB for Spark Max.");
    }

    @Override
    public void setPeakOutputClosedLoop(int pidSlotID, double peakOutput) {
        pidController.setOutputRange(-peakOutput, peakOutput, pidSlotID);
    }

    @Override
    public double get_ClosedLoopError() {
        // This isn't worth implementing as of 2023-24 because we aren't using rev motors for driving or anything that needs that much precision.
        // If anyone in the future wants to take a stab at it go ahead:
            //This is theoretically possible in a few ways
                // The actual firmware implementation is here https://docs.revrobotics.com/sparkmax/operating-modes/closed-loop-control but error is not retrievable
                    //if we figured out what pv meant then we could calc it ourselves
                // Could also reverse engineer the output from the PID equation but that could potentially be really slow
        return Double.NaN;
    }

    @Override
    public void setMotionProfileMaxVelocity(double maxVelocity) {
        pidController.setSmartMotionMaxVelocity(maxVelocity, currentPIDSlot);
    }

    @Override
    public void setMotionProfileMaxAcceleration(double maxAcceleration) {
        pidController.setSmartMotionMaxAccel(maxAcceleration, currentPIDSlot);
    }

    @Override
    public void configMotionCurve(MotionCurveType motionCurveType, int curveStrength) {
        pidController.setSmartMotionAccelStrategy(
            motionCurveType == MotionCurveType.S_CURVE ? SparkPIDController.AccelStrategy.kSCurve : SparkPIDController.AccelStrategy.kTrapezoidal,
            currentPIDSlot
        );
    }

    @Override
    public boolean hasResetOccurred() {
        GreenLogger.log("SparkMax does not track resets."); //Apparently tracks resets as a fault but I'm not implementing a method for that
        return false;
    }

    @Override
    public int getDeviceID() {
        return super.getDeviceId();
    }

    @Override
    public double getMotorOutputCurrent() {
        return super.getOutputCurrent();
    }

    @Override
    public GreenControlMode get_ControlMode() {
        return currentControlMode;
    }

    @Override
    public void follow(IGreenMotor leader, boolean opposeLeaderDirection) {
        if (leader instanceof LazySparkMax) {
            super.follow((CANSparkMax) leader, opposeLeaderDirection);
        } else {
            super.follow(ExternalFollower.kFollowerPhoenix, leader.getDeviceID(), opposeLeaderDirection);
        }
    }

    @Override
    public void restore_FactoryDefaults(int timeoutMs) {
        //This method doesn't do anything because sparkmax restorefactorydefaults also resets USB-exclusive settings and that's annoying to deal with
    }

    @Override
    public void configControlFramePeriod(ControlFrame controlFrame, int periodms) {
        super.setControlFramePeriodMs(periodms);
    }
}
