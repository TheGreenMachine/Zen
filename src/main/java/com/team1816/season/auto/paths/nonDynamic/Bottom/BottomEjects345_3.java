package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomEjects345_3 extends AutoPath {
    public BottomEjects345_3(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(8.09, 2.00, Rotation2d.fromDegrees(85)),
            new Pose2d(8.24, 3.66, Rotation2d.fromDegrees(85))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(85),
            Rotation2d.fromDegrees(85)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
