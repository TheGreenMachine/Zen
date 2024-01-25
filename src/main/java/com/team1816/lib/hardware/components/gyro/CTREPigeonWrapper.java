package com.team1816.lib.hardware.components.gyro;

import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.team1816.lib.util.Util;

public class CTREPigeonWrapper implements IPigeonIMU {
    Pigeon2 pigeon;

    public CTREPigeonWrapper(Pigeon2 pigeon) {
        this.pigeon = pigeon;
    }
    @Override
    public double getYawValue() {
        return pigeon.getYaw().getValueAsDouble();
    }

    @Override
    public double getPitchValue() {
        return pigeon.getPitch().getValueAsDouble();
    }

    @Override
    public double getRollValue() {
        return pigeon.getRoll().getValueAsDouble();
    }

    @Override
    public double[] getAcceleration() {
        return new double[]{
                pigeon.getAccelerationX().getValueAsDouble(),
                pigeon.getAccelerationY().getValueAsDouble(),
                pigeon.getAccelerationZ().getValueAsDouble()
        };
    }

    @Override
    public void set_Yaw(double angle) {
        pigeon.setYaw(angle);
    }

    @Override
    public boolean hasResetOccurred() {
        return pigeon.hasResetOccurred();
    }

    @Override
    public void configFactoryDefaults() {
        pigeon.getConfigurator().apply(new Pigeon2Configuration());
    }

    @Override
    public void set_StatusFramePeriod(PigeonIMU_StatusFrame statusFrame, int periodMs) {
        BaseStatusSignal.setUpdateFrequencyForAll(
                Util.msToHz(periodMs),
                pigeon.getAccelerationX(), //Don't you just love varargs? i don't
                pigeon.getAccelerationY(),
                pigeon.getAccelerationZ(),
                pigeon.getRoll(),
                pigeon.getPitch(),
                pigeon.getYaw(),
                pigeon.getAngularVelocityZWorld() //Used internally in Pigeon2
        );
    }
}
