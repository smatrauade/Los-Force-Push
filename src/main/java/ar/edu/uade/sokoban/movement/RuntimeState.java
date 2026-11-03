package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.ExtendedLevelDefinition;
import ar.edu.uade.sokoban.level.ExperimentalFragileLevel;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.level.Terrain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Immutable playable runtime. Fragile identity and resistance never depend on cell coordinates. */
public record RuntimeState(List<List<Ground>> terrain, Position player, List<Box> boxes,
        List<Lock> locks, List<DynamicWall> walls) {
    public enum Ground { WALL, FLOOR, GOAL, ICE, LOCK, DYNAMIC_WALL }
    public enum BoxKind { NORMAL, KEY, FRAGILE }

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

    public record Box(BoxKind kind, Position position, String id, int resistance) {
        public Box {
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(position, "position");
            if (kind == BoxKind.NORMAL) {
                if (id != null || resistance != 0) {
                    throw new IllegalArgumentException("Normal boxes have neither id nor resistance");
                }
            } else {
                validateId(id);
                if (kind == BoxKind.KEY && resistance != 0) {
                    throw new IllegalArgumentException("Key boxes have no resistance");
                }
                if (kind == BoxKind.FRAGILE && resistance <= 0) {
                    throw new IllegalArgumentException("Fragile boxes require positive resistance");
                }
            }
        }

        /** Compatibility constructor for the historical normal/key box API. */
        public Box(BoxKind kind, Position position, String id) {
            this(kind, position, id, 0);
        }

        public static Box normal(Position position) {
            return new Box(BoxKind.NORMAL, position, null, 0);
        }

        public static Box key(String id, Position position) {
            return new Box(BoxKind.KEY, position, id, 0);
        }

        public static Box fragile(String id, Position position, int resistance) {
            return new Box(BoxKind.FRAGILE, position, id, resistance);
        }

        /** Relocation is an immutable value operation, not a mutation of a session. */
        public Box movedTo(Position destination) {
            return new Box(kind, destination, id, resistance);
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

    /** Explicit opt-in adapter: preserve each D-020 Fragile id/resistance from its definition. */
    public static RuntimeState fromExperimentalFragile(ExperimentalFragileLevel definition) {
        Objects.requireNonNull(definition, "definition");
        RuntimeState base = fromBoard(definition.spatialTemplate());
        List<Box> next = new ArrayList<>(base.boxes());
        for (ExperimentalFragileLevel.FragileBox fragile : definition.fragileBoxes()) {
            next.add(Box.fragile(fragile.id(), fragile.position(), fragile.initialResistance()));
        }
        return new RuntimeState(base.terrain(), base.player(), next);
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
                } else if (cell.occupant() == Occupant.FRAGILE_BOX) {
                    // Board/Level cells have resistance, but no D-020 id.
                    throw new IllegalArgumentException("Fragile requires an identity-preserving definition");
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
                    case ICE -> Terrain.ICE;
                    case DYNAMIC_WALL -> wallOpen(position) ? Terrain.FLOOR : Terrain.WALL;
                    default -> Terrain.FLOOR;
                };
                Occupant occupant = Occupant.EMPTY;
                if (player.equals(position)) {
                    occupant = Occupant.PLAYER;
                } else if (boxAt(position) != null) {
                    Box box = boxAt(position);
                    occupant = box.kind() == BoxKind.FRAGILE ? Occupant.FRAGILE_BOX : Occupant.BOX;
                }
                Box box = boxAt(position);
                int resistance = box != null && box.kind() == BoxKind.FRAGILE ? box.resistance() : 0;
                line.add(new Cell(cellTerrain, occupant, resistance));
            }
            rows.add(line);
        }
        return new Board(rows);
    }
}
