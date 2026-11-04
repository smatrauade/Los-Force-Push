package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.replay.ReplayPlayer;
import ar.edu.uade.sokoban.replay.ReplayTrace;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.nio.file.Path;
import java.util.List;

/** End-to-end recorder contract using the real controller and shipped levels. */
public final class ReplayControllerTest {
    private static int passed;
    private ReplayControllerTest() { }
    private static final class Probe implements GameView {
        GamePresentation screen;
        ReplayTrace replay;
        LevelResult victory;
        String error;
        @Override public void render(GamePresentation value) { screen = value; }
        @Override public void configureReplay(ReplayTrace value) { replay = value; }
        @Override public void showVictory(LevelResult value) { victory = value; }
        @Override public void showLoadError(String value) { error = value; }
    }
    public static void main(String[] args) {
        Path levels = Path.of(args[0]);
        successfulOnly(levels);
        undoBranch(levels);
        restart(levels);
        loadsAndAdvance(levels);
        for (String name : List.of("ice_demo.txt", "fragile_demo.txt", "campaign-b.txt")) {
            special(levels, name);
        }
        overflow(levels);
        System.out.println("REPLAY CONTROLLER RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static void successfulOnly(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        require(game.completedReplay().isEmpty(), "no level must have no replay");
        game.load(levels.resolve("demo.txt"));
        move(game, Direction.UP, Direction.UP); // second UP hits the boundary
        require(view.replay == null, "unfinished attempt exposed");
        move(game, Direction.DOWN, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        ReplayTrace trace = view.replay;
        require(trace != null && trace.commands().size() == 5 && trace.expectedPushes() == 2,
                "blocked command recorded or valid command lost");
        LevelResult result = game.getLevelResult();
        GamePresentation stable = view.screen;
        ReplayPlayer replay = new ReplayPlayer(trace);
        require(replay.verify(), "completion verification failed");
        while (replay.hasNext()) { replay.step(); }
        replay.reset();
        move(game, Direction.LEFT);
        game.undo();
        game.restart();
        require(view.screen == stable && game.getLevelResult().equals(result)
                && game.results().size() == 1 && view.replay == trace,
                "playback or frozen actions modified the real game");
        passed++;
    }
    private static void undoBranch(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levels.resolve("demo.txt"));
        game.undo(); // unavailable Undo must not trim anything
        move(game, Direction.UP, Direction.RIGHT, Direction.LEFT, Direction.RIGHT,
                Direction.LEFT, Direction.RIGHT);
        game.undo(); // retains only UP
        move(game, Direction.DOWN, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        require(view.replay != null && view.replay.commands().size() == 5
                && view.replay.commands().get(1).direction() == Direction.DOWN,
                "discarded branch survived authoritative Undo");
        require(view.victory.undoUses() == 1 && view.victory.score() == 981
                && new ReplayPlayer(view.replay).verify(), "Undo changed score or replay metrics");
        passed++;
    }
    private static void restart(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levels.resolve("demo.txt"));
        move(game, Direction.UP, Direction.RIGHT, Direction.LEFT, Direction.RIGHT, Direction.LEFT);
        game.undo();
        game.restart();
        move(game, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        require(view.replay != null && view.replay.expectedMoves() == 3
                && view.victory.score() == 993 && view.victory.undoUses() == 0,
                "Restart retained old commands or metrics");
        passed++;
    }
    private static void loadsAndAdvance(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.loadCatalog(new LevelCatalog(List.of(
                new LevelCatalog.Entry("demo", levels.resolve("demo.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("bad", levels.resolve("missing.txt"), LevelCatalog.Format.STANDARD))));
        move(game, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        ReplayTrace stable = view.replay;
        game.nextLevel();
        require(view.error != null && game.completedReplay().orElseThrow() == stable,
                "failed Next destroyed replay");
        game.load(levels.resolve("missing.txt"));
        require(view.replay == stable, "invalid load destroyed replay");
        game.loadCatalog(new LevelCatalog(List.of(
                new LevelCatalog.Entry("demo", levels.resolve("demo.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("ice", levels.resolve("ice_demo.txt"), LevelCatalog.Format.STANDARD))));
        require(view.replay == null && game.results().isEmpty(), "valid load retained old replay");
        move(game, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        ReplayTrace first = view.replay;
        game.nextLevel();
        require(view.replay == null && game.completedReplay().isEmpty(), "Next retained old replay");
        move(game, Direction.DOWN, Direction.RIGHT);
        require(view.replay != null && view.replay.levelContext().equals("ice")
                && !view.replay.initialState().equals(first.initialState())
                && game.results().size() == 2 && new ReplayPlayer(first).verify(),
                "level identity/history corrupted");
        passed++;
    }
    private static void special(Path levels, String name) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levels.resolve(name));
        switch (name) {
            case "ice_demo.txt" -> move(game, Direction.DOWN, Direction.RIGHT);
            case "fragile_demo.txt" -> move(game, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
            default -> move(game, Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN,
                    Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP,
                    Direction.LEFT, Direction.LEFT);
        }
        require(view.replay != null && new ReplayPlayer(view.replay).verify()
                && view.replay.expectedFinalState().equals(view.screen.state())
                && view.replay.expectedMoves() == view.victory.moves()
                && view.replay.expectedPushes() == view.victory.pushes(), "special replay mismatch: " + name);
        passed++;
    }
    private static void overflow(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levels.resolve("demo.txt"));
        for (int i = 0; i < 5001; i++) { move(game, Direction.UP, Direction.DOWN); }
        move(game, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);
        require(view.victory != null && view.replay == null && game.results().size() == 1
                && view.victory.score() == 0, "overflow interrupted victory or exposed truncated replay");
        passed++;
    }
    private static void move(GameController game, Direction... path) {
        for (Direction direction : path) { game.move(direction); }
    }
    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
