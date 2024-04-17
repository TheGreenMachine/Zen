package com.team1816.season.auto.actions;

import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.season.subsystems.Shooter;

public class ShootDistanceAction extends SeriesAction {
    public ShootDistanceAction() {
        super(
                new SeriesAction(
                        new ShootAction(Shooter.PIVOT_STATE.AUTO_AIM),
                        new WaitAction(0.3),
                        new ShootAction(Shooter.ROLLER_STATE.SHOOT_DISTANCE, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.AUTO_AIM),
                        new WaitAction(0.3),
                        new ShootAction(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW)
                )
        );
    }
}
