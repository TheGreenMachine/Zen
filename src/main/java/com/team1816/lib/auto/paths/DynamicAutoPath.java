package com.team1816.lib.auto.paths;

import com.team1816.lib.auto.Color;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.team1816.season.auto.AutoModeManager.Position;

public abstract class DynamicAutoPath extends AutoPath {
    public Position startPosition;
    public Position endPosition;

    public DynamicAutoPath(Position startPosition, Position endPosition) {
        super();
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public DynamicAutoPath(Color color, Position startPosition, Position endPosition) {
        super(color);
        this.startPosition = startPosition;
        this.endPosition = endPosition;

    }

    public List<Pose2d> waypoints;
    public boolean hasCachedWaypoints = false;
    public List<Rotation2d> headings;

    protected boolean isReversed;

    protected boolean isAmpPath = false;

    protected void setAmpPath(boolean isAmpPath) {
        this.isAmpPath = isAmpPath;
    }

    public boolean isAmpPath() {
        return isAmpPath;
    }

    public List<Pose2d> updateWaypoints(List<Pose2d> waypoints) {
        if (!hasCachedWaypoints) {
            this.waypoints = waypoints;
        }
        return this.waypoints;
    }

    public List<Rotation2d> updateHeadings(List<Rotation2d> headings) {
        if (!hasCachedWaypoints) {
            this.headings = headings;
        }
        return this.headings;
    }

    @Override
    public Trajectory getAsTrajectory() {

        if (trajectory == null) {
            if (!reflected && !rotated) {
                trajectory = PathUtil.generateTrajectoryWithError(getWaypoints());
            } else if (reflected) {
                trajectory = PathUtil.generateTrajectoryWithError(getReflectedWaypoints());
            } else {
                trajectory = PathUtil.generateTrajectoryWithError(getRotatedWaypoints());
            }
        }
        return trajectory;
    }

    public List<Pose2d> getInverseWaypoints() {
        ArrayList<Pose2d> waypointsInverted = new ArrayList<Pose2d>(getWaypoints());

        waypointsInverted.replaceAll(
                waypoint -> new Pose2d (
                        waypoint.getX(),
                        waypoint.getY(),
                        Rotation2d.fromDegrees(waypoint.getRotation().getDegrees() - 180)
                )
        );
        Collections.reverse(waypointsInverted);
        return waypointsInverted;
    }

    /**
     * Returns the selected path, "backwards"
     * @return The Path with reversed waypoints
     */
    public DynamicAutoPath withInversedWaypoints() {
        this.isReversed = true;
        this.waypoints = getInverseWaypoints();
        this.headings = new ArrayList<>(getWaypointHeadings());
        Collections.reverse(headings);
        this.hasCachedWaypoints = true;
        return this;
    }

    public DynamicAutoPath withColor(Color color) {
        super.updateColor(color);
        return this;
    }

    public abstract DynamicAutoPath getInstance();
}
