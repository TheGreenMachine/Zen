package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.nonDynamic.Bottom.*;
import com.team1816.season.subsystems.Collector;

import java.util.List;

public class ScorchedEarthNoraTopMode extends AutoMode {
    public ScorchedEarthNoraTopMode(Color color) {
        super(
                List.of(
                        new TrajectoryAction(
                                new ScorchedEarthNoraAuto(color)
                        ),
                        new TrajectoryAction(
                                new ScorchedEarthNoraPath2(color)
                        ),
                        new TrajectoryAction(
                                new ScorchedEarthNoraPath3(color)
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
                        new ShootSpeakerAction()
                )
        );
    }
}
