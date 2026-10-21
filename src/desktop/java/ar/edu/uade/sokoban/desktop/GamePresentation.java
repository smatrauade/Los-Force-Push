package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import java.util.Objects;

/** Read-only projection; the Board reference is the core's immutable value. */
public record GamePresentation(Board board, String levelName, long moves, long pushes) {
    public GamePresentation {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(levelName, "levelName");
    }
}
