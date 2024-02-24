package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class BottomSpeakerToNoteTwoPath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.BOTTOM_SPEAKER;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_NOTE;

    public BottomSpeakerToNoteTwoPath(){
        super(startingPos, endingPos);
    }
    public BottomSpeakerToNoteTwoPath(Color color){
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(Constants.bottomSpeakerPosition, Rotation2d.fromDegrees(-10)),
                new Pose2d(1.70, 4.94, Rotation2d.fromDegrees(60)),
                new Pose2d(Constants.noteTwoPosition, Rotation2d.fromDegrees(30))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-60),
                Rotation2d.fromDegrees(super.isReversed ? -50 : 0),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new BottomSpeakerToNoteTwoPath();
    }

}
