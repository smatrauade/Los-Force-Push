package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.ExperimentalFragileLevel;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Immutable runtime value; terrain and typed boxes have a single source of occupancy. */
public record RuntimeState(List<List<Ground>> terrain, Position player, List<Box> boxes) {
    public enum Ground { WALL, FLOOR, GOAL, ICE }
    public enum BoxKind { NORMAL, FRAGILE }

    /** Normal boxes have no identity or resistance; fragile identities are definition-local. */
    public record Box(BoxKind kind, Position position, String id, int resistance) {
        public Box {
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(position, "position");
            if (kind == BoxKind.NORMAL) {
                if (id != null || resistance != 0) {
                    throw new IllegalArgumentException("Normal boxes have no id or resistance");
                }
            } else if (id == null || !id.matches("[a-z][a-z0-9_]{0,31}") || resistance <= 0) {
                throw new IllegalArgumentException("Invalid fragile id or resistance");
            }
        }
        public static Box normal(Position position) { return new Box(BoxKind.NORMAL, position, null, 0); }
        public static Box fragile(String id, Position position, int resistance) {
            return new Box(BoxKind.FRAGILE, position, id, resistance);
        }
    }

    public RuntimeState {
        Objects.requireNonNull(terrain, "terrain");
        Objects.requireNonNull(player, "player");
        terrain = terrain.stream().map(List::copyOf).toList();
        boxes = List.copyOf(boxes);
        if (terrain.isEmpty() || terrain.size() > Board.MAX_DIMENSION || terrain.get(0).isEmpty()
                || terrain.get(0).size() > Board.MAX_DIMENSION) {
            throw new IllegalArgumentException("Invalid terrain dimensions");
        }
        int width = terrain.get(0).size();
        for (List<Ground> row : terrain) {
            if (row.size() != width) { throw new IllegalArgumentException("Ragged terrain"); }
        }
        if (!validCell(terrain, player)) { throw new IllegalArgumentException("Invalid player position"); }
        Set<Position> occupied = new HashSet<>();
        Set<String> ids = new HashSet<>();
        occupied.add(player);
        for (Box box : boxes) {
            if (!validCell(terrain, box.position()) || !occupied.add(box.position())) {
                throw new IllegalArgumentException("Invalid or overlapping box position");
            }
            if (box.kind() == BoxKind.FRAGILE && !ids.add(box.id())) {
                throw new IllegalArgumentException("Duplicate fragile id");
            }
        }
    }

    private static boolean validCell(List<List<Ground>> terrain, Position p) {
        return p.row() < terrain.size() && p.column() < terrain.get(0).size()
                && terrain.get(p.row()).get(p.column()) != Ground.WALL;
    }

    public int height() { return terrain.size(); }
    public int width() { return terrain.get(0).size(); }
    public Box boxAt(Position position) {
        Objects.requireNonNull(position, "position");
        return boxes.stream().filter(b -> b.position().equals(position)).findFirst().orElse(null);
    }
    public static RuntimeState fromLevel(Level level) {
        Objects.requireNonNull(level, "level");
        return fromBoard(level.board(), List.of());
    }
    public static RuntimeState fromDefinition(ExperimentalFragileLevel definition) {
        Objects.requireNonNull(definition, "definition");
        List<Box> fragile = definition.fragileBoxes().stream()
                .map(b -> Box.fragile(b.id(), b.position(), b.initialResistance())).toList();
        return fromBoard(definition.spatialTemplate(), fragile);
    }
    private static RuntimeState fromBoard(Board board, List<Box> fragile) {
        List<List<Ground>> terrain = new ArrayList<>();
        List<Box> boxes = new ArrayList<>(fragile);
        for (int r = 0; r < board.height(); r++) {
            List<Ground> row = new ArrayList<>();
            for (int c = 0; c < board.width(); c++) {
                Cell cell = board.cellAt(new Position(r, c));
                row.add(Ground.valueOf(cell.terrain().name()));
                if (cell.occupant() == Occupant.BOX) { boxes.add(Box.normal(new Position(r, c))); }
            }
            terrain.add(row);
        }
        return new RuntimeState(terrain, board.playerPosition(), boxes);
    }
}
