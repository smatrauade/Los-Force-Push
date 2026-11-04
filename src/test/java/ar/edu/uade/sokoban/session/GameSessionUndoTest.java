package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** INT-05/06 P2c: screen-free checks of the authoritative session state. */
public final class GameSessionUndoTest {
    private static int passed;

    private GameSessionUndoTest() { }

    public static void main(String[] args) throws Exception {
        testNoHistoryAndBlocked();
        testFiveFifteenThreeLimit();
        testBoundedHistory();
        testPushCountersAndRestart();
        testFragileIceSnapshot();
        testDynamicLockSnapshot();
        testDynamicLockOpenRestoration();
        testChangeLevelBoundary();
        testInvalidInputsPreserveSession();
        testOverflowIsAtomic();
        System.out.println("SESSION UNDO P2C RESULT: " + passed + " PASS, 0 FAIL");
    }

    private static void testNoHistoryAndBlocked() {
        GameSession session = new GameSession(state(2, 0, List.of(RuntimeState.Box.normal(p(0, 1)))), "x");
        RuntimeState first = session.currentState();
        check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.BLOCKED,
                "blocked push reported");
        check(session.currentState() == first && session.metrics().equals(new GameSession.Metrics(0, 0)),
                "blocked move preserves exact world and counters");
        check(!session.canUndo() && !session.undo() && session.undoUsesTotal() == 0,
                "blocked move cannot create Undo history");
    }

    private static void testFiveFifteenThreeLimit() {
        RuntimeState initial = state(30, 0, List.of());
        GameSession session = new GameSession(initial, "walk");
        for (int i = 0; i < 4; i++) {
            check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.MOVED, "walk step " + i);
        }
        check(!session.canUndo() && !session.undo(), "Undo unavailable at four moves");
        session.move(Direction.RIGHT);
        check(session.canUndo(), "Undo available on fifth successful move");
        check(session.undo() && session.currentState() == initial
                && session.metrics().equals(new GameSession.Metrics(0, 0)),
                "Undo five restores world and metrics");
        check(session.undoUsesTotal() == 1 && session.consecutiveUndoUses() == 1,
                "Undo use is accumulated outside snapshot");
        for (int i = 0; i < 15; i++) {
            session.move(Direction.RIGHT);
        }
        check(session.undo() && session.currentState().player().equals(p(0, 10)),
                "second Undo restores ten moves");
        check(session.undo() && session.currentState().player().equals(p(0, 5)),
                "third Undo restores five moves");
        check(session.undoUsesTotal() == 3 && session.consecutiveUndoUses() == 3,
                "three-use limit accumulated through movements");
        session.move(Direction.RIGHT);
        session.move(Direction.RIGHT);
        session.move(Direction.RIGHT);
        session.move(Direction.RIGHT);
        session.move(Direction.RIGHT);
        check(!session.canUndo() && !session.undo(), "new moves do not restore Undo budget");
    }

    private static void testBoundedHistory() {
        GameSession session = new GameSession(state(30, 0, List.of()), "capacity");
        for (int i = 0; i < 16; i++) {
            session.move(Direction.RIGHT);
        }
        check(session.undo() && session.undo() && session.undo(),
                "fifteen latest moves support three Undos");
        check(session.currentState().player().equals(p(0, 1))
                && session.metrics().moves() == 1,
                "oldest move evicted from the bounded history");
    }

    private static void testPushCountersAndRestart() {
        RuntimeState initial = state(10, 0, List.of(RuntimeState.Box.normal(p(0, 1))));
        GameSession session = new GameSession(initial, "push");
        for (int i = 0; i < 5; i++) {
            check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.PUSHED,
                    "successful push " + i);
        }
        check(session.metrics().equals(new GameSession.Metrics(5, 5)), "push metrics count five");
        check(session.undo() && session.currentState() == initial
                && session.metrics().equals(new GameSession.Metrics(0, 0)),
                "Undo restores pushed box and all reversible metrics");
        check(session.undoUsesTotal() == 1 && session.consecutiveUndoUses() == 1,
                "Undo uses incremented");
        check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.PUSHED, "push after undo");
        check(session.metrics().moves() > 0 && session.metrics().pushes() > 0
                && session.undoUsesTotal() > 0 && session.consecutiveUndoUses() > 0,
                "all four counters strictly positive before restart");
        session.restart();
        check(session.currentState() == initial && !session.canUndo()
                && session.metrics().moves() == 0 && session.metrics().pushes() == 0
                && session.undoUsesTotal() == 0 && session.consecutiveUndoUses() == 0,
                "Restart clears history and all four counters");
    }

    private static void testFragileIceSnapshot() {
        RuntimeState initial = new RuntimeState(
                List.of(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR,
                        RuntimeState.Ground.ICE, RuntimeState.Ground.ICE,
                        RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR,
                        RuntimeState.Ground.FLOOR)),
                p(0, 0), List.of(RuntimeState.Box.fragile("f1", p(0, 1), 1)));
        GameSession session = new GameSession(initial, "ice_fragile");
        check(session.move(Direction.RIGHT).boxShattered(), "Fragile completes slide then shatters");
        check(session.currentState().boxes().isEmpty(), "shattered Fragile absent before Undo");
        for (int i = 0; i < 4; i++) {
            session.move(Direction.RIGHT);
        }
        check(session.undo() && session.currentState() == initial,
                "Undo restores full ICE state after fragile destruction");
        RuntimeState.Box restored = session.currentState().boxAt(p(0, 1));
        check(restored.id().equals("f1") && restored.resistance() == 1
                && session.currentState().terrain().get(0).get(2) == RuntimeState.Ground.ICE,
                "Fragile ID, resistance and ICE preserved without Board conversion");
        check(session.metrics().equals(new GameSession.Metrics(0, 0)),
                "Fragile Undo restores movement and push counts");
    }

    private static void testDynamicLockSnapshot() {
        RuntimeState initial = new RuntimeState(List.of(List.of(
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.LOCK, RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.DYNAMIC_WALL, RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR)), p(0, 0),
                List.of(RuntimeState.Box.key("key", p(0, 1))),
                List.of(new RuntimeState.Lock("lock", p(0, 2))),
                List.of(new RuntimeState.DynamicWall("wall", p(0, 4), Set.of("lock"))));
        GameSession session = new GameSession(initial, "lock");
        check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.PUSHED
                && session.currentState().wallOpen(p(0, 4)), "key opens dynamic wall");
        check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.PUSHED
                && !session.currentState().wallOpen(p(0, 4)), "key leaving lock closes wall");
        session.move(Direction.LEFT);
        session.move(Direction.RIGHT);
        session.move(Direction.LEFT);
        check(session.undo() && session.currentState() == initial,
                "Undo restores Lock / Key / DynamicWall from RuntimeState");
        check(!session.currentState().wallOpen(p(0, 4))
                && !session.currentState().lockActive("lock")
                && session.currentState().boxAt(p(0, 1)).id().equals("key"),
                "Lock associations and key ID preserved");
    }

    private static void testDynamicLockOpenRestoration() {
        // Grid: 3 rows x 4 cols of FLOORs, Lock at (0,2), DynamicWall at (1,3)
        List<List<RuntimeState.Ground>> terrain = List.of(
                List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK, RuntimeState.Ground.FLOOR),
                List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.DYNAMIC_WALL),
                List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR)
        );
        RuntimeState initial = new RuntimeState(terrain, p(0, 0),
                List.of(RuntimeState.Box.key("key", p(0, 1))),
                List.of(new RuntimeState.Lock("lock", p(0, 2))),
                List.of(new RuntimeState.DynamicWall("wall", p(1, 3), Set.of("lock"))));
        GameSession session = new GameSession(initial, "lock_open_undo");
        // Push KeyBox onto Lock: wall opens!
        check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.PUSHED
                && session.currentState().wallOpen(p(1, 3))
                && session.currentState().lockActive("lock"),
                "key lands on lock and opens dynamic wall");
        RuntimeState openState = session.currentState();
        // 5 successful moves without disturbing the KeyBox on the Lock
        check(session.move(Direction.DOWN).kind() == MoveResult.Kind.MOVED, "move 1 down");
        check(session.move(Direction.DOWN).kind() == MoveResult.Kind.MOVED, "move 2 down");
        check(session.move(Direction.RIGHT).kind() == MoveResult.Kind.MOVED, "move 3 right");
        check(session.move(Direction.UP).kind() == MoveResult.Kind.MOVED, "move 4 up");
        check(session.move(Direction.LEFT).kind() == MoveResult.Kind.MOVED, "move 5 left");
        check(session.canUndo(), "undo available after five subsequent moves");
        check(session.undo(), "undo executes successfully");
        // Verify restored state has DynamicWall OPEN and Lock ACTIVE!
        check(session.currentState() == openState, "undo restores exact state with dynamic wall open");
        check(session.currentState().wallOpen(p(1, 3)), "dynamic wall remains open after undo");
        check(session.currentState().lockActive("lock"), "lock remains active after undo");
        RuntimeState.Box restoredBox = session.currentState().boxAt(p(0, 2));
        check(restoredBox != null && restoredBox.id().equals("key") && restoredBox.kind() == RuntimeState.BoxKind.KEY,
                "key box preserved on lock with proper id and kind");
        check(session.currentState().player().equals(p(0, 1)), "player position restored to (0,1)");
        check(session.metrics().equals(new GameSession.Metrics(1, 1)), "metrics restored to 1 move and 1 push");
        check(session.undoUsesTotal() == 1 && session.consecutiveUndoUses() == 1,
                "undo usages incremented correctly");
    }

    private static void testChangeLevelBoundary() {
        GameSession session = new GameSession(state(15, 0, List.of()), "one");
        for (int i = 0; i < 5; i++) {
            session.move(Direction.RIGHT);
        }
        session.undo();
        RuntimeState next = state(8, 0, List.of());
        session.changeLevel(next, "two");
        check(session.currentState() == next && session.initialState() == next
                && session.levelContext().equals("two"), "changeLevel selects new authoritative world");
        check(!session.canUndo() && session.consecutiveUndoUses() == 0,
                "changeLevel clears history and Undo limit");
    }

    private static void testInvalidInputsPreserveSession() {
        GameSession session = new GameSession(state(4, 0, List.of()), "v");
        RuntimeState old = session.currentState();
        try {
            session.changeLevel(old, " ");
            throw new AssertionError("Expected blank level ID rejection");
        } catch (IllegalArgumentException expected) {
            check(session.currentState() == old && session.levelContext().equals("v"),
                    "invalid context does not change state");
        }
        try {
            session.move(null);
            throw new AssertionError("Expected null direction rejection");
        } catch (NullPointerException expected) {
            check(session.currentState() == old && session.metrics().moves() == 0,
                    "null direction does not change state");
        }
    }

    private static void testOverflowIsAtomic() throws Exception {
        GameSession session = new GameSession(state(5, 0, List.of()), "overflow");
        Field moves = GameSession.class.getDeclaredField("moves");
        moves.setAccessible(true);
        moves.setLong(session, Long.MAX_VALUE);
        RuntimeState old = session.currentState();
        try {
            session.move(Direction.RIGHT);
            throw new AssertionError("Expected counter overflow rejection");
        } catch (ArithmeticException expected) {
            check(session.currentState() == old && session.metrics().moves() == Long.MAX_VALUE
                    && !session.canUndo() && session.undoUsesTotal() == 0,
                    "counter overflow cannot partially move world or history");
        }
        GameSession pushSession = new GameSession(
                state(5, 0, List.of(RuntimeState.Box.normal(p(0, 1)))), "push_overflow");
        Field pushes = GameSession.class.getDeclaredField("pushes");
        pushes.setAccessible(true);
        pushes.setLong(pushSession, Long.MAX_VALUE);
        RuntimeState pushBefore = pushSession.currentState();
        try {
            pushSession.move(Direction.RIGHT);
            throw new AssertionError("Expected push counter overflow rejection");
        } catch (ArithmeticException expected) {
            check(pushSession.currentState() == pushBefore && pushSession.metrics().moves() == 0
                    && pushSession.metrics().pushes() == Long.MAX_VALUE && !pushSession.canUndo(),
                    "push overflow cannot partially update world, counters or Undo history");
        }
    }

    private static RuntimeState state(int width, int playerColumn, List<RuntimeState.Box> boxes) {
        List<RuntimeState.Ground> row = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            row.add(RuntimeState.Ground.FLOOR);
        }
        return new RuntimeState(List.of(row), p(0, playerColumn), boxes);
    }

    private static Position p(int row, int col) {
        return new Position(row, col);
    }

    private static void check(boolean ok, String label) {
        if (!ok) {
            throw new AssertionError(label);
        }
        passed++;
        System.out.println("PASS " + label);
    }
}
