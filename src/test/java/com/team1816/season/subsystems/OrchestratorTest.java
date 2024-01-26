package com.team1816.season.subsystems;

import com.team1816.TestUtil;
import com.team1816.lib.Injector;
import com.team1816.season.states.Orchestrator;
import com.team1816.season.states.RobotState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class OrchestratorTest {
    private final RobotState state;

    private Orchestrator mOrchestrator;

    public OrchestratorTest() {
        TestUtil.SetupMockRobotFactory(null);
        state = Injector.get(RobotState.class);
    }

    @BeforeEach
    public void setUp() {
        mOrchestrator = Injector.get(Orchestrator.class);
        state.resetPosition();
    }

    //TODO test clear threads?

    //TODO test calculateSingleTargetTranslation;

    //TODO if we want to test update/calc from camera, we'll need to create a setupMockRobotState

    //TODO test any pertinent season-specific actions
}
