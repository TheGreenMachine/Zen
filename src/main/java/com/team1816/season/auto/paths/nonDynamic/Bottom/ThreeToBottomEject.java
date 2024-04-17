package com.team1816.season.auto.paths.nonDynamic.Bottom;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ThreeToBottomEject extends AutoPath {
    public ThreeToBottomEject(Color allianceColor) {
        super(allianceColor);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
            new Pose2d(8.10, 4, Rotation2d.fromDegrees(-110)),
            new Pose2d(7.55, 2.59, Rotation2d.fromDegrees(-115)),
            new Pose2d(6.9, 1.53, Rotation2d.fromDegrees(-140)),
            new Pose2d(5.81, 1.21, Rotation2d.fromDegrees(140)),
            new Pose2d(5.82, 1.49, Rotation2d.fromDegrees(140))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
            Rotation2d.fromDegrees(70),
            Rotation2d.fromDegrees(65),
            Rotation2d.fromDegrees(40),
            Rotation2d.fromDegrees(-40),
            Rotation2d.fromDegrees(-40)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
