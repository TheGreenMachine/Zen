package com.team1816.season.auto.modes.distance;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.actions.*;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.actions.*;
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
                    paths.get(0).isArbStart() ? new ShootAndRotateDistanceAction(paths.get(0).endPosition) :
                            paths.get(0).isAmpPath() ? new ShootAmpAction() : new ShootSpeakerAction(),
                    new ParallelAction(
                        new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_DISTANCE, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                        trajectoryActions.get(0)
                    ),
                    new ShootAndRotateDistanceAction(paths.get(0).endPosition),
                    trajectoryActions.get(1),
                    new ShootAndRotateDistanceAction(paths.get(1).endPosition),
                    trajectoryActions.get(2),
                    new ShootAndRotateDistanceAction(paths.get(2).startPosition)
                )
        );
    }
}
