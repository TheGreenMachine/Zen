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
            new Pose2d(8.10, 0.95, Rotation2d.fromDegrees(170)),
            new Pose2d(5.82, 1.49, Rotation2d.fromDegrees(140))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(-10),
            Rotation2d.fromDegrees(-40)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
