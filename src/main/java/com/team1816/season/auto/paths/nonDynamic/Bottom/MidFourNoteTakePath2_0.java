//package com.team1816.season.auto.paths.nonDynamic.Bottom;
//
//import com.team1816.lib.auto.Color;
//import com.team1816.lib.auto.paths.AutoPath;
//import edu.wpi.first.math.geometry.Pose2d;
//import edu.wpi.first.math.geometry.Rotation2d;
//
//import java.util.List;
//
//public class MidFourNoteTakePath2_0 extends AutoPath {
//    public MidFourNoteTakePath2_0(Color color) {
//        super(color);
//    }
//
//    @Override
//    protected List<Pose2d> getWaypoints() {
//        return List.of(
//                new Pose2d(8.32, 2.48, Rotation2d.fromDegrees(-14+180)),
//                new Pose2d(4.4, 5, Rotation2d.fromDegrees(180-25))
////                new Pose2d(8.26, 4.14, Rotation2d.fromDegrees(0)),
////                new Pose2d(5.22, 4.29, Rotation2d.fromDegrees(20)),
////                new Pose2d(8.28, 5.79, Rotation2d.fromDegrees(30))
//        );
//    }
//
//    @Override
//    protected List<Rotation2d> getWaypointHeadings() {
//        return List.of(
//                Rotation2d.fromDegrees(-15),
//                Rotation2d.fromDegrees(-20)
////                Rotation2d.fromDegrees(0),
////                Rotation2d.fromDegrees(80),
////                Rotation2d.fromDegrees(-20)
//        );
//    }
//
//    @Override
//    protected boolean usingApp() {
//        return true;
//    }
//}
