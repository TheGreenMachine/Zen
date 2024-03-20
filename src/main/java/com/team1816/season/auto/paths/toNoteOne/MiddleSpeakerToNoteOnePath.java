package com.team1816.season.auto.paths.toNoteOne;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class MiddleSpeakerToNoteOnePath extends DynamicAutoPath {

    private static AutoModeManager.Position startingPos = AutoModeManager.Position.MIDDLE_SPEAKER;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.TOP_NOTE;

    public MiddleSpeakerToNoteOnePath(){
        super(startingPos, endingPos);
    }
    public MiddleSpeakerToNoteOnePath(Color color){
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(1.368, 5.55, Rotation2d.fromDegrees(68)),
                new Pose2d(2.73, 7, Rotation2d.fromDegrees(28.5))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(10)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new MiddleSpeakerToNoteOnePath();
    }

}
