package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * Headless acceptance for the visual theme selector: three deterministic
 * families, reversible original output, untouched asset files, no gameplay
 * mutation and a real EDT selection that changes board and controls.
 */
public final class VisualThemeTest {
    private static final String[] SPRITES = {
        "floor_0", "floor_1", "floor_2", "wall_0", "wall_1", "wall_2",
        "goal", "goal_active", "box", "box_goal", "box_key",
        "box_fragile_3", "box_fragile_2", "box_fragile_1", "box_fragile_0",
        "box_broken", "ice", "lock", "lock_active", "gate_closed", "gate_open",
        "player_down", "player_up", "player_left", "player_right",
        "player_down_step", "player_up_step", "player_left_step", "player_right_step"
    };
    private static final String[] LEVELS = {
        "ice_demo.txt", "fragile_demo.txt", "campaign-b.txt"
    };

    private static int passed;
    private static int failed;
    private static Path spritesDirectory;
    private static Path levelsDirectory;

    private VisualThemeTest() { }

    @FunctionalInterface
    private interface Check { void run() throws Exception; }

    private static final class Probe implements GameView {
        private GamePresentation state;
        private String error;
        @Override
        public void render(GamePresentation presentation) { state = presentation; }
        @Override
        public void showLoadError(String message) { error = message; }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Se esperaban sprites y niveles");
        }
        spritesDirectory = Path.of(args[0]);
        levelsDirectory = Path.of(args[1]);
        Map<String, String> before = spriteHashes();

        test("tres familias visuales distintas y estables", VisualThemeTest::families);
        test("sprites alternativos deterministas y con alfa intacto",
                VisualThemeTest::alternativeSprites);
        test("tema original identico al PNG y reversible", VisualThemeTest::originalReversible);
        test("tablero ICE Fragile y Lock conserva mundo metricas y zoom",
                VisualThemeTest::boardFamilies);
        test("seleccion Swing en EDT cambia tablero y controles", VisualThemeTest::edtSelection);
        test("entrada nula no deja estado a medias", VisualThemeTest::nullSafety);

        Map<String, String> after = spriteHashes();
        test("archivos de sprites intactos en disco", () -> {
            check(!before.isEmpty(), "no se encontraron PNG");
            check(before.equals(after), "hash de sprites cambiado");
            check(before.size() == after.size(), "cantidad de sprites cambiada");
        });

        System.out.println("THEME RESULT: " + passed + " PASS, " + failed + " FAIL");
        if (failed != 0) {
            System.exit(1);
        }
    }

    private static void families() {
        List<VisualThemeFactory> themes = VisualThemes.all();
        check(themes.size() == 3, "se esperaban tres temas");
        Set<String> ids = new TreeSet<>();
        Set<String> names = new TreeSet<>();
        Set<Integer> backgrounds = new TreeSet<>();
        for (VisualThemeFactory theme : themes) {
            ids.add(theme.id());
            names.add(theme.displayName());
            backgrounds.add(theme.palette().background().getRGB());
            check(theme.palette().accent().getRGB() != theme.palette().background().getRGB(),
                    "paleta sin contraste " + theme.id());
        }
        check(ids.size() == 3, "ids duplicados");
        check(names.size() == 3, "nombres duplicados");
        check(backgrounds.size() == 3, "fondos no diferenciables");
        check(VisualThemes.original().id().equals("original_cobre"), "tema original distinto");
        check(VisualThemes.byId("nocturno") == themes.get(1), "busqueda por id");
        check(VisualThemes.all().equals(VisualThemes.all()), "catalogo no estable");
    }

    private static void alternativeSprites() {
        PixelAssets source = new PixelAssets();
        for (VisualThemeFactory theme : alternatives()) {
            PixelAssets first = new PixelAssets();
            first.setTheme(theme);
            PixelAssets second = new PixelAssets();
            second.setTheme(theme);
            for (String name : SPRITES) {
                BufferedImage original = source.image(name);
                BufferedImage mine = first.image(name);
                check(mine.getWidth() == 32 && mine.getHeight() == 32, "tamano " + name);
                check(alphaEquals(original, mine), "alfa alterado " + theme.id() + " " + name);
                check(!samePixels(original, mine), "sprite sin cambio " + theme.id() + " " + name);
                BufferedImage twin = second.image(name);
                check(samePixels(mine, twin), "sprite no determinista " + theme.id() + " " + name);
            }
        }
    }

    private static void originalReversible() throws Exception {
        PixelAssets assets = new PixelAssets();
        for (String name : SPRITES) {
            BufferedImage stored = ImageIO.read(spritesDirectory.resolve(name + ".png").toFile());
            check(stored != null, "PNG ilegible " + name);
            check(samePixels(stored, assets.image(name)), "original no coincide " + name);
        }
        BufferedImage box = assets.image("box");
        for (VisualThemeFactory theme : alternatives()) {
            assets.setTheme(theme);
            check(!samePixels(box, assets.image("box")), "cache no invalidada " + theme.id());
            assets.setTheme(VisualThemes.original());
            check(samePixels(box, assets.image("box")), "original no restaurado " + theme.id());
        }
    }

    private static void boardFamilies() throws Exception {
        LevelLoader loader = new LevelLoader(new LevelParser());
        for (String level : LEVELS) {
            Probe view = new Probe();
            GameController controller = new GameController(view);
            controller.load(levelsDirectory.resolve(level));
            check(view.error == null && view.state != null, "carga fallida " + level);
            GamePresentation presentation = view.state;
            RuntimeState world = presentation.state();
            long moves = presentation.moves();
            long pushes = presentation.pushes();

            final BufferedImage[] rendered = new BufferedImage[3];
            onEdt(() -> {
                BoardPanel panel = new BoardPanel();
                panel.showState(world);
                panel.setScale(3);
                int width = panel.getPreferredSize().width;
                int height = panel.getPreferredSize().height;
                rendered[0] = render(panel);
                panel.setTheme(VisualThemes.byId(VisualThemes.NIGHT_ID));
                check(panel.getScale() == 3, "zoom alterado " + level);
                check(panel.getPreferredSize().width == width
                        && panel.getPreferredSize().height == height, "tamano alterado " + level);
                check(panel.getBackground().equals(
                        VisualThemes.byId(VisualThemes.NIGHT_ID).palette().boardBackground()),
                        "fondo de tablero no aplicado " + level);
                rendered[1] = render(panel);
                panel.setTheme(VisualThemes.original());
                rendered[2] = render(panel);
            });
            check(!samePixels(rendered[0], rendered[1]), "tablero no cambio " + level);
            check(samePixels(rendered[0], rendered[2]), "tablero original inestable " + level);
            check(view.state == presentation && view.state.state() == world,
                    "mundo reemplazado " + level);
            check(view.state.moves() == moves && view.state.pushes() == pushes,
                    "metricas alteradas " + level);
            check(controller.undoUsesTotal() == 0 && controller.consecutiveUndoUses() == 0,
                    "historial alterado " + level);
        }
        Probe view = new Probe();
        GameController controller = new GameController(view);
        controller.load(levelsDirectory.resolve("demo.txt"));
        for (int step = 0; step < 5; step++) {
            controller.move(Direction.UP);
            controller.move(Direction.DOWN);
        }
        long moves = view.state.moves();
        check(moves > 0, "sin movimientos previos");
        onEdt(() -> new BoardPanel().setTheme(VisualThemes.byId(VisualThemes.JADE_ID)));
        check(view.state.moves() == moves, "cambio de tema movio el contador");
        check(controller.consecutiveUndoUses() == 0, "cambio de tema consumio Undo");
    }

    private static void edtSelection() throws Exception {
        Board demo = new LevelLoader(new LevelParser())
                .load(levelsDirectory.resolve("demo.txt")).board();
        RuntimeState world = RuntimeState.fromLevel(new Level(demo));
        onEdt(() -> {
            JPanel root = new JPanel();
            JButton button = PixelTheme.button("PRUEBA");
            JLabel label = new JLabel("TEXTO");
            label.setForeground(PixelTheme.CREAM);
            PixelTheme.Surface surface = (PixelTheme.Surface) PixelTheme.surface();
            BoardPanel panel = new BoardPanel();
            panel.showState(world);
            root.add(surface);
            root.add(button);
            root.add(label);
            root.add(panel);

            VisualThemeController themes =
                    new VisualThemeController(root, panel, new PixelAssets());
            JComboBox<VisualThemeFactory> selector =
                    new JComboBox<>(VisualThemes.all().toArray(VisualThemeFactory[]::new));
            selector.addActionListener(event ->
                    themes.select((VisualThemeFactory) selector.getSelectedItem()));

            BufferedImage boardBefore = render(panel);
            Color buttonBackground = button.getBackground();
            Color buttonForeground = button.getForeground();
            Border buttonBorder = button.getBorder();
            Color labelForeground = label.getForeground();

            // An empty combo selection must not start a half-applied palette.
            selector.setSelectedItem(null);
            check(themes.current() == VisualThemes.original(), "nulo cambio tema activo");
            check(button.getBackground().equals(buttonBackground), "nulo cambio fondo");
            check(samePixels(boardBefore, render(panel)), "nulo cambio el tablero");

            VisualThemeFactory night = VisualThemes.byId(VisualThemes.NIGHT_ID);
            selector.setSelectedItem(night);
            check(selector.getSelectedItem() == night, "seleccion no aplicada");
            check(themes.current() == night, "tema activo no actualizado");
            check(button.getBackground().equals(night.palette().surfaceLight()),
                    "boton sin paleta alternativa");
            check(button.getForeground().equals(night.palette().text()), "texto de boton");
            check(label.getForeground().equals(night.palette().text()), "texto de etiqueta");
            check(surface.getBackground().equals(night.palette().background()),
                    "superficie sin paleta alternativa");
            check(!samePixels(boardBefore, render(panel)), "tablero no cambio por seleccion");

            VisualThemeFactory original = VisualThemes.original();
            selector.setSelectedItem(original);
            check(button.getBackground().equals(buttonBackground), "fondo original no restaurado");
            check(button.getForeground().equals(buttonForeground), "texto original no restaurado");
            check(button.getBorder() == buttonBorder, "borde original no restaurado");
            check(label.getForeground().equals(labelForeground), "etiqueta original no restaurada");
            check(surface.getBackground().equals(PixelTheme.BACKGROUND),
                    "superficie original no restaurada");
            BufferedImage boardAfter = render(panel);
            check(samePixels(boardBefore, boardAfter),
                    "tablero original inestable por selector " + difference(boardBefore, boardAfter));
        });
    }

    private static void nullSafety() throws Exception {
        RuntimeState world = RuntimeState.fromLevel(new Level(
                new LevelLoader(new LevelParser()).load(levelsDirectory.resolve("demo.txt")).board()));
        final BufferedImage[] rendered = new BufferedImage[2];
        onEdt(() -> {
            JPanel root = new JPanel();
            JButton button = PixelTheme.button("PRUEBA");
            JLabel label = new JLabel("TEXTO");
            label.setForeground(PixelTheme.CREAM);
            BoardPanel panel = new BoardPanel();
            panel.showState(world);
            panel.setScale(2);
            root.add(button);
            root.add(label);
            root.add(panel);
            VisualThemeController themes =
                    new VisualThemeController(root, panel, new PixelAssets());

            themes.select(VisualThemes.byId(VisualThemes.NIGHT_ID));
            Color nightBackground = button.getBackground();
            Color nightForeground = button.getForeground();
            Border nightBorder = button.getBorder();
            rendered[0] = render(panel);

            check(!themes.select(null), "nulo fue aplicado");
            check(themes.current().id().equals(VisualThemes.NIGHT_ID), "nulo cambio tema activo");
            check(button.getBackground().equals(nightBackground), "nulo cambio fondo");
            check(button.getForeground().equals(nightForeground), "nulo cambio texto");
            check(button.getBorder() == nightBorder, "nulo cambio borde");
            check(samePixels(rendered[0], render(panel)), "nulo altero el tablero");

            themes.select(VisualThemes.original());
            check(button.getBackground().equals(PixelTheme.SURFACE_LIGHT),
                    "original no restaurado tras nulo");
            rendered[1] = render(panel);

            expectNullPointer(() -> panel.setTheme(null));
            check(panel.getScale() == 2, "nulo cambio zoom");
            check(panel.getBackground().equals(
                    VisualThemes.original().palette().boardBackground()), "nulo cambio fondo");
            check(samePixels(rendered[1], render(panel)), "nulo altero el tablero");
        });

        PixelAssets assets = new PixelAssets();
        BufferedImage before = assets.image("box");
        expectNullPointer(() -> assets.setTheme(null));
        check(samePixels(before, assets.image("box")), "tema nulo invalido cache");

        boolean rejected = false;
        try {
            VisualThemes.byId("inexistente");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "id invalido aceptado");
    }

    private static List<VisualThemeFactory> alternatives() {
        List<VisualThemeFactory> result = new ArrayList<>();
        for (VisualThemeFactory theme : VisualThemes.all()) {
            if (!VisualThemes.ORIGINAL_ID.equals(theme.id())) {
                result.add(theme);
            }
        }
        check(result.size() == 2, "se esperaban dos alternativas");
        return result;
    }

    private static BufferedImage render(Component component) {
        component.setSize(component.getPreferredSize());
        BufferedImage image = new BufferedImage(Math.max(1, component.getWidth()),
                Math.max(1, component.getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            component.paint(graphics);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    private static String difference(BufferedImage first, BufferedImage second) {
        int[] left = pixels(first);
        int[] right = pixels(second);
        int count = 0;
        int firstIndex = -1;
        for (int index = 0; index < Math.min(left.length, right.length); index++) {
            if (left[index] != right[index]) {
                if (firstIndex < 0) {
                    firstIndex = index;
                }
                count++;
            }
        }
        String detail = "diff=" + count + "/" + left.length;
        if (firstIndex >= 0) {
            detail += " at(" + (firstIndex % first.getWidth()) + "," + (firstIndex / first.getWidth())
                    + ") " + Integer.toHexString(left[firstIndex]) + "->"
                    + Integer.toHexString(right[firstIndex]);
        }
        return detail;
    }

    private static boolean samePixels(BufferedImage first, BufferedImage second) {
        return first.getWidth() == second.getWidth()
                && first.getHeight() == second.getHeight()
                && Arrays.equals(pixels(first), pixels(second));
    }

    private static boolean alphaEquals(BufferedImage first, BufferedImage second) {
        if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) {
            return false;
        }
        int[] left = pixels(first);
        int[] right = pixels(second);
        for (int index = 0; index < left.length; index++) {
            if ((left[index] >>> 24) != (right[index] >>> 24)) {
                return false;
            }
        }
        return true;
    }

    private static int[] pixels(BufferedImage image) {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    private static Map<String, String> spriteHashes() throws Exception {
        Map<String, String> result = new TreeMap<>();
        try (var files = Files.list(spritesDirectory)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".png")).toList()) {
                result.put(file.getFileName().toString(), sha256(file));
            }
        }
        return result;
    }

    private static String sha256(Path file) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file));
        StringBuilder text = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            text.append(Character.forDigit((value >> 4) & 0xF, 16));
            text.append(Character.forDigit(value & 0xF, 16));
        }
        return text.toString();
    }

    private static void onEdt(Check action) throws Exception {
        Throwable[] failure = new Throwable[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                action.run();
            } catch (Throwable problem) {
                failure[0] = problem;
            }
        });
        if (failure[0] instanceof Exception exception) {
            throw exception;
        }
        if (failure[0] instanceof Error error) {
            throw error;
        }
        if (failure[0] != null) {
            throw new AssertionError(failure[0]);
        }
    }

    private static void expectNullPointer(Runnable action) {
        try {
            action.run();
            throw new AssertionError("se esperaba NullPointerException");
        } catch (NullPointerException expected) {
            // Expected rejection; the caller checks the preserved state.
        }
    }

    private static void check(boolean condition, String problem) {
        if (!condition) {
            throw new AssertionError(problem);
        }
    }

    private static void test(String name, Check check) {
        try {
            check.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Exception | AssertionError failure) {
            failed++;
            System.err.println("FAIL " + name + ": " + failure);
        }
    }
}
