package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.level.Terrain;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MovementEngine;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** Integration checks without a display or a second movement implementation. */
public final class DesktopIntegrationTest {
    private static int passed;
    private static int failed;
    private static Path directory;
    private static final String OPEN = "#####\n#   #\n# @ #\n#   #\n#####\n";

    private DesktopIntegrationTest() { }

    @FunctionalInterface
    private interface Check { void run() throws Exception; }

    private static final class Probe implements GameView {
        private GamePresentation state;
        private int renders;
        private String error;
        @Override
        public void render(GamePresentation value) {
            state = value;
            renders++;
        }
        @Override
        public void showLoadError(String value) { error = value; }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected the demo TXT path");
        }
        directory = Files.createTempDirectory("sokoban-desktop-tests-");
        try {
            test("load versioned demo TXT", () -> {
                Probe view = new Probe();
                controller(view).load(Path.of(args[0]));
                check(view.error == null && view.renders == 1, "load failed");
                check(view.state.levelName().equals("demo.txt"), "level label");
                check(view.state.board().playerPosition().equals(new Position(2, 2)), "player");
                check(view.state.board().boxCount() == 1 && view.state.board().goalCount() == 1, "entities");
                check(view.state.board().rows().get(2).get(6).terrain() == Terrain.GOAL, "goal");
            });
            test("external TXT reload reads changed bytes", () -> {
                Probe view = new Probe();
                GameController game = controller(view);
                Path file = write("nivel externo ñ.txt", OPEN);
                game.load(file);
                check(view.state.board().playerPosition().equals(new Position(2, 2)), "first read");
                Files.writeString(file, "#####\n#@  #\n#   #\n#   #\n#####\n", StandardCharsets.UTF_8);
                game.load(file);
                check(view.state.board().playerPosition().equals(new Position(1, 1)), "stale cache");
                check(view.state.levelName().equals(file.getFileName().toString()), "external label");
            });
            for (Direction direction : Direction.values()) {
                test("MOVED " + direction, () -> {
                    Probe view = new Probe();
                    GameController game = loaded(view, OPEN);
                    Board before = view.state.board();
                    game.move(direction);
                    Position expected = switch (direction) {
                        case UP -> new Position(1, 2);
                        case DOWN -> new Position(3, 2);
                        case LEFT -> new Position(2, 1);
                        case RIGHT -> new Position(2, 3);
                    };
                    check(view.state.board().playerPosition().equals(expected), "wrong position");
                    check(view.state.board() != before && view.state.moves() == 1
                            && view.state.pushes() == 0 && view.renders == 2, "MOVED publication");
                    check(before.playerPosition().equals(new Position(2, 2)), "mutated old board");
                });
            }
            test("PUSHED onto floor", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, "######\n#@$  #\n######\n");
                game.move(Direction.RIGHT);
                check(view.state.board().playerPosition().equals(new Position(1, 2)), "player");
                check(view.state.board().rows().get(1).get(3).occupant() == Occupant.BOX, "box");
                check(view.state.moves() == 1 && view.state.pushes() == 1, "push counters");
            });
            test("PUSHED onto goal preserves terrain", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, "#####\n#@$.#\n#####\n");
                game.move(Direction.RIGHT);
                var target = view.state.board().rows().get(1).get(3);
                check(target.terrain() == Terrain.GOAL && target.occupant() == Occupant.BOX, "goal overlay");
                check(view.state.moves() == 1 && view.state.pushes() == 1, "counters");
            });
            test("BLOCKED wall preserves state counters and view", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, "#####\n#@  #\n#####\n");
                GamePresentation before = view.state;
                game.move(Direction.UP);
                check(view.state == before && view.renders == 1, "blocked publication");
                check(view.state.moves() == 0 && view.state.pushes() == 0, "blocked counter");
            });
            test("BLOCKED two boxes preserves model", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, "######\n#@$$ #\n######\n");
                GamePresentation before = view.state;
                game.move(Direction.RIGHT);
                check(view.state == before && view.renders == 1, "box chain moved");
            });
            test("actual Swing key bindings route four directions on EDT", () -> {
                SwingUtilities.invokeAndWait(() -> {
                    JPanel target = new JPanel();
                    List<Direction> seen = new ArrayList<>();
                    ArrowBindings.install(target, seen::add);
                    for (Direction direction : Direction.values()) {
                        Object key = target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                                .get(KeyStroke.getKeyStroke(direction.name()));
                        check(key != null, "missing key");
                        var action = target.getActionMap().get(key);
                        check(action != null, "missing action");
                        action.actionPerformed(new ActionEvent(target, ActionEvent.ACTION_PERFORMED, "test"));
                    }
                    check(seen.equals(List.of(Direction.values())), "wrong directional mapping");
                });
            });
            test("view projection matches immutable core transition", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, OPEN);
                Board before = view.state.board();
                Board expected = new MovementEngine().move(before, Direction.RIGHT).board();
                game.move(Direction.RIGHT);
                check(view.state.board().equals(expected), "projection differs from core");
                try {
                    view.state.board().rows().get(0).clear();
                    throw new AssertionError("mutable board exposed");
                } catch (UnsupportedOperationException expectedException) {
                    check(before.playerPosition().equals(new Position(2, 2)), "old board changed");
                }
            });
            test("demo counters reset only on successful load", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, "########\n#@ $ . #\n########\n");
                game.move(Direction.RIGHT);
                game.move(Direction.RIGHT);
                game.move(Direction.UP);
                check(view.state.moves() == 2 && view.state.pushes() == 1, "sequence counters");
                game.load(write("another.txt", OPEN));
                check(view.state.moves() == 0 && view.state.pushes() == 0, "reset");
            });
            test("missing initial file is controlled; arrows have no state", () -> {
                Probe view = new Probe();
                GameController game = controller(view);
                game.load(directory.resolve("missing.txt"));
                game.move(Direction.RIGHT);
                check(view.error != null && view.state == null && view.renders == 0, "failed initial load");
            });
            test("unsupported entity rejects load and preserves previous level", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, OPEN);
                GamePresentation before = view.state;
                game.load(write("unsupported.txt", "#####\n#@~ #\n#####\n"));
                check(view.error != null && view.state == before && view.renders == 1, "partial load published");
            });
            test("Swing panel paints current projection headlessly on EDT", () -> {
                Board value = new LevelLoader(new LevelParser()).load(Path.of(args[0])).board();
                SwingUtilities.invokeAndWait(() -> {
                    BoardPanel panel = new BoardPanel();
                    panel.showBoard(value);
                    panel.setSize(panel.getPreferredSize());
                    BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    var graphics = image.createGraphics();
                    try { panel.paint(graphics); } finally { graphics.dispose(); }
                    check(panel.getWidth() > 0 && panel.getHeight() > 0, "invalid drawing extent");
                });
            
            test("UNDO restores state from five moves ago", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, OPEN);

                game.move(Direction.UP);
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.RIGHT);
                game.move(Direction.UP);

                check(view.state.moves() == 5, "expected five moves");

                game.undo();

                check(view.state.moves() == 0, "moves not restored");
                check(view.state.pushes() == 0, "pushes not restored");
                check(view.state.board().playerPosition().equals(new Position(2, 2)),
                        "player position not restored");
            });
            test("UNDO restores pushes counter", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, "######\n#@$  #\n#    #\n######\n");

                game.move(Direction.RIGHT); // Empuja la caja.
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.UP);
                game.move(Direction.RIGHT);

                check(view.state.moves() == 5, "expected five moves");
                check(view.state.pushes() == 1, "expected one push");

                game.undo();

                check(view.state.moves() == 0, "moves not restored");
                check(view.state.pushes() == 0, "pushes not restored");
                check(view.state.board().playerPosition().equals(new Position(1, 1)),
                        "player position not restored");
            });
            test("UNDO can be used only three times", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, OPEN);

                // Primer bloque de cinco movimientos.
                game.move(Direction.UP);
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.RIGHT);
                game.move(Direction.UP);
                game.undo();

                // Segundo bloque de cinco movimientos.
                game.move(Direction.UP);
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.RIGHT);
                game.move(Direction.UP);
                game.undo();

                // Tercer bloque de cinco movimientos.
                game.move(Direction.UP);
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.RIGHT);
                game.move(Direction.UP);
                game.undo();

                check(game.undoUsesTotal() == 3, "expected three undo uses");
                check(view.state.moves() == 0, "third undo did not restore initial state");

                // Hacemos otros cinco movimientos normales.
                game.move(Direction.UP);
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.RIGHT);
                game.move(Direction.UP);

                // El cuarto Undo no debería funcionar.
                game.undo();

                check(game.undoUsesTotal() == 3, "undo count should remain three");
                check(view.state.moves() == 5, "fourth undo should not restore state");
            });
            test("RESTART restores initial state and resets counters", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, "######\n#@$  #\n#    #\n######\n");

                Board initialBoard = view.state.board();

                // Hacemos cinco movimientos, incluyendo un empuje.
                game.move(Direction.RIGHT);
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.UP);
                game.move(Direction.RIGHT);

                check(view.state.moves() == 5, "expected five moves");
                check(view.state.pushes() == 1, "expected one push");

                // Reiniciamos el nivel.
                game.restart();

                check(view.state.board().equals(initialBoard), "board not restored");
                check(view.state.moves() == 0, "moves not reset");
                check(view.state.pushes() == 0, "pushes not reset");
                check(game.undoUsesTotal() == 0, "undo total not reset");
                check(game.consecutiveUndoUses() == 0, "consecutive undo uses not reset");
            });
            test("UNDO is unavailable with fewer than five moves", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, OPEN);

                game.move(Direction.UP);
                game.move(Direction.DOWN);
                game.move(Direction.LEFT);
                game.move(Direction.RIGHT);

                game.undo();

                check(view.state.moves() == 4, "undo should not have been executed");
                check(game.undoUsesTotal() == 0, "undo count should remain zero");
            });
            test("BLOCKED movement is not added to undo history", () -> {
                Probe view = new Probe();
                GameController game = loaded(view, OPEN);

                game.move(Direction.UP);
                game.move(Direction.LEFT);
                game.move(Direction.UP); // Bloqueado contra la pared.
                game.move(Direction.DOWN);
                game.move(Direction.RIGHT);
                game.move(Direction.UP);

                check(view.state.moves() == 5, "blocked movement counted as a move");

                game.undo();

                check(view.state.moves() == 0, "undo did not restore initial state");
                check(view.state.board().playerPosition().equals(new Position(2, 2)),
                        "player position not restored");
            });
            });
        } finally {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
        System.out.println("DESKTOP RESULT: " + passed + " PASS, " + failed + " FAIL");
        if (failed != 0) { System.exit(1); }
    }

    private static GameController controller(Probe view) {
        return new GameController(new LevelLoader(new LevelParser()), new MovementEngine(), view);
    }

    private static GameController loaded(Probe view, String text) throws Exception {
        GameController game = controller(view);
        game.load(write("input.txt", text));
        check(view.error == null && view.state != null, "setup load failed");
        return game;
    }

    private static Path write(String name, String text) throws Exception {
        Path path = directory.resolve(name);
        Files.writeString(path, text, StandardCharsets.UTF_8);
        return path;
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
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
