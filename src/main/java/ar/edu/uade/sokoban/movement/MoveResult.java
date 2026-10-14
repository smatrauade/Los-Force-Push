package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Board;
import java.util.Objects;

/** Transition outcome and immutable resulting state; no counters or scoring. */
public record MoveResult(Kind kind, Board board) {
    public enum Kind {
        MOVED, PUSHED, BLOCKED
    }

    public MoveResult {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(board, "board");
    }
}
