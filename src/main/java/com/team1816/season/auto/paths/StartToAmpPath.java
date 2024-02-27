package com.team1816.season.auto.paths;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class StartToAmpPath extends DynamicAutoPath {

    public StartToAmpPath() {
        super(AutoModeManager.Position.ARB_START_AMP, AutoModeManager.Position.AMP);
    }

    public StartToAmpPath(Color color) {
        super(color, AutoModeManager.Position.ARB_START_AMP, AutoModeManager.Position.AMP);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(1.47, 7.3, Rotation2d.fromDegrees(90)),
                new Pose2d(super.isReversed ? 1.84 : 1.86, 7.74, Rotation2d.fromDegrees(90))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    @Override
    public DynamicAutoPath getInstance() {
        return new StartToAmpPath();
    }
}
