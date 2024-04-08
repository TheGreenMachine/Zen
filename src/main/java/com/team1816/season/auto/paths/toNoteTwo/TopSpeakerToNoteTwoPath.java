package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToNoteTwoPath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.TOP_SPEAKER;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_NOTE;

    public TopSpeakerToNoteTwoPath(){
        super(startingPos, endingPos);
    }
    public TopSpeakerToNoteTwoPath(Color color){
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.742, 6.709, Rotation2d.fromDegrees(30)),
                new Pose2d(1.78, 6.5, Rotation2d.fromDegrees(-39)),
                new Pose2d(2.92, 5.53, Rotation2d.fromDegrees(-32))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(super.isReversed ? 50 : -20),
                Rotation2d.fromDegrees(-45)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new TopSpeakerToNoteTwoPath();
    }

}
