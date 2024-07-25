package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.auto.modes.ScorchedEarthLeonTopAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;
public class ScorchedEarthLeonAuto extends AutoPath {
    public ScorchedEarthLeonAuto(Color color){
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.2, 7.14, Rotation2d.fromDegrees(0)),
                new Pose2d(7, 7.14, Rotation2d.fromDegrees(0)),
                new Pose2d(8.45, 5.74, Rotation2d.fromDegrees(-90)),
                new Pose2d(8.35, 0.86, Rotation2d.fromDegrees(-90))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true; //WHY NOT
    }
}
