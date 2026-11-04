package ar.edu.uade.sokoban.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Internal C2-L7 syntax boundary; EXPERIMENTAL_NOT_RATIFIED. Only ICE is admitted so far. */
final class C2LevelParser {
    static final String PREFIX = "!sokoban-txt";
    private static final int MAX_TOKEN_LENGTH = 128; // experimental ASCII-token quota
    private static final Pattern FRAGILE_CELL = Pattern.compile(
            "\\{(floor|goal|ice)\\|fragile:(" + ExperimentalFragileLevel.ID_REGEX + ")\\}");
    private static final Pattern FRAGILE_ATTRIBUTE = Pattern.compile(
            "fragile (" + ExperimentalFragileLevel.ID_REGEX + ") resistance=([1-9][0-9]*)");

    private C2LevelParser() { }

    static Level parse(String text) {
        Lines lines = new Lines(text);
        lines.advance(); // dispatch guarantees a nonempty reserved prefix
        if (!lines.is("!sokoban-txt 2")) {
            throw lines.error("HEADER", "Expected exactly !sokoban-txt 2; no legacy fallback");
        }
        if (!lines.advance() || !lines.is("[grid]")) {
            throw lines.error("SECTION", "Expected [grid] after header");
        }
        List<List<Cell>> grid = new ArrayList<>();
        Map<String, Position> fragileCells = new LinkedHashMap<>();
        int height = 0;
        int width = -1;
        while (lines.advance()) {
            if (lines.is("[attributes]")) {
                if (height == 0) {
                    throw lines.error("DIMENSION", "Grid must contain at least one row");
                }
                return publish(lines, grid, fragileCells);
            }
            if (lines.startsWith("[") || lines.startsWith(PREFIX)) {
                throw lines.error("SECTION", "Unexpected section inside grid");
            }
            if (height == Board.MAX_DIMENSION) {
                throw lines.error("DIMENSION", "Height exceeds " + Board.MAX_DIMENSION);
            }
            List<String> tokens = tokens(lines);
            if (width != -1 && tokens.size() != width) {
                throw lines.error("DIMENSION", "Inconsistent token width at grid row " + (height + 1));
            }
            width = tokens.size();
            List<Cell> row = new ArrayList<>(width);
            for (int column = 0; column < width; column++) {
                String token = tokens.get(column);
                Matcher fragile = FRAGILE_CELL.matcher(token);
                if (!fragile.matches()) {
                    row.add(decode(token, lines, column + 1));
                    continue;
                }
                String id = fragile.group(2);
                if (fragileCells.putIfAbsent(id, new Position(height, column)) != null) {
                    throw lines.error("ATTRIBUTE", "Duplicate fragile id: " + id);
                }
                // Placeholder: the declared resistance only arrives in [attributes].
                row.add(new Cell(terrainOf(fragile.group(1)), Occupant.FRAGILE_BOX, 1));
            }
            grid.add(row);
            height++;
        }
        throw lines.error("SECTION", "Missing [attributes]");
    }

    /** Reads [attributes], applies individual resistance, and only then validates the aggregate. */
    private static Level publish(Lines lines, List<List<Cell>> grid,
                                 Map<String, Position> fragileCells) {
        Map<String, Integer> declared = new HashMap<>();
        while (lines.advance()) {
            if (lines.isEmpty() || lines.startsWith("[")) {
                throw lines.error("SECTION", "[attributes] accepts only attribute records");
            }
            Matcher record = FRAGILE_ATTRIBUTE.matcher(lines.value());
            if (!record.matches()) {
                throw lines.error("UNSUPPORTED_FEATURE", "Unsupported attribute record");
            }
            String id = record.group(1);
            if (!fragileCells.containsKey(id)) {
                throw lines.error("ATTRIBUTE", "Attribute for an absent fragile id: " + id);
            }
            int resistance;
            try {
                resistance = Integer.parseInt(record.group(2));
            } catch (NumberFormatException overflow) {
                throw lines.error("ATTRIBUTE", "Resistance exceeds integer range");
            }
            if (declared.putIfAbsent(id, resistance) != null) {
                throw lines.error("ATTRIBUTE", "Duplicate attribute for fragile id: " + id);
            }
        }
        for (Map.Entry<String, Position> entry : fragileCells.entrySet()) {
            Integer resistance = declared.get(entry.getKey());
            if (resistance == null) {
                throw new LevelFormatException("C2-L7 ATTRIBUTE: fragile "
                        + entry.getKey() + " declares no resistance");
            }
            Position at = entry.getValue();
            Cell placeholder = grid.get(at.row()).get(at.column());
            grid.get(at.row()).set(at.column(),
                    new Cell(placeholder.terrain(), Occupant.FRAGILE_BOX, resistance));
        }
        LevelBuilder builder = new LevelBuilder();
        for (List<Cell> row : grid) {
            builder.addRow(row);
        }
        return builder.build(); // validate and publish only after the entire envelope
    }

    private static Terrain terrainOf(String name) {
        return switch (name) {
            case "goal" -> Terrain.GOAL;
            case "ice" -> Terrain.ICE;
            default -> Terrain.FLOOR;
        };
    }

    private static List<String> tokens(Lines lines) {
        if (lines.isEmpty()) {
            throw lines.error("DIMENSION", "Empty grid row");
        }
        List<String> result = new ArrayList<>();
        int start = lines.start;
        for (int index = start; index < lines.end; index++) {
            char symbol = lines.text.charAt(index);
            if (symbol == ' ') {
                if (index == start || index + 1 == lines.end) {
                    throw lines.error("SEPARATOR", "Expected one ASCII space between tokens");
                }
                addToken(result, lines, start, index);
                start = index + 1;
            } else {
                if (symbol == '\t' || symbol == '\r') {
                    throw lines.error("SEPARATOR", "Tab or isolated CR in grid row");
                }
                if (symbol < '!' || symbol > '~') {
                    throw lines.error("TOKEN", "Only printable ASCII is allowed in C2-L7 tokens");
                }
                if (index - start >= MAX_TOKEN_LENGTH) {
                    throw lines.error("TOKEN_LIMIT", "Token exceeds " + MAX_TOKEN_LENGTH + " characters");
                }
            }
        }
        addToken(result, lines, start, lines.end);
        return result;
    }

    private static void addToken(List<String> tokens, Lines lines, int start, int end) {
        if (tokens.size() == Board.MAX_DIMENSION) {
            throw lines.error("DIMENSION", "Width exceeds " + Board.MAX_DIMENSION + " cells");
        }
        tokens.add(lines.text.substring(start, end)); // length already bounded before allocation
    }

    private static Cell decode(String token, Lines lines, int column) {
        return switch (token) {
            case "#", "{wall|empty}" -> new Cell(Terrain.WALL, Occupant.EMPTY);
            case "_", "{floor|empty}" -> new Cell(Terrain.FLOOR, Occupant.EMPTY);
            case ".", "{goal|empty}" -> new Cell(Terrain.GOAL, Occupant.EMPTY);
            case "@", "{floor|player}" -> new Cell(Terrain.FLOOR, Occupant.PLAYER);
            case "$", "{floor|box}" -> new Cell(Terrain.FLOOR, Occupant.BOX);
            case "+", "{goal|player}" -> new Cell(Terrain.GOAL, Occupant.PLAYER);
            case "*", "{goal|box}" -> new Cell(Terrain.GOAL, Occupant.BOX);
            case "{ice|empty}" -> new Cell(Terrain.ICE, Occupant.EMPTY);
            case "{ice|box}" -> new Cell(Terrain.ICE, Occupant.BOX);
            case "{ice|player}" -> new Cell(Terrain.ICE, Occupant.PLAYER);
            default -> {
                if (token.startsWith("{") && !token.endsWith("}")) {
                    throw lines.error("TOKEN", "Unclosed token at cell " + column);
                }
                if (token.startsWith("{lock:")
                        || token.startsWith("{wall:") || token.contains("|fragile:")
                        || token.contains("|key:")) {
                    throw lines.error("UNSUPPORTED_FEATURE", "Special entity at cell " + column);
                }
                throw lines.error("TOKEN", "Unsupported combination at cell " + column + ": " + token);
            }
        };
    }

    /** Per-call line cursor: no split of the full input or unbounded line copies. */
    private static final class Lines {
        private final String text;
        private int next;
        private int start;
        private int end;
        private int number;

        private Lines(String text) {
            this.text = text;
        }

        private boolean advance() {
            if (next == text.length()) {
                return false;
            }
            start = next;
            int newline = text.indexOf('\n', start);
            end = newline < 0 ? text.length() : newline;
            next = newline < 0 ? text.length() : newline + 1;
            if (newline >= 0 && end > start && text.charAt(end - 1) == '\r') {
                end--;
            }
            number++;
            return true;
        }

        private boolean is(String expected) {
            return end - start == expected.length() && text.startsWith(expected, start);
        }

        private boolean startsWith(String prefix) {
            return end - start >= prefix.length() && text.startsWith(prefix, start);
        }

        private boolean isEmpty() {
            return start == end;
        }

        private String value() {
            return text.substring(start, end);
        }

        private LevelFormatException error(String code, String message) {
            return new LevelFormatException("C2-L7 " + code + " at line " + number + ": " + message);
        }
    }
}
