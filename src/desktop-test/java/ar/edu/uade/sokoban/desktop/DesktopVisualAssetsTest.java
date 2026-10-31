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
        Path root = demo.toAbsolutePath().getParent().getParent().getParent().getParent().getParent();
        var fixture = new LevelLoader(new LevelParser()).loadExtended(root.resolve("src/test/resources/levels/extended-runtime.txt"));
        var state = ar.edu.uade.sokoban.movement.RuntimeState.fromExtended(fixture);
        var ice = new ar.edu.uade.sokoban.level.Position(1, 3);
        var lock = new ar.edu.uade.sokoban.level.Position(1, 4);
        var wall = new ar.edu.uade.sokoban.level.Position(1, 6);
        check(BoardPanel.terrainSprite(state, ice).equals("ice"), "ice selection"); total++;
        check(BoardPanel.terrainSprite(state, lock).equals("lock"), "inactive lock"); total++;
        check(BoardPanel.terrainSprite(state, wall).equals("gate_closed"), "closed gate"); total++;
        check(BoardPanel.boxSprite(state, new ar.edu.uade.sokoban.level.Position(1, 2)).equals("box_key"), "key sprite"); total++;
        check(BoardPanel.boxSprite(state, new ar.edu.uade.sokoban.level.Position(2, 2)).equals("box_fragile_2"), "fragile sprite"); total++;
        var active = new ar.edu.uade.sokoban.movement.RuntimeMovementEngine().move(state,
                ar.edu.uade.sokoban.movement.Direction.RIGHT).state();
        check(BoardPanel.terrainSprite(active, lock).equals("lock_active"), "active lock"); total++;
        check(BoardPanel.terrainSprite(active, wall).equals("gate_open"), "open gate"); total++;
        check(BoardPanel.terrainSprite(state, new ar.edu.uade.sokoban.level.Position(2, 3)).equals("goal"), "free goal"); total++;
        var parser = new ar.edu.uade.sokoban.level.ExtendedLevelParser();
        var sample = ar.edu.uade.sokoban.movement.RuntimeState.fromExtended(parser.parse(
                "!sokoban-txt 2\n[grid]\n@ {goal|box} {floor|fragile:f}\n[attributes]\nfragile f resistance=12\n"));
        check(BoardPanel.terrainSprite(sample, new ar.edu.uade.sokoban.level.Position(0, 1)).equals("goal_active")
                && BoardPanel.boxSprite(sample, new ar.edu.uade.sokoban.level.Position(0, 1)).equals("box_goal"), "occupied goal"); total++;
        check(BoardPanel.boxSprite(sample, new ar.edu.uade.sokoban.level.Position(0, 2)).equals("box_fragile_3"), "fragile high resistance clamped"); total++;
        var low = ar.edu.uade.sokoban.movement.RuntimeState.fromExtended(parser.parse(
                "!sokoban-txt 2\n[grid]\n@ {floor|fragile:f}\n[attributes]\nfragile f resistance=1\n"));
        check(BoardPanel.boxSprite(low, new ar.edu.uade.sokoban.level.Position(0, 1)).equals("box_fragile_1"), "fragile low resistance"); total++;
        System.out.println("VISUAL ASSETS RESULT: " + total + " PASS, 0 FAIL");
    }

    private static void check(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }
}
