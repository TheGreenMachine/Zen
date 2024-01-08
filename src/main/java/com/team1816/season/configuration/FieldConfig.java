package com.team1816.season.configuration;

import com.google.inject.Singleton;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class that stores the positions of location targets used for global localization with the help of a vision system
 */
@Singleton
public class FieldConfig {

    public static Field2d field;
    public static final HashMap<Integer, Pose3d> fiducialTargets = new HashMap<>() {
        {
            /**
             * April Tag Targets
             */

            //I LOVE SIG FIGS!!!!!!!!!!

            put(1, new Pose3d(new Translation3d(15.5, 0.5,1.356), new Rotation3d(new Quaternion(0.5,0,0,0.866))));
            put(2, new Pose3d(new Translation3d(16.239, 0.9,1.356), new Rotation3d(new Quaternion(0.5,0,0,0.866))));
            put(3, new Pose3d(new Translation3d(16.579, 4.983,1.451), new Rotation3d(new Quaternion(0,0,0,1))));
            put(4, new Pose3d(new Translation3d(16.579, 5.549,1.451), new Rotation3d(new Quaternion(0,0,0,1))));
            put(5, new Pose3d(new Translation3d(15.007, 8.204,1.356), new Rotation3d(new Quaternion(-0.707,0,0,0.707))));
            put(6, new Pose3d(new Translation3d(2.8, 8.204,1.356), new Rotation3d(new Quaternion(-0.707,0,0,0.707))));
            put(7, new Pose3d(new Translation3d(1.1, 5.548,1.451), new Rotation3d(new Quaternion(1,0,0,0))));
            put(8, new Pose3d(new Translation3d(1.1, 4.983,1.451), new Rotation3d(new Quaternion(1,0,0,0))));
            put(9, new Pose3d(new Translation3d(1.22, 1.06,1.356), new Rotation3d(new Quaternion(0.866,0,0,0.5))));
            put(10, new Pose3d(new Translation3d(1.96, 0.66,1.356), new Rotation3d(new Quaternion(0.866,0,0,0.5))));
            put(11, new Pose3d(new Translation3d(12.5, 3.713,1.321), new Rotation3d(new Quaternion(-0.866,0,0,0.5))));
            put(12, new Pose3d(new Translation3d(12.5, 4.5,1.321), new Rotation3d(new Quaternion(0.866,0,0,0.5))));
            put(13, new Pose3d(new Translation3d(11.72, 4.105,1.321), new Rotation3d(new Quaternion(0,0,0,1))));
            put(14, new Pose3d(new Translation3d(6.1, 4.105,1.321), new Rotation3d(new Quaternion(1,0,0,0))));
            put(15, new Pose3d(new Translation3d(5.2, 4.498,1.321), new Rotation3d(new Quaternion(0.5,0,0,0.866))));
            put(16, new Pose3d(new Translation3d(5.4, 3.713,1.321), new Rotation3d(new Quaternion(-0.5,0,0,0.866))));
        }
    };

    public static void setupField(Field2d field) {
        if (FieldConfig.field != null) {
            return;
        }
        FieldConfig.field = field;

        SmartDashboard.putData("Field", field);

        if (RobotBase.isSimulation()) {
            // Initialize April Tag Poses
            List<Pose2d> aprilTagPoses = new ArrayList<>();
            for (int i = 0; i <= fiducialTargets.size(); i++) {
                if (!fiducialTargets.containsKey(i)) {
                    aprilTagPoses.add(
                            i,
                            new Pose2d(new Translation2d(-1, -1), new Rotation2d())
                    );
                    continue;
                }
                aprilTagPoses.add(
                        i,
                        new Pose2d(
                                fiducialTargets.get(i).getX(),
                                fiducialTargets.get(i).getY(),
                                fiducialTargets.get(i).getRotation().toRotation2d()
                        )
                );
            }
            field.getObject("April Tags").setPoses(aprilTagPoses);
        }
    }
}
