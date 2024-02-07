package com.team1816.season.configuration;

import com.google.inject.Singleton;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
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
    public static List<AprilTag> aprilTags = AprilTagFields.kDefaultField.loadAprilTagLayoutField().getTags(); //should be current season field
    public static final HashMap<Integer, Pose3d> fiducialTargets = new HashMap<>() {
        {
            /**
             * April Tag Targets
             */
            aprilTags.forEach((aprilTag -> {
                put(aprilTag.ID, aprilTag.pose);
            }));
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
