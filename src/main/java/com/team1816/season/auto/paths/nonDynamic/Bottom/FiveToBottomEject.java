package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class FiveToBottomEject extends AutoPath {
    public FiveToBottomEject(Color allianceColor) {
        super(allianceColor);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(7.77, 0.84, Rotation2d.fromDegrees(170)),
            new Pose2d(4.63, 1.57, Rotation2d.fromDegrees(160))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-10),
            Rotation2d.fromDegrees(-20)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
