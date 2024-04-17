package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class EjectBottomMiddleThree_4 extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(5.58, 3.97, Rotation2d.fromDegrees(-20)),
            new Pose2d(6.7, 3.76, Rotation2d.fromDegrees(0)),
            new Pose2d(7.9, 3.98, Rotation2d.fromDegrees(15))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-20),
            Rotation2d.fromDegrees(0),
            Rotation2d.fromDegrees(15)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
