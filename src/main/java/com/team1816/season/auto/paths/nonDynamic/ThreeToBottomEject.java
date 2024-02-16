package com.team1816.season.auto.paths.nonDynamic;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ThreeToBottomEject extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(8.02, 3.76, Rotation2d.fromDegrees(0)),
            new Pose2d(7.55, 2.59, Rotation2d.fromDegrees(0)),
            new Pose2d(6.9, 1.53, Rotation2d.fromDegrees(0)),
            new Pose2d(5.81, 1.21, Rotation2d.fromDegrees(0)),
            new Pose2d(4.63, 1.57, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return null;
//        return List.of(
//            Rotation2d.fromDegrees(-110),
//            Rotation2d.fromDegrees(-115),
//            Rotation2d.fromDegrees(-140),
//            Rotation2d.fromDegrees(175),
//            Rotation2d.fromDegrees(160)
//        );
    }

    @Override
    protected boolean usingApp() {
        return false;
    }
}
