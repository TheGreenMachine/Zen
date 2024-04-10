package com.team1816.lib.auto.actions;

import com.team1816.lib.Injector;
import com.team1816.lib.auto.Color;
import com.team1816.lib.subsystems.drive.Drive;
import com.team1816.lib.subsystems.drive.EnhancedSwerveDrive;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

import static com.team1816.lib.subsystems.Subsystem.robotState;
import static com.team1816.lib.subsystems.drive.Drive.kRotationActionControllerConstraints;

public class RotateSwerveAction implements AutoAction {

    private final Drive drive;

    private Rotation2d desiredHeading;

    private final double tolerance = Constants.kClosedLoopRotationTolerance;

    private final SwerveDriveKinematics kinematics;

    private ProfiledPIDController thetaController;

    public RotateSwerveAction(Rotation2d inputHeading) {
        drive = Injector.get(Drive.Factory.class).getInstance();
        if(robotState.allianceColor == Color.BLUE) {
            desiredHeading = inputHeading;
        } else {
            desiredHeading = new Rotation2d(180 - inputHeading.getDegrees());
        }
        this.kinematics = ((EnhancedSwerveDrive) drive).getKinematics();

        thetaController = new ProfiledPIDController(
                20,
                0,
                0,
                kRotationActionControllerConstraints
        );
        thetaController.enableContinuousInput(-Math.PI, Math.PI);
    }

    @Override
    public void start() {
        thetaController.reset(drive.getPose().getRotation().getRadians());
        double rotationalSpeed = thetaController.calculate(
                drive.getPose().getRotation().getRadians(), desiredHeading.getRadians());

        ((EnhancedSwerveDrive) drive).setModuleStates(
                kinematics.toSwerveModuleStates(
                        new ChassisSpeeds(0,0, rotationalSpeed)
                )
        );
    }

    @Override
    public void update() {
        double rotationalSpeed = thetaController.calculate(
                drive.getPose().getRotation().getRadians(), desiredHeading.getRadians());

        ((EnhancedSwerveDrive) drive).setModuleStates(
                kinematics.toSwerveModuleStates(
                        new ChassisSpeeds(0,0, rotationalSpeed)
                )
        );
    }

    @Override
    public boolean isFinished() {
        return thetaController.atGoal();
    }

    @Override
    public void done() {
        drive.stop();
    }
}
