package com.team1816.season.auto;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.modes.DoNothingMode;
import com.team1816.lib.auto.modes.DriveStraightMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.auto.modes.*;
import com.team1816.season.auto.modes.eject.BottomMiddleEjects;
import com.team1816.season.auto.modes.eject.TopMiddleEjects;
import com.team1816.season.auto.paths.StartToAmpPath;
import com.team1816.season.auto.paths.toNoteOne.AmpToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.BottomSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.MiddleSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteThree.AmpToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.BottomSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.MiddleSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.TopSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteTwo.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * An integrated and optimized manager for autonomous mode selection and configuration
 */
@Singleton
public class AutoModeManager {

    /**
     * Properties: Selection
     */
    public static com.team1816.season.states.RobotState robotState;
    private final SendableChooser<DesiredAuto> autoModeChooser;
    private final SendableChooser<Color> sideChooser;
    private DesiredAuto desiredAuto;
    private Color teamColor;

    /**
     * Properties: Dynamic Auto
     */
    private final SendableChooser<ShootPos> startPosChooser;
    private final SendableChooser<DesiredCollect> firstCollectChooser;
    private final SendableChooser<DesiredCollect> secondCollectChooser;
    private final SendableChooser<ShootPos> firstShootChooser;
    private final SendableChooser<ShootPos> secondShootChooser;
    private ShootPos desiredStart;
    private DesiredCollect desiredFirstCollect;
    private ShootPos desiredFirstShoot;
    private DesiredCollect desiredSecondCollect;
    private ShootPos desiredSecondShoot;

    private final SendableChooser<ScramChoice> scramChooser;
    private ScramChoice desiredScram;

    /**
     * Properties: Execution
     */
    private AutoMode autoMode;
    private static Thread autoModeThread;

    /**
     * Instantiates and AutoModeManager with a default option and selective computation
     *
     * @param rs RobotState
     */
    @Inject
    public AutoModeManager(com.team1816.season.states.RobotState rs) {
        robotState = rs;
        autoModeChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose desired auto mode
        sideChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose desired side / bumper color
        startPosChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose desired starting position for dynamic auto

        firstCollectChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose first desired note collected for dynamic auto
        firstShootChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose first desired shoot position for dynamic auto

        secondCollectChooser = new SendableChooser<>();
        secondShootChooser = new SendableChooser<>();

        scramChooser = new SendableChooser<>();

        SmartDashboard.putData("Auto mode", autoModeChooser); // appends chooser to shuffleboard

        for (DesiredAuto desiredAuto : DesiredAuto.values()) {
            autoModeChooser.addOption(desiredAuto.name(), desiredAuto);
        }
        autoModeChooser.setDefaultOption(
            DesiredAuto.DRIVE_STRAIGHT.name(),
            DesiredAuto.DRIVE_STRAIGHT
        );

        SmartDashboard.putData("Robot color", sideChooser); // appends chooser to shuffleboard

        sideChooser.setDefaultOption(Color.BLUE.name(), Color.BLUE); // initialize options
        sideChooser.addOption(Color.RED.name(), Color.RED); // initialize options

        /**
         * Dynamic Auto
         */
        SmartDashboard.putData("Dynamic Start", startPosChooser);
        for (ShootPos shootPos : ShootPos.values()) {
            startPosChooser.addOption(shootPos.name(), shootPos);
        }
        startPosChooser.setDefaultOption(ShootPos.MIDDLE_SPEAKER.name(), ShootPos.MIDDLE_SPEAKER);

        SmartDashboard.putData("First collected note", firstCollectChooser);
        for (DesiredCollect noteToCollect : DesiredCollect.values()) {
            firstCollectChooser.addOption(noteToCollect.name(), noteToCollect);
        }
        firstCollectChooser.setDefaultOption(DesiredCollect.TOP_NOTE.name(), DesiredCollect.TOP_NOTE);

        SmartDashboard.putData("First shooting position", firstShootChooser);
        for (ShootPos shootPos : ShootPos.values()) {
            firstShootChooser.addOption(shootPos.name(), shootPos);
        }
        firstShootChooser.setDefaultOption(ShootPos.TOP_SPEAKER.name(), ShootPos.TOP_SPEAKER);


        SmartDashboard.putData("Second collected note", secondCollectChooser);
        for (DesiredCollect noteToCollect : DesiredCollect.values()) {
            secondCollectChooser.addOption(noteToCollect.name(), noteToCollect);
        }
        secondCollectChooser.setDefaultOption(DesiredCollect.TOP_NOTE.name(), DesiredCollect.TOP_NOTE);

        SmartDashboard.putData("Second shooting position", secondShootChooser);
        for (ShootPos shootPos : ShootPos.values()) {
            secondShootChooser.addOption(shootPos.name(), shootPos);
        }
        secondShootChooser.setDefaultOption(ShootPos.TOP_SPEAKER.name(), ShootPos.TOP_SPEAKER);

        DynamicAutoUtil.registerPaths(List.of(
                new TopSpeakerToNoteOnePath(), new TopSpeakerToNoteTwoPath(), new TopSpeakerToNoteThreePath(), new TopSpeakerToNoteTwoTopPath(),
                new MiddleSpeakerToNoteOnePath(), new MiddleSpeakerToNoteTwoPath(), new MiddleSpeakerToNoteThreePath(),
                new BottomSpeakerToNoteOnePath(), new BottomSpeakerToNoteTwoPath(), new BottomSpeakerToNoteThreePath(),
                new AmpToNoteOnePath(), new AmpToNoteTwoPath(), new AmpToNoteThreePath(), new AmpToNoteTwoTopPath()
        ));

        SmartDashboard.putData("Scram Or Not", scramChooser);
        for (ScramChoice scramOption : ScramChoice.values()) {
            scramChooser.addOption(scramOption.name(), scramOption);
        }
        scramChooser.setDefaultOption(ScramChoice.NOT.name(), ScramChoice.NOT);

        reset();
    }

    /**
     * Resets properties to default and resets the thread
     */
    public void reset() {
        autoMode = new DriveStraightMode();
        autoModeThread = new Thread(autoMode::run);
        desiredAuto = DesiredAuto.DRIVE_STRAIGHT;
        teamColor = Color.RED;
        robotState.allianceColor = teamColor;

        desiredStart = ShootPos.TOP_SPEAKER;
        desiredFirstCollect = DesiredCollect.TOP_NOTE;
        desiredFirstShoot = ShootPos.TOP_SPEAKER;

        desiredSecondCollect = DesiredCollect.TOP_NOTE;
        desiredSecondShoot = ShootPos.TOP_SPEAKER;

        desiredScram = ScramChoice.NOT;
    }

    /**
     * Updates the choosers in realtime
     *
     * @return true if updated
     */
    public boolean update() {
        DesiredAuto selectedAuto = autoModeChooser.getSelected();
        ShootPos selectedStartPos = startPosChooser.getSelected();

        DesiredCollect selectedFirstCollect = firstCollectChooser.getSelected();
        ShootPos selectedFirstShoot = firstShootChooser.getSelected();

        DesiredCollect selectedSecondCollect = secondCollectChooser.getSelected();
        ShootPos selectedSecondShoot = secondShootChooser.getSelected();

        ScramChoice selectedScram = scramChooser.getSelected();

        Color selectedColor = Color.BLUE;

        if (RobotBase.isSimulation()) {
            selectedColor = sideChooser.getSelected();
        } else if (RobotBase.isReal()) {
            var dsAlliance = DriverStation.getAlliance().isPresent() ? DriverStation.getAlliance().get() : sideChooser.getSelected(); //ternary hell
            selectedColor = (dsAlliance == DriverStation.Alliance.Red) ? Color.RED : Color.BLUE;
        }

        boolean autoChanged = desiredAuto != selectedAuto;
        boolean colorChanged = teamColor != selectedColor;
        boolean dynamicAutoChanged = //TODO make iterative
                selectedStartPos != desiredStart
                || selectedFirstCollect != desiredFirstCollect
                || selectedFirstShoot != desiredFirstShoot
                || selectedSecondCollect != desiredSecondCollect
                || selectedSecondShoot != desiredSecondShoot
                || selectedScram != desiredScram;

        // if auto has been changed, update selected auto mode + thread
        if (autoChanged || colorChanged || dynamicAutoChanged) {
            if (autoChanged) {
                GreenLogger.log(
                    "Auto changed from: " + desiredAuto + ", to: " + selectedAuto.name()
                );
            }
            if (colorChanged) {
                GreenLogger.log("Robot color changed from: " + teamColor + ", to: " + selectedColor);
            }

            if(selectedScram != desiredScram) {
                GreenLogger.log("Scram changed from: " + desiredScram + ", to: " + selectedScram);
                desiredScram = selectedScram; //In this conditional so that scram generation works properly
            }

            if (selectedAuto == DesiredAuto.TWO_SCORE || selectedAuto == DesiredAuto.SCORE_AND_EXIT || selectedAuto == DesiredAuto.THREE_SCORE) {
                autoMode = generateDynamicAutoMode(selectedAuto, selectedColor,
                        List.of(selectedStartPos, selectedFirstShoot, selectedSecondShoot),
                        List.of(selectedFirstCollect, selectedSecondCollect)
                );
            } else {
                dynamicAutoChanged = false; //Stops unnecessary defaulting/zeroing
                autoMode = generateAutoMode(selectedAuto, selectedColor);
            }


            autoModeThread = new Thread(autoMode::run);
        }
        desiredAuto = selectedAuto;
        teamColor = selectedColor;
        robotState.allianceColor = teamColor;

        desiredStart = selectedStartPos;
        desiredFirstCollect = selectedFirstCollect;
        desiredFirstShoot = selectedFirstShoot;

        desiredSecondCollect = selectedSecondCollect;
        desiredSecondShoot = selectedSecondShoot;

        //Legacy 2023 pathfinder code
//                if (robotState.allianceColor == Color.BLUE) {
//            robotState.pathFinder = new PathFinder(List.of(Constants.blueChargeStation));
//        } else {
//            robotState.pathFinder = new PathFinder(List.of(Constants.redChargeStation));
//        }

        return autoChanged || colorChanged || dynamicAutoChanged;
    }

    /**
     * Outputs values to SmartDashboard
     */
    public void outputToSmartDashboard() {
        if (desiredAuto != null) {
            SmartDashboard.putString("AutoModeSelected", desiredAuto.name());
        }
        if (teamColor != null) {
            SmartDashboard.putString("RobotColorSelected", teamColor.name());
        }
    }

    /**
     * Returns the selected autonomous mode
     *
     * @return AutoMode
     * @see AutoMode
     */
    public AutoMode getSelectedAuto() {
        return autoMode;
    }

    /**
     * Returns the selected color
     *
     * @return Color
     * @see Color
     */
    public Color getSelectedColor() {
        return sideChooser.getSelected();
    }

    /**
     * Executes the auto mode and respective thread
     */
    public void startAuto() {
        autoModeThread.start();
    }

    /**
     * Stops the auto mode
     */
    public void stopAuto() {
        if (autoMode != null) {
            autoMode.stop();
            autoModeThread = new Thread(autoMode::run);
        }
    }

    /**
     * Enum for AutoModes
     */
    enum DesiredAuto {
        // Test : 2020 Legacy
        DO_NOTHING,
        TUNE_DRIVETRAIN,
        LIVING_ROOM,
        DRIVE_STRAIGHT,

        // New Auto Modes : 2024
        TEST,
        TWO_SCORE,
        THREE_SCORE,
        SCORE_AND_EXIT,

        BOTTOM_MIDDLE_EJECTS,

        TOP_MIDDLE_EJECTS,

        SCORE_AND_SCRAM
    }

    public enum ScramChoice {
        SCRAM,
        NOT //dumb
    }

    public enum ShootPos {
        TOP_SPEAKER,
        MIDDLE_SPEAKER,
        BOTTOM_SPEAKER,
        AMP
    }

    public enum DesiredCollect {
        TOP_NOTE,
        MIDDLE_NOTE,
        MIDDLE_NOTE_TOP,
        BOTTOM_NOTE
    }

    public enum Position { //For use in the dynamic lookup table
        TOP_SPEAKER,
        MIDDLE_SPEAKER,
        BOTTOM_SPEAKER,
        AMP,
        ARB_START,

        TOP_NOTE,
        MIDDLE_NOTE,
        MIDDLE_NOTE_TOP,
        BOTTOM_NOTE
    }

    public static Position toPosition(ShootPos shootPosition) {
        return switch (shootPosition) {
            case AMP -> Position.AMP;
            case TOP_SPEAKER -> Position.TOP_SPEAKER;
            case MIDDLE_SPEAKER -> Position.MIDDLE_SPEAKER;
            case BOTTOM_SPEAKER -> Position.BOTTOM_SPEAKER;
        };
    }

    public static Position toPosition(DesiredCollect shootPosition) {
        return switch (shootPosition) {
            case TOP_NOTE -> Position.TOP_NOTE;
            case MIDDLE_NOTE -> Position.MIDDLE_NOTE;
            case MIDDLE_NOTE_TOP -> Position.MIDDLE_NOTE_TOP;
            case BOTTOM_NOTE -> Position.BOTTOM_NOTE;
        };
    }


    /**
     * Generates each AutoMode by demand
     *
     * @param mode desiredMode
     * @return AutoMode
     * @see AutoMode
     */
    private AutoMode generateAutoMode(DesiredAuto mode, Color color) {
        switch (mode) {
            case DO_NOTHING:
                return new DoNothingMode();
//            case TUNE_DRIVETRAIN: // commented for competition purposes
//                return new TuneDrivetrainMode();
//            case LIVING_ROOM:
//                return (new LivingRoomMode(color));
            case TEST:
                return new TestMode();
            case DRIVE_STRAIGHT:
                return new DriveStraightMode();
            case BOTTOM_MIDDLE_EJECTS:
                return new BottomMiddleEjects();
            case TOP_MIDDLE_EJECTS:
                return new TopMiddleEjects();
            case SCORE_AND_SCRAM:
                return new ScoreAndScramMode();
            default:
                GreenLogger.log("Defaulting to drive straight mode");
                return new DriveStraightMode();
        }
    }

    private AutoMode generateDynamicAutoMode(DesiredAuto mode, Color color, List<ShootPos> shootPositions, List<DesiredCollect> collectPositions) {
        List<DynamicAutoPath> dynamicPathList = generateDynamicPathList(color, shootPositions , collectPositions);
        boolean isScramming = desiredScram == ScramChoice.SCRAM;
        if (mode == DesiredAuto.TWO_SCORE) {
            if (dynamicPathList.get(0).isAmpPath()) {
                dynamicPathList.add(0, new StartToAmpPath());
                return new TwoScoreFromAmpMode(dynamicPathList, isScramming);
            } else {
                return new TwoScoreFromSpeakerMode(dynamicPathList, isScramming);
            }
        } else if (mode == DesiredAuto.THREE_SCORE) {
            if (dynamicPathList.get(0).isAmpPath()) {
                dynamicPathList.add(0, new StartToAmpPath());
                return new ThreeScoreFromAmpMode(dynamicPathList, isScramming);
            } else {
                return new ThreeScoreFromSpeakerMode(dynamicPathList, isScramming);
            }
        } else {
            return new ShootAndExitMode(dynamicPathList);
        }
    }

    private List<DynamicAutoPath> generateDynamicPathList(Color color, List<ShootPos> shootPositions, List<DesiredCollect> collectPositions) {
        ArrayList<DynamicAutoPath> paths = new ArrayList<>();

        Position start;
        Position end;
        for (int i = 0; i < shootPositions.size() - 1; i++) {
            start = toPosition(shootPositions.get(i));
            end = toPosition(collectPositions.get(i));

            paths.add(DynamicAutoUtil.getDynamicPath(start, end, color).orElse(new MiddleSpeakerToNoteTwoPath()));

            start = toPosition(collectPositions.get(i));
            end = toPosition(shootPositions.get(i+1));

            paths.add(DynamicAutoUtil.getReversedDynamicPath(start, end, color).orElse(new MiddleSpeakerToNoteTwoPath().withInversedWaypoints()));
        }

        paths.get(0).setUsingCorrection(true);
        if (collectPositions.size() >= shootPositions.size()) { //should provide the ability to have an ending collect
            paths.add(DynamicAutoUtil.getDynamicPath(
                    toPosition(shootPositions.get(shootPositions.size()-1)),
                    toPosition(collectPositions.get(collectPositions.size()-1)),
                    color)
                    .orElse(new MiddleSpeakerToNoteTwoPath())
            );
        }

        return paths;
    }
}
