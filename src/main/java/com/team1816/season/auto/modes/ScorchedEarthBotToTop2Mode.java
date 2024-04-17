package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthSourceToAmp2Path;
import com.team1816.season.auto.paths.nonDynamic.Bottom.ScorchedEarthSourceToAmpPath;

import java.util.List;

public class ScorchedEarthBotToTop2Mode extends AutoMode {
    public ScorchedEarthBotToTop2Mode() {
        super(
                List.of(
                        new TrajectoryAction(
                                new ScorchedEarthSourceToAmp2Path(robotState.allianceColor)
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
