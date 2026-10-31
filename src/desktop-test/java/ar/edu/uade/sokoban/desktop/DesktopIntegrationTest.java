package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** Exercises the real desktop controller and runtime without opening a window. */
public final class DesktopIntegrationTest {
    private static int passed;
    private DesktopIntegrationTest() { }
    private static final class Probe implements GameView {
        GamePresentation state;
        UiCommands commands = UiCommands.EMPTY;
        LevelResult result;
        String error;
        int renders;
        int victories;
        final List<SoundManager.Effect> effects = new ArrayList<>();
        @Override public void render(GamePresentation value) { state = value; renders++; }
        @Override public void showLoadError(String message) { error = message; }
        @Override public void configureUiCommands(UiCommands value) { commands = value; }
        @Override public void showVictory(LevelResult value) { result = value; victories++; }
        @Override public void playEffect(SoundManager.Effect effect) { effects.add(effect); }
    }
    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]).toAbsolutePath().getParent().getParent().getParent().getParent().getParent();
        Path fixtures = root.resolve("src/test/resources/levels");
        Path resources = root.resolve("src/desktop/resources/levels");
        Probe view = new Probe(); GameController game = new GameController(view);
        game.load(Path.of(args[0]));
        check(view.error == null && view.state.state().player().equals(p(2, 2)), "standard load");
        GamePresentation before = view.state;
        game.move(Direction.UP);
        check(view.state.moves() == 1 && before.state().player().equals(p(2, 2)), "runtime move and immutable projection");
        game.load(resources.resolve("campaign-b.txt"), LevelCatalog.Format.STANDARD);
        check(view.error != null && view.state.moves() == 1, "invalid load atomic");
        view.error = null;
        game.load(fixtures.resolve("extended-runtime.txt"), LevelCatalog.Format.EXTENDED);
        check(view.error == null && view.state.state().boxAt(p(1, 2)).kind() == RuntimeState.BoxKind.KEY
                && view.state.state().boxAt(p(2, 2)).resistance() == 2, "extended load preserves entities");
        game.move(Direction.RIGHT);
        check(view.state.state().lockActive("l1") && view.state.state().wallOpen(p(1, 6))
                && view.effects.contains(SoundManager.Effect.ICE) && view.effects.contains(SoundManager.Effect.LOCK), "key ice lock effects");
        game.move(Direction.RIGHT); game.move(Direction.RIGHT);
        check(!view.state.state().wallOpen(p(1, 6)), "lock deactivation");
        game.move(Direction.LEFT); game.move(Direction.LEFT);
        check(view.commands.undo() != null && view.state.moves() == 5, "undo available at five");
        view.commands.undo().run();
        check(view.state.moves() == 0 && view.state.undoRemaining() == 2
                && view.state.state().boxAt(p(1, 2)).id().equals("k1"), "undo restores dynamic state");
        view.commands.restart().run();
        check(view.state.moves() == 0 && view.state.undoRemaining() == 3 && view.commands.undo() == null, "restart resets history and budget");
        game.move(Direction.DOWN); game.move(Direction.RIGHT); game.move(Direction.RIGHT);
        check(view.state.state().boxAt(p(2, 4)) == null && view.effects.contains(SoundManager.Effect.FRAGILE), "fragile break effect");
        Path sharedWall = Files.createTempFile("sokoban-shared-wall-", ".txt");
        try {
            Files.writeString(sharedWall, "!sokoban-txt 2\n[grid]\n@ {lock:a|key:k1} _ {lock:b|key:k2} {wall:w|empty}\n[attributes]\nwall w locks=a,b\n");
            Probe soundProbe = new Probe(); GameController soundGame = new GameController(soundProbe);
            soundGame.load(sharedWall, LevelCatalog.Format.EXTENDED); soundGame.move(Direction.RIGHT);
            check(soundProbe.effects.contains(SoundManager.Effect.LOCK)
                    && !soundProbe.effects.contains(SoundManager.Effect.GATE), "no gate effect without wall change");
        } finally { Files.deleteIfExists(sharedWall); }
        check(Files.mismatch(resources.resolve("campaign-a.txt"), fixtures.resolve("completion-a.txt")) == -1
                && Files.mismatch(resources.resolve("campaign-b.txt"), fixtures.resolve("completion-b.txt")) == -1, "campaign files match verified fixtures");
        Probe failureView = new Probe(); GameController failureGame = new GameController(failureView);
        failureGame.loadCatalog(new LevelCatalog(List.of(
                new LevelCatalog.Entry("a", resources.resolve("campaign-a.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("b", resources.resolve("missing-next.txt"), LevelCatalog.Format.EXTENDED))));
        failureGame.move(Direction.RIGHT);
        GamePresentation finishedState = failureView.state;
        failureView.commands.nextLevel().run();
        check(failureView.error != null && failureView.state == finishedState
                && failureView.victories == 1 && failureView.commands.nextLevel() != null, "failed next preserves finished state");
        game.loadCatalog(new LevelCatalog(List.of(
                new LevelCatalog.Entry("nivel_1", resources.resolve("campaign-a.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("nivel_2", resources.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED))));
        game.move(Direction.RIGHT);
        check(view.victories == 1 && view.result.score() == 9965 && view.commands.nextLevel() != null
                && view.commands.restart() == null, "victory score and next available");
        game.move(Direction.RIGHT);
        check(view.victories == 1, "victory not duplicated");
        view.commands.nextLevel().run();
        check(view.state.levelName().equals("nivel_2") && view.state.moves() == 0, "advance level");
        for (Direction d : List.of(Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT,
                Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.LEFT)) { game.move(d); }
        check(view.victories == 2 && view.result.score() == 9815 && view.commands.nextLevel() == null,
                "complete campaign and disable next");
        SwingUtilities.invokeAndWait(() -> {
            JPanel target = new JPanel(); List<Direction> seen = new ArrayList<>();
            ArrowBindings.install(target, seen::add);
            for (Direction d : Direction.values()) {
                Object key = target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke(d.name()));
                target.getActionMap().get(key).actionPerformed(new ActionEvent(target, ActionEvent.ACTION_PERFORMED, "test"));
            }
            check(seen.equals(List.of(Direction.values())), "arrow bindings on EDT");
            BoardPanel panel = new BoardPanel(); panel.showState(view.state.state()); panel.setSize(panel.getPreferredSize());
            BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
            var graphics = image.createGraphics(); try { panel.paint(graphics); } finally { graphics.dispose(); }
            check(panel.getWidth() > 0 && panel.getHeight() > 0, "runtime rendered headlessly");
        });
        System.out.println("DESKTOP RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static Position p(int row, int col) { return new Position(row, col); }
    private static void check(boolean value, String label) {
        if (!value) { throw new AssertionError(label); }
        passed++; System.out.println("PASS " + label);
    }
}
