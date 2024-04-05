package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.RotateSwerveAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.scram.BottomSpeakerToScramPath;
import com.team1816.season.auto.paths.scram.TopSpeakerToScramPath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class QuickEjectsMode extends AutoMode {
    public QuickEjectsMode() {
        super(
                List.of(
                        new TrajectoryAction(
                                new TopSpeakerToNoteOnePath(robotState.allianceColor)
                        )

                )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new ShootSpeakerAction(),
                        trajectoryActions.get(0),
                        new RotateSwerveAction(Rotation2d.fromDegrees(robotState.allianceColor == Color.BLUE ? -20 : 20))
                )
        );
    }
}
