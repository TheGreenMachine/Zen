package com.team1816.season;

import com.ctre.phoenix6.CANBus;
import com.team1816.lib.Infrastructure;
import com.team1816.lib.Injector;
import com.team1816.lib.PlaylistManager;
import com.team1816.lib.auto.Color;
import com.team1816.lib.hardware.factory.RobotFactory;
import com.team1816.lib.input_handler.*;
import com.team1816.lib.input_handler.controlOptions.ActionState;
import com.team1816.lib.loops.Looper;
import com.team1816.lib.subsystems.LedManager;
import com.team1816.lib.subsystems.SubsystemLooper;
import com.team1816.lib.subsystems.drive.Drive;
import com.team1816.lib.subsystems.vision.Camera;
import com.team1816.lib.util.Util;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.variableInputs.VariableInput;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.configuration.Constants;
import com.team1816.season.states.Orchestrator;
import com.team1816.season.states.RobotState;
import com.team1816.season.subsystems.Climber;
import com.team1816.season.subsystems.Shooter;
import com.team1816.season.subsystems.Collector;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Robot extends TimedRobot {
    //TODO remove this variable
    private boolean isLooping = false;

    /**
     * Looper
     */
    private final Looper enabledLoop;
    private final Looper disabledLoop;

    /**
     * Controls
     */
    private InputHandler inputHandler;

    private Infrastructure infrastructure;
    private SubsystemLooper subsystemManager;

    /**
     * State Managers
     */
    private Orchestrator orchestrator;
    private RobotState robotState;

    private PlaylistManager playlistManager;
    private boolean desireToPlaySong;

    /**
     * Subsystems
     */
    private Drive drive;

    private Collector collector;
    private LedManager ledManager;
    private Camera camera;
    private Shooter shooter;
    private Climber climber;

    /**
     * Factory
     */
    private static RobotFactory factory;

    /**
     * Autonomous
     */
    private AutoModeManager autoModeManager;

    private Thread autoTargetAlignThread;

    /**
     * Timing
     */
    private double loopStart;
    public static double looperDt; //looptime delta
    public static double robotDt;
    public static double autoStart;
    public static double teleopStart;

    private DoubleLogEntry robotLoopLogger;
    private DoubleLogEntry looperLogger;
    private DoubleLogEntry canivoreTrafficLogger;
    private DoubleLogEntry lowSpeedTrafficLogger;


    /**
     * Properties
     */
    private boolean faulted;


    /**
     * Instantiates the Robot by injecting all systems and creating the enabled and disabled loopers
     */
    Robot() {
        super();
        // initialize injector
        Injector.registerModule(new SeasonModule());
        enabledLoop = new Looper(this);
        disabledLoop = new Looper(this);

        desireToPlaySong = false;

        if (Constants.kLoggingRobot) {
            robotLoopLogger = new DoubleLogEntry(DataLogManager.getLog(), "Timings/Robot");
            looperLogger = new DoubleLogEntry(DataLogManager.getLog(), "Timings/RobotState");

            if (Constants.kHasCANivore) {
                canivoreTrafficLogger = new DoubleLogEntry(DataLogManager.getLog(), "CAN/highSpeedUtilization");
            }
            lowSpeedTrafficLogger = new DoubleLogEntry(DataLogManager.getLog(), "CAN/lowSpeedUtilization");
        }
    }

    /**
     * Returns the length of the last loop that the Robot was on
     *
     * @return duration (ms)
     */
    public Double getLastRobotLoop() {
        return (Timer.getFPGATimestamp() - loopStart) * 1000;
    }

    /**
     * Returns the duration of the last enabled loop
     *
     * @return duration (ms)
     * @see Looper#getLastLoop()
     */
    public Double getLastSubsystemLoop() {
        return enabledLoop.isRunning() ? enabledLoop.getLastLoop() : disabledLoop.getLastLoop();
    }

    /**
     * Actions to perform when the robot has just begun being powered and is done booting up.
     * Initializes the robot by injecting the controlboard, and registering all subsystems.
     */
    @Override
    public void robotInit() {
        try {
            /** Register All Subsystems */
            DriverStation.silenceJoystickConnectionWarning(true);
            // Remember to register our subsystems below! The subsystem manager deals with calling
            // readFromHardware and writeToHardware on a loop, but it can only call read/write it if it
            // can recognize the subsystem. To recognize your subsystem, just add it alongside the
            // drive, ledManager, and camera parameters.


            // TODO: Set up any other subsystems here.

            factory = Injector.get(RobotFactory.class);
            ledManager = Injector.get(LedManager.class);
            camera = Injector.get(Camera.class);
            camera.setDriverMode(true);
            robotState = Injector.get(RobotState.class);
            orchestrator = Injector.get(Orchestrator.class);
            infrastructure = Injector.get(Infrastructure.class);
            subsystemManager = Injector.get(SubsystemLooper.class);
            autoModeManager = Injector.get(AutoModeManager.class);
            playlistManager = Injector.get(PlaylistManager.class);
            shooter = Injector.get(Shooter.class);
            collector = Injector.get(Collector.class);
            climber = Injector.get(Climber.class);

            /** Logging */
            if (Constants.kLoggingRobot) {
                var logFile = new SimpleDateFormat("MMdd_HH-mm").format(new Date());
                var robotName = System.getenv("ROBOT_NAME");
                if (robotName == null) robotName = "default";
                var logFileDir = "/home/lvuser/";
                String OS_NAME = System.getProperty("os.name").toLowerCase();
                // if there is a USB drive use it
                if (Files.exists(Path.of("/media/sda1"))) {
                    logFileDir = "/media/sda1/";
                }

//                // Characterize SignalLogger
//                SignalLogger.setPath(logFileDir);
//                SignalLogger.enableAutoLogging(DriverStation.isFMSAttached());

                if (RobotBase.isSimulation()) {
                    if (OS_NAME.contains("win")) {
                        logFileDir = System.getenv("temp") + "\\";
                    } else {
                        logFileDir = System.getProperty("user.dir") + "/";
                    }
                    if (!OS_NAME.contains("mac")) { //Can't open .hoot on mac so won't clog logs up in sim
//                        SignalLogger.start();
                    }
                }

                // start logging
                DataLogManager.start(logFileDir, "", Constants.kLooperDt);
                if (RobotBase.isReal()) {
                    if (!DriverStation.isFMSAttached()) {
                        Util.cleanLogFiles();
                    }
//                    SignalLogger.start();
                }
                DriverStation.startDataLog(DataLogManager.getLog(), false);
            }

            drive = (Injector.get(Drive.Factory.class)).getInstance();

            subsystemManager.setSubsystems(drive, ledManager, camera, collector, shooter, climber);

            subsystemManager.registerEnabledLoops(enabledLoop);
            subsystemManager.registerDisabledLoops(disabledLoop);

            subsystemManager.zeroSensors();
            // zeroing ypr - (-90) pigeon is mounted with the "y" axis facing forward
            drive.resetPigeon(Rotation2d.fromDegrees(-90));


            /** [Specific subsystem] not zeroed on boot up - letting ppl know */
            faulted = true;

            SmartDashboard.putBoolean("PlaySong", false);
            SmartDashboard.putNumber("FeederShootSpeed",10);
            SmartDashboard.putNumber("TestShuttleShot",71);

            RobotController.setBrownoutVoltage(6);

            /** Register inputHandler */
            inputHandler = Injector.get(InputHandler.class);


            /** Driver Commands */
            inputHandler.listenAction(
                    "zeroPose",
                    ActionState.PRESSED,
                    () ->
                            drive.resetHeading(
                                    robotState.allianceColor == Color.BLUE ?
                                            Rotation2d.fromDegrees(0) :
                                            Rotation2d.fromDegrees(180)
                            )
            );

            inputHandler.listenAction(
                    "hardZeroPose", //FIXME this sometimes does weird rotation in sim, idk about in real life
                    ActionState.PRESSED,
                    () ->
                            drive.zeroSensors(
                                    robotState.allianceColor == Color.BLUE ?
                                            Constants.kDefaultZeroingPose :
                                            Constants.kFlippedZeroingPose
                            )
            );


            inputHandler.listenActionPressAndRelease(
                    "brakeMode",
                    drive::setBraking
            );

            inputHandler.listenActionPressAndRelease(
                    "turboMode",
                    drive::setTurboMode
            );

            inputHandler.listenActionPressAndRelease(
                    "slowMode",
                    drive::setSlowMode
            );
    //balls
            //balls
            inputHandler.listenActionPressAndRelease(
                    "snapToPickup",
                    (pressed) -> {
                        robotState.snapDirection = pressed ? RobotState.SnappingDirection.PICKUP : RobotState.SnappingDirection.NO_SNAP;
                    }
            );

            inputHandler.listenActionPressAndRelease(
                    "snapToScore",
                    (pressed) -> {
                        robotState.snapDirection = pressed ? RobotState.SnappingDirection.SCORE : RobotState.SnappingDirection.NO_SNAP;
                    }
            );

            inputHandler.listenActionPressAndRelease(
                    "snapToBottomSpeaker",
                    (pressed) -> {
                        robotState.snapDirection = pressed ? RobotState.SnappingDirection.BOTTOM_SPEAKER : RobotState.SnappingDirection.NO_SNAP;
                    }
            );

            inputHandler.listenActionPressAndRelease(
                    "snapToTopSpeaker",
                    (pressed) -> {
                        robotState.snapDirection = pressed ? RobotState.SnappingDirection.TOP_SPEAKER : RobotState.SnappingDirection.NO_SNAP;
                    }
            );

            /** Operator Commands */
            inputHandler.listenAction(
                    "revSpeaker",
                    ActionState.PRESSED,
                    () -> {
                        shooter.setDesiredRollerState(Shooter.ROLLER_STATE.SHOOT_SPEAKER);
                    }
            );
            inputHandler.listenActionPressAndRelease(
                    "shoot",
                    (pressed) -> {
                        robotState.isShooting = pressed;
                        if (pressed) {
                            if (shooter.getDesiredPivotState() == Shooter.PIVOT_STATE.SHOOT_AMP) {
                                shooter.setDesiredState(Shooter.ROLLER_STATE.SHOOT_AMP, Shooter.FEEDER_STATE.SHOOT);
                            } else if (shooter.getDesiredPivotState() == Shooter.PIVOT_STATE.AUTO_AIM) {
                                shooter.setDesiredState(Shooter.ROLLER_STATE.SHOOT_DISTANCE, Shooter.FEEDER_STATE.SHOOT);
                            } else {
                                shooter.setDesiredState(Shooter.ROLLER_STATE.SHOOT_SPEAKER, Shooter.FEEDER_STATE.SHOOT);
                            }
                        }
                        else {
                            shooter.setDesiredState(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.STOP, Shooter.PIVOT_STATE.STOW);
                        }
                    }
            );
            inputHandler.listenAction(
                    "ampPivot",
                    ActionState.PRESSED,
                    () -> {
                        if (shooter.getDesiredPivotState() == Shooter.PIVOT_STATE.STOW) {
                            shooter.setDesiredPivotState(Shooter.PIVOT_STATE.SHOOT_AMP);
                        }
                        else {
                            shooter.setDesiredPivotState(Shooter.PIVOT_STATE.STOW);
                        }

                        GreenLogger.log("Changing pivot to: " + shooter.getDesiredPivotState());
                    }
            );
            inputHandler.listenAction(
                    "shootPivot",
                    ActionState.PRESSED,
                    () -> {
                        if(shooter.getDesiredPivotState() == Shooter.PIVOT_STATE.AUTO_AIM)
                            shooter.setDesiredPivotState(Shooter.PIVOT_STATE.STOW);
                        else
                            shooter.setDesiredPivotState(Shooter.PIVOT_STATE.AUTO_AIM);
                        GreenLogger.log("Changing pivot to: " + shooter.getDesiredPivotState());
                    }
            );

            inputHandler.listenAction(
                    "stopShooter",
                    ActionState.PRESSED,
                    () -> {
                        shooter.stop();
                    }
            );
            inputHandler.listenActionPressAndRelease(
                    "collectorOuttake",
                    (pressed) -> {
                        if (!shooter.isBeamBreakTriggered()) {
                            collector.setDesiredState(pressed ? Collector.COLLECTOR_STATE.OUTTAKE : Collector.COLLECTOR_STATE.INTAKE);
                        }
                    }
            );
            inputHandler.listenActionPressAndRelease(
                    "climbSlow",
                    (pressed) -> {
                        climber.setDesiredState(pressed ? Climber.CLIMBER_STATE.CLIMB_SLOW : Climber.CLIMBER_STATE.STOP);
                    }
            );
            inputHandler.listenActionPressAndRelease(
                    "climbFast",
                    (pressed) -> {
                        climber.setDesiredState(pressed ? Climber.CLIMBER_STATE.CLIMB_FAST : Climber.CLIMBER_STATE.STOP);
                    }
            );

            inputHandler.listenAction(
                    "climbPosition",
                    ActionState.PRESSED,
                    () -> {
                        climber.setDesiredState(Climber.CLIMBER_STATE.PRECISE_TOP);
                    }
            );

            //Buttonboard commands
            inputHandler.listenActionPressAndRelease(
                    "reSpool",
                    (pressed) -> {
                        climber.setDesiredState(pressed ? Climber.CLIMBER_STATE.RE_SPOOL : Climber.CLIMBER_STATE.STOP);
                    }
            );

            inputHandler.listenAction(
                    "zeroClimber",
                    ActionState.PRESSED,
                    climber::zeroSensors
            );

            //Not yet assigned
            inputHandler.listenAction(
                    "toggleOverride",
                    ActionState.PRESSED,
                    () -> {
                        GreenLogger.log("Toggling beambreak override to " + !robotState.isBeamBreakOverridden);
                        robotState.isBeamBreakOverridden = !robotState.isBeamBreakOverridden;
                    }
            );

            inputHandler.listenActionPressAndRelease(
                    "manualFeedingTransfer",
                    (pressed) -> {
                        shooter.setDesiredFeederState(pressed ? Shooter.FEEDER_STATE.MANUAL_TRANSFER : Shooter.FEEDER_STATE.STOP);
                    }
            );

            inputHandler.listenActionPressAndRelease(
                    "rotateToPoint",
                    drive::setRotatingClosedLoop
            );

            inputHandler.listenActionPressAndRelease(
                    "SpeakerRev",
                    (pressed) -> {
                        if (!robotState.isShooting) {
                           shooter.setDesiredRollerState(pressed ? Shooter.ROLLER_STATE.SHOOT_SPEAKER : Shooter.ROLLER_STATE.STOP);
                        }
                    }
            );

            inputHandler.listenActionPressAndRelease(
                    "ShuttleRev",
                    (pressed) -> {
                        if (!robotState.isShooting) {
                            //REPLACE WITH SHUTTLE TODO TODO TODO
                            shooter.setDesiredRollerState(pressed ? Shooter.ROLLER_STATE.SHOOT_SPEAKER : Shooter.ROLLER_STATE.STOP);
                        }
                    }
            );

            SmartDashboard.putString("Git Hash", Constants.kGitHash);

        } catch (Throwable t) {
            faulted = true;
            throw t;
        }
    }

    /**
     * Actions to perform when the robot has entered the disabled period
     */
    @Override
    public void disabledInit() {
        try {
            orchestrator.clearThreads();

            enabledLoop.stop();
            // Stop any running autos
            autoModeManager.stopAuto();
            ledManager.setDefaultStatus(LedManager.RobotStatus.DISABLED);
            ledManager.writeToHardware();

            if (autoModeManager.getSelectedAuto() == null) {
                autoModeManager.reset();
            }

            subsystemManager.stop();

            robotState.resetAllStates();
            drive.zeroSensors();

            disabledLoop.start();
        } catch (Throwable t) {
            faulted = true;
            throw t;
        }
    }

    /**
     * Actions to perform when the robot has entered the autonomous period
     */
    @Override
    public void autonomousInit() {
        disabledLoop.stop();
        orchestrator.stopSong();
        ledManager.setDefaultStatus(LedManager.RobotStatus.AUTONOMOUS);
        ledManager.indicateStatus(LedManager.RobotStatus.AUTONOMOUS);

        drive.zeroSensors(autoModeManager.getSelectedAuto().getInitialPose());
//        shooter.zeroMotor();

        shooter.setDesiredState(Shooter.ROLLER_STATE.STOP, Shooter.FEEDER_STATE.STOP, Shooter.PIVOT_STATE.STOW);
        collector.setDesiredState(Collector.COLLECTOR_STATE.STOP);
        climber.setDesiredState(Climber.CLIMBER_STATE.STOP);

        drive.setControlState(Drive.ControlState.TRAJECTORY_FOLLOWING);
        autoModeManager.startAuto();

        autoStart = Timer.getFPGATimestamp();
        enabledLoop.start();
    }


    /**
     * Actions to perform when the robot has entered the teleoperated period
     */
    @Override
    public void teleopInit() {
        try {
            disabledLoop.stop();
            orchestrator.stopSong();
            ledManager.setDefaultStatus(LedManager.RobotStatus.ENABLED);
            ledManager.indicateStatus(LedManager.RobotStatus.ENABLED);

            infrastructure.startCompressor();

            teleopStart = Timer.getFPGATimestamp();
            enabledLoop.start();

            drive.resetHeading(
                    robotState.allianceColor == Color.BLUE ?
                            Rotation2d.fromDegrees(0) :
                            Rotation2d.fromDegrees(180)
            );

//            shooter.zeroMotor();
        } catch (Throwable t) {
            faulted = true;
            throw t;
        }
    }

    /**
     * Actions to perform when the robot has entered the test period
     */
    @Override
    public void testInit() {
        try {
            orchestrator.stopSong();
            double initTime = System.currentTimeMillis();

            ledManager.indicateStatus(LedManager.RobotStatus.ENABLED, LedManager.ControlState.BLINK);
            // Warning - blocks thread - intended behavior?
            while (System.currentTimeMillis() - initTime <= 3000) {
                ledManager.writeToHardware();
            }

            enabledLoop.stop();
            disabledLoop.start();
            drive.zeroSensors();

            ledManager.indicateStatus(LedManager.RobotStatus.DISABLED, LedManager.ControlState.BLINK);

            if (subsystemManager.testSubsystems()) {
                GreenLogger.log("ALL SYSTEMS PASSED");
                ledManager.indicateStatus(LedManager.RobotStatus.ENABLED);
            } else {
                System.err.println("CHECK ABOVE OUTPUT SOME SYSTEMS FAILED!!!");
                ledManager.indicateStatus(LedManager.RobotStatus.ERROR);
            }
        } catch (Throwable t) {
            faulted = true;
            throw t;
        }
    }

    /**
     * Actions to perform periodically on the robot when the robot is powered
     */
    @Override
    public void robotPeriodic() {
        try {
            // updating loop timers
            Robot.looperDt = getLastSubsystemLoop();
            Robot.robotDt = getLastRobotLoop();
            loopStart = Timer.getFPGATimestamp();


            if (Constants.kLoggingRobot) {
                looperLogger.append(looperDt);
                robotLoopLogger.append(robotDt);

                if (Constants.kHasCANivore) {
                    canivoreTrafficLogger.append(CANBus.getStatus(Constants.kCANivoreName).BusUtilization);
                }
                lowSpeedTrafficLogger.append(CANBus.getStatus(Constants.kLowSpeedBusName).BusUtilization);
            }

            subsystemManager.outputToSmartDashboard(); // update shuffleboard for subsystem values
            robotState.outputToSmartDashboard(); // update robot state on field for Field2D widget
            autoModeManager.outputToSmartDashboard(); // update shuffleboard selected auto mode
            playlistManager.outputToSmartDashboard(); // update shuffleboard selected song

            double activeRumble = robotState.readyToShoot ? 0.9 : 0.7;
            orchestrator.setControllerRumble(InputHandler.ControllerRole.DRIVER, InputHandler.RumbleDirection.UNIFORM,
                    robotState.isBeamBreakTriggered && !robotState.isBeamBreakOverridden ? activeRumble : 0);
        } catch (Throwable t) {
            faulted = true;
            GreenLogger.log(t.getMessage());
        }
    }

    /**
     * Actions to perform periodically when the robot is in the disabled period
     */
    @Override
    public void disabledPeriodic() {
        inputHandler.updateControllerLayout();

        try {
            if (RobotController.getUserButton()) {
                drive.zeroSensors(Constants.kDefaultZeroingPose);
                ledManager.indicateStatus(LedManager.RobotStatus.ZEROING);
            } else {
                // non-camera LEDs will flash red if robot periodic updates fail
                if (faulted) {
                    if (ledManager.getCurrentControlStatus() != LedManager.RobotStatus.ERROR) {
                        ledManager.indicateStatus(LedManager.RobotStatus.ERROR, LedManager.ControlState.BLINK);
                    }
                    ledManager.writeToHardware();
                }
            }

            if (RobotBase.isReal()) {
//                // logic for zeroing elevator
//                if (lastButton != zeroingButton.get() && lastButton) { // will only be true when changing from false to true
//                    if (zeroing == null) { // zeroing
//                        faulted = false;
//                        zeroing = true;
//                        shooter.zeroSensors();
//                        drive.resetPigeon(Rotation2d.fromDegrees(-90));
//                    } else if (zeroing) { // ready
//                        zeroing = false;
//                        shooter.setBraking(true);
//                    } else { // needs zeroing
//                        zeroing = null;
//                        shooter.setBraking(false);
//
//                        faulted = true;
//                    }
//                }
//                lastButton = zeroingButton.get();
//
//                if (ledManager.getCurrentControlStatus() == LedManager.RobotStatus.ZEROING) {
//                    // only keep looping through write if zeroing elevator cus we need to update its blinking
//                    ledManager.writeToHardware();
//                }
            }

            // Periodically check if drivers changed desired auto - if yes, then update the robot's position on the field
            if (autoModeManager.update()) {
                drive.zeroSensors(autoModeManager.getSelectedAuto().getInitialPose());
                robotState.field
                        .getObject("Trajectory")
                        .setTrajectory(
                                autoModeManager.getSelectedAuto().getCurrentTrajectory()
                        );
            }

            if (drive.isDemoMode()) { // Demo-mode
                drive.update();
            }

            playlistManager.update();
            desireToPlaySong = SmartDashboard.getBoolean("PlaySong", false);
            orchestrator.playSong(desireToPlaySong);
        } catch (Throwable t) {
            faulted = true;
            throw t;
        }
    }

    /**
     * Actions to perform periodically when the robot is in the autonomous period
     */
    @Override
    public void autonomousPeriodic() {
        robotState.field
                .getObject("Trajectory")
                .setTrajectory(autoModeManager.getSelectedAuto().getCurrentTrajectory());

        if(Constants.kLoggingRobot) {
            GreenLogger.updatePeriodicLogs();
        }

    }

    /**
     * Actions to perform periodically when the robot is in the teleoperated period
     */
    @Override
    public void teleopPeriodic() {
        try {

            if (Constants.kUseVision) {
                if (robotState.currentCamFind && robotState.actualPivotState != Shooter.PIVOT_STATE.AUTO_AIM) {
                    orchestrator.updatePoseWithVisionData();
                }
            }

            if(Constants.kLoggingRobot) {
                GreenLogger.updatePeriodicLogs();
            }


            manualControl();
        } catch (Throwable t) {
            faulted = true;
            throw t;
        }
    }

    /**
     * Sets manual inputs for subsystems like the drivetrain when criteria met
     */
    public void manualControl() {
        inputHandler.update();

        if (robotState.rotatingClosedLoop) {
            drive.rotationPeriodic();
        } else {
            drive.setTeleopInputs(
                    -inputHandler.getActionAsDouble("throttle"),
                    -inputHandler.getActionAsDouble("strafe"),
                    -inputHandler.getActionAsDouble("rotation")
            );
        }

        orchestrator.autoSetCollectorState();
    }

    /**
     * Actions to perform periodically when the robot is in the test period
     */
    @Override
    public void testPeriodic() {
    }
}
