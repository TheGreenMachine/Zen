package com.team1816.season.auto.paths.nonDynamic;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomEjectToFour extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(4.63, 1.57, Rotation2d.fromDegrees(-20)),
            new Pose2d(6.42, 1.47, Rotation2d.fromDegrees(20)),
            new Pose2d(7.85, 2.2, Rotation2d.fromDegrees(30))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-20),
            Rotation2d.fromDegrees(20),
            Rotation2d.fromDegrees(30)
        );
    }

    @Override
    protected boolean usingApp() {
        return false;
    }
}
