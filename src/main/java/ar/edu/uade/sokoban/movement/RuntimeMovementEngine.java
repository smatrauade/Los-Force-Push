package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Atomic, stateless movement over a fully typed runtime state. */
public final class RuntimeMovementEngine {
    public RuntimeMoveResult move(RuntimeState state, Direction direction) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(direction, "direction");
        Position target = step(state, state.player(), direction);
        if (target == null || state.terrain().get(target.row()).get(target.column()) == RuntimeState.Ground.WALL) {
            return simple(MoveResult.Kind.BLOCKED, state);
        }
        RuntimeState.Box box = state.boxAt(target);
        if (box == null) {
            return simple(MoveResult.Kind.MOVED, new RuntimeState(state.terrain(), target, state.boxes()));
        }
        Position destination = step(state, target, direction);
        if (!free(state, destination)) { return simple(MoveResult.Kind.BLOCKED, state); }
        int distance = 1;
        while (state.terrain().get(destination.row()).get(destination.column()) == RuntimeState.Ground.ICE) {
            Position next = step(state, destination, direction);
            if (!free(state, next)) { break; }
            destination = next;
            distance++;
        }
        boolean broken = box.kind() == RuntimeState.BoxKind.FRAGILE && box.resistance() == 1;
        List<RuntimeState.Box> boxes = new ArrayList<>(state.boxes());
        int index = boxes.indexOf(box);
        if (broken) {
            boxes.remove(index);
        } else if (box.kind() == RuntimeState.BoxKind.FRAGILE) {
            boxes.set(index, RuntimeState.Box.fragile(box.id(), destination, box.resistance() - 1));
        } else {
            boxes.set(index, RuntimeState.Box.normal(destination));
        }
        RuntimeState next = new RuntimeState(state.terrain(), target, boxes);
        return new RuntimeMoveResult(MoveResult.Kind.PUSHED, next, box, destination, distance, broken);
    }
    private RuntimeMoveResult simple(MoveResult.Kind kind, RuntimeState state) {
        return new RuntimeMoveResult(kind, state, null, null, 0, false);
    }
    private boolean free(RuntimeState state, Position p) {
        return p != null && state.terrain().get(p.row()).get(p.column()) != RuntimeState.Ground.WALL
                && state.boxAt(p) == null && !state.player().equals(p);
    }
    private Position step(RuntimeState state, Position p, Direction direction) {
        int row = p.row() + direction.rowDelta();
        int col = p.column() + direction.columnDelta();
        return row < 0 || col < 0 || row >= state.height() || col >= state.width()
                ? null : new Position(row, col);
    }
}
