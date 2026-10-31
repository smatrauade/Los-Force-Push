package ar.edu.uade.sokoban.level;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Individual C2 extension; immutable initial definition, independent of movement. */
public record ExtendedLevelDefinition(ExperimentalFragileLevel base, Set<Position> ice,
        List<Key> keys, List<Lock> locks, List<Wall> walls) {
    public record Key(String id, Position position) {
        public Key { validateId(id); Objects.requireNonNull(position, "position"); }
    }
    public record Lock(String id, Position position) {
        public Lock { validateId(id); Objects.requireNonNull(position, "position"); }
    }
    public record Wall(String id, Position position, Set<String> lockIds) {
        public Wall {
            validateId(id); Objects.requireNonNull(position, "position");
            lockIds = Set.copyOf(lockIds);
            if (lockIds.isEmpty()) { throw new IllegalArgumentException("Wall requires lock association"); }
            lockIds.forEach(ExtendedLevelDefinition::validateId);
        }
    }
    private static void validateId(String id) {
        if (id == null || !id.matches(ExperimentalFragileLevel.ID_REGEX)) {
            throw new IllegalArgumentException("Invalid id");
        }
    }
    public ExtendedLevelDefinition {
        Objects.requireNonNull(base, "base");
        ice = Set.copyOf(ice); keys = List.copyOf(keys); locks = List.copyOf(locks); walls = List.copyOf(walls);
        Board board = base.spatialTemplate();
        Set<Position> ground = new HashSet<>();
        for (Position p : ice) { checkFloor(board, p); if (!ground.add(p)) { throw new IllegalArgumentException("Duplicate terrain"); } }
        Set<String> boxIds = new HashSet<>();
        Set<Position> occupied = new HashSet<>();
        for (ExperimentalFragileLevel.FragileBox box : base.fragileBoxes()) {
            boxIds.add(box.id()); occupied.add(box.position());
        }
        for (Key key : keys) {
            checkFloorOrGoal(board, key.position());
            if (!boxIds.add(key.id()) || !occupied.add(key.position())
                    || board.cellAt(key.position()).occupant() != Occupant.EMPTY) {
                throw new IllegalArgumentException("Duplicate or occupied key");
            }
        }
        Set<String> lockIds = new HashSet<>();
        for (Lock lock : locks) {
            checkFloor(board, lock.position());
            if (!lockIds.add(lock.id()) || !ground.add(lock.position())) { throw new IllegalArgumentException("Duplicate lock"); }
        }
        List<Key> immutableKeys = keys;
        Set<String> wallIds = new HashSet<>();
        for (Wall wall : walls) {
            checkFloor(board, wall.position());
            if (!wallIds.add(wall.id()) || !ground.add(wall.position()) || !lockIds.containsAll(wall.lockIds())) {
                throw new IllegalArgumentException("Invalid wall reference or position");
            }
            boolean open = locks.stream().anyMatch(l -> wall.lockIds().contains(l.id())
                    && immutableKeys.stream().anyMatch(k -> k.position().equals(l.position())));
            if (!open && (occupied.contains(wall.position())
                    || board.cellAt(wall.position()).occupant() != Occupant.EMPTY)) {
                throw new IllegalArgumentException("Closed wall occupied");
            }
        }
    }
    private static void checkFloor(Board board, Position p) {
        checkFloorOrGoal(board, p);
        if (board.cellAt(p).terrain() != Terrain.FLOOR) { throw new IllegalArgumentException("Special terrain requires floor template"); }
    }
    private static void checkFloorOrGoal(Board board, Position p) {
        Objects.requireNonNull(p, "position");
        if (p.row() >= board.height() || p.column() >= board.width()
                || board.cellAt(p).terrain() == Terrain.WALL) { throw new IllegalArgumentException("Invalid position"); }
    }
}
