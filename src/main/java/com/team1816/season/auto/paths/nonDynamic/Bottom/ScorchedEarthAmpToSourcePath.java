package com.team1816.season.auto.paths.nonDynamic.Bottom;

        import com.team1816.lib.auto.Color;
        import com.team1816.lib.auto.paths.AutoPath;
        import edu.wpi.first.math.geometry.Pose2d;
        import edu.wpi.first.math.geometry.Rotation2d;

        import java.util.List;

public class ScorchedEarthAmpToSourcePath extends AutoPath {
    public ScorchedEarthAmpToSourcePath(Color color) {
        super(color);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return List.of(
                new Pose2d(.54, 7.26, Rotation2d.fromDegrees(0)),
                new Pose2d(8.59, 7.5, Rotation2d.fromDegrees(20)),
                new Pose2d(8.60, 7.5, Rotation2d.fromDegrees(-90)),
                new Pose2d(8.60, 0.75, Rotation2d.fromDegrees(-90))
        );
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return List.of(
                Rotation2d.fromDegrees(90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90),
                Rotation2d.fromDegrees(35+90)
        );
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

}
