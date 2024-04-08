package com.team1816.season.auto.paths.scram;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class NoteOneToMiddleOnePath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.TOP_NOTE;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_ONE;

    public NoteOneToMiddleOnePath(Color color) {
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(2.62, 6.97, Rotation2d.fromDegrees(0)),
                new Pose2d(8.15, 7.47, Rotation2d.fromDegrees(0))
        ));
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

    @Override
    public DynamicAutoPath getInstance() {
        return null;
    }
}
