package com.team1816.season.auto.actions;

import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;

public class ShootSpeakerAction extends SeriesAction {
    public ShootSpeakerAction() {
        super(
                new SeriesAction(
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                        new WaitAction(0.5),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW)
                )
        );
    }
}
