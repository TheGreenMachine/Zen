package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class MidFourNoteTakePath0_1 extends AutoPath {
    public MidFourNoteTakePath0_1(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(0.73, 4.36, Rotation2d.fromDegrees(-60)),
                new Pose2d(8.25, 0.71, Rotation2d.fromDegrees(-20))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(-60),
                Rotation2d.fromDegrees(-15)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
