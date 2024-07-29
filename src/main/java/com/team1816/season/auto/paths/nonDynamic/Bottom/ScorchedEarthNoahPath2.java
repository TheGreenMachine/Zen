package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthNoahPath2 extends AutoPath {
    public ScorchedEarthNoahPath2(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(4.7, 0.57, Rotation2d.fromDegrees(177)),
                new Pose2d(1.71, 2.48, Rotation2d.fromDegrees(93)),
                new Pose2d(2.45, 3.97, Rotation2d.fromDegrees(29))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(90-80),
                Rotation2d.fromDegrees(90-80)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
