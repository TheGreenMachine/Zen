//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.team1816.lib.subsystems.drive.StatusSignal;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.jni.ErrorReportingJNI;
import edu.wpi.first.wpilibj.RobotBase;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public class StatusSignal<T> extends BaseStatusSignal implements Cloneable {
    private Class<T> classOfSignal;
    private boolean _containsUnderlyingTypes;
    private Map<Integer, StatusSignal<T>> _basicTypeMap;

    public StatusSignal(DeviceIdentifier deviceIdentifier, int spn, Runnable reportIfOldFunc, Class<T> classOfSignal, String signalName) {
        super(deviceIdentifier, spn, signalName, reportIfOldFunc);
        this.classOfSignal = classOfSignal;
        this._containsUnderlyingTypes = false;
        this._basicTypeMap = null;
    }

    public StatusSignal(DeviceIdentifier deviceIdentifier, int spn, Runnable reportIfOldFunc, Class<T> classOfSignal, ParentDevice.MapGenerator<T> generator, String signalName) {
        super(deviceIdentifier, spn, signalName, reportIfOldFunc);
        this.classOfSignal = classOfSignal;
        this._containsUnderlyingTypes = true;
        this._basicTypeMap = generator.run();
    }

    public StatusSignal(Class<T> classOfSignal, StatusCode error) {
        super(error);
        this.classOfSignal = classOfSignal;
        this._containsUnderlyingTypes = false;
        this._basicTypeMap = null;
    }

    public Supplier<T> asSupplier() {
        return () -> {
            return this.refresh().getValue();
        };
    }

    public String toString() {
        if (this.getValue() != null && this.units != null) {
            String var10000 = this.getValue().toString();
            return var10000 + " " + this.units;
        } else {
            return "Invalid signal";
        }
    }

    public StatusSignal<T> clone() {
        try {
            StatusSignal<T> toReturn = (StatusSignal)StatusSignal.class.cast(super.clone());
            toReturn.timestamps = this.timestamps.clone();
            toReturn.jni = this.jni.clone();
            if (this._basicTypeMap != null) {
                toReturn._basicTypeMap = new HashMap();
                Iterator var2 = this._basicTypeMap.entrySet().iterator();

                while(var2.hasNext()) {
                    Map.Entry<Integer, StatusSignal<T>> entry = (Map.Entry)var2.next();
                    toReturn._basicTypeMap.put((Integer)entry.getKey(), ((StatusSignal)entry.getValue()).clone());
                }
            }

            return toReturn;
        } catch (CloneNotSupportedException var4) {
            return new StatusSignal(this.classOfSignal, StatusCode.InvalidParamValue);
        }
    }

    public Class<T> getTypeClass() {
        return this.classOfSignal;
    }

    public T getValue() {
        if (this.classOfSignal.equals(Double.class)) {
            return this.classOfSignal.cast(this.baseValue);
        } else if (this.classOfSignal.equals(Integer.class)) {
            return this.classOfSignal.cast((int)this.baseValue);
        } else if (this.classOfSignal.equals(Boolean.class)) {
            return this.classOfSignal.cast(this.baseValue != 0.0);
        } else {
            if (this.classOfSignal.isEnum()) {
                try {
                    return this.classOfSignal.cast(this.classOfSignal.getMethod("valueOf", Integer.TYPE).invoke((Object)null, (int)this.baseValue));
                } catch (IllegalAccessException var2) {
                    this.error = StatusCode.CouldNotCast;
                } catch (IllegalArgumentException var3) {
                    this.error = StatusCode.CouldNotCast;
                } catch (InvocationTargetException var4) {
                    this.error = StatusCode.CouldNotCast;
                } catch (NoSuchMethodException var5) {
                    this.error = StatusCode.CouldNotCast;
                } catch (ClassCastException var6) {
                    this.error = StatusCode.CouldNotCast;
                }
            } else {
                try {
                    return this.classOfSignal.cast(this.baseValue);
                } catch (ClassCastException var7) {
                    this.error = StatusCode.CouldNotCast;
                }
            }

            return null;
        }
    }

    private void refreshMappable(boolean waitForSignal, double timeout) {
        if (this._containsUnderlyingTypes) {
            if (waitForSignal) {
                this.error = StatusCode.valueOf(this.jni.JNI_WaitForSignal(timeout));
            } else {
                this.error = StatusCode.valueOf(this.jni.JNI_RefreshSignal(timeout));
            }

            if (this._basicTypeMap.containsKey((int)this.jni.value)) {
                StatusSignal<T> gottenValue = (StatusSignal)this._basicTypeMap.get((int)this.jni.value);
                gottenValue.updateValue(waitForSignal, timeout, false);
                this.copyFrom(gottenValue);
            }

        }
    }

    private void refreshNonmappable(boolean waitForSignal, double timeout) {
        if (!this._containsUnderlyingTypes) {
            if (waitForSignal) {
                this.error = StatusCode.valueOf(this.jni.JNI_WaitForSignal(timeout));
            } else {
                this.error = StatusCode.valueOf(this.jni.JNI_RefreshSignal(timeout));
            }

            if (!this.error.isError()) {
                this.baseValue = this.jni.value;
                this.timestamps.update(this.jni.swtimeStampSeconds, Timestamp.TimestampSource.System, true, this.jni.hwtimeStampSeconds, Timestamp.TimestampSource.CANivore, true, this.jni.ecutimeStampSeconds, Timestamp.TimestampSource.Device, this.jni.ecutimeStampSeconds != 0.0);
            }
        }
    }

    private void updateValue(boolean waitForSignal, double timeout, boolean reportError) {
        this._reportIfOldFunc.run();
        if (this._containsUnderlyingTypes) {
            this.refreshMappable(waitForSignal, timeout);
        } else {
            this.refreshNonmappable(waitForSignal, timeout);
        }

        if (reportError && !this.error.isOK()) {
            String var10000 = this.deviceIdentifier.toString();
            String device = var10000 + " Status Signal " + this.signalName;
            ErrorReportingJNI.reportStatusCode(this.error.value, device);
        }

    }

    public StatusSignal<T> refresh(boolean reportError) {
        if(RobotBase.isReal()) {
            this.updateValue(false, 0.0, reportError);
        } else {
            this.updateValue(false, 0.0, reportError);
        }
        return this;
    }

    public StatusSignal<T> refresh() {
        return this.refresh(true);
    }

    public StatusSignal<T> waitForUpdate(double timeoutSec, boolean reportError) {
        this.updateValue(true, timeoutSec, reportError);
        return this;
    }

    public StatusSignal<T> waitForUpdate(double timeoutSec) {
        return this.waitForUpdate(timeoutSec, true);
    }

    public StatusCode setUpdateFrequency(double frequencyHz, double timeoutSeconds) {
        return this._containsUnderlyingTypes ? ((StatusSignal)this._basicTypeMap.values().iterator().next()).setUpdateFrequency(frequencyHz, timeoutSeconds) : StatusCode.valueOf(this.jni.JNI_SetUpdateFrequency(frequencyHz, timeoutSeconds));
    }

    public double getAppliedUpdateFrequency() {
        return this._containsUnderlyingTypes ? ((StatusSignal)this._basicTypeMap.values().iterator().next()).getAppliedUpdateFrequency() : this.jni.JNI_GetAppliedUpdateFrequency();
    }

    public SignalMeasurement<T> getDataCopy() {
        SignalMeasurement<T> toRet = new SignalMeasurement();
        toRet.value = this.getValue();
        toRet.status = this.getStatus();
        toRet.units = this.getUnits();
        toRet.timestamp = this.getTimestamp().getTime();
        return toRet;
    }

    public static class SignalMeasurement<L> {
        public L value;
        public double timestamp;
        public StatusCode status;
        public String units;

        public SignalMeasurement() {
        }
    }
}
