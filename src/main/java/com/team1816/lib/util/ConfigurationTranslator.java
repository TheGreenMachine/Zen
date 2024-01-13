package com.team1816.lib.util;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.signals.ControlModeValue;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkRelativeEncoder;
import com.team1816.lib.hardware.components.motor.configurations.*;
import com.team1816.lib.util.logUtil.GreenLogger;

import java.util.Arrays;

import static com.team1816.lib.util.Util.closestTo;

/**
 * Utility for translating between vendordep configuration classes and 1816 proprietary classes
 *
 */
public class ConfigurationTranslator {
    //Also known as: Switch statements: the class!
    private ConfigurationTranslator() {}

    /**
     * Translates 1816 FeedbackDeviceType to TalonSRXFeedbackDevice
     *
     * @see TalonSRXFeedbackDevice
     * @see FeedbackDeviceType
     * @param deviceType The generalized device type
     * @return The translated device type
     */
    public static TalonSRXFeedbackDevice toTalonSRXFeedbackDevice(FeedbackDeviceType deviceType) {
        TalonSRXFeedbackDevice talonSRXFeedbackDevice;
        switch (deviceType) {
            case NO_SENSOR -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.None;
            case SENSOR_SUM -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.SensorSum;
            case SENSOR_DIFFERENCE -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.SensorDifference;
            case REMOTE_SENSOR_0 -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.RemoteSensor0;
            case REMOTE_SENSOR_1 -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.RemoteSensor1;
            case SOFTWARE_EMULATED_SENSOR -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.SoftwareEmulatedSensor;
            case ANALOG -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.Analog;
            case TACHOMETER -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.Tachometer;
            case PULSE_WIDTH -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.PulseWidthEncodedPosition;
            case ABSOLUTE_MAG_ENCODER -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.CTRE_MagEncoder_Absolute;
            case RELATIVE_MAG_ENCODER -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.CTRE_MagEncoder_Relative;
            case QUADRATURE -> talonSRXFeedbackDevice = TalonSRXFeedbackDevice.QuadEncoder;
            default -> {
                talonSRXFeedbackDevice = TalonSRXFeedbackDevice.None;
                GreenLogger.log("Attempted application of non-applicable feedback device type " + deviceType + " to TalonSRX, defaulting to No sensor");
            }
        }
        return talonSRXFeedbackDevice;
    }

    /**
     * Translates 1816 FeedbackDeviceType to CTRE RemoteFeedbackDevice
     *
     * @see RemoteFeedbackDevice
     * @see FeedbackDeviceType
     * @param deviceType The generalized device type
     * @return The translated device type
     */
    public static RemoteFeedbackDevice toRemoteFeedbackDevice(FeedbackDeviceType deviceType) {
        RemoteFeedbackDevice remoteFeedbackDevice;
        switch (deviceType) {
            case NO_SENSOR -> remoteFeedbackDevice = RemoteFeedbackDevice.None;
            case SENSOR_SUM -> remoteFeedbackDevice = RemoteFeedbackDevice.SensorSum;
            case SENSOR_DIFFERENCE -> remoteFeedbackDevice = RemoteFeedbackDevice.SensorDifference;
            case REMOTE_SENSOR_0 -> remoteFeedbackDevice = RemoteFeedbackDevice.RemoteSensor0;
            case REMOTE_SENSOR_1 -> remoteFeedbackDevice = RemoteFeedbackDevice.RemoteSensor1;
            case SOFTWARE_EMULATED_SENSOR -> remoteFeedbackDevice = RemoteFeedbackDevice.SoftwareEmulatedSensor;
            default -> {
                remoteFeedbackDevice = RemoteFeedbackDevice.None;
                GreenLogger.log("Attempted application of non-applicable feedback device type " + deviceType + " to VictorSPX, defaulting to No sensor");
            }
        }
        return remoteFeedbackDevice;
    }

    /**
     * Translates 1816 FeedbackDeviceType to REV SparkMaxRelativeEncoder.Type
     *
     * @see SparkRelativeEncoder.Type
     * @see FeedbackDeviceType
     * @param deviceType The generalized device type
     * @return The translated encoder type
     */
    public static SparkRelativeEncoder.Type toSparkRelativeEncoderType(FeedbackDeviceType deviceType) {
        SparkRelativeEncoder.Type encoderType;
        switch (deviceType) {
            case QUADRATURE -> encoderType = SparkRelativeEncoder.Type.kQuadrature;
            case HALL_SENSOR -> encoderType = SparkRelativeEncoder.Type.kHallSensor;
            case NO_SENSOR -> encoderType = SparkRelativeEncoder.Type.kNoSensor;
            default -> {
                GreenLogger.log("Non-SparkMax encoder type " + deviceType + " cannot be applied to SparkMaxRelativeEncoder, defaulting to No sensor.");
                encoderType = SparkRelativeEncoder.Type.kNoSensor;
            }
        }
        return encoderType;
    }

    /**
     * Translates 1816 GreenControlMode into CTRE ControlMode
     *
     * @see ControlMode
     * @see GreenControlMode
     * @param controlMode The generalized control mode
     * @return The translated control mode
     */
    public static ControlMode toCTREControlMode(GreenControlMode controlMode) {
        ControlMode CTREControlMode;
        switch (controlMode) {
            case PERCENT_OUTPUT -> CTREControlMode = ControlMode.PercentOutput;
            case VELOCITY_CONTROL -> CTREControlMode = ControlMode.Velocity;
            case POSITION_CONTROL -> CTREControlMode = ControlMode.Position;
            case MOTION_PROFILE -> CTREControlMode = ControlMode.MotionProfile;
            case CURRENT -> CTREControlMode = ControlMode.Current;
            case FOLLOWER -> CTREControlMode = ControlMode.Follower;
            case MOTION_MAGIC -> CTREControlMode = ControlMode.MotionMagic;
            case MOTION_PROFILE_ARC -> CTREControlMode = ControlMode.MotionProfileArc;
            case MUSIC_TONE -> CTREControlMode = ControlMode.MusicTone;
            case DISABLED -> CTREControlMode = ControlMode.Disabled;
            default -> {
                GreenLogger.log("REV-Exclusive Control mode " + controlMode + " cannot be set to TalonFX, defaulting to Percent-Output");
                CTREControlMode = ControlMode.PercentOutput;
            }
        }
        return CTREControlMode;
    }

    public static CANSparkMax.ControlType toSparkMaxControlType(GreenControlMode controlMode) {
        CANSparkMax.ControlType controlType;
        switch (controlMode) {
            case PERCENT_OUTPUT -> controlType = CANSparkMax.ControlType.kDutyCycle;
            case VELOCITY_CONTROL -> controlType = CANSparkMax.ControlType.kVelocity;
            case POSITION_CONTROL -> controlType = CANSparkMax.ControlType.kPosition;
            case MOTION_PROFILE -> controlType = CANSparkMax.ControlType.kSmartMotion;
            case CURRENT -> controlType = CANSparkMax.ControlType.kCurrent;
            case VOLTAGE_CONTROL -> controlType = CANSparkMax.ControlType.kVoltage;
            case SMART_VELOCITY -> controlType = CANSparkMax.ControlType.kSmartVelocity;
            default -> {
                GreenLogger.log("Motor Control Mode " + controlMode + " not applicable to SparkMax ControlType, defaulting to Percent-Output");
                controlType = CANSparkMax.ControlType.kDutyCycle;
            }
        }
        return controlType;
    }

    /**
     * Translates CTRE ControlMode into 1816 GreenControlMode
     * @see ControlMode
     * @see GreenControlMode
     *
     * @param controlMode The CTRE Control Mode
     * @return The generalized translation
     */
    public static GreenControlMode toGreenControlMode(ControlMode controlMode) {
        GreenControlMode greenControlMode;
        switch (controlMode) {
            case PercentOutput -> greenControlMode = GreenControlMode.PERCENT_OUTPUT;
            case Velocity -> greenControlMode = GreenControlMode.VELOCITY_CONTROL;
            case Position -> greenControlMode = GreenControlMode.POSITION_CONTROL;
            case MotionProfile -> greenControlMode = GreenControlMode.MOTION_PROFILE;
            case Current -> greenControlMode = GreenControlMode.CURRENT;
            case Follower -> greenControlMode = GreenControlMode.FOLLOWER;
            case MotionMagic -> greenControlMode = GreenControlMode.MOTION_MAGIC;
            case MotionProfileArc -> greenControlMode = GreenControlMode.MOTION_PROFILE_ARC;
            case MusicTone -> greenControlMode = GreenControlMode.MUSIC_TONE;
            default -> greenControlMode = GreenControlMode.DISABLED;
        }
        return greenControlMode;
    }

    /**
     * Translates CTRE ControlModeValue into 1816 GreenControlMode
     * @see com.ctre.phoenix6.signals.ControlModeValue
     * @see GreenControlMode
     *
     * @param controlMode The CTRE Control Mode Value
     * @return The generalized translation
     */
    public static GreenControlMode toGreenControlMode(ControlModeValue controlMode) {
        GreenControlMode greenControlMode;
        switch (controlMode) {
            case DutyCycleOut -> greenControlMode = GreenControlMode.PERCENT_OUTPUT;
            case VelocityDutyCycle -> greenControlMode = GreenControlMode.VELOCITY_CONTROL;
            case PositionDutyCycle -> greenControlMode = GreenControlMode.POSITION_CONTROL;
            case MotionMagicDutyCycle -> greenControlMode = GreenControlMode.MOTION_PROFILE;
            case Follower -> greenControlMode = GreenControlMode.FOLLOWER;
            case MusicTone -> greenControlMode = GreenControlMode.MUSIC_TONE;
            default -> greenControlMode = GreenControlMode.DISABLED;
        }
        return greenControlMode;
    }

    /**
     * Translates 1816 MotionCurveType into an int
     *
     * @param motionCurveType The Generalized motion curve type
     * @param motionCurveStrength The strength of the motion curve; 0 for trapezoidal, [1,8] for S-curve
     * @see MotionCurveType
     * @return The motion curve type as an int
     */
    public static int toMotionCurveInt(MotionCurveType motionCurveType, int motionCurveStrength) {
        if (motionCurveType == MotionCurveType.S_CURVE && motionCurveStrength > 0) {
            return motionCurveStrength;
        }
        return  0;
    }

    /**
     * Translates Phoenix 6 CurrentLimitsConfig into Phoenix 5 SupplyCurrentLimitConfiguration
     * @param currentLimitsConfigs The Phoenix 6 config
     * @see CurrentLimitsConfigs
     * @see SupplyCurrentLimitConfiguration
     * @return The phoenix 5 config
     */
    public static SupplyCurrentLimitConfiguration toSupplyCurrentLimitConfiguration(CurrentLimitsConfigs currentLimitsConfigs) {
        return new SupplyCurrentLimitConfiguration(
                currentLimitsConfigs.SupplyCurrentLimitEnable,
                currentLimitsConfigs.SupplyCurrentLimit,
                currentLimitsConfigs.SupplyCurrentThreshold,
                currentLimitsConfigs.SupplyTimeThreshold
        );
    }

}
