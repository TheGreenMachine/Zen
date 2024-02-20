package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.paths.scram.AmpToScramPath;
import com.team1816.season.auto.paths.scram.BottomSpeakerToScramPath;
import com.team1816.season.auto.paths.scram.MiddleSpeakerToScramPath;
import com.team1816.season.auto.paths.scram.TopSpeakerToScramPath;
import com.team1816.season.auto.paths.toNoteTwo.MiddleSpeakerToNoteTwoPath;
import com.team1816.season.auto.paths.toNoteTwo.TopSpeakerToNoteTwoTopPath;
import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.List;

public class TestMode extends AutoMode {

    public TestMode() {
        super(
                List.of(
                new TrajectoryAction(
                        new AmpToScramPath()
        )));
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
