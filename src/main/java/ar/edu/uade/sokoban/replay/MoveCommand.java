package ar.edu.uade.sokoban.replay;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** One immutable Command; its receiver is the runtime engine, never the live GameSession. */
public record MoveCommand(Direction direction) {
    public MoveCommand {
        Objects.requireNonNull(direction, "direction");
    }

    public RuntimeMoveResult execute(RuntimeMovementEngine engine, RuntimeState state) {
        return Objects.requireNonNull(engine, "engine")
                .move(Objects.requireNonNull(state, "state"), direction);
    }
}
