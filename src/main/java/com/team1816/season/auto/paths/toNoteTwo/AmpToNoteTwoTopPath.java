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
        return updateWaypoints(List.of( //TODO make sure this doesn't break on reverse
                new Pose2d(1.82, 7.78, Rotation2d.fromDegrees(-67)),
//                new Pose2d(2.9, 6, Rotation2d.fromDegrees(-90)),
                new Pose2d(2.9, 5.64, Rotation2d.fromDegrees(-90))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-90),
//                Rotation2d.fromDegrees(-90),
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
