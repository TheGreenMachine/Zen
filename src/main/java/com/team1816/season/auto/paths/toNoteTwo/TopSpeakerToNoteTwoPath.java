package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToNoteTwoPath extends DynamicAutoPath {
    public TopSpeakerToNoteTwoPath(){}
    public TopSpeakerToNoteTwoPath(Color color){super(color);}

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.70, 6.82, Rotation2d.fromDegrees(0)),
                new Pose2d(1.57, 6.82, Rotation2d.fromDegrees(0)),
                new Pose2d(2.12, 5.58, Rotation2d.fromDegrees(0)),
                new Pose2d(2.57, 5.58, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(super.isReversed ? 60 : 0),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
