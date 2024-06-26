package com.team1816.season.auto.actions;

import com.team1816.lib.auto.actions.AutoAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.season.subsystems.Shooter;

public class EjectAction extends SeriesAction {
    public EjectAction() {
        super(
            new SeriesAction(
                new ShootAction(Shooter.ROLLER_STATE.EJECT, Shooter.FEEDER_STATE.SHOOT, Shooter.PIVOT_STATE.STOW),
                new WaitAction(0.1),
                new ShootAction(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW)
                    //TODO @Elena we had made it IDLE when we thought it was a problem with that, was the beambreak instead - if you find that this causes issues just go ahead and switch it back to IDLE
            )
        );
    }
}
