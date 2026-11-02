package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Atomic, stateless movement over typed Key/Lock/Wall runtime state. */
public final class RuntimeMovementEngine {
    public RuntimeMoveResult move(RuntimeState state, Direction direction) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(direction, "direction");
        Position target = step(state, state.player(), direction);
        if (target == null || blockedTerrain(state, target)) {
            return new RuntimeMoveResult(MoveResult.Kind.BLOCKED, state);
        }
        RuntimeState.Box box = state.boxAt(target);
        if (box == null) {
            return new RuntimeMoveResult(MoveResult.Kind.MOVED,
                    new RuntimeState(state.terrain(), target, state.boxes(), state.locks(), state.walls()));
        }
        Position destination = step(state, target, direction);
        if (!free(state, destination)) {
            return new RuntimeMoveResult(MoveResult.Kind.BLOCKED, state);
        }
        List<RuntimeState.Box> boxes = new ArrayList<>(state.boxes());
        int index = boxes.indexOf(box);
        boxes.set(index, box.kind() == RuntimeState.BoxKind.KEY
                ? RuntimeState.Box.key(box.id(), destination)
                : RuntimeState.Box.normal(destination));
        if (!RuntimeState.closuresSafe(target, boxes, state.locks(), state.walls())) {
            return new RuntimeMoveResult(MoveResult.Kind.BLOCKED, state);
        }
        RuntimeState next = new RuntimeState(state.terrain(), target, boxes, state.locks(), state.walls());
        List<RuntimeMoveResult.LockChange> changes = state.locks().stream()
                .filter(lock -> state.lockActive(lock.id()) != next.lockActive(lock.id()))
                .map(lock -> new RuntimeMoveResult.LockChange(lock.id(), next.lockActive(lock.id())))
                .toList();
        return new RuntimeMoveResult(MoveResult.Kind.PUSHED, next, box, destination, changes);
    }

    private boolean free(RuntimeState state, Position position) {
        return position != null && !blockedTerrain(state, position)
                && state.boxAt(position) == null && !state.player().equals(position);
    }

    private boolean blockedTerrain(RuntimeState state, Position position) {
        RuntimeState.Ground ground = state.terrain().get(position.row()).get(position.column());
        return ground == RuntimeState.Ground.WALL
                || (ground == RuntimeState.Ground.DYNAMIC_WALL && !state.wallOpen(position));
    }

    private Position step(RuntimeState state, Position position, Direction direction) {
        int row = position.row() + direction.rowDelta();
        int col = position.column() + direction.columnDelta();
        return row < 0 || col < 0 || row >= state.height() || col >= state.width()
                ? null : new Position(row, col);
    }
}
