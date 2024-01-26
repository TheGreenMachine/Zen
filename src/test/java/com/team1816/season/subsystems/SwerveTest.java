package com.team1816.season.subsystems;

import com.team1816.TestUtil;
import com.team1816.lib.DriveFactory;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.Injector;
import com.team1816.lib.hardware.components.gyro.IPigeonIMU;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.subsystems.drive.Drive;
import com.team1816.lib.subsystems.drive.SwerveDrive;
import com.team1816.season.states.RobotState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SwerveTest {

    private SwerveDrive swerveDrive;
    private final DriveFactory driveFactory;
    private final RobotState state;

    public SwerveTest() {
        registerInjects();
        TestUtil.SetupMockRobotFactory(null);
        driveFactory = Injector.get(DriveFactory.class);

        swerveDrive = (SwerveDrive) driveFactory.getInstance();

        state = Injector.get(RobotState.class);
    }

    @BeforeEach
    public void setUp() {
        state.resetPosition();
        swerveDrive.zeroSensors();
    }


    @Test
    public void SwerveSubsystemTest() {
        Assertions.assertFalse(swerveDrive.isDemoMode());
    }

    public static void registerInjects() {
        Injector.register(mock(Infrastructure.class));
        Injector.register(RobotState.class);
        Injector.register(mock(LedManager.class));
        Injector.register(mock(DriveFactory.class));
        Injector.register(mock(Drive.class));
        Injector.register(mock(SwerveDrive.class));
    }

}
