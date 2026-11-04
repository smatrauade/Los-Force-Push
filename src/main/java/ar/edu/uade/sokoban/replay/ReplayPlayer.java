package ar.edu.uade.sokoban.replay;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Detached playback cursor. It owns no GameSession, Swing timer, score or Undo state. */
public final class ReplayPlayer {
    private final ReplayTrace trace;
    private final RuntimeMovementEngine engine = new RuntimeMovementEngine();
    private RuntimeState state;
    private int index;
    private long pushes;

    public ReplayPlayer(ReplayTrace trace) {
        this.trace = Objects.requireNonNull(trace, "trace");
        reset();
    }

    public void reset() {
        state = trace.initialState();
        index = 0;
        pushes = 0;
    }

    public boolean hasNext() {
        return index < trace.commands().size();
    }

    public RuntimeState currentState() {
        return state;
    }

    public int index() {
        return index;
    }

    public long moves() {
        return index;
    }

    public long pushes() {
        return pushes;
    }

    public Direction nextDirection() {
        if (!hasNext()) {
            throw new IllegalStateException("Replay exhausted");
        }
        return trace.commands().get(index).direction();
    }

    /** Executes one command atomically; a blocked command never advances the cursor. */
    public RuntimeMoveResult step() {
        if (!hasNext()) {
            throw new IllegalStateException("Replay exhausted");
        }
        RuntimeMoveResult result = trace.commands().get(index).execute(engine, state);
        if (result.kind() == MoveResult.Kind.BLOCKED) {
            throw new IllegalStateException("Replay diverged: recorded command became blocked");
        }
        long nextPushes = pushes + (result.kind() == MoveResult.Kind.PUSHED ? 1L : 0L);
        state = result.state();
        pushes = nextPushes;
        index++;
        return result;
    }

    /** Independent verification; does not alter this cursor or the original game. */
    public boolean verify() {
        ReplayPlayer candidate = new ReplayPlayer(trace);
        try {
            while (candidate.hasNext()) {
                candidate.step();
            }
        } catch (IllegalStateException | IllegalArgumentException failure) {
            return false;
        }
        return candidate.state.equals(trace.expectedFinalState())
                && candidate.moves() == trace.expectedMoves()
                && candidate.pushes() == trace.expectedPushes();
    }
}
