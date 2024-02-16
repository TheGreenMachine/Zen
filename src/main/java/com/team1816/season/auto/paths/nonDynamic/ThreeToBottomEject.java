package com.team1816.season.auto.paths.nonDynamic;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ThreeToBottomEject extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(8.02, 3.76, Rotation2d.fromDegrees(70)),
            new Pose2d(7.55, 2.59, Rotation2d.fromDegrees(65)),
            new Pose2d(6.9, 1.53, Rotation2d.fromDegrees(40)),
            new Pose2d(5.81, 1.21, Rotation2d.fromDegrees(-5)),
            new Pose2d(4.63, 1.57, Rotation2d.fromDegrees(-20))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(180),
            Rotation2d.fromDegrees(180),
            Rotation2d.fromDegrees(180),
            Rotation2d.fromDegrees(180),
            Rotation2d.fromDegrees(180)
        );
    }

    @Override
    protected boolean usingApp() {
        return false;
    }
}
