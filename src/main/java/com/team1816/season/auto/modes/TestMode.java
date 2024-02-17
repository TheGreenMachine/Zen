package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.paths.StartToAmpPath;
import com.team1816.season.auto.paths.toNoteOne.AmpToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.BottomSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.MiddleSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteThree.AmpToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.BottomSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.MiddleSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.TopSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteTwo.AmpToNoteTwoPath;
import com.team1816.season.auto.paths.toNoteTwo.AmpToNoteTwoTopPath;
import com.team1816.season.auto.paths.toNoteTwo.TopSpeakerToNoteTwoTopPath;

import java.util.List;

public class TestMode extends AutoMode {

    public TestMode() {
        super(
                List.of(
                new TrajectoryAction(
                        new AmpToNoteTwoTopPath()
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
