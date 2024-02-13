package com.team1816.lib.auto;

import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.paths.toNoteTwo.MiddleSpeakerToNoteTwoPath;
import org.apache.commons.math3.util.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static com.team1816.season.auto.AutoModeManager.Position;

/**
 * Holder class for the dynamic auto lookup table
 * @see DynamicAutoPath
 * @see Position
 */
public class DynamicAutoUtil {
    static Callable<DynamicAutoPath> defaultPathGetter = MiddleSpeakerToNoteTwoPath::new;

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

    private static HashMap<Pair<Position, Position>, Callable<DynamicAutoPath>> pathLookup = new HashMap<>() {};

    public static boolean putToTable(Position startPosition, Position endPosition, Callable<DynamicAutoPath> pathGetter) {
       pathLookup.putIfAbsent(new Pair<>(startPosition, endPosition), pathGetter);
       return true;
    }

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

    public static Optional<DynamicAutoPath> getReversedDynamicPath(Position startPosition, Position endPosition, Color color) {
        var path = getDynamicPath(endPosition, startPosition, color);

        return path.map(DynamicAutoPath::withInversedWaypoints);
    }

    public static List<TrajectoryAction> encapsulateAutoPaths(@Nonnull List<DynamicAutoPath> paths) {
        List<TrajectoryAction> trajectories = new ArrayList<>();

        paths.forEach(path -> {
            trajectories.add(new TrajectoryAction(path));
        });
        return trajectories;
    }

}
