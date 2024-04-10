package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.actions.TrajectoryAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.season.auto.paths.nonDynamic.Bottom.*;

import java.util.List;

public class EjectMiddleThreeBottomMode extends AutoMode {
    public EjectMiddleThreeBottomMode() {
        super(
            List.of(
                new TrajectoryAction(
                    new EjectBottomMiddleThree()
                ),
                new TrajectoryAction(
                    new EjectBottomMiddleThree_2()
                ),
                new TrajectoryAction(
                    new EjectBottomMiddleThree_3()
                ),
                new TrajectoryAction(
                    new EjectBottomMiddleThree_4()
                ),
                new TrajectoryAction(
                    new EjectBottomMiddleThree_5()
                    ''
                )
            )
        );
    }

    @Override
    protected void routine() throws AutoModeEndedException {

    }
}
