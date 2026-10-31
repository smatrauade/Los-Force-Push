package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.List;
import java.util.Objects;

public record ReplayTrace(String levelContext, RuntimeState initialState,
        List<MoveCommand> commands, RuntimeState expectedFinalState) {
    public ReplayTrace {
        Objects.requireNonNull(levelContext, "levelContext"); Objects.requireNonNull(initialState, "initialState");
        Objects.requireNonNull(expectedFinalState, "expectedFinalState"); commands = List.copyOf(commands);
    }
}
