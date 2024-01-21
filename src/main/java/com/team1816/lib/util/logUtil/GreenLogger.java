package com.team1816.lib.util.logUtil;

import com.team1816.season.configuration.Constants;
import edu.wpi.first.util.datalog.*;
import edu.wpi.first.wpilibj.DataLogManager;

import java.util.HashMap;

/**
 * The universal project-wide message logging wrapper.
 * This class utilizes WPI's DataLogManager and is a wrapper for the standard System.out.println()
 *
 * @see DataLogManager
 */
public class GreenLogger {

    public static HashMap<String, DataLogEntry> dynamicLogs = new HashMap<>();

    /**
     * Logs a string message
     *
     * @param s message
     */
    public static void log(String s) {
        if (Constants.kLoggingRobot) {
            DataLogManager.log(s);
        }
    }

    /**
     * Logs a boolean message
     *
     * @param b message
     */
    public static void log(boolean b) {
        if (Constants.kLoggingRobot) {
            DataLogManager.log(String.valueOf(b));
        }
    }

    /**
     * Logs an exception message
     *
     * @param e exception
     */
    public static void log(Exception e) {
        System.out.println(e.getMessage());
    }

    /**
     * Dynamic Log Appending
     */

    public static void appendQuickLog(String logName, double value) {
        DataLogEntry entry = dynamicLogs.get(logName);
        dynamicLogs.putIfAbsent(logName, new DoubleLogEntry(DataLogManager.getLog(), "GreenLogs/" + logName));
        if (entry instanceof DoubleLogEntry) ((DoubleLogEntry) entry).append(value);
    }

    public static void appendQuickLog(String logName, double... value) {
        DataLogEntry entry = dynamicLogs.get(logName);
        dynamicLogs.putIfAbsent(logName, new DoubleArrayLogEntry(DataLogManager.getLog(), "GreenLogs/" + logName));
        if (entry instanceof DoubleArrayLogEntry) ((DoubleArrayLogEntry) entry).append(value);
    }

    public static void appendQuickLog(String logName, boolean value) {
        DataLogEntry entry = dynamicLogs.get(logName);
        dynamicLogs.putIfAbsent(logName, new BooleanLogEntry(DataLogManager.getLog(), "GreenLogs/" + logName));
        if (entry instanceof BooleanLogEntry) ((BooleanLogEntry) entry).append(value);

    }

    public static void appendQuickLog(String logName, Object value) {
        DataLogEntry entry = dynamicLogs.get(logName);
        dynamicLogs.putIfAbsent(logName, new StringLogEntry(DataLogManager.getLog(), "GreenLogs/" + logName));
        if (entry instanceof StringLogEntry) ((StringLogEntry) entry).append(value.toString());
    }

    public static void appendQuickLog(String logName, Object... values) {
        String[] valuesToString = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            valuesToString[i] = values[i].toString();
        }
        DataLogEntry entry = dynamicLogs.get(logName);
        dynamicLogs.putIfAbsent(logName, new StringArrayLogEntry(DataLogManager.getLog(), "GreenLogs/" + logName));
        if (entry instanceof StringArrayLogEntry) ((StringArrayLogEntry) entry).append(valuesToString);
    }

}
