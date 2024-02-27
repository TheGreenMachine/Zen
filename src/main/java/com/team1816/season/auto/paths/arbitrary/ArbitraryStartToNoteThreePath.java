package com.team1816.season.auto.paths.arbitrary;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class ArbitraryStartToNoteThreePath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.ARB_START;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.BOTTOM_NOTE;

    public ArbitraryStartToNoteThreePath() {
        super(startingPos, endingPos);
        super.setArbStart(true);
    }

    public ArbitraryStartToNoteThreePath(Color color) {
        super(color, startingPos, endingPos);
        super.setArbStart(true);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(1.5, 4.13, Rotation2d.fromDegrees(0)),
                new Pose2d(2.57, 4.13, Rotation2d.fromDegrees(0))
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
        return new ArbitraryStartToNoteThreePath();
    }

}
