package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.RotateSwerveAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.actions.ShootAmpAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.arbitrary.ArbitraryStartToNoteOnePath;
import com.team1816.season.auto.paths.noteToNote.NoteThreeToNoteOneUnderStagePath;
import com.team1816.season.auto.paths.scram.*;
import com.team1816.season.auto.paths.toNoteOne.AmpToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.MiddleSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteThree.AmpToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.BottomSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.MiddleSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.TopSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteTwo.*;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;
import edu.wpi.first.math.geometry.Rotation2d;
import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.List;

public class QuickEjectsMode extends AutoMode {

    public QuickEjectsMode(Color color) {
        super(
                List.of(

                        new TrajectoryAction(
                                new TopSpeakerToNoteOnePath(color)
                        ),
                        new TrajectoryAction(
                                new TopSpeakerToNoteOnePath(color).withInversedWaypoints()
                        ),
                        new TrajectoryAction(
                                new TopSpeakerToScramPath(color)
                        ),
                        new TrajectoryAction(
                                new TopSpeakerToScramPath(color).withInversedWaypoints()
                        )
                ));
    }
    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new ShootSpeakerAction(),
                        new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                        trajectoryActions.get(0),
                        trajectoryActions.get(1),
                        new ShootSpeakerAction(),
                        trajectoryActions.get(2),
                        trajectoryActions.get(3),
                        new ShootSpeakerAction()
                )
        );
    }
}
