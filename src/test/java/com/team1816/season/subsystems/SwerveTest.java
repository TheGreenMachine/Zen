package com.team1816.season.subsystems;

import com.team1816.TestUtil;
import com.team1816.lib.DriveFactory;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.Injector;
import com.team1816.lib.hardware.components.gyro.IPigeonIMU;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.subsystems.drive.Drive;
import com.team1816.lib.subsystems.drive.SwerveDrive;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.RobotState;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;

import static com.team1816.lib.subsystems.drive.SwerveDrive.swerveKinematics;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SwerveTest {

    private SwerveDrive swerveDrive;
    private final DriveFactory driveFactory;
    private final RobotState state;

    private final double maxVel = 3.75; //  m per sec

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
    public void testTeleopForward() {
        swerveDrive.setTeleopInputs(1,0,0);
        swerveDrive.writeToHardware();
        swerveDrive.readFromHardware();
        verifyStates(swerveDrive.getStates(), maxVel, 0, 0);
    }

    public static void registerInjects() {
        Injector.register(mock(Infrastructure.class));
        Injector.register(RobotState.class);
        Injector.register(mock(LedManager.class));
        Injector.register(mock(DriveFactory.class));
        Injector.register(mock(Drive.class));
        Injector.register(mock(SwerveDrive.class));
    }

    public void verifyStates(
            SwerveModuleState[] states,
            double vxMetersPerSecond,
            double vyMetersPerSecond,
            double omegaRadiansPerSecond
    ) {
        var expected = getExpectedState(
                vxMetersPerSecond,
                vyMetersPerSecond,
                omegaRadiansPerSecond,
                Constants.EmptyRotation2d
        );

        // We verify the returned value from getState to match the original value.
        // So even though we are percent output the getState is used for feedback
        // this needs to be real velocity values that are returned
        for (int i = 0; i < states.length; i++) {
            var actVel = states[i].speedMetersPerSecond;
            assertEquals(
                    Math.abs(expected[i].speedMetersPerSecond),
                    Math.abs(actVel),
                    .01,
                    "Velocity does not match"
            );
            var actRot = states[i].angle.getRadians();
            var expRot = expected[i].angle.getRadians();
            assertEquals( expRot, actRot, .2,"Rotation does not match");
        }
    }

    private SwerveModuleState[] getExpectedState(
            double vxMetersPerSecond,
            double vyMetersPerSecond,
            double omegaRadiansPerSecond,
            Rotation2d robotAngle
    ) {
        var states = swerveKinematics.toSwerveModuleStates(
                ChassisSpeeds.fromFieldRelativeSpeeds(
                        vxMetersPerSecond,
                        vyMetersPerSecond,
                        omegaRadiansPerSecond,
                        robotAngle
                )
        );
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxVel);
        return states;
    }

}
