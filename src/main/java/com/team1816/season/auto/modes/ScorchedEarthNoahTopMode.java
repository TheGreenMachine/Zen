package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.nonDynamic.Bottom.*;

import java.util.List;

public class ScorchedEarthNoahTopMode extends AutoMode{
    public ScorchedEarthNoahTopMode(Color color) {
        super(
                List.of(
                        new TrajectoryAction(
                                new ScorchedEarthNoahPath(color)
                        ),
                        new TrajectoryAction(
                                new ScorchedEarthNoahPath2(color)
                        ),
                        new TrajectoryAction(
                                new ScorchedEarthNoahPath3(color)
                        )
                )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        trajectoryActions.get(0),
                        trajectoryActions.get(1),
                        trajectoryActions.get(2),
                        new ShootSpeakerAction()
                )
        );
    }
}