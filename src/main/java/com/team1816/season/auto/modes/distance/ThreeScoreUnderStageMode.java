package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.actions.ShootAndRotateDistanceAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.noteToNote.NoteThreeToNoteOneUnderStagePath;
import com.team1816.season.auto.paths.scram.BottomSpeakerToScramPath;
import com.team1816.season.auto.paths.toNoteThree.BottomSpeakerToNoteThreePath;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

import java.util.List;

public class ThreeScoreUnderStageMode extends AutoMode {

    public ThreeScoreUnderStageMode() {
        super(
                List.of(
                        new TrajectoryAction(
                                new BottomSpeakerToNoteThreePath(robotState.allianceColor)
                        ),
                        new TrajectoryAction(
                                new NoteThreeToNoteOneUnderStagePath(robotState.allianceColor)
                        )
                )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                        new ShootSpeakerAction(),
                        trajectoryActions.get(0),
                        new ShootAndRotateDistanceAction(AutoModeManager.Position.BOTTOM_NOTE),
                        trajectoryActions.get(1),
                        new ShootAndRotateDistanceAction(AutoModeManager.Position.TOP_NOTE),
                        new ShootAction(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.STOP, Shooter.PIVOT_STATE.STOW)
                )
        );
    }
}