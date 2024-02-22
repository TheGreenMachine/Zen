package com.team1816.season.auto.modes.distance;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
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

public class FourScoreFromDistanceMode extends AutoMode {
    ArrayList<DynamicAutoPath> paths;
    boolean scramAtEnd;
    TrajectoryAction scramAction;

    public FourScoreFromDistanceMode(List<DynamicAutoPath> paths, boolean scramAtEnd) {
        super(DynamicAutoUtil.encapsulateAutoPaths(paths));
        this.paths = new ArrayList<>(paths);
        this.scramAtEnd = scramAtEnd;
        if (scramAtEnd) {
            scramAction = new TrajectoryAction(DynamicAutoUtil.getScram(paths.get(1)));
            trajectoryActions.add(scramAction);
        }

    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new ShootSpeakerAction(),
                        new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                        trajectoryActions.get(0),//Trajectory from speaker to note 1
                        new CollectAction(Collector.COLLECTOR_STATE.STOP),
                        trajectoryActions.get(1),//Note 1 to middle
                        new ShootSpeakerAction(), //Shoot TODO replace
                        new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                        new ShootSpeakerAction(),
                        trajectoryActions.get(2), //middle to third note
                        trajectoryActions.get(3), //third to middle
                        new ShootSpeakerAction()
                )
        );
    }
}
