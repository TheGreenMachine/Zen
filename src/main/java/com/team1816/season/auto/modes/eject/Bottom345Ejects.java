package com.team1816.season.auto.modes.eject;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.RotateSwerveAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.nonDynamic.Bottom.*;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class Bottom345Ejects extends AutoMode {
    public Bottom345Ejects() {
        super(
            List.of(
                new TrajectoryAction(
                    new BottomEjects345_1(robotState.allianceColor)
                ),
                new TrajectoryAction(
                    new BottomEjects345_2(robotState.allianceColor)
                ),
                new TrajectoryAction(
                    new BottomEjects345_3(robotState.allianceColor)
                )
            )

        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
            new SeriesAction(
                new ShootSpeakerAction(),
                new ParallelAction(
                    trajectoryActions.get(0),
                    new CollectAction(Collector.COLLECTOR_STATE.INTAKE)
                ),
                new RotateSwerveAction(Rotation2d.fromDegrees(-60)),
                new ShootAction(Shooter.ROLLER_STATE.EJECT, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                new RotateSwerveAction(Rotation2d.fromDegrees(76)),
                trajectoryActions.get(1),
                new RotateSwerveAction(Rotation2d.fromDegrees(-50)),
                new ShootAction(Shooter.ROLLER_STATE.EJECT, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                new RotateSwerveAction(Rotation2d.fromDegrees(85)),
                trajectoryActions.get(2),
                new RotateSwerveAction(Rotation2d.fromDegrees(-30)),
                new ShootAction(Shooter.ROLLER_STATE.EJECT, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW)
            )
        );
    }
}
