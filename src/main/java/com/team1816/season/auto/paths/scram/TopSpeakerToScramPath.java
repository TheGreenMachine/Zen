package com.team1816.season.auto.paths.scram;

import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToScramPath extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(Constants.topSpeakerPosition, Rotation2d.fromDegrees(0)),
                new Pose2d(Constants.topScramPosition, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(-5)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}