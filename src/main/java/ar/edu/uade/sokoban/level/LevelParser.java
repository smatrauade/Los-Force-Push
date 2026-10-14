package ar.edu.uade.sokoban.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Stateless interpreter of the explicitly experimental seven-symbol grammar. */
public final class LevelParser {
    public Level parse(String text) {
        Objects.requireNonNull(text, "text");
        if (text.isEmpty()) {
            throw new LevelFormatException("Level text is empty");
        }
        LevelBuilder builder = new LevelBuilder();
        int start = 0;
        int rowIndex = 0;
        int expectedWidth = -1;
        while (start < text.length()) {
            // Bound work before decoding cells; Board still protects direct construction.
            if (rowIndex == Board.MAX_DIMENSION) {
                throw new LevelFormatException("Height exceeds " + Board.MAX_DIMENSION);
            }
            int newline = text.indexOf('\n', start);
            int end = newline < 0 ? text.length() : newline;
            // Strip only the CR belonging to CRLF; an isolated CR stays an unknown symbol.
            if (newline >= 0 && end > start && text.charAt(end - 1) == '\r') {
                end--;
            }
            int width = end - start;
            if (width == 0 || width > Board.MAX_DIMENSION) {
                throw new LevelFormatException("Width must be between 1 and "
                        + Board.MAX_DIMENSION + " at row " + (rowIndex + 1));
            }
            if (expectedWidth != -1 && width != expectedWidth) {
                throw new LevelFormatException("Inconsistent width at row " + (rowIndex + 1));
            }
            expectedWidth = width;
            List<Cell> row = new ArrayList<>(width);
            for (int c = 0; c < width; c++) {
                row.add(decode(text.charAt(start + c), rowIndex, c));
            }
            builder.addRow(row);
            rowIndex++;
            if (newline < 0) {
                break;
            }
            start = newline + 1; // a single terminal LF/CRLF creates no extra row
        }
        return builder.build();
    }

    private Cell decode(char symbol, int row, int column) {
        return switch (symbol) {
            case '#' -> new Cell(Terrain.WALL, Occupant.EMPTY);
            case ' ' -> new Cell(Terrain.FLOOR, Occupant.EMPTY);
            case '.' -> new Cell(Terrain.GOAL, Occupant.EMPTY);
            case '@' -> new Cell(Terrain.FLOOR, Occupant.PLAYER);
            case '$' -> new Cell(Terrain.FLOOR, Occupant.BOX);
            case '+' -> new Cell(Terrain.GOAL, Occupant.PLAYER);
            case '*' -> new Cell(Terrain.GOAL, Occupant.BOX);
            default -> throw new LevelFormatException(String.format(Locale.ROOT,
                    "Unknown symbol U+%04X at row %d, column %d",
                    (int) symbol, row + 1, column + 1));
        };
    }
}
