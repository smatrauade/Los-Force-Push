package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/** Deterministic session/memento contracts; no GUI and no assertions flag required. */
public final class GameSessionTest {
    private static int passed;
    private GameSessionTest() { }
    public static void main(String[] args) throws Exception {
        RuntimeState initial = RuntimeState.fromExtended(new LevelLoader(new LevelParser())
                .loadExtended(Path.of(args[0]).resolve("extended-runtime.txt")));
        GameSession session = new GameSession(initial, "fixture");
        check(session.currentState().equals(initial) && session.initialState() == initial
                && session.historyAvailable() == 0 && session.undoUsesRemaining() == 3, "initial state");
        check(session.undoFive() == GameSession.UndoResult.INSUFFICIENT_HISTORY, "zero undo rejected");
        check(session.move(Direction.UP).kind() == MoveResult.Kind.BLOCKED
                && session.historyAvailable() == 0 && session.metrics().moves() == 0, "blocked not recorded");
        session.move(Direction.DOWN); session.move(Direction.RIGHT); session.move(Direction.RIGHT);
        RuntimeState broken = session.currentState();
        check(broken.boxAt(p(2, 4)) == null && session.metrics().moves() == 3 && session.metrics().pushes() == 3,
                "mixed normal and fragile pushes counted");
        session.move(Direction.LEFT);
        RuntimeState beforeBlocked = session.currentState();
        check(session.move(Direction.UP).kind() == MoveResult.Kind.BLOCKED
                && session.currentState() == beforeBlocked && session.historyAvailable() == 4
                && session.metrics().moves() == 4, "interleaved blocked move leaves history intact");
        GameSession.Metrics four = session.metrics();
        check(session.undoFive() == GameSession.UndoResult.INSUFFICIENT_HISTORY
                && session.metrics().equals(four) && session.currentState().boxAt(p(2, 3)) == null
                && session.historyAvailable() == 4, "four moves reject atomically");
        session.move(Direction.LEFT);
        check(session.canUndo() && session.undoFive() == GameSession.UndoResult.RESTORED
                && session.currentState().equals(initial) && session.metrics().moves() == 0
                && session.metrics().pushes() == 0 && session.metrics().undoUsesTotal() == 1
                && session.undoUsesRemaining() == 2, "five moves restore fragile and counters");
        check(broken.boxAt(p(2, 4)) == null && initial.boxAt(p(2, 2)).resistance() == 2, "old values immutable");
        session.move(Direction.RIGHT); session.move(Direction.RIGHT); session.move(Direction.RIGHT);
        check(!session.currentState().lockActive("l1") && !session.currentState().wallOpen(p(1, 6))
                && session.metrics().pushes() == 2, "key ice push and lock closure counted once each");
        session.move(Direction.LEFT); session.move(Direction.LEFT);
        check(session.undoFive() == GameSession.UndoResult.RESTORED && session.currentState().equals(initial)
                && !session.currentState().lockActive("l1") && !session.currentState().wallOpen(p(1, 6))
                && session.metrics().undoUsesTotal() == 2, "undo restores dynamic world");
        session.restart();
        check(session.currentState() == initial && session.historyAvailable() == 0 && session.undoUsesRemaining() == 3
                && session.metrics().undoUsesTotal() == 2 && session.metrics().moves() == 0, "restart preserves lifetime undo total");
        session.restart();
        check(session.currentState() == initial && session.metrics().undoUsesTotal() == 2, "repeat restart");
        GameSession corridor = new GameSession(corridor(), "corridor");
        alternate(corridor, 6);
        RuntimeState afterSix = corridor.currentState();
        check(corridor.historyAvailable() == 6 && corridor.undoFive() == GameSession.UndoResult.RESTORED
                && corridor.metrics().moves() == 1 && corridor.historyAvailable() == 1
                && !corridor.currentState().equals(afterSix), "six returns to one");
        corridor.restart(); alternate(corridor, 14);
        check(corridor.historyAvailable() == 14, "fourteen snapshots");
        alternate(corridor, 1); check(corridor.historyAvailable() == 15, "fifteen snapshots");
        alternate(corridor, 1); check(corridor.historyAvailable() == 15 && corridor.metrics().moves() == 16, "sixteen capped");
        alternate(corridor, 14); check(corridor.historyAvailable() == 15 && corridor.metrics().moves() == 30, "long history capped");
        check(corridor.undoFive() == GameSession.UndoResult.RESTORED && corridor.metrics().moves() == 25,
                "first undo of capped history");
        check(corridor.undoFive() == GameSession.UndoResult.RESTORED && corridor.metrics().moves() == 20,
                "second undo of capped history");
        check(corridor.undoFive() == GameSession.UndoResult.RESTORED && corridor.metrics().moves() == 15
                && corridor.historyAvailable() == 0 && corridor.undoUsesRemaining() == 0, "third undo and oldest discarded");
        alternate(corridor, 5);
        RuntimeState beforeRejected = corridor.currentState(); GameSession.Metrics beforeMetrics = corridor.metrics();
        check(corridor.undoFive() == GameSession.UndoResult.LIMIT_REACHED && corridor.currentState() == beforeRejected
                && corridor.metrics().equals(beforeMetrics) && corridor.historyAvailable() == 5,
                "fourth undo rejected after new moves");
        corridor.restart(); check(corridor.undoUsesRemaining() == 3 && corridor.metrics().undoUsesTotal() == 4,
                "restart resets limit but not cumulative total");
        GameSession branch = new GameSession(corridor(), "branch"); alternate(branch, 6);
        branch.undoFive(); RuntimeState restored = branch.currentState();
        branch.move(Direction.LEFT);
        check(branch.historyAvailable() == 2 && branch.metrics().moves() == 2 && branch.undoUsesRemaining() == 2
                && !branch.currentState().equals(restored), "branch after undo, no redo and budget retained");
        GameSession other = new GameSession(corridor(), "other");
        check(other.historyAvailable() == 0 && other.metrics().undoUsesTotal() == 0, "independent sessions");
        RuntimeState beforeInvalid = branch.currentState(); GameSession.Metrics invalidMetrics = branch.metrics();
        expectFailure(() -> branch.move(null), "null direction");
        expectFailure(() -> branch.changeLevel(null, "new"), "null level");
        expectFailure(() -> branch.changeLevel(corridor(), null), "null context");
        expectFailure(() -> branch.changeLevel(corridor(), "  "), "blank context");
        expectFailure(() -> new GameSession(null, "new"), "null initial");
        check(branch.currentState() == beforeInvalid && branch.metrics().equals(invalidMetrics)
                && branch.historyAvailable() == 2, "invalid inputs atomic");
        branch.changeLevel(initial, "second");
        check(branch.currentState() == initial && branch.initialState() == initial && branch.levelContext().equals("second")
                && branch.historyAvailable() == 0 && branch.undoUsesRemaining() == 3
                && branch.metrics().equals(new GameSession.Metrics(0, 0, 0, 0)), "level change resets level metrics");
        GameSession same = new GameSession(corridor(), "other"); alternate(other, 8); alternate(same, 8);
        check(other.currentState().equals(same.currentState()) && other.metrics().equals(same.metrics()), "reproducible sessions");
        Path temp = Files.createTempFile("sokoban-session-", ".txt");
        try {
            Files.copy(Path.of(args[0]).resolve("extended-runtime.txt"), temp,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            RuntimeState loaded = RuntimeState.fromExtended(new LevelLoader(new LevelParser()).loadExtended(temp));
            GameSession fromFile = new GameSession(loaded, "external");
            fromFile.move(Direction.DOWN);
            Files.writeString(temp, "invalid", StandardCharsets.UTF_8);
            fromFile.restart();
            check(fromFile.currentState().equals(loaded) && fromFile.metrics().moves() == 0,
                    "restart never rereads edited file");
        } finally { Files.deleteIfExists(temp); }
        RuntimeState multi = multiLock(); GameSession dynamic = new GameSession(multi, "multi");
        dynamic.move(Direction.RIGHT); dynamic.move(Direction.DOWN); dynamic.move(Direction.LEFT);
        dynamic.move(Direction.UP); dynamic.move(Direction.RIGHT);
        check(dynamic.historyAvailable() == 5 && dynamic.undoFive() == GameSession.UndoResult.RESTORED
                && dynamic.currentState().equals(multi) && dynamic.currentState().lockActive("a")
                && dynamic.currentState().lockActive("b") && dynamic.currentState().wallOpen(p(1, 1))
                && dynamic.currentState().wallOpen(p(1, 3)), "undo restores multiple locks and walls");
        System.out.println("SESSION RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static RuntimeState corridor() {
        return new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR)), p(0, 1), List.of());
    }
    private static RuntimeState multiLock() {
        return new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK, RuntimeState.Ground.FLOOR),
                List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.DYNAMIC_WALL, RuntimeState.Ground.FLOOR,
                        RuntimeState.Ground.DYNAMIC_WALL, RuntimeState.Ground.FLOOR)), p(0, 0),
                List.of(RuntimeState.Box.key("k1", p(0, 1)), RuntimeState.Box.key("k2", p(0, 3))),
                List.of(new RuntimeState.Lock("a", p(0, 1)), new RuntimeState.Lock("b", p(0, 3))),
                List.of(new RuntimeState.DynamicWall("w1", p(1, 1), Set.of("a", "b")),
                        new RuntimeState.DynamicWall("w2", p(1, 3), Set.of("b"))));
    }
    private static void alternate(GameSession session, int n) {
        for (int i = 0; i < n; i++) {
            Direction direction = session.currentState().player().column() == 1 ? Direction.RIGHT : Direction.LEFT;
            if (session.move(direction).kind() == MoveResult.Kind.BLOCKED) { throw new AssertionError("blocked alternating move"); }
        }
    }
    private static Position p(int row, int col) { return new Position(row, col); }
    private static void expectFailure(Runnable task, String label) {
        try { task.run(); } catch (IllegalArgumentException | NullPointerException expected) { check(true, label); return; }
        throw new AssertionError("Expected rejection: " + label);
    }
    private static void check(boolean condition, String label) {
        if (!condition) { throw new AssertionError(label); }
        passed++; System.out.println("PASS " + label);
    }
}
