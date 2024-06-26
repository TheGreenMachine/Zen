package com.team1816.season.auto.paths.nonDynamic.Top;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TwoToTopEject extends AutoPath {
    public TwoToTopEject(Color color) {
        super(color);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(7.87, 6.08, Rotation2d.fromDegrees(155)),
            new Pose2d(7.11, 6.57, Rotation2d.fromDegrees(150)),
            new Pose2d(5.22, 6.93, Rotation2d.fromDegrees(-160))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-35),
            Rotation2d.fromDegrees(-30),
            Rotation2d.fromDegrees(20)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
