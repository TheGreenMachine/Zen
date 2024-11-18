package com.team1816.lib.subsystems.drive.StatusSignal;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.jni.ErrorReportingJNI;
import com.ctre.phoenix6.jni.StatusSignalJNI;

public abstract class BaseStatusSignal {
    protected DeviceIdentifier deviceIdentifier;
    protected int spn;
    protected String units;
    protected StatusCode error;
    protected double baseValue;
    protected AllTimestamps timestamps;
    protected final String signalName;
    protected Runnable _reportIfOldFunc;
    private double _lastTimestamp;
    protected StatusSignalJNI jni;

    BaseStatusSignal(DeviceIdentifier deviceIdentifier, int spn, String signalName, Runnable reportIfOldFunc) {
        this.error = StatusCode.StatusCodeNotInitialized;
        this.baseValue = 0.0;
        this.timestamps = new AllTimestamps();
        this._lastTimestamp = 0.0;
        this.jni = new StatusSignalJNI();
        this.deviceIdentifier = deviceIdentifier;
        this.spn = spn;
        this.signalName = signalName;
        this._reportIfOldFunc = reportIfOldFunc;
        this.jni.network = deviceIdentifier.getNetwork();
        this.jni.deviceHash = deviceIdentifier.getDeviceHash();
        this.jni.spn = spn;
        this.units = this.jni.JNI_GetUnits();
    }

    BaseStatusSignal(StatusCode error) {
        this(new DeviceIdentifier(), 0, "Invalid", () -> {
        });
        this.error = error;
    }

    protected void copyFrom(BaseStatusSignal other) {
        this.units = other.units;
        this.error = other.error;
        this.baseValue = other.baseValue;
        this.timestamps = other.timestamps;
    }

    private static StatusCode waitForAllImpl(String location, double timeoutSeconds, BaseStatusSignal... signals) {
        if (signals.length < 1) {
            ErrorReportingJNI.reportStatusCode(StatusCode.InvalidParamValue.value, location);
            return StatusCode.InvalidParamValue;
        } else {
            String network = signals[0].deviceIdentifier.getNetwork();
            StatusSignalJNI[] toGet = new StatusSignalJNI[signals.length];

            int err;
            for(err = 0; err < signals.length; ++err) {
                BaseStatusSignal sig = signals[err];
                if (err != 0 && !sig.deviceIdentifier.getNetwork().equals(network)) {
                    ErrorReportingJNI.reportStatusCode(StatusCode.InvalidNetwork.value, location);
                    return StatusCode.InvalidNetwork;
                }

                toGet[err] = sig.jni;
            }

            BaseStatusSignal[] var10 = signals;
            int i = signals.length;

            for(int var8 = 0; var8 < i; ++var8) {
                BaseStatusSignal signal = var10[var8];
                signal._reportIfOldFunc.run();
            }

            err = StatusSignalJNI.JNI_WaitForAll(network, timeoutSeconds, toGet);

            for(i = 0; i < signals.length; ++i) {
                signals[i].error = StatusCode.valueOf(toGet[i].statusCode);
                signals[i].baseValue = toGet[i].value;
                signals[i].timestamps.update(toGet[i].swtimeStampSeconds, Timestamp.TimestampSource.System, true, toGet[i].hwtimeStampSeconds, Timestamp.TimestampSource.CANivore, true, toGet[i].ecutimeStampSeconds, Timestamp.TimestampSource.Device, toGet[i].ecutimeStampSeconds != 0.0);
            }

            StatusCode retval = StatusCode.valueOf(err);
            if (!retval.isOK()) {
                ErrorReportingJNI.reportStatusCode(retval.value, location);
            }

            return StatusCode.valueOf(err);
        }
    }

    public static StatusCode waitForAll(double timeoutSeconds, BaseStatusSignal... signals) {
        return waitForAllImpl("ctre.phoenix6.BaseStatusSignal.waitForAll", timeoutSeconds, signals);
    }

    public static StatusCode refreshAll(BaseStatusSignal... signals) {
        return waitForAllImpl("ctre.phoenix6.BaseStatusSignal.refreshAll", 0.0, signals);
    }

    public static double getLatencyCompensatedValue(StatusSignal<Double> signal, StatusSignal<Double> signalSlope) {
        return getLatencyCompensatedValue(signal, signalSlope, 0.3);
    }

    public static double getLatencyCompensatedValue(StatusSignal<Double> signal, StatusSignal<Double> signalSlope, double maxLatencySeconds) {
        double nonCompensatedSignal = (Double)signal.getValue();
        double changeInSignal = (Double)signalSlope.getValue();
        double latency = signal.getTimestamp().getLatency();
        if (maxLatencySeconds > 0.0 && latency > maxLatencySeconds) {
            latency = maxLatencySeconds;
        }

        return nonCompensatedSignal + changeInSignal * latency;
    }

    public static boolean isAllGood(BaseStatusSignal... signals) {
        BaseStatusSignal[] var1 = signals;
        int var2 = signals.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            BaseStatusSignal sig = var1[var3];
            if (!sig.getStatus().isOK()) {
                return false;
            }
        }

        return true;
    }

    public static StatusCode setUpdateFrequencyForAll(double frequencyHz, BaseStatusSignal... signals) {
        StatusSignalJNI[] toSet = new StatusSignalJNI[signals.length];

        for(int i = 0; i < signals.length; ++i) {
            toSet[i] = signals[i].jni;
        }

        return StatusCode.valueOf(StatusSignalJNI.JNI_SetUpdateFrequencyForAll(frequencyHz, toSet, 0.05));
    }

    public StatusCode setUpdateFrequency(double frequencyHz) {
        return this.setUpdateFrequency(frequencyHz, 0.05);
    }

    public abstract StatusCode setUpdateFrequency(double var1, double var3);

    public abstract double getAppliedUpdateFrequency();

    public String getName() {
        return this.signalName;
    }

    public String getUnits() {
        return this.units;
    }

    public double getValueAsDouble() {
        return this.baseValue;
    }

    public AllTimestamps getAllTimestamps() {
        return this.timestamps;
    }

    public Timestamp getTimestamp() {
        return this.timestamps.getBestTimestamp();
    }

    public StatusCode getStatus() {
        return this.error;
    }

    public boolean hasUpdated() {
        boolean retval = false;
        Timestamp timestamp = this.getAllTimestamps().getSystemTimestamp();
        if (timestamp.isValid() && this._lastTimestamp != timestamp.getTime()) {
            this._lastTimestamp = timestamp.getTime();
            retval = true;
        }

        return retval;
    }
}
