package com.team1816.season.auto.paths.scram;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class AmpToScramPath extends AutoPath {
    public AmpToScramPath(Color color) {
        super(color);
    }


    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.86, 7.74, Rotation2d.fromDegrees(-19)),
                new Pose2d(3.6, 7.26, Rotation2d.fromDegrees(0)),
                new Pose2d(5.43, 7.26, Rotation2d.fromDegrees(0)),
                new Pose2d(7.87, 7.47, Rotation2d.fromDegrees(0))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
