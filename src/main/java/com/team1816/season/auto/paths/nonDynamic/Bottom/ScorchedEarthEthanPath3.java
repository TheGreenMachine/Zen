package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthEthanPath3 extends AutoPath {
    public ScorchedEarthEthanPath3(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(2.57, 4.14, Rotation2d.fromDegrees(173)),
                new Pose2d(0.75, 4.36, Rotation2d.fromDegrees(173))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(-55)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
