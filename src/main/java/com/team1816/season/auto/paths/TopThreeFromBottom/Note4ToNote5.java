package com.team1816.season.auto.paths.TopThreeFromBottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class Note4ToNote5 extends AutoPath {
    public Note4ToNote5(Color color) {
        super(color);
    }


    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(8.28, 5.81, Rotation2d.fromDegrees(161)),
                new Pose2d(5.82, 6.68, Rotation2d.fromDegrees(161)),
                new Pose2d(5.82, 6.69, Rotation2d.fromDegrees(18)),
                new Pose2d(8.26, 7.5, Rotation2d.fromDegrees(18))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(18),
                Rotation2d.fromDegrees(18)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
