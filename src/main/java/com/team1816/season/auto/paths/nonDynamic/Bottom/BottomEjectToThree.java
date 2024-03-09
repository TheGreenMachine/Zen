package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomEjectToThree extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(Constants.bottomEjectPosition, Rotation2d.fromDegrees(0)),
            new Pose2d(7.00, 2.32, Rotation2d.fromDegrees(60)),
            new Pose2d(Constants.centerNoteThreePosition, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(0),
            Rotation2d.fromDegrees(0),
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
