package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.ShootAction;
import com.team1816.season.auto.actions.ShootAmpAction;
import com.team1816.season.auto.actions.WaitForCollectAction;
import com.team1816.season.subsystems.Collector;

public class SystemCheckMode extends AutoMode {
    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new ShootAmpAction()
        );
    }
}
