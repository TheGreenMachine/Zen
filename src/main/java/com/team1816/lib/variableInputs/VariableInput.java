package com.team1816.lib.variableInputs;


public interface VariableInput {
    static Numeric number(String smartDashboardReference, double defaultValue) {
        return new Numeric(smartDashboardReference, defaultValue);
    }

    static Numeric number(double value) {
        return new Numeric(null, value);
    }

    double toNumber();

    String toDescriptiveString();
}
