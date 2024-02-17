package com.team1816.season.auto.paths.toNoteThree;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class AmpToNoteThreePath extends DynamicAutoPath {

    private static AutoModeManager.Position startingPos = AutoModeManager.Position.AMP;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.BOTTOM_NOTE;

    public AmpToNoteThreePath() {
        super(startingPos, endingPos);
        super.setAmpPath(true);
    }

    public AmpToNoteThreePath(Color color) {
        super(color, startingPos, endingPos);
        super.setAmpPath(true);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(super.isReversed ? 1.84 : 1.86, 7.74, Rotation2d.fromDegrees(-90)),
                new Pose2d(1.82, 6.03, Rotation2d.fromDegrees(-90)),
                new Pose2d(2.57, 4.13, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(super.isReversed ? -90 : 0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new AmpToNoteThreePath();
    }

}
