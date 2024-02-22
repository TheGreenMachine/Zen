package com.team1816.season.auto.modes.eject;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.actions.WaitAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.auto.actions.CollectAction;
import com.team1816.season.auto.actions.EjectAction;
import com.team1816.season.auto.actions.ShootSpeakerAction;
import com.team1816.season.auto.paths.nonDynamic.Bottom.*;
import com.team1816.season.subsystems.Collector;

import java.util.List;

public class BottomMiddleEjects extends AutoMode {
    public BottomMiddleEjects() {
        super(
            List.of(
                new TrajectoryAction(
                    new BottomSpeakerToFive()
                ),
                new TrajectoryAction(
                    new FiveToBottomEject()
                ),
                new TrajectoryAction(
                    new BottomEjectToFour()
                ),
                new TrajectoryAction(
                    new FourToBottomEject()
                ),
                new TrajectoryAction(
                    new BottomEjectToThree()
                ),
                new TrajectoryAction(
                    new ThreeToBottomEject()
                )
            )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        GreenLogger.log("Running Bottom Middle Ejects Mode");
        runAction(
            new SeriesAction(
                new ShootSpeakerAction(),
                new ParallelAction(
                    trajectoryActions.get(0),
                    new CollectAction(Collector.COLLECTOR_STATE.INTAKE)
                ),
                new WaitAction(.3),
                trajectoryActions.get(1),
                new EjectAction(),
                new WaitAction(.3),
                trajectoryActions.get(2),
                new WaitAction(.3),
                trajectoryActions.get(3),
                new EjectAction(),
                new WaitAction(.3),
                trajectoryActions.get(4),
                new WaitAction(.3),
                trajectoryActions.get(5),
                new EjectAction()
            )
        );

    }
}
