package ar.edu.uade.sokoban.level;

/** Zero-based coordinates; board bounds are checked by Board. */
public record Position(int row, int column) {
    public Position {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Position coordinates must be nonnegative");
        }
    }
}
