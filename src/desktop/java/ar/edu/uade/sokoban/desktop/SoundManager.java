package ar.edu.uade.sokoban.desktop;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/** Optional view-side audio. The application works when no sound device exists. */
public final class SoundManager implements AutoCloseable {
    public enum Effect {
        STEP, PUSH, BLOCKED, GOAL, MENU, UNDO, RESTART,
        FRAGILE, ICE, LOCK, GATE, VICTORY
    }

    private final EnumMap<Effect, Clip> clips = new EnumMap<>(Effect.class);
    private boolean enabled = true;
    private boolean audioAvailable = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
        if (!enabled) {
            for (Clip clip : clips.values()) {
                clip.stop();
            }
        }
    }

    public void play(Effect effect) {
        if (!enabled || !audioAvailable) {
            return;
        }
        try {
            Clip clip = clips.get(effect);
            if (clip == null) {
                clip = openClip(effect);
                clips.put(effect, clip);
            }
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException
                | IllegalArgumentException exception) {
            // Missing audio devices are common in remote desktop environments.
            audioAvailable = false;
            System.err.println("Audio no disponible (se puede seguir jugando): "
                    + exception.getMessage());
        }
    }

    private static Clip openClip(Effect effect) throws IOException,
            UnsupportedAudioFileException, LineUnavailableException {
        String filename = "/assets/sfx/" + effect.name().toLowerCase(java.util.Locale.ROOT) + ".wav";
        InputStream source = SoundManager.class.getResourceAsStream(filename);
        if (source == null) {
            throw new IOException("Falta recurso de audio " + filename);
        }
        try (InputStream input = source;
                AudioInputStream decoded = AudioSystem.getAudioInputStream(
                        new BufferedInputStream(input))) {
            Clip clip = AudioSystem.getClip();
            try {
                clip.open(decoded);
                return clip;
            } catch (IOException | LineUnavailableException | RuntimeException exception) {
                clip.close();
                throw exception;
            }
        }
    }

    @Override
    public void close() {
        for (Clip clip : clips.values()) {
            clip.stop();
            clip.close();
        }
        clips.clear();
    }
}
