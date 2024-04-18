package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.scram.BottomSpeakerToScramPath;

import java.util.List;

public class ScoreAndScramMode extends AutoMode {

    public ScoreAndScramMode(Color color) {
        super(
                List.of(
                        new TrajectoryAction(
                                new BottomSpeakerToScramPath(color)
                        )
                )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new ShootSpeakerAction(),
                        trajectoryActions.get(0)
                )
        );
    }
}