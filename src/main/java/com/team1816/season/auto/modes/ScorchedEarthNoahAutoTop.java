package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthAmpToSource2Path;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthAmpToSourcePath;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthNoahAuto;

import java.util.List;

public class ScorchedEarthNoahAutoTop extends AutoMode{
    public ScorchedEarthNoahAutoTop(Color color) {
        super(
                List.of(
                        new TrajectoryAction(
                                new ScorchedEarthNoahAuto(color)
                        )
                )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        trajectoryActions.get(0)
                )
        );
    }
}
