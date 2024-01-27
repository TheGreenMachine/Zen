package com.team1816.lib;

import com.team1816.lib.subsystems.drive.Drive;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.states.Orchestrator;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * A manager for song selection using CTRE's CHIRP files
 * @see com.ctre.phoenix6.Orchestra
 */
@Singleton
public class PlaylistManager {
    /**
     * Properties: Selection
     */
    protected SendableChooser<Playlist> songChooser;
    protected Playlist desiredSong;

    private Drive drive;

    private Orchestrator orchestrator;

    /**
     * Instantiates the Playlist manager
     */
    @Inject
    public PlaylistManager() {
        drive = (Injector.get(Drive.Factory.class)).getInstance();
        orchestrator = Injector.get(Orchestrator.class);
        GreenLogger.log("Result of file load = " + orchestrator.loadSong(Playlist.COCONUT_MALL));

        songChooser = new SendableChooser<>();
        SmartDashboard.putData("Playlist", songChooser);
        for (Playlist playlist : Playlist.values()) {
            songChooser.addOption(playlist.name(), playlist);
        }
        songChooser.setDefaultOption(Playlist.COCONUT_MALL.name(), Playlist.COCONUT_MALL);
    }

    /**
     * Updates the song to the desiredSong and stops the song if it has been playing for too long
     *
     * @return songChanged
     */
    public boolean update() {
        Playlist selectedSong = songChooser.getSelected();
        boolean songChanged = desiredSong != selectedSong;

        if (songChanged) {
            GreenLogger.log("Song changed from: " + desiredSong + " to: " + selectedSong.name());
            desiredSong = selectedSong;

            drive.orchestra.loadMusic(desiredSong.getFilePath());
        }

        return songChanged;
    }

    /**
     * Outputs values to SmartDashboard
     */
    public void outputToSmartDashboard() {
        if (desiredSong != null) {
            SmartDashboard.putString("SongSelected", desiredSong.name());
        }
    }

    /**
     * Enum for songs
     */
    public enum Playlist {
        COCONUT_MALL("coconutMall"),
        TIMBER_PITBULL("timber"),
        AFRICA_TOTO("africaToto"),
        LOWRIDER_WAR("lowrider"),
        SEPTEMBER_EWF("september"),
        FIRST_TRAIN("firstTrain"),
        CRUEL_ANGELS_THESIS("cruelAngel");

        final String path;

        Playlist(String path) {
            this.path = path;
        }

        public String getFilePath() {
            return path + ".chrp";
        }
    }
}
