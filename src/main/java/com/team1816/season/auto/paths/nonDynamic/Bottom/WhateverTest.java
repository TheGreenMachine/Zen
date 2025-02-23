package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

import java.util.List;

public class WhateverTest extends AutoPath {
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(new Translation2d(2.6, 5.5), Rotation2d.fromDegrees(180)),
                new Pose2d(new Translation2d(1.6, 5.5), Rotation2d.fromDegrees(180))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                new Rotation2d(0),
                Rotation2d.fromDegrees(90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
