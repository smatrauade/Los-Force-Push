package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/** No display or audio device required; validates packaging and real pixel rendering. */
public final class DesktopVisualAssetsTest {
    private static final String[] SPRITES = {
        "floor_0", "floor_1", "floor_2", "wall_0", "wall_1", "wall_2",
        "goal", "goal_active", "box", "box_goal", "box_key",
        "box_fragile_3", "box_fragile_2", "box_fragile_1", "box_fragile_0",
        "box_broken", "ice", "lock", "lock_active", "gate_closed", "gate_open",
        "player_down", "player_up", "player_left", "player_right",
        "player_down_step", "player_up_step", "player_left_step", "player_right_step"
    };

    private DesktopVisualAssetsTest() { }

    public static void main(String[] args) throws Exception {
        int total = 0;
        PixelAssets assets = new PixelAssets();
        for (String sprite : SPRITES) {
            BufferedImage image = assets.image(sprite);
            check(image.getWidth() == 32 && image.getHeight() == 32, sprite);
            total++;
        }
        for (SoundManager.Effect sound : SoundManager.Effect.values()) {
            String name = "/assets/sfx/" + sound.name().toLowerCase(java.util.Locale.ROOT)
                    + ".wav";
            try (var stream = DesktopVisualAssetsTest.class.getResourceAsStream(name)) {
                check(stream != null, name);
                try (AudioInputStream audio = AudioSystem.getAudioInputStream(
                        new java.io.BufferedInputStream(stream))) {
                    check(audio.getFrameLength() > 0, name);
                    total++;
                }
            }
        }
        Path demo = args.length > 0 ? Path.of(args[0])
                : Path.of("src/desktop/resources/levels/demo.txt");
        var board = new LevelLoader(new LevelParser()).load(demo).board();
        BoardPanel panel = new BoardPanel(assets);
        panel.showBoard(board);
        BufferedImage full = new BufferedImage(panel.getPreferredSize().width,
                panel.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        panel.setSize(panel.getPreferredSize());
        Graphics2D graphics = full.createGraphics();
        panel.paint(graphics);
        graphics.dispose();
        check(full.getRGB(24 + 2 * 64 + 16, 24 + 2 * 64 + 24) != 0,
                "BoardPanel no dibujo el personaje");
        total++;
        panel.setScale(1);
        check(panel.getPreferredSize().width == board.width() * 32 + 48,
                "scale 1x width");
        total++;
        panel.setScale(3);
        check(panel.getPreferredSize().height == board.height() * 96 + 48,
                "scale 3x height");
        total++;
        System.out.println("VISUAL ASSETS RESULT: " + total + " PASS, 0 FAIL");
    }

    private static void check(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }
}
