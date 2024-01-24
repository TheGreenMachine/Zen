package com.team1816.season.auto.paths.toNoteThree;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToNoteThreePath extends AutoPath {
    public TopSpeakerToNoteThreePath() {

    }
    public TopSpeakerToNoteThreePath(Color color) {
        super(color);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(0.71, 6.76, Rotation2d.fromDegrees(-20)),
                new Pose2d(2.06, 5.12, Rotation2d.fromDegrees(-60)),
                new Pose2d(2.75, 4.13, Rotation2d.fromDegrees(0))
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
