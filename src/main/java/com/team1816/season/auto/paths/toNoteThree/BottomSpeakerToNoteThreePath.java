package com.team1816.season.auto.paths.toNoteThree;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomSpeakerToNoteThreePath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.BOTTOM_SPEAKER;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.BOTTOM_NOTE;

    public BottomSpeakerToNoteThreePath(){
        super(startingPos, endingPos);
    }
    public BottomSpeakerToNoteThreePath(Color color){
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.749, 4.391, Rotation2d.fromDegrees(-10)),
                new Pose2d(2.2, 4.13, Rotation2d.fromDegrees(0)),
                new Pose2d(2.62, 4.13, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-60),
                Rotation2d.fromDegrees(super.isReversed ? -45 : 0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new BottomSpeakerToNoteThreePath();
    }

}
