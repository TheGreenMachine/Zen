package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthNoraPath2 extends AutoPath {
    public ScorchedEarthNoraPath2(Color color){
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                        new Pose2d(5.06, 0.86, Rotation2d.fromDegrees(-164)),
                        new Pose2d(2.83, 4.06, Rotation2d.fromDegrees(10))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
