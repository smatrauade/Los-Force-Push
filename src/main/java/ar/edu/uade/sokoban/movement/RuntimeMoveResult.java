package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.List;
import java.util.Objects;

/** One immutable transition; boxPath includes the landing cell and never mutates the session. */
public record RuntimeMoveResult(MoveResult.Kind kind, RuntimeState state,
        RuntimeState.Box pushedBox, Position boxDestination, List<LockChange> lockChanges,
        List<Position> boxPath, boolean boxShattered) {
    public record LockChange(String lockId, boolean active) {
        public LockChange {
            Objects.requireNonNull(lockId, "lockId");
        }
    }

    public RuntimeMoveResult(MoveResult.Kind kind, RuntimeState state) {
        this(kind, state, null, null, List.of(), List.of(), false);
    }

    /** Compatibility for historical callers with no explicit visual path. */
    public RuntimeMoveResult(MoveResult.Kind kind, RuntimeState state,
            RuntimeState.Box pushedBox, Position boxDestination, List<LockChange> lockChanges) {
        this(kind, state, pushedBox, boxDestination, lockChanges,
                boxDestination == null ? List.of() : List.of(boxDestination), false);
    }

    public RuntimeMoveResult {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(state, "state");
        lockChanges = List.copyOf(lockChanges);
        boxPath = List.copyOf(boxPath);
        if (kind == MoveResult.Kind.PUSHED) {
            Objects.requireNonNull(pushedBox, "pushedBox");
            Objects.requireNonNull(boxDestination, "boxDestination");
            if (boxPath.isEmpty() || !boxPath.get(boxPath.size() - 1).equals(boxDestination)
                    || (boxShattered && pushedBox.kind() != RuntimeState.BoxKind.FRAGILE)) {
                throw new IllegalArgumentException("Invalid push trajectory or fragile break");
            }
        } else if (pushedBox != null || boxDestination != null || !lockChanges.isEmpty()
                || !boxPath.isEmpty() || boxShattered) {
            throw new IllegalArgumentException("Non-push cannot carry box outcome");
        }
    }
}
