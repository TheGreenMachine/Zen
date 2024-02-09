package com.team1816.lib.auto.paths;

import com.team1816.lib.auto.Color;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DynamicAutoPath extends AutoPath{

    public DynamicAutoPath() {}
    public DynamicAutoPath(Color color) {
        super(color);
    }

    public List<Pose2d> waypoints;
    public boolean hasCachedWaypoints = false;
    public List<Rotation2d> headings;

    protected boolean isReversed;

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

}
