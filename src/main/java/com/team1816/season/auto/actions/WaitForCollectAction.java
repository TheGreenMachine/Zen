package com.team1816.season.auto.actions;

import com.team1816.lib.Injector;
import com.team1816.lib.auto.actions.AutoAction;
import com.team1816.season.states.RobotState;
import com.team1816.season.subsystems.Collector;
import edu.wpi.first.wpilibj.RobotBase;

public class WaitForCollectAction implements AutoAction {
    private RobotState robotState;
    public WaitForCollectAction() {
        this.robotState = Injector.get(RobotState.class);
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean isFinished() {
        return RobotBase.isSimulation() || robotState.isBeamBreakTriggered;
    }

    @Override
    public void done() {

    }
}
