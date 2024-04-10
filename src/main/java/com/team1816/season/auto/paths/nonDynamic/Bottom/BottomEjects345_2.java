package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomEjects345_2 extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(7.77, 0.84, Rotation2d.fromDegrees(76)),
            new Pose2d(8.09, 2.00, Rotation2d.fromDegrees(76))

        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(76),
            Rotation2d.fromDegrees(76)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
