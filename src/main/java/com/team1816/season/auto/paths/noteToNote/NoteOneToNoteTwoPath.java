package com.team1816.season.auto.paths.noteToNote;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class NoteOneToNoteTwoPath extends DynamicAutoPath {

    private static AutoModeManager.Position startingPos = AutoModeManager.Position.TOP_NOTE;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_NOTE;

    public NoteOneToNoteTwoPath(){
        super(startingPos, endingPos);
    }
    public NoteOneToNoteTwoPath(Color color){
        super(color, startingPos, endingPos);
    }


    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(2.7, 7, Rotation2d.fromDegrees(-140)),
                new Pose2d(2.57, 5.58, Rotation2d.fromDegrees(-45))
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
        return new NoteOneToNoteTwoPath();
    }

}
