package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import com.team1816.lib.auto.Color;

import java.util.List;

public class ScorchedEarthNoahAuto extends AutoPath {
    public ScorchedEarthNoahAuto(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.31, 7.52, Rotation2d.fromDegrees(0)),
                new Pose2d(6.83, 7.57, Rotation2d.fromDegrees(0)),
                new Pose2d(8.47, 6.79, Rotation2d.fromDegrees(-87)),
                new Pose2d(8.59, 0.98, Rotation2d.fromDegrees(-87)),
                new Pose2d(7.35, 0.58, Rotation2d.fromDegrees(178)),
                new Pose2d(4.7, 0.57, Rotation2d.fromDegrees(177))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(36+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
