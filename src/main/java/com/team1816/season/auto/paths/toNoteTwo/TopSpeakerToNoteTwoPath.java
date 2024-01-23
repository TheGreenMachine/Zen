package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToNoteTwoPath extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(0.77, 6.76, Rotation2d.fromDegrees(-30)),
                new Pose2d(2.32, 5.87, Rotation2d.fromDegrees(-30)),
                new Pose2d(2.87, 5.56, Rotation2d.fromDegrees(-30))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return false;
    }
}
