package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Replays the actual shipped files through the same controller used by Swing. */
public final class CampaignAcceptanceTest {
    private static int checks;
    private CampaignAcceptanceTest() { }
    private static final class View implements GameView {
        GamePresentation state;
        LevelResult result;
        UiCommands commands = UiCommands.EMPTY;
        MoveResult.Kind move;
        String error;
        int victories;
        @Override public void render(GamePresentation value) { state = value; }
        @Override public void configureUiCommands(UiCommands value) { commands = value; }
        @Override public void showVictory(LevelResult value) { result = value; victories++; }
        @Override public void onMove(MoveResult.Kind value) { move = value; }
        @Override public void showLoadError(String value) { error = value; }
    }
    public static void main(String[] args) throws Exception {
        Path levels = Path.of(args[0]).resolve("src/desktop/resources/levels");
        LevelCatalog catalog = new LevelCatalog(List.of(
            new LevelCatalog.Entry("nivel_1", levels.resolve("campaign-a.txt"), LevelCatalog.Format.STANDARD),
            new LevelCatalog.Entry("nivel_2", levels.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED),
            new LevelCatalog.Entry("nivel_3", levels.resolve("campaign-c.txt"), LevelCatalog.Format.EXTENDED)));
        List<List<Direction>> solutions = List.of(
            List.of(Direction.RIGHT),
            List.of(Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT,
                Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.LEFT),
            List.of(Direction.RIGHT, Direction.RIGHT));
        long[] pushes = {1, 3, 1}; long[] scores = {9965, 9815, 9955};
        for (int repetition = 0; repetition < 2; repetition++) {
            View v = new View(); GameController controller = new GameController(v); controller.loadCatalog(catalog);
            check(v.error == null, "campaign loaded");
            for (int level = 0; level < solutions.size(); level++) {
                RuntimeState initial = v.state.state();
                check(v.state.moves() == 0 && v.victories == level, "fresh level and no premature victory");
                if (level == 2) {
                    check(initial.boxAt(new Position(1, 3)).resistance() == 2, "fragile initial resistance");
                    check(initial.terrain().get(1).get(4) == RuntimeState.Ground.ICE, "ice in shipped level");
                }
                for (Direction direction : solutions.get(level)) {
                    controller.move(direction);
                    check(v.move != MoveResult.Kind.BLOCKED, "witness move valid");
                }
                check(v.victories == level + 1 && v.result.moves() == solutions.get(level).size()
                    && v.result.pushes() == pushes[level] && v.result.score() == scores[level], "victory metrics and score");
                ReplayTrace trace = controller.replay().orElseThrow();
                ReplayPlayer replay = new ReplayPlayer(trace);
                check(replay.verify(), "replay complete equality");
                while (replay.hasNext()) { replay.step(); }
                check(replay.currentState().equals(v.state.state()) && trace.initialState().equals(initial), "replay isolated");
                if (level == 2) {
                    RuntimeState.Box box = v.state.state().boxAt(new Position(1, 6));
                    check(box != null && box.kind() == RuntimeState.BoxKind.FRAGILE && box.resistance() == 1,
                        "fragile survives sliding onto goal");
                    check(initial.boxAt(new Position(1, 3)).resistance() == 2, "initial state remains immutable");
                }
                controller.move(Direction.RIGHT);
                check(v.victories == level + 1, "finalization idempotent");
                if (level < 2) { check(v.commands.nextLevel() != null, "next enabled"); v.commands.nextLevel().run(); }
                else { check(v.commands.nextLevel() == null, "campaign complete"); }
            }
            controller.close();
        }
        // Undo and restart on the shipped ice level leave replay and accumulated score consistent.
        View retry = new View(); GameController retried = new GameController(retry);
        retried.load(levels.resolve("campaign-c.txt"), LevelCatalog.Format.EXTENDED);
        RuntimeState initial = retry.state.state();
        for (Direction d : List.of(Direction.RIGHT, Direction.LEFT, Direction.RIGHT, Direction.LEFT, Direction.RIGHT)) {
            retried.move(d);
        }
        retried.undo();
        check(retry.state.state().equals(initial) && retry.state.undoRemaining() == 2, "undo on campaign restores full world");
        retried.move(Direction.RIGHT); retried.restart();
        check(retry.state.state().equals(initial) && retry.state.undoRemaining() == 3, "restart on campaign restores full world");
        retried.move(Direction.RIGHT); retried.move(Direction.RIGHT);
        check(retry.result.score() == 9855 && retried.replay().orElseThrow().commands().size() == 2
                && new ReplayPlayer(retried.replay().orElseThrow()).verify(), "replay and undo penalty after restart");
        retried.close();
        // Failed external loads must not replace either an active or finalized session.
        Path temp = Files.createTempDirectory("sokoban qa spaces ");
        try {
            Path invalid = temp.resolve("invalid utf8.txt"); Files.write(invalid, new byte[] {(byte) 0xc3, 0x28});
            View v = new View(); GameController c = new GameController(v); c.load(levels.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED);
            c.move(Direction.RIGHT); GamePresentation active = v.state;
            c.load(invalid, LevelCatalog.Format.EXTENDED);
            check(v.error != null && v.state == active, "invalid UTF-8 preserves active session");
            v.error = null; c.load(temp.resolve("missing.txt"), LevelCatalog.Format.STANDARD);
            check(v.error != null && v.state == active, "missing file preserves active session");
            Path withSpaces = temp.resolve("valid level.txt"); Files.copy(levels.resolve("campaign-a.txt"), withSpaces);
            v.error = null; c.load(withSpaces, LevelCatalog.Format.STANDARD); c.move(Direction.RIGHT);
            check(v.error == null && v.result.score() == 9965, "path with spaces and victory");
            GamePresentation finished = v.state; LevelResult result = v.result;
            c.load(invalid, LevelCatalog.Format.EXTENDED);
            check(v.error != null && v.state == finished && v.result.equals(result), "failed load preserves final result");
            c.close();
        } finally {
            try (var paths = Files.walk(temp)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) { Files.delete(path); }
            }
        }
        // Fixed-size stress checks: no unbounded solver, file, movement or rendering loops.
        Path large = Files.createTempFile("sokoban-large-", ".txt");
        try {
            StringBuilder txt = new StringBuilder("!sokoban-txt 2\n[grid]\n");
            for (int row = 0; row < 100; row++) {
                for (int col = 0; col < 100; col++) {
                    if (col > 0) { txt.append(' '); }
                    int index = row * 100 + col;
                    txt.append(index == 0 ? "@" : index <= 3700 ? "{floor|empty}" : "_");
                }
                txt.append('\n');
            }
            txt.append("[attributes]\n"); Files.writeString(large, txt);
            check(Files.size(large) > 60000 && Files.size(large) <= 65536, "near-limit file size");
            RuntimeState largeState = RuntimeState.fromExtended(new ar.edu.uade.sokoban.level.LevelLoader(new ar.edu.uade.sokoban.level.LevelParser()).loadExtended(large));
            check(largeState.terrain().size() == 100 && largeState.terrain().get(0).size() == 100, "large grid loaded");
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                BoardPanel panel = new BoardPanel(); panel.showState(largeState); panel.setSize(panel.getPreferredSize());
                java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(256, 256, java.awt.image.BufferedImage.TYPE_INT_RGB);
                var graphics = image.createGraphics();
                try { panel.paint(graphics); } finally { graphics.dispose(); }
                for (int i = 0; i < 30; i++) { panel.setTheme(VisualThemes.all().get(i % 3)); }
                check(panel.getPreferredSize().width > 256, "large render and repeated themes");
            });
        } finally { Files.delete(large); }
        RuntimeState start = RuntimeState.fromExtended(new ar.edu.uade.sokoban.level.LevelLoader(new ar.edu.uade.sokoban.level.LevelParser()).loadExtended(levels.resolve("campaign-c.txt")));
        var engine = new ar.edu.uade.sokoban.movement.RuntimeMovementEngine();
        ReplayRecorder recorder = new ReplayRecorder(start, "bounded"); RuntimeState current = start;
        for (int i = 0; i < 1000; i++) {
            Direction direction = i % 2 == 0 ? Direction.RIGHT : Direction.LEFT;
            var moved = engine.move(current, direction);
            if (moved.kind() == MoveResult.Kind.BLOCKED) { throw new AssertionError("bounded move blocked"); }
            current = moved.state(); recorder.record(direction);
        }
        check(current.equals(start) && recorder.verified(current).isPresent(), "1000 moves and replay");
        try (SoundManager sound = new SoundManager()) {
            sound.setEnabled(false); sound.play(SoundManager.Effect.LOCK);
            check(!sound.isEnabled(), "muted audio dispatch");
            sound.setEnabled(true); check(sound.isEnabled(), "audio reenabled");
        }
        System.out.println("CAMPAIGN ACCEPTANCE RESULT: " + checks + " PASS, 0 FAIL");
    }
    private static void check(boolean condition, String description) {
        if (!condition) { throw new AssertionError(description); }
        checks++;
    }
}
