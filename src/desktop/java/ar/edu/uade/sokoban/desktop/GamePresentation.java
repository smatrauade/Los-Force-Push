package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Read-only projection of the runtime world and session counters. */
public record GamePresentation(RuntimeState state, String levelName, long moves, long pushes) {
    public GamePresentation {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(levelName, "levelName");
    }

    public Board board() {
        return state.toBoard();
    }
}
