package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class EjectBottomMiddleThree extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(0.74, 4.27, Rotation2d.fromDegrees(-61)),
            new Pose2d(3.52, 1.81, Rotation2d.fromDegrees(-20)),
            new Pose2d(6.39, 2.04, Rotation2d.fromDegrees(20)),
            new Pose2d(7.88, 2.37, Rotation2d.fromDegrees(5))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-61),
            Rotation2d.fromDegrees(-20),
            Rotation2d.fromDegrees(20),
            Rotation2d.fromDegrees(5)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
