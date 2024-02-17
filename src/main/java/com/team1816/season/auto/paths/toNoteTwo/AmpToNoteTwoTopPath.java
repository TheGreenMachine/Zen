package com.team1816.season.auto.paths.toNoteTwo;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class AmpToNoteTwoTopPath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.AMP;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_NOTE_TOP;

    public AmpToNoteTwoTopPath() {
        super(startingPos, endingPos);
        super.setAmpPath(true);
    }

    public AmpToNoteTwoTopPath(Color color) {
        super(color, startingPos, endingPos);
        super.setAmpPath(true);
    }
    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(super.isReversed ? 1.84 : 1.86, 7.74, Rotation2d.fromDegrees(-67)),
                new Pose2d(2.63, 6.78, Rotation2d.fromDegrees(-54)),
                new Pose2d(2.9, 5.64, Rotation2d.fromDegrees(-90))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-90)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    public DynamicAutoPath getInstance() {
        return new AmpToNoteTwoTopPath();
    }

}
