package com.team1816.season.auto.paths.noteToNote;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class NoteThreeToNoteOneUnderStagePath extends DynamicAutoPath {

    private static AutoModeManager.Position startingPos = AutoModeManager.Position.BOTTOM_NOTE;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.TOP_NOTE;

    public NoteThreeToNoteOneUnderStagePath(){
        super(startingPos, endingPos);
    }
    public NoteThreeToNoteOneUnderStagePath(Color color){
        super(color, startingPos, endingPos);
    }


    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(2.57, 4.13, Rotation2d.fromDegrees(-130)),
                new Pose2d(3.45, 2.95, Rotation2d.fromDegrees(0)),
                new Pose2d(4.47, 3.80, Rotation2d.fromDegrees(90)),
                new Pose2d(3.05, 6.83, Rotation2d.fromDegrees(130))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(0),
                Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(130)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new NoteOneToNoteTwoPath();
    }

}
