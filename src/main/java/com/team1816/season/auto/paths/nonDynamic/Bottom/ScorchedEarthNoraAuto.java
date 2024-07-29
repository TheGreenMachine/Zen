package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthNoraAuto extends AutoPath {
    public ScorchedEarthNoraAuto(Color color){
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.95, 7.64, Rotation2d.fromDegrees(0)),
                new Pose2d(8.47, 6.1, Rotation2d.fromDegrees(-78)),
                new Pose2d(8.31, 1.78, Rotation2d.fromDegrees(-95)),
                new Pose2d(5.06, 0.86, Rotation2d.fromDegrees(-11))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
        Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
