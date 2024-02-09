package com.team1816.season.auto;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.modes.DoNothingMode;
import com.team1816.lib.auto.modes.DriveStraightMode;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.modes.TuneDrivetrainMode;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.auto.modes.*;
import com.team1816.season.auto.paths.toNoteOne.AmpToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.BottomSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.MiddleSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteThree.AmpToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.BottomSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.MiddleSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.TopSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteTwo.AmpToNoteTwoPath;
import com.team1816.season.auto.paths.toNoteTwo.BottomSpeakerToNoteTwoPath;
import com.team1816.season.auto.paths.toNoteTwo.MiddleSpeakerToNoteTwoPath;
import com.team1816.season.auto.paths.toNoteTwo.TopSpeakerToNoteTwoPath;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    private final SendableChooser<ShootPos> firstShootChooser;
    private ShootPos desiredStart;
    private DesiredCollect desiredFirstCollect;
    private ShootPos desiredFirstShoot;


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
        // Doing manually because can't start @ amp
        startPosChooser.setDefaultOption(ShootPos.TOP_SPEAKER.name(), ShootPos.TOP_SPEAKER);
        startPosChooser.addOption(ShootPos.MIDDLE_SPEAKER.name(), ShootPos.MIDDLE_SPEAKER);
        startPosChooser.addOption(ShootPos.BOTTOM_SPEAKER.name(), ShootPos.BOTTOM_SPEAKER);

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

        Color selectedColor = Color.BLUE;


        if (RobotBase.isSimulation()) {
            selectedColor = sideChooser.getSelected();
        } else if (RobotBase.isReal()) {
            var dsAlliance = DriverStation.getAlliance().isPresent() ? DriverStation.getAlliance().get() : sideChooser.getSelected(); //ternary hell
            selectedColor = (dsAlliance == DriverStation.Alliance.Red) ? Color.RED : Color.BLUE;
        }

        boolean autoChanged = desiredAuto != selectedAuto;
        boolean colorChanged = teamColor != selectedColor;
        boolean dynamicAutoChanged = selectedStartPos != desiredStart || selectedFirstCollect != desiredFirstCollect || selectedFirstShoot != desiredFirstShoot;

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

            if (selectedAuto == DesiredAuto.TWO_SCORE) {
                autoMode = generateDynamicAutoMode(selectedAuto, selectedColor, selectedStartPos, selectedFirstCollect, selectedFirstShoot);
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
        TWO_SCORE
    }

    enum ShootPos {
        TOP_SPEAKER,
        MIDDLE_SPEAKER,
        BOTTOM_SPEAKER,
        AMP
    }

    enum DesiredCollect {
        TOP_NOTE,
        MIDDLE_NOTE,
        BOTTOM_NOTE
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
            case TUNE_DRIVETRAIN: // commented for competition purposes
                return new TuneDrivetrainMode();
//            case LIVING_ROOM:
//                return (new LivingRoomMode(color));
            case TEST:
                return new TestMode();
            default:
                GreenLogger.log("Defaulting to drive straight mode");
                return new DriveStraightMode();
        }
    }

    private AutoMode generateDynamicAutoMode(DesiredAuto mode, Color color, ShootPos selectedStart, DesiredCollect selectedCollect, ShootPos selectedShoot) {
        return new TwoScoreMode(generateDynamicPathList(color, selectedStart,selectedCollect,selectedShoot));
    }

    private List<AutoPath> generateDynamicPathList(Color color, ShootPos start, DesiredCollect collectOne, ShootPos shootOne) {
        //TODO generalize?

        // Start -> first collect
        AutoPath startToCollect;
        if (start == ShootPos.TOP_SPEAKER) {
            startToCollect = switch (collectOne) {
                case TOP_NOTE -> new TopSpeakerToNoteOnePath(color);
                case MIDDLE_NOTE -> new TopSpeakerToNoteTwoPath(color);
                case BOTTOM_NOTE -> new TopSpeakerToNoteThreePath(color);
            };
        } else if (start == ShootPos.MIDDLE_SPEAKER) {
            startToCollect = switch (collectOne) {
                case TOP_NOTE -> new MiddleSpeakerToNoteOnePath(color);
                case MIDDLE_NOTE -> new MiddleSpeakerToNoteTwoPath(color);
                case BOTTOM_NOTE -> new MiddleSpeakerToNoteThreePath(color);
            };
        } else {
            startToCollect = switch (collectOne) {
                case TOP_NOTE -> new BottomSpeakerToNoteOnePath(color);
                case MIDDLE_NOTE -> new BottomSpeakerToNoteTwoPath(color);
                case BOTTOM_NOTE -> new BottomSpeakerToNoteThreePath(color);
            };
        }

        // Collect one -> Shoot one
        AutoPath collectToShoot; //TODO make amp paths
        if (collectOne == DesiredCollect.TOP_NOTE) {
            collectToShoot = switch (shootOne) {
                case TOP_SPEAKER -> new TopSpeakerToNoteOnePath(color).withInversedWaypoints();
                case MIDDLE_SPEAKER -> new MiddleSpeakerToNoteOnePath(color).withInversedWaypoints();
                case BOTTOM_SPEAKER -> new BottomSpeakerToNoteOnePath(color).withInversedWaypoints();
                case AMP -> new AmpToNoteOnePath(color).withInversedWaypoints();
            };
        } else if (collectOne == DesiredCollect.MIDDLE_NOTE) {
            collectToShoot = switch (shootOne) {
                case TOP_SPEAKER -> new TopSpeakerToNoteTwoPath(color).withInversedWaypoints();
                case MIDDLE_SPEAKER -> new MiddleSpeakerToNoteTwoPath(color).withInversedWaypoints();
                case BOTTOM_SPEAKER -> new BottomSpeakerToNoteTwoPath(color).withInversedWaypoints();
                case AMP -> new AmpToNoteTwoPath(color).withInversedWaypoints();
            };
        } else {
            collectToShoot = switch (shootOne) {
                case TOP_SPEAKER -> new TopSpeakerToNoteThreePath(color).withInversedWaypoints();
                case MIDDLE_SPEAKER -> new MiddleSpeakerToNoteThreePath(color).withInversedWaypoints();
                case BOTTOM_SPEAKER -> new BottomSpeakerToNoteThreePath(color).withInversedWaypoints();
                case AMP -> new AmpToNoteThreePath(color).withInversedWaypoints();
            };
        }

        return List.of(startToCollect, collectToShoot);
    }
}
