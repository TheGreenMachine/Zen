package com.team1816.season.auto.paths.toNoteOne;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import org.apache.commons.math3.util.Pair;

import java.util.List;

public class AmpToNoteOnePath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.AMP;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.TOP_NOTE;

    public AmpToNoteOnePath() {
        super(startingPos, endingPos);
        super.setAmpPath(true);
    }

    public AmpToNoteOnePath(Color color) {
        super(color, startingPos, endingPos);
        super.setAmpPath(true);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(super.isReversed ? 1.84 : 1.86, 7.74, Rotation2d.fromDegrees(-67)),
                new Pose2d(2.06, 7.18, Rotation2d.fromDegrees(-46)),
                new Pose2d(2.73, 7, Rotation2d.fromDegrees(0))
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
        return new AmpToNoteOnePath();
    }

}
