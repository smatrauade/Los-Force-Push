package ar.edu.uade.sokoban.level;

import java.util.Objects;

/** Resistance is meaningful only for FRAGILE_BOX; every other occupant carries zero. */
public record Cell(Terrain terrain, Occupant occupant, int resistance) {
    public Cell {
        Objects.requireNonNull(terrain, "terrain");
        Objects.requireNonNull(occupant, "occupant");
        if (terrain == Terrain.WALL && occupant != Occupant.EMPTY) {
            throw new IllegalArgumentException("A wall cannot contain an occupant");
        }
        if (occupant == Occupant.FRAGILE_BOX) {
            if (resistance <= 0) {
                throw new IllegalArgumentException("A fragile box needs positive resistance");
            }
        } else if (resistance != 0) {
            throw new IllegalArgumentException("Only a fragile box carries resistance");
        }
    }

    /** Occupants without resistance; keeps every existing call site compiling. */
    public Cell(Terrain terrain, Occupant occupant) {
        this(terrain, occupant, 0);
    }

    /** A pushed fragile box loses one point; at zero it shatters and the cell is left empty. */
    public static Occupant afterPush(Occupant occupant, int resistance) {
        return occupant == Occupant.FRAGILE_BOX && resistance - 1 <= 0
                ? Occupant.EMPTY : occupant;
    }
}
