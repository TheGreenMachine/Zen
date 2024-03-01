package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.RotateSwerveAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.actions.ShootAmpAction;
import com.team1816.season.auto.paths.arbitrary.ArbitraryStartToNoteOnePath;
import com.team1816.season.auto.paths.scram.AmpToScramPath;
import com.team1816.season.auto.paths.scram.BottomSpeakerToScramPath;
import com.team1816.season.auto.paths.scram.MiddleSpeakerToScramPath;
import com.team1816.season.auto.paths.scram.TopSpeakerToScramPath;
import com.team1816.season.auto.paths.toNoteOne.AmpToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.MiddleSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteThree.AmpToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.MiddleSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.TopSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteTwo.*;
import com.team1816.season.subsystems.Shooter;
import edu.wpi.first.math.geometry.Rotation2d;
import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.List;

public class TestMode extends AutoMode {

    public TestMode() {
        super(
                List.of(
                new TrajectoryAction(
                        new TopSpeakerToNoteThreePath()
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
