package com.team1816.season.auto.paths.toNoteThree;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class MiddleSpeakerToNoteThreePath extends DynamicAutoPath {
    public MiddleSpeakerToNoteThreePath() {}
    public MiddleSpeakerToNoteThreePath(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(1.4, 5.62, Rotation2d.fromDegrees(0)),
                new Pose2d(2.2, 4.45, Rotation2d.fromDegrees(-60)),
                new Pose2d(2.75, 4.13, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
