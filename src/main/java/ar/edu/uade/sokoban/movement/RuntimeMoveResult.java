package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.List;
import java.util.Objects;

/** A push reports its original box, final position and distance, including a broken box. */
public record RuntimeMoveResult(MoveResult.Kind kind, RuntimeState state,
        RuntimeState.Box pushedBox, Position boxDestination, int boxDistance, boolean broken,
        List<LockChange> lockChanges) {
    public record LockChange(String lockId, boolean active) {
        public LockChange { Objects.requireNonNull(lockId, "lockId"); }
    }
    public RuntimeMoveResult(MoveResult.Kind kind, RuntimeState state, RuntimeState.Box pushedBox,
            Position boxDestination, int boxDistance, boolean broken) {
        this(kind, state, pushedBox, boxDestination, boxDistance, broken, List.of());
    }
    public RuntimeMoveResult {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(state, "state");
        lockChanges = List.copyOf(lockChanges);
        if (kind == MoveResult.Kind.PUSHED) {
            Objects.requireNonNull(pushedBox, "pushedBox");
            Objects.requireNonNull(boxDestination, "boxDestination");
            if (boxDistance < 1 || (broken && (pushedBox.kind() != RuntimeState.BoxKind.FRAGILE
                    || pushedBox.resistance() != 1))) {
                throw new IllegalArgumentException("Invalid push outcome");
            }
        } else if (pushedBox != null || boxDestination != null || boxDistance != 0 || broken
                || !lockChanges.isEmpty()) {
            throw new IllegalArgumentException("Non-push cannot carry box outcome");
        }
    }
}
