package com.team1816.lib.auto.paths;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DynamicAutoPath extends AutoPath{
    public List<Pose2d> waypoints;
    public boolean hasCachedWaypoints = false;
    private ArrayList<Rotation2d> headings;

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
        this.waypoints = getInverseWaypoints();
        this.headings = new ArrayList<>(getWaypointHeadings());
        Collections.reverse(headings);
        this.hasCachedWaypoints = true;
        return this;
    }

}
