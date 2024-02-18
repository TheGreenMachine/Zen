package com.team1816.season.auto.paths.scram;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class AmpToScramPath extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.86, 7.74, Rotation2d.fromDegrees(-9)),
                new Pose2d(7.2, 7.35, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
