package com.team1816.season.auto.actions;

import com.team1816.lib.Injector;
import com.team1816.lib.auto.actions.AutoAction;
import com.team1816.season.states.RobotState;
import com.team1816.season.subsystems.Collector;

public class CollectAction implements AutoAction {
    private RobotState robotState;
    private Collector collector;
    private Collector.COLLECTOR_STATE desiredState;

    public CollectAction(Collector.COLLECTOR_STATE desiredState) {
        this.robotState = Injector.get(RobotState.class);
        this.collector = Injector.get(Collector.class);
        this.desiredState = desiredState;
    }

    @Override
    public void start() {
        collector.setDesiredState(desiredState);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isFinished() {
        return robotState.actualCollectorState == desiredState;
    }

    @Override
    public void done() {

    }
}
