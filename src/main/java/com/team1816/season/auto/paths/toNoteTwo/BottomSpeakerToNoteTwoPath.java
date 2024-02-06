package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomSpeakerToNoteTwoPath extends DynamicAutoPath {
    public BottomSpeakerToNoteTwoPath(){}
    public BottomSpeakerToNoteTwoPath(Color color) {super(color);}

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.77, 4.37, Rotation2d.fromDegrees(30)),
                new Pose2d(1.1, 4.559, Rotation2d.fromDegrees(30)),
                new Pose2d(2.32, 5.25, Rotation2d.fromDegrees(30)),
                new Pose2d(2.87, 5.57, Rotation2d.fromDegrees(30))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-60),
                Rotation2d.fromDegrees(-60),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
