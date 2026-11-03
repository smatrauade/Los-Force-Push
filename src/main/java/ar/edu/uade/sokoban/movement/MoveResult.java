package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Position;
import java.util.List;
import java.util.Objects;

/** Transition outcome and immutable resulting state; no counters or scoring. */
public record MoveResult(Kind kind, Board board, List<Position> boxPath) {
    public enum Kind {
        MOVED, PUSHED, BLOCKED
    }

    public MoveResult {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(board, "board");
        boxPath = List.copyOf(boxPath);   // inmutable, y falla si es null
    }

    /** Resultados sin trayectoria; mantiene compilando a los llamadores existentes. */
    public MoveResult(Kind kind, Board board) {
        this(kind, board, List.of());
    }
}
