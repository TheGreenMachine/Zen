package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class AmpToNoteTwoPath extends DynamicAutoPath {
    public AmpToNoteTwoPath() {
        super.setAmpPath(true);
    }
    public AmpToNoteTwoPath(Color color) {
        super(color);
        super.setAmpPath(true);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(1.82, 7.73, Rotation2d.fromDegrees(-90)),
                new Pose2d(1.82, 7.28, Rotation2d.fromDegrees(-90)),
                new Pose2d(2.57, 5.58, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
