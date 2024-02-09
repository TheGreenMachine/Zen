package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.auto.actions.ShootAction;

import java.util.ArrayList;
import java.util.List;

public class TwoScoreMode extends AutoMode {
    public TwoScoreMode(List<AutoPath> paths) {
        super(encapsulateAutoPaths(paths));
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
            new SeriesAction(
                    ShootAction()
                trajectoryActions.get(0), trajectoryActions.get(1)
            )
        );
    }

    private static List<TrajectoryAction> encapsulateAutoPaths(List<AutoPath> paths) {
        List<TrajectoryAction> trajectories = new ArrayList<TrajectoryAction>();

        paths.forEach(path -> {
            trajectories.add(new TrajectoryAction(path));
        });
        return trajectories;
    }
}
