package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthSourceToAmp2Path extends AutoPath {
    public ScorchedEarthSourceToAmp2Path(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.38, 1.61, Rotation2d.fromDegrees(0)),
                new Pose2d(7.02, 1.25, Rotation2d.fromDegrees(-12)),
                new Pose2d(8.65, 1, Rotation2d.fromDegrees(90)),
                new Pose2d(8.65, 7.5, Rotation2d.fromDegrees(90))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-35-90),
                Rotation2d.fromDegrees(-35-90),
                Rotation2d.fromDegrees(-35-90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}