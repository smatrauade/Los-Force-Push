package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/** Owns the runtime world, bounded Undo history and per-attempt counters. */
public final class GameSession {
    private static final int HISTORY_LIMIT = 15;
    private static final int MOVES_PER_UNDO = 5;
    private static final int UNDO_LIMIT = 3;

    public record Metrics(long moves, long pushes) { }

    /** Only reversible metrics belong in a snapshot; Undo usage is not reversible. */
    private record Snapshot(RuntimeState state, long moves, long pushes) {
        private Snapshot {
            Objects.requireNonNull(state, "state");
        }
    }

    private final RuntimeMovementEngine engine = new RuntimeMovementEngine();
    private final Deque<Snapshot> history = new ArrayDeque<>();
    private RuntimeState initial;
    private RuntimeState current;
    private String levelContext;
    private long moves;
    private long pushes;
    private int undoUsesTotal;
    private int consecutiveUndoUses;

    public GameSession(RuntimeState initial, String levelContext) {
        changeLevel(initial, levelContext);
    }

    public void changeLevel(RuntimeState next, String context) {
        Objects.requireNonNull(next, "initial state");
        Objects.requireNonNull(context, "level context");
        if (context.isBlank()) {
            throw new IllegalArgumentException("Level context is blank");
        }
        initial = next;
        current = next;
        levelContext = context;
        resetAttempt();
    }

    public RuntimeMoveResult move(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        RuntimeMoveResult result = engine.move(current, direction);
        if (result.kind() == MoveResult.Kind.BLOCKED) {
            return result;
        }
        // Perform all potentially throwing arithmetic BEFORE changing session state.
        long nextMoves = Math.addExact(moves, 1);
        long nextPushes = pushes;
        if (result.kind() == MoveResult.Kind.PUSHED) {
            nextPushes = Math.addExact(pushes, 1);
        }
        history.addLast(new Snapshot(current, moves, pushes));
        if (history.size() > HISTORY_LIMIT) {
            history.removeFirst();
        }
        current = result.state();
        moves = nextMoves;
        pushes = nextPushes;
        return result;
    }

    /** Restores the state preceding five successful moves; false means no state change. */
    public boolean undo() {
        if (!canUndo()) {
            return false;
        }
        Snapshot restored = null;
        for (int i = 0; i < MOVES_PER_UNDO; i++) {
            restored = history.removeLast();
        }
        // canUndo guarantees the loop removed exactly five non-null snapshots.
        Snapshot snapshot = Objects.requireNonNull(restored, "undo snapshot");
        current = snapshot.state();
        moves = snapshot.moves();
        pushes = snapshot.pushes();
        undoUsesTotal++;
        consecutiveUndoUses++;
        return true;
    }

    public boolean canUndo() {
        return history.size() >= MOVES_PER_UNDO && consecutiveUndoUses < UNDO_LIMIT;
    }

    public int undoUsesTotal() {
        return undoUsesTotal;
    }

    public int consecutiveUndoUses() {
        return consecutiveUndoUses;
    }

    public void restart() {
        current = initial;
        resetAttempt();
    }

    private void resetAttempt() {
        moves = 0;
        pushes = 0;
        undoUsesTotal = 0;
        consecutiveUndoUses = 0;
        history.clear();
    }

    public RuntimeState currentState() {
        return current;
    }

    public RuntimeState initialState() {
        return initial;
    }

    public String levelContext() {
        return levelContext;
    }

    public Metrics metrics() {
        return new Metrics(moves, pushes);
    }
}
