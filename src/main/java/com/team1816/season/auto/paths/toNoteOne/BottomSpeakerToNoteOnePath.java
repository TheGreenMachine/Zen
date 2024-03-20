package com.team1816.season.auto.paths.toNoteOne;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomSpeakerToNoteOnePath extends DynamicAutoPath {

    private static AutoModeManager.Position startingPos = AutoModeManager.Position.BOTTOM_SPEAKER;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.TOP_NOTE;

    public BottomSpeakerToNoteOnePath(){
        super(startingPos, endingPos);
    }
    public BottomSpeakerToNoteOnePath(Color color){
        super(color, startingPos, endingPos);
    }


    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.749, 4.391, Rotation2d.fromDegrees(-20)),
                new Pose2d(1.70, 4.20, Rotation2d.fromDegrees(82)),
                new Pose2d(1.58, 6.2, Rotation2d.fromDegrees(82)),
                new Pose2d(2.73, 7, Rotation2d.fromDegrees(18))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-60),
                Rotation2d.fromDegrees(super.isReversed ? -55 : 0),
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new BottomSpeakerToNoteOnePath();
    }

}
