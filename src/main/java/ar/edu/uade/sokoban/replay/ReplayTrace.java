package ar.edu.uade.sokoban.replay;

import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.List;
import java.util.Objects;

/** Immutable complete attempt: all successful commands after Undo/Restart reconciliation. */
public record ReplayTrace(String levelContext, RuntimeState initialState,
        List<MoveCommand> commands, RuntimeState expectedFinalState,
        long expectedMoves, long expectedPushes) {

    public ReplayTrace {
        Objects.requireNonNull(levelContext, "levelContext");
        if (levelContext.isBlank()) {
            throw new IllegalArgumentException("Blank replay level context");
        }
        Objects.requireNonNull(initialState, "initialState");
        Objects.requireNonNull(expectedFinalState, "expectedFinalState");
        commands = List.copyOf(commands);
        if (commands.size() > ReplayRecorder.MAX_COMMANDS || expectedMoves != commands.size()
                || expectedPushes < 0 || expectedPushes > expectedMoves) {
            throw new IllegalArgumentException("Invalid complete replay metrics or size");
        }
    }
}
