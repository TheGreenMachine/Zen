package com.team1816.season.auto.modes;

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
import com.team1816.season.configuration.Constants;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

import java.util.ArrayList;
import java.util.List;

public class ThreeScoreFromAmpMode extends AutoMode {
    ArrayList<DynamicAutoPath> paths;
    boolean scramAtEnd;
    TrajectoryAction scramAction;

    public ThreeScoreFromAmpMode(List<DynamicAutoPath> paths, boolean scramAtEnd) {
        super(DynamicAutoUtil.encapsulateAutoPaths(paths));
        this.paths = new ArrayList<>(paths);
//        if (trajectoryActions.size() > 3) {
//            trajectoryActions.subList(4, trajectoryActions.size()).clear();
//        }
        this.scramAtEnd = scramAtEnd;
        if (scramAtEnd) {
            scramAction = new TrajectoryAction(DynamicAutoUtil.getScram(paths.get(4)));
            trajectoryActions.add(scramAction);
        }

    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                        trajectoryActions.get(0),
                        new ShootAmpAction(),
                        new WaitAction(Constants.kAutoTimeBetweenFirstShot),
                        trajectoryActions.get(1),
                        trajectoryActions.get(2),
                        paths.get(2).isAmpPath() ? new ShootAmpAction() : new ShootSpeakerAction(),
                        new WaitAction(Constants.kAutoTimeBetweenSecondShot),
                        trajectoryActions.get(3),
                        trajectoryActions.get(4),
                        paths.get(4).isAmpPath() ? new ShootAmpAction() : new ShootSpeakerAction(),
                        new WaitAction(Constants.kAutoTimeBetweenThirdShot),
                        new ShootAction(Shooter.ROLLER_STATE.IDLE, Shooter.FEEDER_STATE.STOP, Shooter.PIVOT_STATE.STOW),
                        new WaitAction(Constants.kAutoTimeBeforeScram),
                        scramAtEnd ? scramAction : new WaitAction(0.1)
                ));
    }
}
