package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.List;
import java.util.Objects;

/** A push reports its original box, destination and lock transitions. */
public record RuntimeMoveResult(MoveResult.Kind kind, RuntimeState state,
        RuntimeState.Box pushedBox, Position boxDestination, List<LockChange> lockChanges) {
    public record LockChange(String lockId, boolean active) {
        public LockChange {
            Objects.requireNonNull(lockId, "lockId");
        }
    }

    public RuntimeMoveResult(MoveResult.Kind kind, RuntimeState state) {
        this(kind, state, null, null, List.of());
    }

    public RuntimeMoveResult {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(state, "state");
        lockChanges = List.copyOf(lockChanges);
        if (kind == MoveResult.Kind.PUSHED) {
            Objects.requireNonNull(pushedBox, "pushedBox");
            Objects.requireNonNull(boxDestination, "boxDestination");
        } else if (pushedBox != null || boxDestination != null || !lockChanges.isEmpty()) {
            throw new IllegalArgumentException("Non-push cannot carry box outcome");
        }
    }
}
