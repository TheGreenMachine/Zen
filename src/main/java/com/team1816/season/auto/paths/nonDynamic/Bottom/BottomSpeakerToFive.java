package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomSpeakerToFive extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(Constants.bottomSpeakerPosition, Rotation2d.fromDegrees(-60)),
            new Pose2d(2.33, 2.56, Rotation2d.fromDegrees(-35)),
            new Pose2d(4.79, 1.47, Rotation2d.fromDegrees(-20)),
            new Pose2d(7.77, 0.84, Rotation2d.fromDegrees(-10))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-60),
            Rotation2d.fromDegrees(-35),
            Rotation2d.fromDegrees(-20),
            Rotation2d.fromDegrees(-10)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
