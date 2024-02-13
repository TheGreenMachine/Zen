package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.actions.*;
import com.team1816.season.auto.paths.StartToAmpPath;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

import java.util.ArrayList;
import java.util.List;

public class TwoScoreFromAmpMode extends AutoMode {
    ArrayList<DynamicAutoPath> paths;

    public TwoScoreFromAmpMode(List<DynamicAutoPath> paths) {
        super(DynamicAutoUtil.encapsulateAutoPaths(paths)); //TODO: Amp paths need reversal safety now.
        this.paths = new ArrayList<>(paths);
        if (trajectoryActions.size() > 3) {
            trajectoryActions.subList(3, trajectoryActions.size()).clear();
        }
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        trajectoryActions.get(0),
                        new ShootAmpAction(),
                        new ParallelAction(
                                new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                                trajectoryActions.get(1)
                        ),
                        trajectoryActions.get(2),
                        paths.get(1).isAmpPath() ? new ShootAmpAction() : new ShootSpeakerAction(), //Yes it being 1 is right
                        new ShootAction(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.STOP, Shooter.PIVOT_STATE.STOW)
                )
        );
    }
}
