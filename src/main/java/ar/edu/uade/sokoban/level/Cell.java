package ar.edu.uade.sokoban.level;

import java.util.Objects;

public record Cell(Terrain terrain, Occupant occupant) {
    public Cell {
        Objects.requireNonNull(terrain, "terrain");
        Objects.requireNonNull(occupant, "occupant");
        if (terrain == Terrain.WALL && occupant != Occupant.EMPTY) {
            throw new IllegalArgumentException("A wall cannot contain an occupant");
        }
    }
}
