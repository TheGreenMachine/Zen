package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.actions.ShootAmpAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

import java.util.ArrayList;
import java.util.List;

public class TwoScoreFromSpeakerMode extends AutoMode {
    ArrayList<DynamicAutoPath> paths;
    public TwoScoreFromSpeakerMode(List<DynamicAutoPath> paths) {
        super(DynamicAutoUtil.encapsulateAutoPaths(paths));
        this.paths = new ArrayList<>(paths);
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
                paths.get(1).isAmpPath() ? new ShootAmpAction() : new ShootSpeakerAction(),
                new ShootAction(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.STOP, Shooter.PIVOT_STATE.STOW)
            )
        );
    }
}
