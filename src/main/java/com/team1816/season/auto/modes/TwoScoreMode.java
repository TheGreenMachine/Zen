package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;

import java.util.List;

public class TwoScoreMode extends AutoMode {
    public TwoScoreMode(List<TrajectoryAction> paths) {
        super(paths);
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
            new SeriesAction(

            )
        );
    }
}
