package com.team1816.lib.hardware.components.motor;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.*;
import com.team1816.lib.Injector;
import com.team1816.lib.hardware.components.motor.configurations.*;
import com.team1816.lib.hardware.factory.RobotFactory;
import com.team1816.lib.util.ConfigurationTranslator;
import com.team1816.lib.util.Util;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.wpilibj.DriverStation;

public class LazyTalonFX extends TalonFX implements IGreenMotor {
    protected double lastSet = Double.NaN;
    protected String name = "";
    protected ControlMode lastControlMode = null;

    protected boolean isFollower = false;

    protected TalonFXConfigurator configurator;
    protected TalonFXConfiguration configs = new TalonFXConfiguration();

    protected final double kCANTimeoutSeconds = Constants.kCANTimeoutMs / 1000.0;
    protected final double kLongCANTimeoutSeconds = Constants.kLongCANTimeoutMs / 1000.0;

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
        selectFeedbackSensor(deviceType, -1);
    }

    @Override
    public void selectFeedbackSensor(FeedbackDeviceType deviceType, int id) {
        // Only 2 needed possibilities
        if (deviceType == FeedbackDeviceType.INTEGRATED_SENSOR) {
            configs.Feedback.withFeedbackSensorSource(FeedbackSensorSourceValue.RotorSensor);
        } else {
            configs.Feedback
                    .withFeedbackSensorSource(FeedbackSensorSourceValue.RemoteCANcoder)
                    .withFeedbackRemoteSensorID(id);
        }

        configurator.apply(configs, kLongCANTimeoutSeconds);
    }

    @Override
    public void configCurrentLimit(SupplyCurrentLimitConfiguration configuration) {
        configs.CurrentLimits
                .withSupplyCurrentLimit(configuration.currentLimit)
                .withSupplyCurrentLimitEnable(configuration.enable)
                .withSupplyCurrentThreshold(configuration.triggerThresholdCurrent)
                .withSupplyTimeThreshold(configuration.triggerThresholdTime);
        configurator.apply(configs, kLongCANTimeoutSeconds);
    }

    @Override
    public void configCurrentLimit(int current) {
        configs.CurrentLimits
                .withSupplyCurrentLimit(current)
                .withSupplyCurrentLimitEnable(true)
                .withSupplyCurrentThreshold(0)
                .withSupplyTimeThreshold(0);
        configurator.apply(configs, kLongCANTimeoutSeconds);
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
        configurator.apply(configs, kCANTimeoutSeconds);
    }

    @Override
    public void configReverseLimitSwitch(boolean normallyOpen) {
        configs.HardwareLimitSwitch
                .withReverseLimitSource(ReverseLimitSourceValue.LimitSwitchPin)
                .withReverseLimitType(normallyOpen ? ReverseLimitTypeValue.NormallyOpen : ReverseLimitTypeValue.NormallyClosed);
        configurator.apply(configs, kCANTimeoutSeconds);
    }

    @Override
    public boolean isLimitSwitchClosed(LimitSwitchDirection direction) {
        return direction == LimitSwitchDirection.FORWARD ?
                (super.getForwardLimit().getValue() == ForwardLimitValue.ClosedToGround) :
                (super.getReverseLimit().getValue() == ReverseLimitValue.Open);
    }


    @Override
    public void neutralOutput() {
        //this is so stupid
        set(GreenControlMode.NEUTRAL, 0);
    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {
        super.setNeutralMode(neutralMode == NeutralMode.Brake ? NeutralModeValue.Brake : NeutralModeValue.Coast);
    }

    @Override
    public void setSensorPhase(boolean isInverted) {
        // TalonFX can't separate invert sensor phase
    }

    @Override
    public void setInverted(boolean isInverted) {
        configurator.apply(configs.MotorOutput.withInverted(isInverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive));
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
                .withDutyCycleClosedLoopRampPeriod(secondsNeutralToFull)
                .withVoltageClosedLoopRampPeriod(secondsNeutralToFull)
                .withTorqueClosedLoopRampPeriod(secondsNeutralToFull);
        configurator.apply(configs);
    }

    @Override
    public void config_PeakOutputForward(double percentOut) {
        config_PeakOutputForward(percentOut, 50);
    }

    @Override
    public void config_PeakOutputForward(double percentOut, int timeoutMs) {
        configs.MotorOutput.withPeakForwardDutyCycle(percentOut);
        configurator.apply(configs, timeoutMs / 1000.0);
    }

    @Override
    public void config_PeakOutputReverse(double percentOut) {
        config_PeakOutputReverse(percentOut, 50);
    }

    @Override
    public void config_PeakOutputReverse(double percentOut, int timeoutMs) {
        configs.MotorOutput.withPeakReverseDutyCycle(percentOut);
        configurator.apply(configs, timeoutMs / 1000.0);
    }

    @Override
    public void config_NeutralDeadband(double deadbandPercent) {
        configs.MotorOutput.withDutyCycleNeutralDeadband(deadbandPercent);
        configurator.apply(configs);
    }

    @Override
    public void enableClearPositionOnLimitF(boolean clearPosition, int timeoutMs) {
        configs.HardwareLimitSwitch.ForwardLimitAutosetPositionEnable = clearPosition;
        configurator.apply(configs, timeoutMs / 1000.0);
    }

    @Override
    public void enableClearPositionOnLimitR(boolean clearPosition, int timeoutMs) {
        configs.HardwareLimitSwitch.ReverseLimitAutosetPositionEnable = clearPosition;
        configurator.apply(configs, timeoutMs / 1000.0);
    }

    @Override
    public double getBusVoltage() {
        return super.getSupplyVoltage().getValueAsDouble();
    }

    @Override
    public double getMotorOutputPercent() {
        return super.get(); //everything is verbose except for this. why
    }

    @Override
    public double getMotorOutputVoltage() {
        return super.getMotorVoltage().getValueAsDouble();
    }

    @Override
    public double get_SupplyCurrent() {
        return super.getSupplyCurrent().getValueAsDouble();
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
        return super.getVelocity().getValueAsDouble();
    }

    @Override
    public void setSensorPosition(double sensorPosition, int closedLoopSlotID) {
        setSensorPosition(sensorPosition, 0, Constants.kLongCANTimeoutMs);
    }

    @Override
    public void setSensorPosition(double sensorPosition, int closedLoopSlotID, int timeoutMs) {
        super.setPosition(sensorPosition, timeoutMs / 1000.0);
    }

    @Override
    public void enableLimitSwitches(boolean isEnabled) {
        configs.HardwareLimitSwitch
                .withForwardLimitEnable(isEnabled)
                .withReverseLimitEnable(isEnabled);
        configurator.apply(configs);
    }

    @Override
    public void configForwardSoftLimit(double forwardSoftLimit) {
        configs.SoftwareLimitSwitch.withForwardSoftLimitThreshold(forwardSoftLimit);
        configurator.apply(configs, kCANTimeoutSeconds);
    }

    @Override
    public void configReverseSoftLimit(double reverseSoftLimit) {
        configs.SoftwareLimitSwitch.withReverseSoftLimitThreshold(reverseSoftLimit);
        configurator.apply(configs, kCANTimeoutSeconds);
    }

    @Override
    public void enableForwardSoftLimit(boolean isEnabled) {
        configs.SoftwareLimitSwitch.withForwardSoftLimitEnable(isEnabled);
        configurator.apply(configs, kCANTimeoutSeconds);
    }

    @Override
    public void enableReverseSoftLimit(boolean isEnabled) {
        configs.SoftwareLimitSwitch.withReverseSoftLimitEnable(isEnabled);
        configurator.apply(configs, kCANTimeoutSeconds);
    }

    @Override
    public void enableSoftLimits(boolean isEnabled) {
        configs.SoftwareLimitSwitch
                .withForwardSoftLimitEnable(isEnabled)
                .withReverseSoftLimitEnable(isEnabled);
        configurator.apply(configs);
    }

    @Override
    public void set_kP(int pidSlotID, double kP) {
        switch (pidSlotID) {
            case 0 -> configs.Slot0.withKP(kP);
            case 1 -> configs.Slot1.withKP(kP);
            case 2 -> configs.Slot2.withKP(kP);
        }
        configurator.apply(configs);
    }

    @Override
    public void set_kI(int pidSlotID, double kI) {
        switch (pidSlotID) {
            case 0 -> configs.Slot0.withKI(kI);
            case 1 -> configs.Slot1.withKI(kI);
            case 2 -> configs.Slot2.withKI(kI);
        }
        configurator.apply(configs);
    }

    @Override
    public void set_kD(int pidSlotID, double kD) {
        switch (pidSlotID) {
            case 0 -> configs.Slot0.withKD(kD);
            case 1 -> configs.Slot1.withKD(kD);
            case 2 -> configs.Slot2.withKD(kD);
        }
        configurator.apply(configs);
    }

    @Override
    public void set_kF(int pidSlotID, double kF) {
        // KF renamed to kV in phoenix 6
        switch (pidSlotID) {
            case 0 -> configs.Slot0.withKV(kF);
            case 1 -> configs.Slot1.withKV(kF);
            case 2 -> configs.Slot2.withKV(kF);
        }
        configurator.apply(configs);
    }

    @Override
    public void selectPIDSlot(int pidSlotID, int closedLoopSlotID) {
        velocity.withSlot(pidSlotID);
        position.withSlot(pidSlotID);
        motionMagic.withSlot(pidSlotID);
    }

    @Override
    public void set_iZone(int pidSlotID, double iZone) {
        // Removed in phoenix 6
    }

    @Override
    public void configAllowableErrorClosedLoop(int pidSlotID, double allowableError) {
        // Currently unavailable in phoenix 6
    }

    @Override
    public void setPeakOutputClosedLoop(int pidSlotID, double peakOutput) {
        config_PeakOutputForward(peakOutput);
        config_PeakOutputReverse(peakOutput);
    }

    @Override
    public double get_ClosedLoopError() {
        return super.getClosedLoopError().getValueAsDouble();
    }

    @Override
    public void setMotionProfileMaxVelocity(double maxVelocity) {
        configs.MotionMagic.withMotionMagicCruiseVelocity(maxVelocity);
        configurator.apply(configs);
    }

    @Override
    public void setMotionProfileMaxAcceleration(double maxAcceleration) {
        configs.MotionMagic.withMotionMagicAcceleration(maxAcceleration);
        configurator.apply(configs);
    }

    @Override
    public void configMotionCurve(MotionCurveType motionCurveType, int curveStrength) {
        configurator.apply(
                configs.MotionMagic.withMotionMagicJerk(curveStrength)
        );
    }

    public void configContinuousWrap(boolean enable) {
        configs.ClosedLoopGeneral.ContinuousWrap = enable;
        configurator.apply(configs);
    }

    public void configRotorOffset(double offset) {
        configurator.apply(
          configs.Feedback.withFeedbackRotorOffset(offset)
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
        return ConfigurationTranslator.toGreenControlMode(super.getControlMode().getValue());
    }

    @Override
    public void follow(IGreenMotor leader) {
        isFollower = true;
        // ONLY works to follow other Talons.
        if (leader.get_MotorType() != MotorType.TalonFX) {
           GreenLogger.log("TalonFX cannot follow non-Talon motor " + leader.getName() + " of type " + leader.get_MotorType());
        } else {
            following.withMasterID(leader.getDeviceID());
            set(GreenControlMode.FOLLOWER, 0);
        }
    }

    @Override
    public void restore_FactoryDefaults(int timeoutMs) {
        configs = new TalonFXConfiguration();
        configs.Audio.withBeepOnConfig(Constants.kSoundOnConfig).withBeepOnBoot(Constants.kSoundOnConfig);
        configurator.apply(configs);
    }

    @Override
    public boolean isFollower() {
        return isFollower;
    }

    @Override
    public void configControlFramePeriod(ControlFrame controlFrame, int periodms) {
        double periodHz = Util.msToHz(periodms);
        dutyCycle.withUpdateFreqHz(periodHz);
        velocity.withUpdateFreqHz(periodHz);
        position.withUpdateFreqHz(periodHz);
        motionMagic.withUpdateFreqHz(periodHz);
        following.withUpdateFreqHz(periodHz);
        neutral.withUpdateFreqHz(periodHz);
        brake.withUpdateFreqHz(periodHz);
    }
}

