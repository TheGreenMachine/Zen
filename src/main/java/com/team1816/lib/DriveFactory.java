package com.team1816.lib;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.hardware.factory.RobotFactory;
import com.team1816.lib.subsystems.drive.*;
import com.team1816.lib.util.logUtil.GreenLogger;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * Decides between drivetrain type based on factory constants. Allows for differential / swerve duality.
 *
 * @see Drive
 */
@Singleton
public class DriveFactory implements Drive.Factory {

    private static Drive mDrive;

    @Override
    public Drive getInstance() {
        RobotFactory factory = Injector.get(RobotFactory.class);
        if (mDrive == null) {
            boolean isImplemented = factory.getSubsystem(Drive.NAME).implemented;
            if (isImplemented) {
                boolean isSwerve = factory.getConstant(Drive.NAME, "isSwerve", 0) == 1;
                boolean isCTRSwerve = factory.getConstant(Drive.NAME, "isCTRSwerve", 0) == 1;
                if (isSwerve) {
                    if (isCTRSwerve) {
                        mDrive = Injector.get(CTRESwerveDrive.class);
                    } else {
                        mDrive = Injector.get(SwerveDrive.class);
                    }
                } else {
                    mDrive = Injector.get(TankDrive.class);
                }
            } else {
                mDrive = Injector.get(GhostDrivetrain.class);
            }
            GreenLogger.log("Created " + mDrive.getClass().getSimpleName());
        }
        return mDrive;
    }
}
