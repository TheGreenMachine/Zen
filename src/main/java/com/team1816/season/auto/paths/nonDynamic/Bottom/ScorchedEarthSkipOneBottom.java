package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.auto.modes.ScorchedEarthBottomSkipOne;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ScorchedEarthSkipOneBottom extends AutoPath {

    public ScorchedEarthSkipOneBottom(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(0.48, 2.06, Rotation2d.fromDegrees(-30)),
                new Pose2d(5.61, 0.9, Rotation2d.fromDegrees(0)),
                new Pose2d(8, 1.6, Rotation2d.fromDegrees(60)),
                new Pose2d(8.4, 3.97, Rotation2d.fromDegrees(90)),
                new Pose2d(8.4, 5.85, Rotation2d.fromDegrees(90)),
                new Pose2d(8.4, 7.41, Rotation2d.fromDegrees(90)),
                new Pose2d(7.5, 7.41, Rotation2d.fromDegrees(180))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(0),
            Rotation2d.fromDegrees(35),
            Rotation2d.fromDegrees(35),
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
