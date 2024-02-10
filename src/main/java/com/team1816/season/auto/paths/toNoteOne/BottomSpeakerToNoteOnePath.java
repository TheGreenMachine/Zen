package com.team1816.season.auto.paths.toNoteOne;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomSpeakerToNoteOnePath extends DynamicAutoPath {

    public BottomSpeakerToNoteOnePath() {}
    public BottomSpeakerToNoteOnePath(Color color) {super(color);}


    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.72, 4.30, Rotation2d.fromDegrees(-20)),
                new Pose2d(1.70, 4.20, Rotation2d.fromDegrees(82)),
                new Pose2d(1.58, 6.31, Rotation2d.fromDegrees(82)),
                new Pose2d(2.57, 7.02, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-60),
                Rotation2d.fromDegrees(super.isReversed ? -55 : 0),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}