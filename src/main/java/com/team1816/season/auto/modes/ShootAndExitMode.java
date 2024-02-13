package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAmpAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.subsystems.Collector;

import java.util.ArrayList;
import java.util.List;

public class ShootAndExitMode extends AutoMode {

    ArrayList<DynamicAutoPath> paths;

    public ShootAndExitMode(List<DynamicAutoPath> paths) {
        super(DynamicAutoUtil.encapsulateAutoPaths(paths));
        this.paths = new ArrayList<>(paths);
        if (trajectoryActions.size() > 1) {
            trajectoryActions.subList(1, trajectoryActions.size()).clear();
        }
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        paths.get(0).isAmpPath() ? new ShootAmpAction() : new ShootSpeakerAction(),
                        new ParallelAction(
                                new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                                trajectoryActions.get(0)
                        ),
                        new CollectAction(Collector.COLLECTOR_STATE.STOP)
                )
        );
    }
}
