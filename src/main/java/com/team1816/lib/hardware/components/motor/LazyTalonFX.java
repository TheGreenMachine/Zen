package com.team1816.lib.hardware.components.motor;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.*;
import com.team1816.lib.hardware.components.motor.configurations.*;
import com.team1816.lib.util.ConfigurationTranslator;
import com.team1816.lib.util.Util;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.wpilibj.DriverStation;

public class LazyTalonFX extends TalonFX implements IGreenMotor {
    protected double lastSet = Double.NaN;
    protected String name = "";
    protected ControlMode lastControlMode = null;

    protected Faults faults;
    protected StickyFaults stickyFaults;

    protected boolean isFollower;

    protected double arbitraryFeedForward = 0;

    protected TalonFXConfigurator configurator;
    protected TalonFXConfiguration configs;

    protected DutyCycleOut dutyCycle = new DutyCycleOut(0);
    protected VelocityDutyCycle velocity = new VelocityDutyCycle(0);
    protected PositionDutyCycle position = new PositionDutyCycle(0);
    protected MotionMagicDutyCycle motionMagic = new MotionMagicDutyCycle(0);
    protected Follower following = new Follower(0, false);
    protected NeutralOut neutral = new NeutralOut();
    protected StaticBrake brake = new StaticBrake();
    public LazyTalonFX(int deviceNumber, String motorName, String canBus) {
        super(deviceNumber, canBus);
        name = motorName;
        configurator = super.getConfigurator();
        configurator.refresh(configs);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MotorType get_MotorType() {
        return MotorType.TalonFX;
    }

    @Override
    public void selectFeedbackSensor(FeedbackDeviceType deviceType) {
        selectFeedbackSensor(deviceType, 0);
    }

    public void selectFeedbackSensor(FeedbackDeviceType deviceType, int closedLoopSlotID) {
        if (deviceType == FeedbackDeviceType.INTEGRATED_SENSOR) {
            configs.Feedback.withFeedbackSensorSource(FeedbackSensorSourceValue.RotorSensor);
        } else {
            configs.Feedback.withFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder);
        }
        configurator.apply(configs);
    }

    @Override
    public void configCurrentLimit(SupplyCurrentLimitConfiguration configuration) {
        configs.CurrentLimits
                .withSupplyCurrentLimit(configuration.currentLimit)
                .withSupplyCurrentLimitEnable(configuration.enable)
                .withSupplyCurrentThreshold(configuration.triggerThresholdCurrent)
                .withSupplyTimeThreshold(configuration.triggerThresholdTime);
        configurator.apply(configs);
    }

    @Override
    public void configCurrentLimit(int current) {
        configs.CurrentLimits
                .withSupplyCurrentLimit(current)
                .withSupplyCurrentLimitEnable(true)
                .withSupplyCurrentThreshold(0)
                .withSupplyTimeThreshold(0);
        configurator.apply(configs);
    }

    @Override
    public double getMotorOutputCurrent() {
        return super.getStatorCurrent().getValueAsDouble();
    }

    @Override
    public void set(GreenControlMode controlMode, double demand) {
        ControlMode mode = ConfigurationTranslator.toCTREControlMode(controlMode);
        if (demand != lastSet || mode != lastControlMode) {
            if (isFollower) {
                super.setControl(following);
            } else if (!super.hasResetOccurred()) {
                lastSet = demand;
                lastControlMode = mode;

                ControlRequest controlRequest;
                switch(controlMode){
                    case PERCENT_OUTPUT -> controlRequest = dutyCycle.withOutput(demand);
                    case VELOCITY_CONTROL -> controlRequest = velocity.withVelocity(demand);
                    case POSITION_CONTROL -> controlRequest = position.withPosition(demand);
                    case MOTION_MAGIC -> controlRequest = motionMagic.withPosition(demand);
                    case BRAKE -> controlRequest = brake;
                    default -> controlRequest = neutral;
                }

                super.setControl(controlRequest);
            } else {
                DriverStation.reportError("MOTOR " + getDeviceID() + " HAS RESET", false);
            }
        }
    }

    @Override
    public void configForwardLimitSwitch(boolean normallyOpen) {
        configs.HardwareLimitSwitch
                .withForwardLimitSource(ForwardLimitSourceValue.LimitSwitchPin)
                .withForwardLimitType(normallyOpen ? ForwardLimitTypeValue.NormallyOpen : ForwardLimitTypeValue.NormallyClosed);
        configurator.apply(configs);
    }

    @Override
    public void configReverseLimitSwitch(boolean normallyOpen) {
        configs.HardwareLimitSwitch
                .withReverseLimitSource(ReverseLimitSourceValue.LimitSwitchPin)
                .withReverseLimitType(normallyOpen ? ReverseLimitTypeValue.NormallyOpen : ReverseLimitTypeValue.NormallyClosed);
        configurator.apply(configs);
    }

    @Override
    public boolean isLimitSwitchClosed(LimitSwitchDirection direction) {
        return direction == LimitSwitchDirection.FORWARD ?
                (super.getForwardLimit().getValue() == ForwardLimitValue.ClosedToGround) :
                (super.getReverseLimit().getValue() == ReverseLimitValue.Open);
    }


    @Override
    public void neutralOutput() {
        set(GreenControlMode.NEUTRAL, 0);
    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {
        super.setNeutralMode(neutralMode == NeutralMode.Brake ? NeutralModeValue.Brake : NeutralModeValue.Coast);
    }

    @Override
    public void setSensorPhase(boolean isInverted) {
    }

    @Override
    public void setInverted(boolean isInverted) {
        super.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return super.getInverted();
    }

    @Override
    public void configOpenLoopRampRate(double secondsNeutralToFull) {
        configs.OpenLoopRamps
                .withDutyCycleOpenLoopRampPeriod(secondsNeutralToFull);
        configurator.apply(configs);
    }

    @Override
    public void configOpenLoopRampRate(double secondsNeutralToFull, int timeoutMs) {
        configs.OpenLoopRamps
                .withDutyCycleOpenLoopRampPeriod(secondsNeutralToFull);
        configurator.apply(configs, timeoutMs/1000.0);
    }

    @Override
    public void configClosedLoopRampRate(double secondsNeutralToFull) {
        configs.ClosedLoopRamps
                .withDutyCycleClosedLoopRampPeriod(secondsNeutralToFull);
        configurator.apply(configs);
    }

    @Override
    public void config_PeakOutputForward(double percentOut) {
        config_PeakOutputForward(percentOut, 0);
    }

    @Override
    public void config_PeakOutputForward(double percentOut, int timeoutMs) {
        configs.MotorOutput.withPeakForwardDutyCycle(percentOut);
        configurator.apply(configs, timeoutMs/1000.0);
    }

    @Override
    public void config_PeakOutputReverse(double percentOut) {
        config_PeakOutputReverse(percentOut, 0);
    }

    @Override
    public void config_PeakOutputReverse(double percentOut, int timeoutMs) {
        configs.MotorOutput.withPeakReverseDutyCycle(percentOut);
        configurator.apply(configs, timeoutMs/1000.0);
    }

    @Override
    public void config_NeutralDeadband(double deadbandPercent) {
        super.configNeutralDeadband(deadbandPercent);
    }

    @Override
    public double getBusVoltage() {
        return super.getSupplyVoltage().getValueAsDouble();
    }

    @Override
    public double getMotorOutputPercent() {
        return super.get();
    }

    @Override
    public double getMotorOutputVoltage() {
        return super.getMotorVoltage().getValueAsDouble();
    }

    @Override
    public double getMotorTemperature() {
        return Math.max(super.getProcessorTemp().getValueAsDouble(), super.getDeviceTemp().getValueAsDouble());
    }

    @Override
    public double getSensorPosition(int closedLoopSlotID) {
        return super.getPosition().getValueAsDouble();
    }

    @Override
    public double getSensorVelocity(int closedLoopSlotID) {
        return super.getSelectedSensorVelocity(closedLoopSlotID);
    }

    @Override
    public void setSensorPosition(double sensorPosition, int closedLoopSlotID) {
        super.setSelectedSensorPosition(sensorPosition, closedLoopSlotID, Constants.kCANTimeoutMs);
    }

    @Override
    public void setSensorPosition(double sensorPosition, int closedLoopSlotID, int timeoutMs) {
        super.setSelectedSensorPosition(sensorPosition, closedLoopSlotID, timeoutMs);
    }

    @Override
    public void enableLimitSwitches(boolean isEnabled) {
        super.overrideLimitSwitchesEnable(isEnabled);
    }

    @Override
    public void configForwardSoftLimit(double forwardSoftLimit) {
        super.configForwardSoftLimitThreshold(forwardSoftLimit, Constants.kCANTimeoutMs);
    }

    @Override
    public void configReverseSoftLimit(double reverseSoftLimit) {
        super.configReverseSoftLimitThreshold(reverseSoftLimit, Constants.kCANTimeoutMs);
    }

    @Override
    public void enableForwardSoftLimit(boolean isEnabled) {
        super.configForwardSoftLimitEnable(isEnabled, Constants.kCANTimeoutMs);
    }

    @Override
    public void enableReverseSoftLimit(boolean isEnabled) {
        super.configReverseSoftLimitEnable(isEnabled, Constants.kCANTimeoutMs);
    }

    @Override
    public void enableSoftLimits(boolean isEnabled) {
        super.overrideSoftLimitsEnable(isEnabled);
    }

    @Override
    public void set_kP(int pidSlotID, double kP) {
        switch (pidSlotID) {
            case 0 -> configs.Slot0.withKP(kP);
            case 1 -> configs.Slot1.withKP(kP);
            case 2 -> configs.Slot2.withKP(kP);
        }
    }

    @Override
    public void set_kI(int pidSlotID, double kI) {
        super.config_kI(pidSlotID, kI);
    }

    @Override
    public void set_kD(int pidSlotID, double kD) {
        super.config_kD(pidSlotID, kD);
    }

    @Override
    public void set_kF(int pidSlotID, double kF) {
        // KF renamed to kV in phoenix 6
        super.config_kF(pidSlotID, kF);
    }

    @Override
    public void selectPIDSlot(int pidSlotID, int closedLoopSlotID) {
        velocity.withSlot(pidSlotID);
        position.withSlot(pidSlotID);
        motionMagic.withSlot(pidSlotID);
    }

    @Override
    public void set_iZone(int pidSlotID, double iZone) {
    }

    @Override
    public void configAllowableErrorClosedLoop(int pidSlotID, double allowableError) {
    }

    @Override
    public void setPeakOutputClosedLoop(int pidSlotID, double peakOutput) {
        super.configClosedLoopPeakOutput(pidSlotID, peakOutput);
    }

    @Override
    public double getClosedLoopError() {
        return super.getClosedLoopError();
    }

    @Override
    public void setMotionProfileMaxVelocity(double maxVelocity) {
        super.configMotionCruiseVelocity(maxVelocity);
    }

    @Override
    public void setMotionProfileMaxAcceleration(double maxAcceleration) {
        super.configMotionAcceleration(maxAcceleration);
    }

    @Override
    public void configMotionCurve(MotionCurveType motionCurveType, int curveStrength) {
        if (curveStrength > 8) {
            GreenLogger.log("Motion Curve Strength cannot exceed 8, adjusting down.");
            curveStrength = 8;
        } else if (curveStrength < 0) {
            GreenLogger.log("Motion Curve Strength cannot be negative, adjusting to 0.");
            curveStrength = 0;
        }
        configurator.apply(
                configs.MotionMagic.withMotionMagicJerk(curveStrength)
        );
    }

    @Override
    public boolean hasResetOccurred() {
        return super.hasResetOccurred();
    }

    @Override
    public int getDeviceID() {
        return super.getDeviceID();
    }

    @Override
    public GreenControlMode get_ControlMode() {
        // Tag Will do this
        return ConfigurationTranslator.toGreenControlMode(super.getControlMode());
    }

    @Override
    public void follow(IGreenMotor leader) {
        isFollower = true;
        // ONLY works to follow CTRE Motor Controllers.
        if (leader.get_MotorType() == MotorType.SparkMax || leader.get_MotorType() == MotorType.GHOST) {
           GreenLogger.log("TalonFX cannot follow non-CTRE motor " + leader.getName() + " of type " + leader.get_MotorType());
        } else {
            following.withMasterID(leader.getDeviceID());
            set(GreenControlMode.FOLLOWER, 0);
        }
    }

    @Override
    public double getSupplyCurrent() {
        return super.getSupplyCurrent();
    }

    @Override
    public void restore_FactoryDefaults(int timeoutMs) {
        super.configFactoryDefault(timeoutMs);
    }

    @Override
    public boolean isFollower() {
        return isFollower;
    }

    @Override
    public void configControlFramePeriod(ControlFrame controlFrame, int periodms) {
        dutyCycle.withUpdateFreqHz(Util.msToHz(periodms));
        super.setControlFramePeriod(controlFrame, periodms);
    }
}

