package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthSkipOneBottom extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(0.75, 2.4, Rotation2d.fromDegrees(-30)),
            new Pose2d(5.61, 0.9, Rotation2d.fromDegrees(0)),
            new Pose2d(6.79, 1.11, Rotation2d.fromDegrees(20)),
            new Pose2d(8.34, 1.85, Rotation2d.fromDegrees(50)),
            new Pose2d(8.72, 3.97, Rotation2d.fromDegrees(90)),
            new Pose2d(8.68, 5.85, Rotation2d.fromDegrees(90)),
            new Pose2d(8.68, 7.41, Rotation2d.fromDegrees(90))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-30),
            Rotation2d.fromDegrees(0),
            Rotation2d.fromDegrees(65),
            Rotation2d.fromDegrees(69),
            Rotation2d.fromDegrees(70),
            Rotation2d.fromDegrees(70),
            Rotation2d.fromDegrees(70)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
