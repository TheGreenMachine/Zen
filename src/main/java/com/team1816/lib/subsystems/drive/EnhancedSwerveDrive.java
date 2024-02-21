package com.team1816.lib.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * Base interface for a swerve drivetrain. Consists of a list of swerve modules.
 */
public interface EnhancedSwerveDrive extends TrackableDrivetrain {
    Rotation2d getTrajectoryHeadings();

    void setModuleStates(SwerveModuleState... desiredStates);

    SwerveDriveKinematics getKinematics();
}
