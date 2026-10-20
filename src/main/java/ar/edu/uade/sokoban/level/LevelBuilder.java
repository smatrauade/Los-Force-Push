package ar.edu.uade.sokoban.level;

import java.util.ArrayList;
import java.util.List;

/** Per-load assembly boundary, deliberately internal and concrete. */
final class LevelBuilder {
    private final List<List<Cell>> rows = new ArrayList<>();
    private boolean published;

    void addRow(List<Cell> row) {
        ensureOpen();
        if (rows.size() == Board.MAX_DIMENSION) {
            throw new LevelFormatException("Height exceeds " + Board.MAX_DIMENSION);
        }
        rows.add(List.copyOf(row));
    }

    Level build() {
        ensureOpen();
        Level result = new Level(new Board(rows));
        published = true;
        return result;
    }

    private void ensureOpen() {
        if (published) {
            throw new IllegalStateException("Builder already published its level");
        }
    }
}
