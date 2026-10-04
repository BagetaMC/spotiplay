package com.spotiplay.audio;

import net.fabricmc.loader.api.FabricLoader;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class LocalAudioPlayer {
    private static Clip currentClip;
    private static boolean isPlaying = false;

    public static File getMusicFolder() {
        File folder = FabricLoader.getInstance().getConfigDir().resolve("spotiplay/music").toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static void playSound(String fileName) {
        stopSound();
        File audioFile = new File(getMusicFolder(), fileName);

        if (!audioFile.exists()) {
            System.err.println("[SpotiPlay] Soubor nenalezen: " + audioFile.getAbsolutePath());
            return;
        }

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            currentClip.start();
            isPlaying = true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stopSound() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
        }
        isPlaying = false;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }
}
