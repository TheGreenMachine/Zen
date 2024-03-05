package com.team1816.season.auto.paths.scram;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToScramPath extends AutoPath {

    public TopSpeakerToScramPath(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(0.62, 6.8, Rotation2d.fromDegrees(0)),
                new Pose2d(7.95, 7.47, Rotation2d.fromDegrees(24))
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