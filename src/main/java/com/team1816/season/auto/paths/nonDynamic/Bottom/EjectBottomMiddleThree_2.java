package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class EjectBottomMiddleThree_2 extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(7.88, 2.37, Rotation2d.fromDegrees(-175)),
            new Pose2d(6.91, 2.92, Rotation2d.fromDegrees(135))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(5),
            Rotation2d.fromDegrees(-45)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
