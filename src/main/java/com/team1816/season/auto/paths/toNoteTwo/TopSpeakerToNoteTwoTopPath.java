package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToNoteTwoTopPath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.TOP_SPEAKER;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_NOTE_TOP;

    public TopSpeakerToNoteTwoTopPath(){
        super(startingPos, endingPos);
    }
    public TopSpeakerToNoteTwoTopPath(Color color){
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.742, 6.709, Rotation2d.fromDegrees(38)),
                new Pose2d(2.1, 7.22, Rotation2d.fromDegrees(0)),
                new Pose2d(2.88, 5.85, Rotation2d.fromDegrees(-85))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(super.isReversed ? 60 : -90),
                Rotation2d.fromDegrees(-90)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new TopSpeakerToNoteTwoTopPath();
    }

}
