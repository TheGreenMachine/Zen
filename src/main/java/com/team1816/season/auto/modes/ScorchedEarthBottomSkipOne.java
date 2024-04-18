package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.EjectAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.nonDynamic.Bottom.*;
import com.team1816.season.subsystems.Collector;

import java.util.List;

public class ScorchedEarthBottomSkipOne extends AutoMode {

    public ScorchedEarthBottomSkipOne(Color color) {
        super(
            List.of(
                new TrajectoryAction(
                    new ScorchedEarthSkipOneBottom(color)
                )
            )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        GreenLogger.log("Running Scorched Earth Bottom Skip One Mode");
        runAction(
            trajectoryActions.get(0)
        );

    }
}
