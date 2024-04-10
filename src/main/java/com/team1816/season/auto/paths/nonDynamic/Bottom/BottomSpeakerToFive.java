package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomSpeakerToFive extends AutoPath {
    public BottomSpeakerToFive(Color allianceColor) {
        super(allianceColor);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(0.75, 4.31, Rotation2d.fromDegrees(-60)),
            new Pose2d(2.33, 2.56, Rotation2d.fromDegrees(-35)),
            new Pose2d(4.79, 1.47, Rotation2d.fromDegrees(-20)),
            new Pose2d(8.10, 0.95, Rotation2d.fromDegrees(-10))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-60),
            Rotation2d.fromDegrees(-35),
            Rotation2d.fromDegrees(-20),
            Rotation2d.fromDegrees(-10)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
