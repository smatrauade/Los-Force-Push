package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Command encapsulates and executes one replayable domain move. */
public record MoveCommand(Direction direction) {
    public MoveCommand { Objects.requireNonNull(direction, "direction"); }
    public RuntimeMoveResult execute(RuntimeMovementEngine engine, RuntimeState state) {
        return Objects.requireNonNull(engine, "engine").move(state, direction);
    }
}
