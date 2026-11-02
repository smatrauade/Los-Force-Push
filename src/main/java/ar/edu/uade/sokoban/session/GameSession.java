package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Mutable coordinator over immutable world values. Undo remains out of this increment. */
public final class GameSession {
    public record Metrics(long moves, long pushes) { }

    private final RuntimeMovementEngine engine = new RuntimeMovementEngine();
    private RuntimeState initial;
    private RuntimeState current;
    private String levelContext;
    private long moves;
    private long pushes;

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
        moves = 0;
        pushes = 0;
    }

    public RuntimeMoveResult move(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        RuntimeMoveResult result = engine.move(current, direction);
        if (result.kind() == MoveResult.Kind.BLOCKED) {
            return result;
        }
        current = result.state();
        moves = Math.addExact(moves, 1);
        if (result.kind() == MoveResult.Kind.PUSHED) {
            pushes = Math.addExact(pushes, 1);
        }
        return result;
    }

    public void restart() {
        current = initial;
        moves = 0;
        pushes = 0;
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
