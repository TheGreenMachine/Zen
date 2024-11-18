//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.team1816.lib.subsystems.drive.StatusSignal;

import com.ctre.phoenix6.Utils;

import java.util.HashMap;

public class Timestamp {
    private double time;
    private TimestampSource source;
    private boolean valid;

    Timestamp(double time, TimestampSource source) {
        this(time, source, true);
    }

    Timestamp(double time, TimestampSource source, boolean valid) {
        this.update(time, source, valid);
    }

    Timestamp() {
        this.valid = false;
    }

    void update(double time, TimestampSource source, boolean valid) {
        this.time = time;
        this.source = source;
        this.valid = valid;
    }

    public double getTime() {
        return this.time;
    }

    public TimestampSource getSource() {
        return this.source;
    }

    public double getLatency() {
        return Utils.getCurrentTimeSeconds() - this.time;
    }

    public boolean isValid() {
        return this.valid;
    }

    public static enum TimestampSource {
        System(0),
        CANivore(1),
        Device(2);

        public final int value;
        private static HashMap<Integer, TimestampSource> _map = null;

        private TimestampSource(int initValue) {
            this.value = initValue;
        }

        public static TimestampSource valueOf(int value) {
            TimestampSource retval = (TimestampSource)_map.get(value);
            return retval != null ? retval : values()[0];
        }

        static {
            _map = new HashMap();
            TimestampSource[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                TimestampSource type = var0[var2];
                _map.put(type.value, type);
            }

        }
    }
}
