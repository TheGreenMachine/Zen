package com.team1816.season.auto.paths.toNoteOne;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class AmpToNoteOnePath extends DynamicAutoPath {

    public AmpToNoteOnePath() {
        super.setAmpPath(true);
    }
    public AmpToNoteOnePath(Color color) {
        super(color);
        super.setAmpPath(true);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(1.82, 7.73, Rotation2d.fromDegrees(-67)),
                new Pose2d(2.06, 7.18, Rotation2d.fromDegrees(-46)),
                new Pose2d(2.57, 7.02, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(-90),
                Rotation2d.fromDegrees(0)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }
}
