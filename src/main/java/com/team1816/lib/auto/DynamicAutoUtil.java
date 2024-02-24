package com.team1816.lib.auto;

import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DriveStraightPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.paths.scram.AmpToScramPath;
import com.team1816.season.auto.paths.scram.BottomSpeakerToScramPath;
import com.team1816.season.auto.paths.scram.MiddleSpeakerToScramPath;
import com.team1816.season.auto.paths.scram.TopSpeakerToScramPath;
import com.team1816.season.auto.paths.toNoteTwo.MiddleSpeakerToNoteTwoPath;
import org.apache.commons.math3.util.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static com.team1816.season.auto.AutoModeManager.Position;
import static com.team1816.season.auto.AutoModeManager.robotState;

/**
 * Holder class for the dynamic auto lookup table and utility for accessing it
 * @see DynamicAutoPath
 * @see Position
 */
public class DynamicAutoUtil {

    /**
     * Puts all inputted paths into the lookup table
     * @param paths The paths to register
     */
    public static void registerPaths(List<DynamicAutoPath> paths) {
        paths.forEach(
                path -> {
                    pathLookup.putIfAbsent(
                            new Pair<>(path.startPosition, path.endPosition),
                            path::getInstance
                    );
                }
        );
    }

    /**
     * The lookup table which contains all the Dynamic Paths.
     */
    private static HashMap<Pair<Position, Position>, Callable<DynamicAutoPath>> pathLookup = new HashMap<>() {};

    /**
     * Returns the dynamic path that matches the start and end position
     * @param startPosition The position the path starts at
     * @param endPosition The position the path ends at
     * @param color The current alliance color
     * @return The dynamic path
     */
    public static Optional<DynamicAutoPath> getDynamicPath(Position startPosition, Position endPosition, Color color) {
        var callable = pathLookup.get(new Pair<>(startPosition, endPosition));

        try {
            return Optional.of(
                    callable.call().withColor(color)
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the dynamic path that matches the end and start position, reversed.
     * @param startPosition The position the reversed path starts at
     * @param endPosition The position the reversed path ends at
     * @param color The current alliance color
     * @return The dynamic path
     */
    public static Optional<DynamicAutoPath> getReversedDynamicPath(Position startPosition, Position endPosition, Color color) {
        var path = getDynamicPath(endPosition, startPosition, color);

        return path.map(DynamicAutoPath::withInversedWaypoints);
    }

    /**
     * Converts a list of dynamic paths into a List of Trajectory actions for use in Dynamic Auto Modes
     * @param paths The list of paths
     * @return The paths as trajectory actions
     */
    public static List<TrajectoryAction> encapsulateAutoPaths(@Nonnull List<DynamicAutoPath> paths) {
        List<TrajectoryAction> trajectories = new ArrayList<>();

        paths.forEach(path -> {
            trajectories.add(new TrajectoryAction(path));
        });
        return trajectories;
    }

    /**
     * Returns the Scram path that would go with the passed in path
     * @param path The path from which the scram starts
     * @return The proper scram path
     */
    public static AutoPath getScram(DynamicAutoPath path) {
        return switch(path.startPosition) {
            case TOP_SPEAKER -> new TopSpeakerToScramPath(robotState.allianceColor);
            case MIDDLE_SPEAKER -> new MiddleSpeakerToScramPath(robotState.allianceColor);
            case BOTTOM_SPEAKER -> new BottomSpeakerToScramPath(robotState.allianceColor);
            case AMP -> new AmpToScramPath(robotState.allianceColor);
            default -> new DriveStraightPath(100); //please never let this be called
        };
    }
}
