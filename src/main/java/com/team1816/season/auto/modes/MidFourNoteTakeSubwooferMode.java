package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.paths.nonDynamic.Bottom.*;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

import java.util.List;

public class MidFourNoteTakeSubwooferMode extends AutoMode {
    public MidFourNoteTakeSubwooferMode(Color color) {
        super(
                List.of(
                        new TrajectoryAction(
                                new MidFourNoteTakePath0_1(color)
                        ),
                        new TrajectoryAction(
                                new MidFourNoteTakePath1_0(color)
                        ),
                        new TrajectoryAction(
                                new MidFourNoteTakePath2(color)
                        ),
                        new TrajectoryAction(
                                new MidFourNoteTakePath3(color)
                        ),
                        new TrajectoryAction(
                                new MidFourNoteTakePath3_0(color)
                        )
                )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                        new WaitAction(.3),
                        new ShootAction(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                        new ParallelAction(
                                new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                                trajectoryActions.get(0)
                        ),
                        new ParallelAction(
                                new ShootAction(Shooter.ROLLER_STATE.SHOOT_DISTANCE, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                                trajectoryActions.get(1)
                        ),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_DISTANCE, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                        new ParallelAction(
                                new ShootAction(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                                trajectoryActions.get(2)
                        ),
                        new ParallelAction(
                                trajectoryActions.get(3),
                                new SeriesAction(
                                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_AMP, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                                        new WaitAction(1.9),
                                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_AMP, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                                        new WaitAction(0.5),
                                        new ShootAction(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW)
                                )
                        ),
                        new ParallelAction(
                                new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                                trajectoryActions.get(4)
                        ),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW)
                )
        );
    }
}
