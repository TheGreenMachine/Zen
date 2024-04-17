package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthAmpToSource2Path extends AutoPath {
    public ScorchedEarthAmpToSource2Path(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.4, 7.29, Rotation2d.fromDegrees(0)),
                new Pose2d(7.05, 7.47, Rotation2d.fromDegrees(0)),
                new Pose2d(8.29, 6.44, Rotation2d.fromDegrees(-90)),
                new Pose2d(8.29, 0.69, Rotation2d.fromDegrees(-90)),
                new Pose2d(7, 0.71, Rotation2d.fromDegrees(180))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

}
