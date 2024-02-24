package com.team1816.season.auto.paths;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class StartToAmpPath extends DynamicAutoPath {

    public StartToAmpPath() {
        super(AutoModeManager.Position.ARB_START, AutoModeManager.Position.AMP);
    }

    public StartToAmpPath(Color color) {
        super(color, AutoModeManager.Position.ARB_START, AutoModeManager.Position.AMP);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(Constants.ampStartPosition, Rotation2d.fromDegrees(90)),
                new Pose2d(super.isReversed ? Constants.ampReversedPosition : Constants.ampPosition, Rotation2d.fromDegrees(90))
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
