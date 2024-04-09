package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class MidFourNoteTakePath3_0 extends AutoPath {
    public MidFourNoteTakePath3_0(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(8.26, 5.79, Rotation2d.fromDegrees(-15+180)),
                new Pose2d(5.71, 6.44, Rotation2d.fromDegrees(180))
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