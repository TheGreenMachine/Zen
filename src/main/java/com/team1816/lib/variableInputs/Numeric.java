package com.team1816.lib.variableInputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;

import java.lang.constant.Constable;
import java.util.Optional;


public class Numeric extends Number
        implements VariableInput, Comparable<Double>, Constable {

    //I'm so cooked

    private final String smartDashboardReference;
    private final double defaultValue;

    protected Numeric(String smartDashboardReference, double defaultValue) {
        this.smartDashboardReference = smartDashboardReference;
        this.defaultValue = defaultValue;
        SmartDashboard.putNumber(smartDashboardReference, defaultValue);
    }

    private double getValue() {
        return SmartDashboard.getNumber(smartDashboardReference, defaultValue); //TODO maybe add an FMS connectivity check.
    }

    @Override
    public int intValue() {
        return (int) getValue();
    }

    @Override
    public long longValue() {
        return (long) getValue();
    }

    @Override
    public float floatValue() {
        return (float) getValue();
    }

    @Override
    public double doubleValue() {
        return getValue();
    }

    @Override
    public int compareTo(@NotNull Double anotherDouble) {
        return Double.compare(getValue(), anotherDouble);
    }

    public int compareTo(@NotNull Numeric anotherNumeric) {
        return Double.compare(getValue(), anotherNumeric.getValue());
    }

    @Override
    public Optional<Double> describeConstable() {
        return Optional.of(this.getValue());
    }

    @Override
    public double toNumber() {
        return getValue();
    }

    @Override
    public String toString() {
        return Double.toString(getValue());
    }

    @Override
    public String toDescriptiveString() {
        return String.format("Numeric(Reference: %s, DefaultValue: %f, CurrentValue: %b)",
                smartDashboardReference, defaultValue, getValue()) ;
    }
}
