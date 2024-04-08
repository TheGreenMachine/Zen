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
        List.of(
                new Pose2d(1.34, 7.26, Rotation2d.fromDegrees(0)),
                new Pose2d(8.55, 7.5, Rotation2d.fromDegrees(0)),
                new Pose2d(8.56, 7.5, Rotation2d.fromDegrees(-90)),
                new Pose2d(8.55, 0.55, Rotation2d.fromDegrees(-90))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
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
