package com.team1816.season.auto.modes.eject;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
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
    public BottomMiddleEjects(Color color) {
        super(
            List.of(
                new TrajectoryAction(
                    new BottomSpeakerToFive(color)
                ),
                new TrajectoryAction(
                    new FiveToBottomEject(color)
                ),
                new TrajectoryAction(
                    new BottomEjectToFour(color)
                ),
                new TrajectoryAction(
                    new FourToBottomEject(color)
                ),
                new TrajectoryAction(
                    new BottomEjectToThree(color)
                ),
                new TrajectoryAction(
                    new ThreeToBottomEject(color)
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
                trajectoryActions.get(1),
                new EjectAction(),
                trajectoryActions.get(2),
                trajectoryActions.get(3),
                new EjectAction(),
                trajectoryActions.get(4),
                trajectoryActions.get(5),
                new EjectAction()
            )
        );

    }
}
