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
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.TopThreeFromBottom.Note3ToNote4;
import com.team1816.season.auto.paths.TopThreeFromBottom.Note4ToNote5;
import com.team1816.season.auto.paths.TopThreeFromBottom.Note5Eject;
import com.team1816.season.auto.paths.TopThreeFromBottom.SubwooferToNote3;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;
import org.yaml.snakeyaml.events.CollectionEndEvent;

import javax.naming.PartialResultException;
import java.util.List;

public class TopThreeNoteTakeMode extends AutoMode {
    public TopThreeNoteTakeMode(Color color) {
        super(
                List.of(
                        new TrajectoryAction(
                                new SubwooferToNote3(color)
                        ),
                        new TrajectoryAction(
                                new Note3ToNote4(color)
                        ),
                        new TrajectoryAction(
                                new Note4ToNote5(color)
                        ),
                        new TrajectoryAction(
                                new Note5Eject(color)
                        )
                )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                        new WaitAction(.5),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                        new ParallelAction(
                                new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                                trajectoryActions.get(0),
                                new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW)
                        ),
                        new ParallelAction(
                                trajectoryActions.get(1),
                                new SeriesAction(
                                        new WaitAction(1.6),
                                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                                        new WaitAction(.3),
                                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW)
                                )
                        ),
                        new ParallelAction(
                                trajectoryActions.get(2),
                                new SeriesAction(
                                        new WaitAction(1.4),
                                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                                        new WaitAction(.3),
                                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW)
                                )
                        ),
                        trajectoryActions.get(3),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW)
                )
        );
    }
}
