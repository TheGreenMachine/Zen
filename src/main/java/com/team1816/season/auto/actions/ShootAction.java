package com.team1816.season.auto.actions;

import com.team1816.lib.Injector;
import com.team1816.lib.auto.actions.AutoAction;
import com.team1816.season.states.RobotState;
import com.team1816.season.subsystems.Shooter;

public class ShootAction implements AutoAction {
    private RobotState robotState;
    private Shooter shooter;
    private Shooter.ROLLER_STATE desiredRollerState;
    private Shooter.FEEDER_STATE desiredFeederState;
    private Shooter.PIVOT_STATE desiredPivotState;

    public ShootAction(Shooter.ROLLER_STATE desiredRollerState, Shooter.FEEDER_STATE desiredFeederState, Shooter.PIVOT_STATE desiredPivotState) {
        this.robotState = Injector.get(RobotState.class);
        this.shooter = Injector.get(Shooter.class);
        this.desiredRollerState = desiredRollerState;
        this.desiredFeederState = desiredFeederState;
        this.desiredPivotState = desiredPivotState;
    }

    public ShootAction(Shooter.PIVOT_STATE desiredPivotState) {
        this.robotState = Injector.get(RobotState.class);
        this.shooter = Injector.get(Shooter.class);
        this.desiredRollerState = robotState.actualRollerState;
        this.desiredFeederState = robotState.actualFeederState;
        this.desiredPivotState = desiredPivotState;
    }


    @Override
    public void start() {
        shooter.setDesiredState(desiredRollerState, desiredFeederState, desiredPivotState);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isFinished() {
        return robotState.actualRollerState == desiredRollerState && robotState.actualFeederState == desiredFeederState && robotState.actualPivotState == desiredPivotState;
    }

    @Override
    public void done() {

    }
}
