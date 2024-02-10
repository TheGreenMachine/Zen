package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.states.RobotState;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

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
                new ShootSpeakerAction(),
                new ParallelAction(
                    new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                    trajectoryActions.get(0)
                ),
                trajectoryActions.get(1),
                new ShootSpeakerAction()
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