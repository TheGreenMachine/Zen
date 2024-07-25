package com.team1816.season.auto;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.modes.DoNothingMode;
import com.team1816.lib.auto.modes.DriveStraightMode;
import com.team1816.lib.auto.modes.TuneDrivetrainMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.lib.hardware.factory.YamlConfig;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.variableInputs.Numeric;
import com.team1816.lib.variableInputs.VariableInput;
import com.team1816.season.auto.modes.*;
import com.team1816.season.auto.modes.distance.FourScoreFromDistanceMode;
import com.team1816.season.auto.modes.eject.Bottom345Ejects;
import com.team1816.season.auto.modes.eject.BottomMiddleEjects;
import com.team1816.season.auto.modes.eject.TopMiddleEjects;
import com.team1816.season.auto.paths.StartToAmpPath;
import com.team1816.season.auto.paths.arbitrary.ArbitraryStartToNoteOnePath;
import com.team1816.season.auto.paths.arbitrary.ArbitraryStartToNoteThreePath;
import com.team1816.season.auto.paths.noteToNote.NoteOneToNoteTwoPath;
import com.team1816.season.auto.paths.noteToNote.NoteThreeToNoteTwoPath;
import com.team1816.season.auto.paths.toNoteOne.AmpToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.BottomSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.MiddleSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteThree.AmpToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.BottomSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.MiddleSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.TopSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteTwo.*;
import com.team1816.season.configuration.Constants;
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
    private final SendableChooser<DesiredCollect> thirdCollectChooser;
    private final SendableChooser<ShootPos> firstShootChooser;
    private final SendableChooser<ShootPos> secondShootChooser;
    private ShootPos desiredStart;
    private DesiredCollect desiredFirstCollect;
    private ShootPos desiredFirstShoot;
    private DesiredCollect desiredSecondCollect;
    private ShootPos desiredSecondShoot;
    private DesiredCollect desiredThirdCollect;

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

        thirdCollectChooser = new SendableChooser<>();

        scramChooser = new SendableChooser<>();

        SmartDashboard.putData("Auto mode", autoModeChooser); // appends chooser to shuffleboard

//        autoModeChooser.addOption("GETTYSBURG", DesiredAuto.RANGE_FOUR_SCORE);
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

        SmartDashboard.putData("Third collected note", thirdCollectChooser);
        for (DesiredCollect noteToCollect : DesiredCollect.values()) {
            thirdCollectChooser.addOption(noteToCollect.name(), noteToCollect);
        }
        thirdCollectChooser.setDefaultOption(DesiredCollect.TOP_NOTE.name(), DesiredCollect.TOP_NOTE);

        DynamicAutoUtil.registerPaths(List.of(
                new TopSpeakerToNoteOnePath(), new TopSpeakerToNoteTwoPath(), new TopSpeakerToNoteThreePath(), new TopSpeakerToNoteTwoTopPath(),
                new MiddleSpeakerToNoteOnePath(), new MiddleSpeakerToNoteTwoPath(), new MiddleSpeakerToNoteThreePath(),
                new BottomSpeakerToNoteOnePath(), new BottomSpeakerToNoteTwoPath(), new BottomSpeakerToNoteThreePath(),
                new AmpToNoteOnePath(), new AmpToNoteTwoPath(), new AmpToNoteThreePath(), new AmpToNoteTwoTopPath(),
                new NoteOneToNoteTwoPath(), new NoteThreeToNoteTwoPath(),
                new ArbitraryStartToNoteOnePath(), new ArbitraryStartToNoteThreePath()
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

        desiredThirdCollect = DesiredCollect.TOP_NOTE;

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

        DesiredCollect selectedThirdCollect = thirdCollectChooser.getSelected();

        ScramChoice selectedScram = scramChooser.getSelected();

        Color selectedColor = Color.BLUE;

        if (RobotBase.isSimulation()) {
            selectedColor = sideChooser.getSelected();
        } else if (RobotBase.isReal()) {
            var dsAlliance = DriverStation.getAlliance().isPresent() ? DriverStation.getAlliance().get() : sideChooser.getSelected(); //ternary hell
            selectedColor = (dsAlliance == DriverStation.Alliance.Red) ? Color.RED : Color.BLUE;
        }

        boolean autoChanged = desiredAuto != selectedAuto;
        boolean startPosChanged = desiredStart != selectedStartPos;
        boolean colorChanged = teamColor != selectedColor;
        boolean dynamicAutoChanged = //TODO make iterative
                selectedStartPos != desiredStart
                || selectedFirstCollect != desiredFirstCollect
                || selectedFirstShoot != desiredFirstShoot
                || selectedSecondCollect != desiredSecondCollect
                || selectedSecondShoot != desiredSecondShoot
                || selectedThirdCollect != desiredThirdCollect
                || selectedScram != desiredScram;

        // if auto has been changed, update selected auto mode + thread
        if (autoChanged || colorChanged || startPosChanged || dynamicAutoChanged ) {
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

            if (selectedAuto == DesiredAuto.TWO_SCORE
//                    || selectedAuto == DesiredAuto.RANGE_TWO_SCORE
                    || selectedAuto == DesiredAuto.THREE_SCORE
//                    || selectedAuto == DesiredAuto.RANGE_FOUR_SCORE
            ) {
                autoMode = generateDynamicAutoMode(selectedAuto, selectedColor,
                        List.of(selectedStartPos, selectedFirstShoot, selectedSecondShoot),
                        List.of(selectedFirstCollect, selectedSecondCollect, selectedThirdCollect)
                );
            } else {
                dynamicAutoChanged = false; //Stops unnecessary defaulting/zeroing
                if (selectedAuto == DesiredAuto.SCORE_AND_SIT) {
                    autoMode = new ScoreAndSitMode(selectedStartPos, selectedColor);
                } else {
                    autoMode = generateAutoMode(selectedAuto, selectedColor);
                }
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

        desiredThirdCollect = selectedThirdCollect;

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
//        DO_NOTHING,
//        TUNE_DRIVETRAIN,
        LIVING_ROOM,
        DRIVE_STRAIGHT,

        // System check
        SYSTEM_CHECK,

        // New Auto Modes : 2024
        TWO_SCORE,
        THREE_SCORE,
//        RANGE_FOUR_SCORE,
//        RANGE_TWO_SCORE,
        SCORE_AND_SCRAM,
        SCORE_AND_SIT,

        QUICK_EJECT,

        //Scorched Earth
//        SCEA_AMPTOSOURCE,
        SCEA_AMPTOSOURCE2,
        SCEA_LEON,
        SCEA_SOURCETOAMP,
//        SCEA_SOURCETOAMP2,
//        SCEA_SOURCETOAMP3,

        SCEA_SKIP_BOTTOM,


        //Weird Stuff
        MID_FOUR_NOTE,
        MID_FOUR_NOTE_SUBWOOFER,
        TOP_THREE_NOTE_SUBWOOFER,


//        BOTTOM_345_EJECTS,
        BOTTOM_MIDDLE_EJECTS,
        TOP_MIDDLE_EJECTS,
        TEST
        }

    public enum ScramChoice {
        SCRAM,
        NOT //dumb
    }

    public enum ShootPos {
        TOP_SPEAKER,
        MIDDLE_SPEAKER,
        BOTTOM_SPEAKER,
        AMP,
        ARB_START
    }

    public enum DesiredCollect {
        TOP_NOTE,
        MIDDLE_NOTE,
//        MIDDLE_NOTE_TOP,
        BOTTOM_NOTE
    }

    public enum Position { //For use in the dynamic lookup table
        TOP_SPEAKER,
        MIDDLE_SPEAKER,
        BOTTOM_SPEAKER,
        AMP,
        ARB_START_AMP,

        ARB_START,

        TOP_NOTE,
        MIDDLE_NOTE,
        MIDDLE_NOTE_TOP,
        BOTTOM_NOTE,

        MIDDLE_ONE
    }

    public static Position toPosition(ShootPos shootPosition) {
        return switch (shootPosition) {
            case AMP -> Position.AMP;
            case TOP_SPEAKER -> Position.TOP_SPEAKER;
            case MIDDLE_SPEAKER -> Position.MIDDLE_SPEAKER;
            case BOTTOM_SPEAKER -> Position.BOTTOM_SPEAKER;
            case ARB_START -> Position.ARB_START;
        };
    }

    public static Position toPosition(DesiredCollect shootPosition) {
        return switch (shootPosition) {
            case TOP_NOTE -> Position.TOP_NOTE;
            case MIDDLE_NOTE -> Position.MIDDLE_NOTE;
//            case MIDDLE_NOTE_TOP -> Position.MIDDLE_NOTE_TOP;
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
//            case DO_NOTHING:
//                return new DoNothingMode();
//            case TUNE_DRIVETRAIN: // commented for competition purposes
//                return new TuneDrivetrainMode();
//            case LIVING_ROOM:
//                return (new LivingRoomMode(color));
            case DRIVE_STRAIGHT:
                return new DriveStraightMode();
            case BOTTOM_MIDDLE_EJECTS: // Can get the bottom two
                return new BottomMiddleEjects(color);
            case TOP_MIDDLE_EJECTS: // Can get the top three
                return new TopMiddleEjects(color);
            case SCORE_AND_SCRAM:
                return new ScoreAndScramMode(color);
            case QUICK_EJECT: // Gets first one and first middle shuttle - probably useless.
                return new QuickEjectsMode(color);
//            case SCEA_AMPTOSOURCE:
//                return new ScorchedEarthTopToBotMode();
            case SCEA_AMPTOSOURCE2:
                return new ScorchedEarthTopToBot2Mode(color);
            case SCEA_LEON:
                return new ScorchedEarthLeonTopAuto(color);
            case SCEA_SOURCETOAMP:
                return new ScorchedEarthBotToTopMode(color);
//            case SCEA_SOURCETOAMP2:
//                return new ScorchedEarthBotToTop2Mode();
//            case SCEA_SOURCETOAMP3:
//                return new ScorchedEarthBotToTop3Mode();
            case SCEA_SKIP_BOTTOM:
                return new ScorchedEarthBottomSkipOne(color);
//            case BOTTOM_345_EJECTS:
//                return new Bottom345Ejects();
            case MID_FOUR_NOTE: // Able to grab bottom two and the second from the top but not bring that one back - worth?
                return new MidFourNoteTakeMode(color);
            case MID_FOUR_NOTE_SUBWOOFER: //Shoots once, gets bottom two and stops before middle- likely not worth it
                return new MidFourNoteTakeSubwooferMode(color);
            case TOP_THREE_NOTE_SUBWOOFER: //Grabs all 3 but doesn't shuttle the last one
                return new TopThreeNoteTakeMode(color);
            case TEST:
                if (!DriverStation.isFMSAttached()) return new TestMode();
            case SYSTEM_CHECK:
                if (!DriverStation.isFMSAttached()) return new SystemCheckMode();
            default:
                GreenLogger.log("Defaulting to drive straight mode");
                return new DriveStraightMode();
        }
    }

    private AutoMode generateDynamicAutoMode(DesiredAuto mode, Color color, List<ShootPos> shootPositions, List<DesiredCollect> collectPositions) {
        boolean isScramming = desiredScram == ScramChoice.SCRAM;

//        if (mode == DesiredAuto.RANGE_FOUR_SCORE) {
//            return new FourScoreFromDistanceMode(
//                    generateFourScorePaths(color, shootPositions.get(0), collectPositions.get(0) == DesiredCollect.TOP_NOTE),
//                    isScramming
//            );
//        }

        List<DynamicAutoPath> dynamicPathList = generateDynamicPathList(color, shootPositions , collectPositions);
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

            paths.add(DynamicAutoUtil.getDynamicPath(start, end, color).orElse(new MiddleSpeakerToNoteTwoPath(color)));

            start = toPosition(collectPositions.get(i));
            end = toPosition(shootPositions.get(i+1));

            paths.add(DynamicAutoUtil.getReversedDynamicPath(start, end, color).orElse(new MiddleSpeakerToNoteTwoPath(color).withInversedWaypoints()));
        }

        paths.get(0).setUsingCorrection(true);
        if (collectPositions.size() >= shootPositions.size()) { //should provide the ability to have an ending collect
            paths.add(DynamicAutoUtil.getDynamicPath(
                    toPosition(shootPositions.get(shootPositions.size()-1)),
                    toPosition(collectPositions.get(collectPositions.size()-1)),
                    color)
                    .orElse(new MiddleSpeakerToNoteTwoPath(color))
            );
        }

        return paths;
    }

    private List<DynamicAutoPath> generateDynamicPathListFromDistance(Color color, ShootPos startPosition, List<DesiredCollect> collectPositions) {
        ArrayList<DynamicAutoPath> paths = new ArrayList<>();

        Position start = toPosition(startPosition);
        Position end = toPosition(collectPositions.get(0));

        paths.add(DynamicAutoUtil.getDynamicPath(start, end, color).orElse(new MiddleSpeakerToNoteTwoPath())); //Add start to collect 1

        for (int i = 0; i < collectPositions.size() - 1; i++) {
           start = toPosition(collectPositions.get(i));
           end = toPosition(collectPositions.get(i + 1));

           paths.add(DynamicAutoUtil.getDynamicPath(start,end,color).orElse(new MiddleSpeakerToNoteTwoPath()));
        }

        return paths;
    }

    private List<DynamicAutoPath> generateFourScorePaths(Color color, ShootPos startPosition, boolean startAtTop) {
        return List.of(
                DynamicAutoUtil.getDynamicPath(toPosition(startPosition), startAtTop ? Position.TOP_NOTE : Position.BOTTOM_NOTE, color).orElse(new MiddleSpeakerToNoteTwoPath()),
                DynamicAutoUtil.getDynamicPath(startAtTop ? Position.TOP_NOTE : Position.BOTTOM_NOTE, Position.MIDDLE_NOTE, color).orElse(new MiddleSpeakerToNoteTwoPath()),
                DynamicAutoUtil.getReversedDynamicPath(Position.MIDDLE_NOTE, startAtTop ? Position.BOTTOM_NOTE : Position.TOP_NOTE, color).orElse(new MiddleSpeakerToNoteTwoPath())
        );
    }

}
