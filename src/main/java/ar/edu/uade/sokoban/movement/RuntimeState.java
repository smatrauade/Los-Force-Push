package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.ExtendedLevelDefinition;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.level.Terrain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Immutable runtime value for ordinary boxes plus Key/Lock/dynamic walls. */
public record RuntimeState(List<List<Ground>> terrain, Position player, List<Box> boxes,
        List<Lock> locks, List<DynamicWall> walls) {
    public enum Ground { WALL, FLOOR, GOAL, LOCK, DYNAMIC_WALL }
    public enum BoxKind { NORMAL, KEY }

    public record Lock(String id, Position position) {
        public Lock {
            validateId(id);
            Objects.requireNonNull(position, "position");
        }
    }

    public record DynamicWall(String id, Position position, Set<String> lockIds) {
        public DynamicWall {
            validateId(id);
            Objects.requireNonNull(position, "position");
            lockIds = Set.copyOf(lockIds);
            if (lockIds.isEmpty()) {
                throw new IllegalArgumentException("Wall requires a lock");
            }
            lockIds.forEach(RuntimeState::validateId);
        }
    }

    public record Box(BoxKind kind, Position position, String id) {
        public Box {
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(position, "position");
            if (kind == BoxKind.NORMAL) {
                if (id != null) {
                    throw new IllegalArgumentException("Normal boxes have no id");
                }
            } else {
                validateId(id);
            }
        }

        public static Box normal(Position position) {
            return new Box(BoxKind.NORMAL, position, null);
        }

        public static Box key(String id, Position position) {
            return new Box(BoxKind.KEY, position, id);
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
            if (row.size() != width) {
                throw new IllegalArgumentException("Ragged terrain");
            }
        }
        if (!validCell(terrain, player)) {
            throw new IllegalArgumentException("Invalid player position");
        }
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
        for (int row = 0; row < terrain.size(); row++) {
            for (int col = 0; col < width; col++) {
                Ground ground = terrain.get(row).get(col);
                if ((ground == Ground.LOCK || ground == Ground.DYNAMIC_WALL)
                        && !specialPositions.contains(new Position(row, col))) {
                    throw new IllegalArgumentException("Special tile requires metadata");
                }
            }
        }
        if (!closuresSafe(player, boxes, locks, walls)) {
            throw new IllegalArgumentException("Closed dynamic wall occupied");
        }
    }

    private static boolean isGround(List<List<Ground>> terrain, Position position, Ground ground) {
        return position.row() < terrain.size() && position.column() < terrain.get(0).size()
                && terrain.get(position.row()).get(position.column()) == ground;
    }

    private static boolean active(Lock lock, List<Box> boxes) {
        return boxes.stream().anyMatch(box -> box.kind() == BoxKind.KEY
                && box.position().equals(lock.position()));
    }

    private static boolean open(DynamicWall wall, List<Lock> locks, List<Box> boxes) {
        return locks.stream().anyMatch(lock -> wall.lockIds().contains(lock.id()) && active(lock, boxes));
    }

    static boolean closuresSafe(Position player, List<Box> boxes, List<Lock> locks, List<DynamicWall> walls) {
        return walls.stream().noneMatch(wall -> !open(wall, locks, boxes)
                && (wall.position().equals(player)
                || boxes.stream().anyMatch(box -> box.position().equals(wall.position()))));
    }

    public boolean lockActive(String id) {
        return locks.stream().anyMatch(lock -> lock.id().equals(id) && active(lock, boxes));
    }

    public boolean wallOpen(Position position) {
        return walls.stream().anyMatch(wall -> wall.position().equals(position) && open(wall, locks, boxes));
    }

    private static boolean validCell(List<List<Ground>> terrain, Position position) {
        return position.row() < terrain.size() && position.column() < terrain.get(0).size()
                && terrain.get(position.row()).get(position.column()) != Ground.WALL;
    }

    public int height() {
        return terrain.size();
    }

    public int width() {
        return terrain.get(0).size();
    }

    public Box boxAt(Position position) {
        Objects.requireNonNull(position, "position");
        return boxes.stream().filter(box -> box.position().equals(position)).findFirst().orElse(null);
    }

    public static RuntimeState fromLevel(Level level) {
        Objects.requireNonNull(level, "level");
        return fromBoard(level.board());
    }

    public static RuntimeState fromExtended(ExtendedLevelDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        RuntimeState base = fromBoard(definition.base().spatialTemplate());
        List<List<Ground>> nextTerrain = new ArrayList<>();
        for (List<Ground> row : base.terrain()) {
            nextTerrain.add(new ArrayList<>(row));
        }
        List<Lock> nextLocks = definition.locks().stream()
                .map(lock -> new Lock(lock.id(), lock.position())).toList();
        List<DynamicWall> nextWalls = definition.walls().stream()
                .map(wall -> new DynamicWall(wall.id(), wall.position(), wall.lockIds())).toList();
        nextLocks.forEach(lock -> nextTerrain.get(lock.position().row())
                .set(lock.position().column(), Ground.LOCK));
        nextWalls.forEach(wall -> nextTerrain.get(wall.position().row())
                .set(wall.position().column(), Ground.DYNAMIC_WALL));
        List<Box> nextBoxes = new ArrayList<>(base.boxes());
        definition.keys().forEach(key -> nextBoxes.add(Box.key(key.id(), key.position())));
        return new RuntimeState(nextTerrain, base.player(), nextBoxes, nextLocks, nextWalls);
    }

    private static RuntimeState fromBoard(Board board) {
        List<List<Ground>> nextTerrain = new ArrayList<>();
        List<Box> nextBoxes = new ArrayList<>();
        for (int row = 0; row < board.height(); row++) {
            List<Ground> line = new ArrayList<>();
            for (int col = 0; col < board.width(); col++) {
                Cell cell = board.cellAt(new Position(row, col));
                line.add(Ground.valueOf(cell.terrain().name()));
                if (cell.occupant() == Occupant.BOX) {
                    nextBoxes.add(Box.normal(new Position(row, col)));
                }
            }
            nextTerrain.add(line);
        }
        return new RuntimeState(nextTerrain, board.playerPosition(), nextBoxes);
    }

    /** Ordinary spatial projection for HUD counts and historical Board consumers. */
    public Board toBoard() {
        List<List<Cell>> rows = new ArrayList<>();
        for (int row = 0; row < height(); row++) {
            List<Cell> line = new ArrayList<>();
            for (int col = 0; col < width(); col++) {
                Position position = new Position(row, col);
                Ground ground = terrain.get(row).get(col);
                Terrain cellTerrain = switch (ground) {
                    case WALL -> Terrain.WALL;
                    case GOAL -> Terrain.GOAL;
                    case DYNAMIC_WALL -> wallOpen(position) ? Terrain.FLOOR : Terrain.WALL;
                    default -> Terrain.FLOOR;
                };
                Occupant occupant = Occupant.EMPTY;
                if (player.equals(position)) {
                    occupant = Occupant.PLAYER;
                } else if (boxAt(position) != null) {
                    occupant = Occupant.BOX;
                }
                line.add(new Cell(cellTerrain, occupant));
            }
            rows.add(line);
        }
        return new Board(rows);
    }
}
