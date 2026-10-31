package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Individual session; mutable coordinator over immutable world values, not thread-safe. */
public final class GameSession {
    public static final int UNDO_STEPS = 5;
    public static final int MAX_UNDO_USES = 3;
    public enum UndoResult { RESTORED, INSUFFICIENT_HISTORY, LIMIT_REACHED }
    public record Metrics(long moves, long pushes, long undoUsesTotal, int undoUsesSinceReset) { }

    /** Private memento is created and read exclusively by its originator. */
    private record Snapshot(RuntimeState state, long moves, long pushes) implements GameMemento { }
    private final RuntimeMovementEngine engine = new RuntimeMovementEngine();
    private final UndoManager history = new UndoManager();
    private RuntimeState initial;
    private RuntimeState current;
    private String levelContext;
    private long moves;
    private long pushes;
    private long undoUsesTotal;
    private int undoUsesSinceReset;

    public GameSession(RuntimeState initial, String levelContext) {
        changeLevel(initial, levelContext);
    }
    public void changeLevel(RuntimeState next, String context) {
        Objects.requireNonNull(next, "initial state");
        Objects.requireNonNull(context, "level context");
        if (context.isBlank()) { throw new IllegalArgumentException("Level context is blank"); }
        initial = next;
        current = next;
        levelContext = context;
        moves = 0; pushes = 0; undoUsesTotal = 0; undoUsesSinceReset = 0;
        history.clear();
    }
    public RuntimeMoveResult move(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        RuntimeMoveResult result = engine.move(current, direction);
        if (result.kind() == MoveResult.Kind.BLOCKED) { return result; }
        long nextMoves = Math.addExact(moves, 1);
        long nextPushes = result.kind() == MoveResult.Kind.PUSHED ? Math.addExact(pushes, 1) : pushes;
        Snapshot before = new Snapshot(current, moves, pushes);
        history.record(before);
        current = result.state(); moves = nextMoves; pushes = nextPushes;
        return result;
    }
    public boolean canUndo() { return history.size() >= UNDO_STEPS && undoUsesRemaining() > 0; }
    public UndoResult undoFive() {
        if (undoUsesRemaining() == 0) { return UndoResult.LIMIT_REACHED; }
        if (history.size() < UNDO_STEPS) { return UndoResult.INSUFFICIENT_HISTORY; }
        Snapshot snapshot = (Snapshot) history.peekBack(UNDO_STEPS);
        long nextTotal = Math.addExact(undoUsesTotal, 1);
        history.discardBack(UNDO_STEPS);
        current = snapshot.state(); moves = snapshot.moves(); pushes = snapshot.pushes();
        undoUsesTotal = nextTotal; undoUsesSinceReset++;
        return UndoResult.RESTORED;
    }
    public void restart() {
        current = initial; moves = 0; pushes = 0; undoUsesSinceReset = 0; history.clear();
    }
    public RuntimeState currentState() { return current; }
    public RuntimeState initialState() { return initial; }
    public String levelContext() { return levelContext; }
    public Metrics metrics() { return new Metrics(moves, pushes, undoUsesTotal, undoUsesSinceReset); }
    public int historyAvailable() { return history.size(); }
    public int undoUsesRemaining() { return MAX_UNDO_USES - undoUsesSinceReset; }
}
