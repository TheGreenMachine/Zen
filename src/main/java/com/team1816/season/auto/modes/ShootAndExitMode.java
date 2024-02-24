package com.team1816.season.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.actions.ParallelAction;
import com.team1816.lib.auto.actions.RotateSwerveAction;
import com.team1816.lib.auto.actions.SeriesAction;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.auto.actions.*;
import com.team1816.season.subsystems.Collector;
import com.team1816.season.subsystems.Shooter;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;

public class ShootAndExitMode extends AutoMode {

    ArrayList<DynamicAutoPath> paths;

    public ShootAndExitMode(List<DynamicAutoPath> paths) {
        super(DynamicAutoUtil.encapsulateAutoPaths(paths));
        this.paths = new ArrayList<>(paths);
        if (trajectoryActions.size() > 1) {
            trajectoryActions.subList(1, trajectoryActions.size()).clear();
        }
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        runAction(
                new SeriesAction(
                        paths.get(0).isAmpPath() ? new ShootAmpAction() : new ShootSpeakerAction(),
                        new ParallelAction(
                                new CollectAction(Collector.COLLECTOR_STATE.INTAKE),
                                new ShootAction(Shooter.ROLLER_STATE.SHOOT_DISTANCE, Shooter.FEEDER_STATE.TRANSFER, Shooter.PIVOT_STATE.STOW),
                                trajectoryActions.get(0)
                        ),
                        new RotateSwerveAction(getNeededRotation(paths.get(0).endPosition)),
                        new ShootDistanceAction()
                )
        );
    }

}
