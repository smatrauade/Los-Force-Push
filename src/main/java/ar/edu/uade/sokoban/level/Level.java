package ar.edu.uade.sokoban.level;

import java.util.Objects;

/** Loaded product; no catalog, metadata or mutable gameplay state yet. */
public record Level(Board board) {
    public Level {
        Objects.requireNonNull(board, "board");
    }
}
