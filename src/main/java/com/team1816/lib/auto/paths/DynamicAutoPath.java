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


/**
 * Abstract class containing all information necessary for running a trajectory as a TrajectoryAction in the Dynamic Auto framework
 *
 * @see AutoPath
 * @see com.team1816.lib.auto.DynamicAutoUtil
 * @see com.team1816.lib.auto.actions.TrajectoryAction
 * @see Trajectory
 */
public abstract class DynamicAutoPath extends AutoPath {
    /**
     * The Positions used in the Dynamic Path lookup table as the key
     */
    public Position startPosition;
    public Position endPosition;

    public Color color = Color.BLUE;

    public DynamicAutoPath(Position startPosition, Position endPosition) {
        super();
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public DynamicAutoPath(Color color, Position startPosition, Position endPosition) {
        super(color);
        this.color = color;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }


    public List<Pose2d> waypoints;
    public List<Rotation2d> headings;
    /**
     * If the waypoints have been inversed previously
     */
    public boolean hasCachedWaypoints = false;

    /**
     * If the path is reversed
     */
    protected boolean isReversed;

    // 2024 season specific
    protected boolean isAmpPath = false;
    protected boolean isArbStart = false;

    protected void setAmpPath(boolean isAmpPath) {
        this.isAmpPath = isAmpPath;
    }
    protected void setArbStart(boolean isArbStart) {
        this.isArbStart = isArbStart;
    }

    public boolean isAmpPath() {
        return isAmpPath;
    }

    public boolean isArbStart() {
        return isArbStart;
    }

    /**
     * Returns the path's waypoints while maintaining the reversed waypoints should the path already be reversed
     * @param waypoints The list of waypoints to return
     * @return The list of waypoints the path is now using
     */
    public List<Pose2d> updateWaypoints(List<Pose2d> waypoints) {
        if (!hasCachedWaypoints) {
            this.waypoints = waypoints;
        }
        return this.waypoints;
    }

    /**
     * Returns the path's headings while maintaining the reversed headings should the path already be reversed
     * @param headings The list of headings to return
     * @return The list of headings the path is now using
     */
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
                trajectory = PathUtil.generateTrajectory(true, getWaypoints());
            } else if (reflected) {
                trajectory = PathUtil.generateTrajectory(true, getReflectedWaypoints());
            } else {
                trajectory = PathUtil.generateTrajectory(true, getRotatedWaypoints());
            }
        }
        return trajectory;
    }


    /**
     * Returns the waypoints of the path reversed in order and mirrored in direction of travel
     * @return The inversed waypoints
     */
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

    /**
     * Updates the color used by the path and returns itself
     * @param color The new color to be used
     * @return Itself
     */
    public DynamicAutoPath withColor(Color color) {
        super.updateColor(color);
        return this;
    }

    /**
     * Returns a new instance of this path for use as a Callable in the lookup table
     * @return A new instance of this path
     */
    public abstract DynamicAutoPath getInstance();
}
