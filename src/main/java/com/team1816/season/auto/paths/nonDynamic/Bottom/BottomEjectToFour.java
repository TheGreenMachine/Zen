package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomEjectToFour extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(Constants.bottomEjectPosition, Rotation2d.fromDegrees(0)),
            new Pose2d(5.50, 1.57, Rotation2d.fromDegrees(0)),
            new Pose2d(Constants.centerNoteFourPosition, Rotation2d.fromDegrees(10))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(0),
            Rotation2d.fromDegrees(0),
            Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
