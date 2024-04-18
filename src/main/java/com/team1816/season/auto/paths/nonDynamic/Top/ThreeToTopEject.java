package com.team1816.season.auto.paths.nonDynamic.Top;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ThreeToTopEject extends AutoPath {
    public ThreeToTopEject(Color color) {
        super(color);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(8.12, 4.59, Rotation2d.fromDegrees(105)),
            new Pose2d(7.76, 5.89, Rotation2d.fromDegrees(105)),
            new Pose2d(6.88, 7.14, Rotation2d.fromDegrees(160)),
            new Pose2d(5.22, 6.93, Rotation2d.fromDegrees(-160))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-75),
            Rotation2d.fromDegrees(-75),
            Rotation2d.fromDegrees(-20),
            Rotation2d.fromDegrees(20)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
