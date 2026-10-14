package ar.edu.uade.sokoban.level;

import java.util.List;
import java.util.Objects;

/** Immutable validated aggregate, equally usable with direct construction. */
public record Board(List<List<Cell>> rows) {
    public static final int MAX_DIMENSION = 100;

    public Board {
        Objects.requireNonNull(rows, "rows");
        if (rows.isEmpty() || rows.size() > MAX_DIMENSION) {
            throw new LevelFormatException("Height must be between 1 and " + MAX_DIMENSION);
        }
        rows = rows.stream().map(List::copyOf).toList();
        int width = rows.get(0).size();
        if (width == 0 || width > MAX_DIMENSION) {
            throw new LevelFormatException("Width must be between 1 and " + MAX_DIMENSION);
        }
        int players = 0;
        for (int r = 0; r < rows.size(); r++) {
            if (rows.get(r).size() != width) {
                throw new LevelFormatException("Inconsistent width at row " + (r + 1));
            }
            for (Cell cell : rows.get(r)) {
                if (cell.occupant() == Occupant.PLAYER) {
                    players++;
                }
            }
        }
        if (players != 1) {
            throw new LevelFormatException("Expected exactly one player; found " + players);
        }
    }

    public int height() {
        return rows.size();
    }

    public int width() {
        return rows.get(0).size();
    }

    public Cell cellAt(Position position) {
        Objects.requireNonNull(position, "position");
        if (position.row() >= height() || position.column() >= width()) {
            throw new IndexOutOfBoundsException("Position outside board: " + position);
        }
        return rows.get(position.row()).get(position.column());
    }

    public Position playerPosition() {
        for (int r = 0; r < height(); r++) {
            for (int c = 0; c < width(); c++) {
                if (rows.get(r).get(c).occupant() == Occupant.PLAYER) {
                    return new Position(r, c);
                }
            }
        }
        throw new IllegalStateException("Validated board lost its player");
    }

    public long boxCount() {
        return rows.stream().flatMap(List::stream)
                .filter(cell -> cell.occupant() == Occupant.BOX).count();
    }

    public long goalCount() {
        return rows.stream().flatMap(List::stream)
                .filter(cell -> cell.terrain() == Terrain.GOAL).count();
    }
}
