package ar.edu.uade.sokoban.desktop;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;

/** Optional native device test: clip completion is evidence of playback, never of human hearing. */
public final class NativeAudioSmokeTest {
    private NativeAudioSmokeTest() { }
    public static void main(String[] args) throws Exception {
        final Clip probe;
        try { probe = AudioSystem.getClip(); }
        catch (LineUnavailableException | IllegalArgumentException failure) {
            System.out.println("NATIVE AUDIO RESULT: NOT_RUN_NO_DEVICE");
            return;
        }
        probe.close();
        int passed = 0;
        for (SoundManager.Effect effect : SoundManager.Effect.values()) {
            String resource = "/assets/sfx/" + effect.name().toLowerCase(Locale.ROOT) + ".wav";
            try (InputStream source = NativeAudioSmokeTest.class.getResourceAsStream(resource)) {
                if (source == null) { throw new AssertionError("Missing " + resource); }
                try (AudioInputStream decoded = AudioSystem.getAudioInputStream(new BufferedInputStream(source));
                        Clip clip = AudioSystem.getClip()) {
                    clip.open(decoded);
                    CountDownLatch stopped = new CountDownLatch(1);
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) { stopped.countDown(); }
                    });
                    clip.start();
                    if (!stopped.await(4, TimeUnit.SECONDS) || clip.getFramePosition() < clip.getFrameLength()) {
                        throw new AssertionError("Clip did not finish: " + effect);
                    }
                    passed++;
                }
            }
        }
        System.out.println("NATIVE AUDIO RESULT: " + passed + " PASS, 0 FAIL (device playback; human listening NOT_RUN)");
    }
}
