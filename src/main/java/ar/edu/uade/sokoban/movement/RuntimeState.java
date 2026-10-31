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
public record RuntimeState(List<List<Ground>> terrain, Position player, List<Box> boxes,
        List<Lock> locks, List<DynamicWall> walls) {
    public enum Ground { WALL, FLOOR, GOAL, ICE, LOCK, DYNAMIC_WALL }
    public enum BoxKind { NORMAL, FRAGILE, KEY }

    public record Lock(String id, Position position) {
        public Lock { validateId(id); Objects.requireNonNull(position, "position"); }
    }
    public record DynamicWall(String id, Position position, Set<String> lockIds) {
        public DynamicWall {
            validateId(id); Objects.requireNonNull(position, "position");
            lockIds = Set.copyOf(lockIds);
            if (lockIds.isEmpty()) { throw new IllegalArgumentException("Wall requires a lock"); }
            lockIds.forEach(RuntimeState::validateId);
        }
    }
    private static void validateId(String id) {
        if (id == null || !id.matches("[a-z][a-z0-9_]{0,31}")) {
            throw new IllegalArgumentException("Invalid runtime id");
        }
    }
    public RuntimeState(List<List<Ground>> terrain, Position player, List<Box> boxes) {
        this(terrain, player, boxes, List.of(), List.of());
    }

    /** Normal boxes have no identity or resistance; fragile identities are definition-local. */
    public record Box(BoxKind kind, Position position, String id, int resistance) {
        public Box {
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(position, "position");
            if (kind == BoxKind.NORMAL) {
                if (id != null || resistance != 0) {
                    throw new IllegalArgumentException("Normal boxes have no id or resistance");
                }
            } else {
                validateId(id);
                if ((kind == BoxKind.FRAGILE && resistance <= 0)
                        || (kind == BoxKind.KEY && resistance != 0)) {
                    throw new IllegalArgumentException("Invalid box resistance");
                }
            }
        }
        public static Box normal(Position position) { return new Box(BoxKind.NORMAL, position, null, 0); }
        public static Box key(String id, Position position) { return new Box(BoxKind.KEY, position, id, 0); }
        public static Box fragile(String id, Position position, int resistance) {
            return new Box(BoxKind.FRAGILE, position, id, resistance);
        }
    }

    public RuntimeState {
        Objects.requireNonNull(terrain, "terrain");
        Objects.requireNonNull(player, "player");
        terrain = terrain.stream().map(List::copyOf).toList();
        boxes = List.copyOf(boxes);
        locks = List.copyOf(locks);
        walls = List.copyOf(walls);
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
            if (box.kind() != BoxKind.NORMAL && !ids.add(box.id())) {
                throw new IllegalArgumentException("Duplicate box id");
            }
        }
        Set<String> lockIds = new HashSet<>();
        Set<String> wallIds = new HashSet<>();
        Set<Position> specialPositions = new HashSet<>();
        for (Lock lock : locks) {
            if (!lockIds.add(lock.id()) || !specialPositions.add(lock.position())
                    || !isGround(terrain, lock.position(), Ground.LOCK)) {
                throw new IllegalArgumentException("Invalid or duplicate lock");
            }
        }
        for (DynamicWall wall : walls) {
            if (!wallIds.add(wall.id()) || !specialPositions.add(wall.position())
                    || !isGround(terrain, wall.position(), Ground.DYNAMIC_WALL)
                    || !lockIds.containsAll(wall.lockIds())) {
                throw new IllegalArgumentException("Invalid wall or lock reference");
            }
        }
        for (int r = 0; r < terrain.size(); r++) {
            for (int c = 0; c < width; c++) {
                Ground ground = terrain.get(r).get(c);
                if ((ground == Ground.LOCK || ground == Ground.DYNAMIC_WALL)
                        && !specialPositions.contains(new Position(r, c))) {
                    throw new IllegalArgumentException("Special tile requires metadata");
                }
            }
        }
        if (!closuresSafe(player, boxes, locks, walls)) {
            throw new IllegalArgumentException("Closed dynamic wall occupied");
        }
    }

    private static boolean isGround(List<List<Ground>> terrain, Position p, Ground ground) {
        return p.row() < terrain.size() && p.column() < terrain.get(0).size()
                && terrain.get(p.row()).get(p.column()) == ground;
    }
    private static boolean active(Lock lock, List<Box> boxes) {
        return boxes.stream().anyMatch(b -> b.kind() == BoxKind.KEY && b.position().equals(lock.position()));
    }
    private static boolean open(DynamicWall wall, List<Lock> locks, List<Box> boxes) {
        return locks.stream().anyMatch(lock -> wall.lockIds().contains(lock.id()) && active(lock, boxes));
    }
    static boolean closuresSafe(Position player, List<Box> boxes, List<Lock> locks, List<DynamicWall> walls) {
        return walls.stream().noneMatch(w -> !open(w, locks, boxes)
                && (w.position().equals(player) || boxes.stream().anyMatch(b -> b.position().equals(w.position()))));
    }
    public boolean lockActive(String id) {
        return locks.stream().anyMatch(lock -> lock.id().equals(id) && active(lock, boxes));
    }
    public boolean wallOpen(Position position) {
        return walls.stream().anyMatch(w -> w.position().equals(position) && open(w, locks, boxes));
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
