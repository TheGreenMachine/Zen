package com.team1816.lib.hardware.components.motor;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team1816.lib.hardware.components.motor.configurations.*;
import com.team1816.lib.util.ConfigurationTranslator;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.wpilibj.DriverStation;

public class LazyVictorSPX extends VictorSPX implements IGreenMotor {
    protected double lastSet = Double.NaN;
    protected String name = "";
    protected ControlMode lastControlMode = null;

    protected Faults faults;
    protected StickyFaults stickyFaults;

    protected boolean isFollower;

    protected SoftLimitStatus softLimitStatus;

    protected double arbitraryFeedForward = 0;

    /**
     * Constructor
     *
     * @param deviceNumber [0,62]
     */
    public LazyVictorSPX(int deviceNumber, String motorName) {
        super(deviceNumber);
        name = motorName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MotorType get_MotorType() {
        return MotorType.VictorSPX;
    }

    @Override
    public void selectFeedbackSensor(FeedbackDeviceType deviceType) {
        selectFeedbackSensor(deviceType, 0);
    }

    public void selectFeedbackSensor(FeedbackDeviceType deviceType, int closedLoopSlotID) {
        super.configSelectedFeedbackSensor(
            ConfigurationTranslator.toRemoteFeedbackDevice(deviceType),
            closedLoopSlotID,
            Constants.kCANTimeoutMs
        );
    }

    @Override
    public void configCurrentLimit(SupplyCurrentLimitConfiguration configuration) {
        GreenLogger.log("Current Limits nonexistent for VictorSPX!!!");
    }

    @Override
    public void configCurrentLimit(int current) {
        GreenLogger.log("Current Limits nonexistent for VictorSPX!!!");
    }

    @Override
    public double getMotorOutputCurrent() {
        return super.getOutputCurrent(); // Deprecated & victors are so old that they don't have getSupplyCurrent() or getStatorCurrent() smile
    }

    @Override
    public void set(GreenControlMode controlMode, double demand) {
        ControlMode mode = ConfigurationTranslator.toCTREControlMode(controlMode);
        if (demand != lastSet || mode != lastControlMode) {
            if (!super.hasResetOccurred()) {
                lastSet = demand;
                lastControlMode = mode;
                super.set(mode, demand, DemandType.ArbitraryFeedForward, arbitraryFeedForward); //Note that arbitraryFF is initialized at 0
            } else {
                DriverStation.reportError("MOTOR " + getDeviceID() + " HAS RESET", false);
            }
        }
    }

    @Override
    public void configForwardLimitSwitch(boolean normallyOpen) {
        LimitSwitchNormal openOrClosed = normallyOpen ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed;
        super.configForwardLimitSwitchSource(
            RemoteLimitSwitchSource.Deactivated,
            openOrClosed,
            Constants.kCANTimeoutMs
        );
    }

    @Override
    public void configReverseLimitSwitch(boolean normallyOpen) {
        LimitSwitchNormal openOrClosed = normallyOpen ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed;
        super.configReverseLimitSwitchSource(
            RemoteLimitSwitchSource.Deactivated,
            openOrClosed,
            Constants.kCANTimeoutMs
        );
    }

    @Override
    public boolean isLimitSwitchClosed(LimitSwitchDirection direction) {
        GreenLogger.log("Limit switch status checking unsupported for VictorSPX");
        return false;
    }


    @Override
    public void configOpenLoopRampRate(double secondsNeutralToFull) {
        super.configOpenloopRamp(secondsNeutralToFull);
    }

    @Override
    public void configOpenLoopRampRate(double secondsNeutralToFull, int timeoutMs) {
        super.configOpenloopRamp(secondsNeutralToFull, timeoutMs);
    }


    @Override
    public void configClosedLoopRampRate(double secondsNeutralToFull) {
        super.configClosedloopRamp(secondsNeutralToFull);
    }

    @Override
    public void config_PeakOutputForward(double percentOut) {
        super.configPeakOutputForward(percentOut);
    }

    @Override
    public void config_PeakOutputForward(double percentOut, int timeoutMs) {
        super.configPeakOutputForward(percentOut, timeoutMs);
    }

    @Override
    public void config_PeakOutputReverse(double percentOut, int timeoutMs) {
        super.configPeakOutputReverse(percentOut, timeoutMs);
    }

    @Override
    public void config_PeakOutputReverse(double percentOut) {
        super.configPeakOutputReverse(percentOut);
    }


    @Override
    public void config_NeutralDeadband(double deadbandPercent) {
        super.configNeutralDeadband(deadbandPercent);
    }

    @Override
    public double getMotorTemperature() {
        return super.getTemperature();
    }

    @Override
    public double getSensorPosition(int closedLoopSlotID) {
        return super.getSelectedSensorPosition(closedLoopSlotID);
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
        super.configForwardSoftLimitThreshold(forwardSoftLimit);
    }

    @Override
    public void configReverseSoftLimit(double reverseSoftLimit) {
        super.configReverseSoftLimitThreshold(reverseSoftLimit);
    }

    @Override
    public void enableForwardSoftLimit(boolean isEnabled) {
        super.configForwardSoftLimitEnable(isEnabled);
    }

    @Override
    public void enableReverseSoftLimit(boolean isEnabled) {
        super.configReverseSoftLimitEnable(isEnabled);
    }

    @Override
    public void enableSoftLimits(boolean isEnabled) {
        super.overrideSoftLimitsEnable(isEnabled);
    }

    @Override
    public void set_kP(int pidSlotID, double kP) {
        super.config_kP(pidSlotID, kP);
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
        super.config_kF(pidSlotID, kF);
    }

    @Override
    public void selectPIDSlot(int pidSlotID, int closedLoopSlotID) {
        super.selectProfileSlot(pidSlotID, closedLoopSlotID);
    }

    @Override
    public void set_iZone(int pidSlotID, double iZone) {
        super.config_IntegralZone(pidSlotID, iZone);
    }

    @Override
    public void configAllowableErrorClosedLoop(int pidSlotID, double allowableError) {
        super.configAllowableClosedloopError(pidSlotID, allowableError);
    }

    @Override
    public void setPeakOutputClosedLoop(int pidSlotID, double peakOutput) {
        super.configClosedLoopPeakOutput(pidSlotID, peakOutput);
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
        super.configMotionSCurveStrength(
            ConfigurationTranslator.toMotionCurveInt(motionCurveType, curveStrength)
        );
    }

    @Override
    public GreenControlMode get_ControlMode() {
        return ConfigurationTranslator.toGreenControlMode(super.getControlMode());
    }

    @Override
    public void follow(IGreenMotor leader) {
        isFollower = true;
        // ONLY works to follow CTRE Motor Controllers.
        if (leader.get_MotorType() == MotorType.SparkMax || leader.get_MotorType() == MotorType.GHOST) {
            GreenLogger.log("TalonFX cannot follow non-CTRE motor " + leader.getName() + " of type " + leader.get_MotorType());
        } else {
            super.follow((IMotorController) leader);
        }
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
        super.setControlFramePeriod(controlFrame, periodms);
    }
}
