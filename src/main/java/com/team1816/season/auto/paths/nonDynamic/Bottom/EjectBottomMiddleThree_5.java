package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class EjectBottomMiddleThree_5 extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(7.9, 3.98, Rotation2d.fromDegrees(-165)),
            new Pose2d(7.27, 3.89, Rotation2d.fromDegrees(175)),
            new Pose2d(6.6, 4.23, Rotation2d.fromDegrees(140))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(15),
            Rotation2d.fromDegrees(-5),
            Rotation2d.fromDegrees(-30)
        );
    }

    @Override
    protected boolean usingApp() {
        return false;
    }
}
