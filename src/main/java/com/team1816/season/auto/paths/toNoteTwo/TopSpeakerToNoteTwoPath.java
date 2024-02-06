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
                new Pose2d(0.77, 6.76, Rotation2d.fromDegrees(-30)),
                new Pose2d(1.10, 6.57, Rotation2d.fromDegrees(-30)),
                new Pose2d(2.32, 5.87, Rotation2d.fromDegrees(-30)),
                new Pose2d(2.87, 5.56, Rotation2d.fromDegrees(-30))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
