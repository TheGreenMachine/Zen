package com.team1816.season.auto.paths.arbitrary;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ArbitraryStartToNoteOnePath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.ARB_START;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.TOP_NOTE;

    public ArbitraryStartToNoteOnePath() {
        super(startingPos, endingPos);
    }

    public ArbitraryStartToNoteOnePath(Color color) {
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(1.5, 7, Rotation2d.fromDegrees(0)),
                new Pose2d(2.73, 7, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new ArbitraryStartToNoteOnePath();
    }

}
