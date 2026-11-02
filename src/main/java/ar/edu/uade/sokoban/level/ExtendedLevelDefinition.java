package ar.edu.uade.sokoban.level;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable Key/Lock/Wall definition. Individual runtime contract; does not
 * ratify OQ-07/08 or change the historical playable parsers.
 */
public record ExtendedLevelDefinition(ExperimentalFragileLevel base,
        List<Key> keys, List<Lock> locks, List<Wall> walls) {
    public record Key(String id, Position position) {
        public Key { validateId(id); Objects.requireNonNull(position, "position"); }
    }
    public record Lock(String id, Position position) {
        public Lock { validateId(id); Objects.requireNonNull(position, "position"); }
    }
    public record Wall(String id, Position position, Set<String> lockIds) {
        public Wall {
            validateId(id);
            Objects.requireNonNull(position, "position");
            lockIds = Set.copyOf(lockIds);
            if (lockIds.isEmpty()) {
                throw new IllegalArgumentException("Wall requires lock association");
            }
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
        keys = List.copyOf(keys);
        locks = List.copyOf(locks);
        walls = List.copyOf(walls);
        Board board = base.spatialTemplate();
        Set<String> boxIds = new HashSet<>();
        Set<Position> occupied = new HashSet<>();
        for (ExperimentalFragileLevel.FragileBox box : base.fragileBoxes()) {
            boxIds.add(box.id());
            occupied.add(box.position());
        }
        for (Key key : keys) {
            checkFloorOrGoal(board, key.position());
            if (!boxIds.add(key.id()) || !occupied.add(key.position())
                    || board.cellAt(key.position()).occupant() != Occupant.EMPTY) {
                throw new IllegalArgumentException("Duplicate or occupied key");
            }
        }
        Set<String> lockIds = new HashSet<>();
        Set<Position> ground = new HashSet<>();
        for (Lock lock : locks) {
            checkFloor(board, lock.position());
            if (!lockIds.add(lock.id()) || !ground.add(lock.position())) {
                throw new IllegalArgumentException("Duplicate lock");
            }
        }
        List<Key> immutableKeys = keys;
        Set<String> wallIds = new HashSet<>();
        for (Wall wall : walls) {
            checkFloor(board, wall.position());
            if (!wallIds.add(wall.id()) || !ground.add(wall.position())
                    || !lockIds.containsAll(wall.lockIds())) {
                throw new IllegalArgumentException("Invalid wall reference or position");
            }
            boolean open = locks.stream().anyMatch(lock -> wall.lockIds().contains(lock.id())
                    && immutableKeys.stream().anyMatch(key -> key.position().equals(lock.position())));
            if (!open && (occupied.contains(wall.position())
                    || board.cellAt(wall.position()).occupant() != Occupant.EMPTY)) {
                throw new IllegalArgumentException("Closed wall occupied");
            }
        }
    }

    private static void checkFloor(Board board, Position position) {
        checkFloorOrGoal(board, position);
        if (board.cellAt(position).terrain() != Terrain.FLOOR) {
            throw new IllegalArgumentException("Special terrain requires floor template");
        }
    }

    private static void checkFloorOrGoal(Board board, Position position) {
        Objects.requireNonNull(position, "position");
        if (position.row() >= board.height() || position.column() >= board.width()
                || board.cellAt(position).terrain() == Terrain.WALL) {
            throw new IllegalArgumentException("Invalid position");
        }
    }
}
