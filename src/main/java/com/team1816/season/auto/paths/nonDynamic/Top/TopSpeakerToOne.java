package com.team1816.season.auto.paths.nonDynamic.Top;

import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToOne extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(Constants.topSpeakerPosition, Rotation2d.fromDegrees(60)),
            new Pose2d(2.23, 7.68, Rotation2d.fromDegrees(5)),
            new Pose2d(3.85, 7.54, Rotation2d.fromDegrees(-5)),
            new Pose2d(7.85, 7.52, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(60),
            Rotation2d.fromDegrees(5),
            Rotation2d.fromDegrees(-5),
            Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
