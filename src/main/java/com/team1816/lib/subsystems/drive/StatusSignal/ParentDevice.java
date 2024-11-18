//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.team1816.lib.subsystems.drive.StatusSignal;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.EmptyControl;
import com.ctre.phoenix6.jni.CtreJniWrapper;
import com.ctre.phoenix6.jni.ErrorReportingJNI;
import com.ctre.phoenix6.jni.StatusSignalJNI;
import com.ctre.phoenix6.spns.SpnValue;
import com.ctre.phoenix6.unmanaged.Unmanaged;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

public abstract class ParentDevice extends CtreJniWrapper {
    protected static final EmptyControl _emptyControl = new EmptyControl();
    protected final DeviceIdentifier deviceIdentifier;
    private final Map<Integer, BaseStatusSignal> _signalValues = new ConcurrentHashMap();
    private ControlRequest _controlReq;
    private final Lock _controlReqLck;
    private final double _creationTime;
    private StatusCode _versionStatus;
    private double _timeToRefreshVersion;
    private final StatusSignal<Integer> _compliancy;
    private final StatusSignal<Integer> _resetSignal;

    private void reportIfTooOld() {
        if (!this._versionStatus.isOK()) {
            if (this._compliancy != null) {
                double currentTime = Utils.getCurrentTimeSeconds();
                if (currentTime >= this._timeToRefreshVersion) {
                    this._timeToRefreshVersion = currentTime + 0.25;
                    this._compliancy.refresh(false);
                    StatusCode code = StatusCode.OK;
                    if (this._compliancy.getStatus().isOK()) {
                        int firmwareCompliancy = (Integer)this._compliancy.getValue();
                        int apiCompliancy = Unmanaged.getApiCompliancy();
                        if (apiCompliancy > firmwareCompliancy) {
                            code = StatusCode.FirmwareTooOld;
                        } else if (apiCompliancy < firmwareCompliancy) {
                            code = StatusCode.ApiTooOld;
                        }
                    } else {
                        code = StatusCode.CouldNotRetrieveV6Firmware;
                    }

                    double deltaTimeSec = currentTime - this._creationTime;
                    if (!code.isOK() && (code == StatusCode.FirmwareTooOld || code == StatusCode.ApiTooOld || deltaTimeSec >= 3.0)) {
                        ErrorReportingJNI.reportStatusCode(code.value, this.deviceIdentifier.toString());
                    }

                    this._versionStatus = code;
                }

            }
        }
    }

    public ParentDevice(int deviceID, String model, String canbus) {
        this._controlReq = _emptyControl;
        this._controlReqLck = new ReentrantLock();
        this._creationTime = Utils.getCurrentTimeSeconds();
        this._versionStatus = StatusCode.CouldNotRetrieveV6Firmware;
        this._timeToRefreshVersion = Utils.getCurrentTimeSeconds();
        this.deviceIdentifier = new DeviceIdentifier(deviceID, model, canbus);
        this._compliancy = this.lookupStatusSignal(SpnValue.Compliancy_Version.value, Integer.class, "Compliancy", false);
        this._resetSignal = this.lookupStatusSignal(SpnValue.Startup_ResetFlags.value, Integer.class, "ResetFlags", false);
    }

    public int getDeviceID() {
        return this.deviceIdentifier.deviceID;
    }

    public String getNetwork() {
        return this.deviceIdentifier.network;
    }

    public long getDeviceHash() {
        return (long)this.deviceIdentifier.deviceHash;
    }

    public ControlRequest getAppliedControl() {
        return this._controlReq;
    }

    public boolean hasResetOccurred() {
        return this._resetSignal.refresh(false).hasUpdated();
    }

    public BooleanSupplier getResetOccurredChecker() {
        StatusSignal<Integer> resetSignal = this._resetSignal.clone();
        return () -> {
            return resetSignal.refresh(false).hasUpdated();
        };
    }

    public StatusCode optimizeBusUtilization() {
        return this.optimizeBusUtilization(0.05);
    }

    public StatusCode optimizeBusUtilization(double timeoutSeconds) {
        return StatusCode.valueOf(StatusSignalJNI.JNI_OptimizeUpdateFrequencies(this.deviceIdentifier.getNetwork(), this.deviceIdentifier.getDeviceHash(), timeoutSeconds));
    }

    public static StatusCode optimizeBusUtilizationForAll(ParentDevice... devices) {
        StatusCode retval = StatusCode.OK;
        ParentDevice[] var2 = devices;
        int var3 = devices.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ParentDevice device = var2[var4];
            StatusCode err = device.optimizeBusUtilization();
            if (retval.isOK()) {
                retval = err;
            }
        }

        return retval;
    }

    protected StatusCode setControlPrivate(ControlRequest request) {
        this._controlReqLck.lock();

        StatusCode status;
        try {
            this.reportIfTooOld();
            if (!this._versionStatus.isOK() && this._compliancy.getStatus().isOK()) {
                this._controlReq = _emptyControl;
                _emptyControl.sendRequest(this.deviceIdentifier.network, this.deviceIdentifier.deviceHash, true);
                status = this._versionStatus;
            } else {
                boolean cancelOtherRequests = false;
                if (request.getName() != this.getAppliedControl().getName()) {
                    cancelOtherRequests = true;
                }

                this._controlReq = request;
                status = this._controlReq.sendRequest(this.deviceIdentifier.network, this.deviceIdentifier.deviceHash, cancelOtherRequests);
            }
        } finally {
            this._controlReqLck.unlock();
        }

        if (!status.isOK()) {
            int var10000 = status.value;
            String var10001 = this.deviceIdentifier.toString();
            ErrorReportingJNI.reportStatusCode(var10000, var10001 + " SetControl " + request.getName());
        }

        return status;
    }

    private <T> StatusSignal<T> commonLookup(int spn, Class<T> classOfSignal, int mapIter, MapGenerator<T> generator, String signalName, boolean reportOnConstruction) {
        int totalHash = spn | mapIter << 16;
        BaseStatusSignal toFind;
        if (this._signalValues.containsKey(totalHash)) {
            toFind = (BaseStatusSignal)this._signalValues.get(totalHash);
            reportOnConstruction = true;
        } else {
            if (mapIter == 0) {
                this._signalValues.put(totalHash, new StatusSignal(this.deviceIdentifier, spn, () -> {
                    this.reportIfTooOld();
                }, classOfSignal, signalName));
            } else {
                this._signalValues.put(totalHash, new StatusSignal(this.deviceIdentifier, spn, () -> {
                    this.reportIfTooOld();
                }, classOfSignal, generator, signalName));
            }

            toFind = (BaseStatusSignal)this._signalValues.get(totalHash);
        }

        StatusSignal<T> toReturn = (StatusSignal)StatusSignal.class.cast(toFind);
        if (toReturn == null) {
            return new StatusSignal(classOfSignal, StatusCode.InvalidParamValue);
        } else if (toReturn.getTypeClass().equals(classOfSignal)) {
            toReturn.refresh(reportOnConstruction);
            return toReturn;
        } else {
            return new StatusSignal(classOfSignal, StatusCode.InvalidParamValue);
        }
    }

    protected <T> StatusSignal<T> lookupStatusSignal(int spn, Class<T> classOfSignal, String signalName, boolean reportOnConstruction) {
        return this.commonLookup(spn, classOfSignal, 0, (MapGenerator)null, signalName, reportOnConstruction);
    }

    protected <T> StatusSignal<T> lookupStatusSignal(int spn, Class<T> classOfSignal, int mapIter, MapGenerator<T> generator, String signalName, boolean reportOnConstruction) {
        return this.commonLookup(spn, classOfSignal, mapIter, generator, signalName, reportOnConstruction);
    }

    public interface MapGenerator<T> {
        Map<Integer, StatusSignal<T>> run();
    }
}
