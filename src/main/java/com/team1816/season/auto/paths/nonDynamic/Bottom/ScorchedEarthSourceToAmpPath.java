package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthSourceToAmpPath extends AutoPath {
    public ScorchedEarthSourceToAmpPath(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(0.48, 2.06, Rotation2d.fromDegrees(0)),
                new Pose2d(6.94, 1.03, Rotation2d.fromDegrees(-5)),
                new Pose2d(8.29, 1.15, Rotation2d.fromDegrees(90)),
                new Pose2d(8.29, 7.46, Rotation2d.fromDegrees(90)),
                new Pose2d(7, 7.46, Rotation2d.fromDegrees(180))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(35),
                Rotation2d.fromDegrees(35),
                Rotation2d.fromDegrees(35),
                Rotation2d.fromDegrees(35)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}