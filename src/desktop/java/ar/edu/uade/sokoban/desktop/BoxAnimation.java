package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.List;
import java.util.Objects;

/**
 * Presentation-only projection of the authoritative box trajectory. It keeps
 * the pushed box metadata and never recalculates movement or terrain.
 */
public record BoxAnimation(RuntimeState.Box box, List<Position> path, boolean shattered) {
    public BoxAnimation {
        Objects.requireNonNull(box, "box");
        path = List.copyOf(path);
        if (path.isEmpty()) {
            throw new IllegalArgumentException("A box animation requires a trajectory");
        }
        if (shattered && box.kind() != RuntimeState.BoxKind.FRAGILE) {
            throw new IllegalArgumentException("Only Fragile boxes can shatter");
        }
    }

    public static BoxAnimation from(RuntimeMoveResult result) {
        Objects.requireNonNull(result, "result");
        if (result.kind() != MoveResult.Kind.PUSHED || result.pushedBox() == null) {
            throw new IllegalArgumentException("A box animation requires an authoritative push");
        }
        return new BoxAnimation(result.pushedBox(), result.boxPath(), result.boxShattered());
    }

    public Position destination() {
        return path.get(path.size() - 1);
    }

    /** A single-cell push has no slide; only ICE or a Fragile break needs frames. */
    public boolean needsFrames() {
        return path.size() > 1 || shattered;
    }
}
