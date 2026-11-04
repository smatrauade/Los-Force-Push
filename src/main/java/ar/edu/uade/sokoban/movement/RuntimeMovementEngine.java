package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Atomic moves over the sole RuntimeState authority; a slide costs one push. */
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
        List<Position> path = new ArrayList<>();
        path.add(destination);
        // E03: when an exit is blocked, stop on the last free tile.
        while (state.terrain().get(destination.row()).get(destination.column()) == RuntimeState.Ground.ICE) {
            Position next = step(state, destination, direction);
            if (!free(state, next)) {
                break;
            }
            destination = next;
            path.add(destination);
        }
        // E04: consume one resistance after the entire trajectory, including on ICE.
        boolean shattered = box.kind() == RuntimeState.BoxKind.FRAGILE && box.resistance() == 1;
        List<RuntimeState.Box> boxes = new ArrayList<>(state.boxes());
        int index = boxes.indexOf(box);
        if (shattered) {
            boxes.remove(index);
        } else if (box.kind() == RuntimeState.BoxKind.FRAGILE) {
            boxes.set(index, RuntimeState.Box.fragile(box.id(), destination, box.resistance() - 1));
        } else {
            boxes.set(index, box.movedTo(destination));
        }
        if (!RuntimeState.closuresSafe(target, boxes, state.locks(), state.walls())) {
            return new RuntimeMoveResult(MoveResult.Kind.BLOCKED, state);
        }
        RuntimeState next = new RuntimeState(state.terrain(), target, boxes, state.locks(), state.walls());
        List<RuntimeMoveResult.LockChange> changes = state.locks().stream()
                .filter(lock -> state.lockActive(lock.id()) != next.lockActive(lock.id()))
                .map(lock -> new RuntimeMoveResult.LockChange(lock.id(), next.lockActive(lock.id())))
                .toList();
        return new RuntimeMoveResult(MoveResult.Kind.PUSHED, next, box, destination, changes, path, shattered);
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
