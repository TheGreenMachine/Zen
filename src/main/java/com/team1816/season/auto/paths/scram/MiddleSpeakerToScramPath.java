package com.team1816.season.auto.paths.scram;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class MiddleSpeakerToScramPath extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.4, 5.55, Rotation2d.fromDegrees(-69)),
                new Pose2d(7.95, 0.77, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
