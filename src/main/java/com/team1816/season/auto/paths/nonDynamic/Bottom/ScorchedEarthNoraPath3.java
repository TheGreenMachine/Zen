package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthNoraPath3 extends AutoPath {
    public ScorchedEarthNoraPath3(Color color){
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(2.83, 4.06, Rotation2d.fromDegrees(-179)),
                new Pose2d(0.5, 4.09, Rotation2d.fromDegrees(178))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(-65)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
