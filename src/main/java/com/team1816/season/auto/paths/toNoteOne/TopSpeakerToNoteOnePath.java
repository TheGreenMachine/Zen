package com.team1816.season.auto.paths.toNoteOne;

import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToNoteOnePath extends DynamicAutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(0.71, 6.76, Rotation2d.fromDegrees(0)),
                new Pose2d(1.91, 7.03, Rotation2d.fromDegrees(0)),
                new Pose2d(2.75, 7.03, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
