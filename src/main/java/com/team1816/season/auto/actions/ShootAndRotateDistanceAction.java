package com.team1816.season.auto.actions;

import com.team1816.lib.auto.actions.RotateSwerveAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.subsystems.Shooter;

public class ShootAndRotateDistanceAction extends SeriesAction {
    public ShootAndRotateDistanceAction(AutoModeManager.Position position) {
        super(
                new SeriesAction(
                        new WaitForCollectAction(),
                        new RotateSwerveAction(AutoMode.getNeededRotation(position)),
                        new ShootDistanceAction()
                )
        );
    }
}
