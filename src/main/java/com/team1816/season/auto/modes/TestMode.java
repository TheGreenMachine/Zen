package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.paths.toNoteOne.BottomSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;

import java.util.List;

public class TestMode extends AutoMode {

    public TestMode() {
        super(List.of(
                new TrajectoryAction(
                        new BottomSpeakerToNoteOnePath()
                ),
                new TrajectoryAction(
                        new BottomSpeakerToNoteOnePath().withInversedWaypoints()
                )
        ));
    }
    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        trajectoryActions.get(0),
                        trajectoryActions.get(1)
                )
        );
    }
}