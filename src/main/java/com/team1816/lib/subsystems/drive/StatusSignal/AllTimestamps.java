package com.team1816.lib.subsystems.drive.StatusSignal;

public class AllTimestamps implements Cloneable {
    private Timestamp systemTimestamp = new Timestamp();
    private Timestamp canivoreTimestamp = new Timestamp();
    private Timestamp deviceTimestamp = new Timestamp();

    public AllTimestamps() {
    }

    void update(Timestamp newSystemTimestamp, Timestamp newCanivoreTimestamp, Timestamp newDeviceTimestamp) {
        this.systemTimestamp = newSystemTimestamp;
        this.canivoreTimestamp = newCanivoreTimestamp;
        this.deviceTimestamp = newDeviceTimestamp;
    }

    void update(double systemTimestampSeconds, Timestamp.TimestampSource systemTimestampSource, boolean systemTimestampValid, double canivoreTimestampSeconds, Timestamp.TimestampSource canivoreTimestampSource, boolean canivoreTimestampValid, double deviceTimestampSeconds, Timestamp.TimestampSource deviceTimestampSource, boolean deviceTimestampValid) {
        this.systemTimestamp.update(systemTimestampSeconds, systemTimestampSource, systemTimestampValid);
        this.canivoreTimestamp.update(canivoreTimestampSeconds, canivoreTimestampSource, canivoreTimestampValid);
        this.deviceTimestamp.update(deviceTimestampSeconds, deviceTimestampSource, deviceTimestampValid);
    }

    public Timestamp getBestTimestamp() {
        if (this.deviceTimestamp.isValid()) {
            return this.deviceTimestamp;
        } else {
            return this.canivoreTimestamp.isValid() ? this.canivoreTimestamp : this.systemTimestamp;
        }
    }

    public Timestamp getSystemTimestamp() {
        return this.systemTimestamp;
    }

    public Timestamp getCANivoreTimestamp() {
        return this.canivoreTimestamp;
    }

    public Timestamp getDeviceTimestamp() {
        return this.deviceTimestamp;
    }

    public AllTimestamps clone() {
        AllTimestamps toReturn = new AllTimestamps();
        toReturn.update(this.systemTimestamp.getTime(), this.systemTimestamp.getSource(), this.systemTimestamp.isValid(), this.canivoreTimestamp.getTime(), this.canivoreTimestamp.getSource(), this.canivoreTimestamp.isValid(), this.deviceTimestamp.getTime(), this.deviceTimestamp.getSource(), this.deviceTimestamp.isValid());
        return toReturn;
    }
}
