//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.team1816.lib.subsystems.drive.StatusSignal;

import com.ctre.phoenix6.hardware.jni.HardwareJNI;

public class DeviceIdentifier {
    String network;
    String model;
    String tostring = null;
    int deviceID;
    int deviceHash;

    public DeviceIdentifier() {
        this.network = "";
        this.model = "";
        this.deviceID = 0;
        this.deviceHash = 0;
    }

    public DeviceIdentifier(int deviceID, String model, String canbus) {
        this.network = canbus;
        this.model = model;
        this.deviceID = deviceID;
        this.deviceHash = HardwareJNI.getDeviceHash(deviceID, model, canbus);
    }

    public String getNetwork() {
        return this.network;
    }

    public String getModel() {
        return this.model;
    }

    public int getDeviceId() {
        return this.deviceID;
    }

    public int getDeviceHash() {
        return this.deviceHash;
    }

    public String toString() {
        if (this.tostring == null) {
            this.tostring = this.model + " " + this.deviceID + " (" + this.network + ")";
        }

        return this.tostring;
    }
}
