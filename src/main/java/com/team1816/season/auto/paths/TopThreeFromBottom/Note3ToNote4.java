package com.team1816.season.auto.paths.TopThreeFromBottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class Note3ToNote4 extends AutoPath {

    public Note3ToNote4(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(8.28, 4.12, Rotation2d.fromDegrees(140)),
                new Pose2d(5.61, 6.33, Rotation2d.fromDegrees(180)),
                new Pose2d(5.61, 6.34, Rotation2d.fromDegrees(0)),
                new Pose2d(8.28, 5.81, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
