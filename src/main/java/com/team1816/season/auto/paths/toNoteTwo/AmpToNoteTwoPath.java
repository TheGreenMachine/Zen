package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class AmpToNoteTwoPath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.AMP;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_NOTE;

    public AmpToNoteTwoPath() {
        super(startingPos, endingPos);
        super.setAmpPath(true);
    }

    public AmpToNoteTwoPath(Color color) {
        super(color, startingPos, endingPos);
        super.setAmpPath(true);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(super.isReversed ? Constants.ampReversedPosition : Constants.ampPosition, Rotation2d.fromDegrees(-90)),
                new Pose2d(1.82, 7.28, Rotation2d.fromDegrees(-90)),
                new Pose2d(Constants.noteTwoPosition, Rotation2d.fromDegrees(0))
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
        return new AmpToNoteTwoPath();
    }

}
