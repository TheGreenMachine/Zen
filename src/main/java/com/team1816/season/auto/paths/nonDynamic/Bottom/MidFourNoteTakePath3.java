package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class MidFourNoteTakePath3 extends AutoPath {
    public MidFourNoteTakePath3(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(8.32, 2.48, Rotation2d.fromDegrees(150)),
                new Pose2d(5.2, 4.38, Rotation2d.fromDegrees(150)),
                new Pose2d(4.47, 5.6, Rotation2d.fromDegrees(88)),
                new Pose2d(5.75, 6.46, Rotation2d.fromDegrees(-8)),
                new Pose2d(8.26, 5.81, Rotation2d.fromDegrees(-14))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(-15),
                Rotation2d.fromDegrees(-15),
                Rotation2d.fromDegrees(-15),
                Rotation2d.fromDegrees(-15),
                Rotation2d.fromDegrees(0)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
