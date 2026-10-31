package ar.edu.uade.sokoban.desktop;

import java.util.Objects;

/** Display-only DTO for the future victory/score module. Does not calculate score. */
public record LevelResult(String levelName, long moves, long pushes, long undoUses, long score) {
    public LevelResult {
        Objects.requireNonNull(levelName, "levelName");
        if (moves < 0 || pushes < 0 || undoUses < 0) {
            throw new IllegalArgumentException("Las estadisticas no pueden ser negativas");
        }
    }
}
