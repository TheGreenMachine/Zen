package com.team1816.season.auto.modes;
import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthEthanPath2;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthEthanPath3;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthLeonPath;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

import java.util.List;

public class ScorchedEarthLeonTopMode extends AutoMode {
    public ScorchedEarthLeonTopMode(Color color) {
        super(
                List.of(
                        new TrajectoryAction(
                                new ScorchedEarthLeonPath(color)
                        ),
                        new TrajectoryAction(
                                new ScorchedEarthEthanPath2(color)
                        ),
                        new TrajectoryAction(
                                new ScorchedEarthEthanPath3(color)
                        )
                )
        );
    }
    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        trajectoryActions.get(0),
                        new ParallelAction(
                                trajectoryActions.get(1),
                                new CollectAction(Collector.COLLECTOR_STATE.INTAKE)
                        ),
                        new CollectAction(Collector.COLLECTOR_STATE.OUTTAKE),
                        trajectoryActions.get(2),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                        new WaitAction(2)
                )
        );
    }
}
