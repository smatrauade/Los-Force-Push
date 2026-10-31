package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Immutable projection of the actual world and session counters. */
public record GamePresentation(RuntimeState state, String levelName, long moves, long pushes,
        int undoRemaining, int historyAvailable) {
    public GamePresentation {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(levelName, "levelName");
    }
}
