package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Separate mutable playback cursor; never holds or mutates the real session. */
public final class ReplayPlayer {
    private final ReplayTrace trace;
    private final RuntimeMovementEngine engine = new RuntimeMovementEngine();
    private RuntimeState current;
    private int index;
    public ReplayPlayer(ReplayTrace trace) { this.trace = Objects.requireNonNull(trace, "trace"); reset(); }
    public void reset() { current = trace.initialState(); index = 0; }
    public boolean hasNext() { return index < trace.commands().size(); }
    public RuntimeState currentState() { return current; }
    public int index() { return index; }
    public ar.edu.uade.sokoban.movement.Direction nextDirection() {
        if (!hasNext()) { throw new IllegalStateException("Replay is complete"); }
        return trace.commands().get(index).direction();
    }
    public void step() {
        if (!hasNext()) { return; }
        var result = trace.commands().get(index).execute(engine, current);
        if (result.kind() == MoveResult.Kind.BLOCKED) { throw new IllegalStateException("Replay command blocked"); }
        current = result.state(); index++;
    }
    public boolean verify() {
        ReplayPlayer check = new ReplayPlayer(trace);
        while (check.hasNext()) { check.step(); }
        return check.currentState().equals(trace.expectedFinalState());
    }
}
