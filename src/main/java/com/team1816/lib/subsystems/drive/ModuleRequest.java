package com.team1816.lib.subsystems.drive;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class ModuleRequest implements SwerveRequest {
    public SwerveModuleState[] moduleStates = new SwerveModuleState[4];

    public ModuleRequest withModuleStates(SwerveModuleState... moduleStates) {
        this.moduleStates = moduleStates;
        return this;
    }

    @Override
    public StatusCode apply(SwerveControlRequestParameters parameters, SwerveModule... modulesToApply) {
        for (int i = 0; i < 4; i++) {
            modulesToApply[i].apply(moduleStates[i], SwerveModule.DriveRequestType.Velocity, SwerveModule.SteerRequestType.MotionMagic);
        }
        return StatusCode.OK;
    }
}
