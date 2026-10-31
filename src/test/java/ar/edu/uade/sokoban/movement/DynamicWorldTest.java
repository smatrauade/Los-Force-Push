package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Golden transitions and construction checks for the individual dynamic world. */
public final class DynamicWorldTest {
    private static final RuntimeMovementEngine ENGINE = new RuntimeMovementEngine();
    private static int passed;
    private DynamicWorldTest() { }
    public static void main(String[] args) {
        RuntimeState initial = world(List.of("FFLFF", "FFDFF"), p(0, 0),
                List.of(RuntimeState.Box.key("key", p(0, 1))), List.of(lock("l", 0, 2)),
                List.of(wall("w", 1, 2, "l")));
        check(!initial.lockActive("l") && !initial.wallOpen(p(1, 2)), "initial closed");
        RuntimeMoveResult activate = ENGINE.move(initial, Direction.RIGHT);
        RuntimeState active = activate.state();
        check(activate.kind() == MoveResult.Kind.PUSHED && active.lockActive("l")
                && active.wallOpen(p(1, 2)) && active.boxAt(p(0, 2)).id().equals("key")
                && activate.lockChanges().equals(List.of(new RuntimeMoveResult.LockChange("l", true))), "key opens wall");
        check(initial.boxAt(p(0, 1)).kind() == RuntimeState.BoxKind.KEY && !initial.lockActive("l"), "previous immutable");
        check(activate.equals(ENGINE.move(initial, Direction.RIGHT)), "deterministic result");
        RuntimeMoveResult leave = ENGINE.move(active, Direction.RIGHT);
        check(!leave.state().lockActive("l") && !leave.state().wallOpen(p(1, 2))
                && leave.lockChanges().equals(List.of(new RuntimeMoveResult.LockChange("l", false)))
                && leave.state().boxAt(p(0, 3)).id().equals("key"), "leave closes wall");
        check(ENGINE.move(active, Direction.DOWN).state().player().equals(p(1, 1)), "walk with active lock");
        RuntimeState onOpenWall = ENGINE.move(ENGINE.move(active, Direction.DOWN).state(), Direction.RIGHT).state();
        check(onOpenWall.player().equals(p(1, 2)), "player crosses open wall");
        RuntimeState cycle = world(List.of("FFLFF", "FFDFF", "FFFFF"), p(0, 0),
                List.of(RuntimeState.Box.key("k", p(0, 1))), List.of(lock("l", 0, 2)),
                List.of(wall("w", 1, 2, "l")));
        cycle = ENGINE.move(ENGINE.move(cycle, Direction.RIGHT).state(), Direction.RIGHT).state();
        for (Direction d : List.of(Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT,
                Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.UP)) {
            cycle = ENGINE.move(cycle, d).state();
        }
        RuntimeMoveResult reopen = ENGINE.move(cycle, Direction.LEFT);
        check(reopen.state().lockActive("l") && reopen.state().wallOpen(p(1, 2))
                && reopen.lockChanges().equals(List.of(new RuntimeMoveResult.LockChange("l", true))),
                "repeated opening after closure");
        RuntimeState chainKey = world(List.of("FFFF"), p(0, 0),
                List.of(RuntimeState.Box.key("k", p(0, 1)), RuntimeState.Box.normal(p(0, 2))), List.of(), List.of());
        check(ENGINE.move(chainKey, Direction.RIGHT).state() == chainKey, "key cannot push chain");
        RuntimeState closedPlayer = world(List.of("FLD"), p(0, 1), List.of(), List.of(lock("l", 0, 1)),
                List.of(wall("w", 0, 2, "l")));
        check(ENGINE.move(closedPlayer, Direction.RIGHT).state() == closedPlayer, "closed wall blocks player");
        RuntimeState closedBox = world(List.of("FFDL"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1))),
                List.of(lock("l", 0, 3)), List.of(wall("w", 0, 2, "l")));
        check(ENGINE.move(closedBox, Direction.RIGHT).state() == closedBox, "closed wall blocks box");
        RuntimeState lockWalk = world(List.of("FLD"), p(0, 0), List.of(), List.of(lock("l", 0, 1)),
                List.of(wall("w", 0, 2, "l")));
        check(ENGINE.move(lockWalk, Direction.RIGHT).state().player().equals(p(0, 1)), "player crosses lock");
        RuntimeState normal = world(List.of("FFLFD"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1))),
                List.of(lock("l", 0, 2)), List.of(wall("w", 0, 4, "l")));
        RuntimeMoveResult normalMove = ENGINE.move(normal, Direction.RIGHT);
        check(!normalMove.state().lockActive("l") && normalMove.lockChanges().isEmpty(), "normal does not activate");
        RuntimeState fragile = world(List.of("FFLFD"), p(0, 0), List.of(RuntimeState.Box.fragile("f", p(0, 1), 2)),
                List.of(lock("l", 0, 2)), List.of(wall("w", 0, 4, "l")));
        check(!ENGINE.move(fragile, Direction.RIGHT).state().lockActive("l")
                && ENGINE.move(fragile, Direction.RIGHT).state().boxAt(p(0, 2)).resistance() == 1, "fragile does not activate");
        RuntimeState slide = world(List.of("FFIILFD"), p(0, 0), List.of(RuntimeState.Box.key("k", p(0, 1))),
                List.of(lock("l", 0, 4)), List.of(wall("w", 0, 6, "l")));
        RuntimeMoveResult slideMove = ENGINE.move(slide, Direction.RIGHT);
        check(slideMove.boxDistance() == 3 && slideMove.state().lockActive("l")
                && slideMove.state().boxAt(p(0, 4)).id().equals("k"), "key slides onto lock");
        RuntimeState exitIce = world(List.of("FLIIFD"), p(0, 0), List.of(RuntimeState.Box.key("k", p(0, 1))),
                List.of(lock("l", 0, 1)), List.of(wall("w", 0, 5, "l")));
        RuntimeMoveResult exitMove = ENGINE.move(exitIce, Direction.RIGHT);
        check(exitMove.boxDestination().equals(p(0, 4)) && !exitMove.state().wallOpen(p(0, 5))
                && exitMove.lockChanges().size() == 1, "key leaves lock over ice");
        RuntimeState blockedPlayer = world(List.of("FDLFF"), p(0, 1), List.of(RuntimeState.Box.key("k", p(0, 2))),
                List.of(lock("l", 0, 2)), List.of(wall("w", 0, 1, "l")));
        check(ENGINE.move(blockedPlayer, Direction.RIGHT).state() != blockedPlayer,
                "player vacates wall before closure");
        RuntimeState closurePlayer = world(List.of("FFFF", "FLFF", "FDFF"), p(2, 1), List.of(RuntimeState.Box.key("k", p(1, 1))),
                List.of(lock("l", 1, 1)), List.of(wall("w", 2, 1, "l")));
        check(ENGINE.move(closurePlayer, Direction.UP).state() != closurePlayer,
                "player leaves wall when pushing key");
        RuntimeState closureBox = world(List.of("FLFF", "FDFF"), p(0, 0),
                List.of(RuntimeState.Box.key("k", p(0, 1)), RuntimeState.Box.normal(p(1, 1))),
                List.of(lock("l", 0, 1)), List.of(wall("w", 1, 1, "l")));
        RuntimeMoveResult rejected = ENGINE.move(closureBox, Direction.RIGHT);
        check(rejected.kind() == MoveResult.Kind.BLOCKED && rejected.state() == closureBox
                && rejected.lockChanges().isEmpty(), "occupied wall box rejects closure atomically");
        RuntimeState closurePlayerBlocked = world(List.of("FLF", "FDF"), p(1, 1),
                List.of(RuntimeState.Box.key("k", p(0, 1))), List.of(lock("l", 0, 1)),
                List.of(wall("w", 1, 1, "l")));
        // The engine has one player: a push necessarily moves it onto the key's old square.
        // A closing wall at that square cannot also be a Lock; direct invalid construction covers the invariant.
        check(closurePlayerBlocked.wallOpen(p(1, 1))
                && !RuntimeState.closuresSafe(p(1, 1), List.of(RuntimeState.Box.key("k", p(0, 2))),
                        closurePlayerBlocked.locks(), closurePlayerBlocked.walls()),
                "occupied wall player prevents closure");
        RuntimeState multi = world(List.of("FLFLF", "FDFDF"), p(0, 0),
                List.of(RuntimeState.Box.key("a", p(0, 1)), RuntimeState.Box.key("b", p(0, 3))),
                List.of(lock("l1", 0, 1), lock("l2", 0, 3)),
                List.of(wall("w1", 1, 1, "l1", "l2"), wall("w2", 1, 3, "l1")));
        check(multi.wallOpen(p(1, 1)) && multi.wallOpen(p(1, 3)), "multiple locks and walls initially open");
        RuntimeMoveResult oneOff = ENGINE.move(multi, Direction.RIGHT);
        check(oneOff.state().wallOpen(p(1, 1)) && !oneOff.state().wallOpen(p(1, 3))
                && oneOff.lockChanges().equals(List.of(new RuntimeMoveResult.LockChange("l1", false))), "any active lock keeps shared wall open");
        RuntimeState slideClosed = world(List.of("FFIIDL"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1))),
                List.of(lock("l", 0, 5)), List.of(wall("w", 0, 4, "l")));
        check(ENGINE.move(slideClosed, Direction.RIGHT).boxDestination().equals(p(0, 3)), "ice stops at closed wall");
        RuntimeState slideOpen = world(List.of("FFIIDF", "LFFFFF"), p(0, 0),
                List.of(RuntimeState.Box.normal(p(0, 1)), RuntimeState.Box.key("k", p(1, 0))),
                List.of(lock("l", 1, 0)), List.of(wall("w", 0, 4, "l")));
        check(ENGINE.move(slideOpen, Direction.RIGHT).boxDestination().equals(p(0, 4)), "ice enters open wall");
        RuntimeState fragileWall = world(List.of("FFIIDL"), p(0, 0), List.of(RuntimeState.Box.fragile("f", p(0, 1), 1)),
                List.of(lock("l", 0, 5)), List.of(wall("w", 0, 4, "l")));
        RuntimeMoveResult shattered = ENGINE.move(fragileWall, Direction.RIGHT);
        check(shattered.broken() && shattered.boxDestination().equals(p(0, 3)) && shattered.state().boxes().isEmpty(), "fragile breaks after ice before wall");
        invalid(() -> world(List.of("FLD"), p(0, 2), List.of(), List.of(lock("l", 0, 1)), List.of(wall("w", 0, 2, "l"))), "closed occupied by player");
        invalid(() -> world(List.of("FLD"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 2))), List.of(lock("l", 0, 1)), List.of(wall("w", 0, 2, "l"))), "closed occupied by box");
        invalid(() -> world(List.of("FLD"), p(0, 0), List.of(), List.of(lock("l", 0, 1)), List.of(wall("w", 0, 2, "missing"))), "missing lock reference");
        invalid(() -> world(List.of("FLLD"), p(0, 0), List.of(), List.of(lock("l", 0, 1), lock("l", 0, 2)), List.of(wall("w", 0, 3, "l"))), "duplicate lock ids");
        invalid(() -> world(List.of("FLDD"), p(0, 0), List.of(), List.of(lock("l", 0, 1)), List.of(wall("w", 0, 2, "l"), wall("w", 0, 3, "l"))), "duplicate wall ids");
        invalid(() -> world(List.of("FLD"), p(0, 0), List.of(), List.of(lock("l", 3, 1)), List.of(wall("w", 0, 2, "l"))), "lock out of bounds");
        invalid(() -> world(List.of("FLD"), p(0, 0), List.of(), List.of(), List.of()), "missing tile metadata");
        invalid(() -> world(List.of("FFFF"), p(0, 0), List.of(RuntimeState.Box.key("a", p(0, 1)), RuntimeState.Box.fragile("a", p(0, 2), 2)), List.of(), List.of()), "duplicate box ids");
        invalid(() -> world(List.of("FLD", "FF"), p(0, 0), List.of(),
                List.of(lock("l", 0, 1)), List.of(wall("w", 0, 2, "l"))), "ragged dimensions");
        invalid(() -> new RuntimeState.DynamicWall("w", p(0, 0), Set.of()), "empty association");
        check(new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR)), p(0, 0), List.of()).walls().isEmpty(), "legacy constructor");
        System.out.println("DYNAMIC RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static RuntimeState world(List<String> rows, Position player, List<RuntimeState.Box> boxes,
            List<RuntimeState.Lock> locks, List<RuntimeState.DynamicWall> walls) {
        List<List<RuntimeState.Ground>> terrain = new ArrayList<>();
        for (String row : rows) {
            List<RuntimeState.Ground> converted = new ArrayList<>();
            for (char c : row.toCharArray()) {
                converted.add(switch (c) { case 'L' -> RuntimeState.Ground.LOCK; case 'D' -> RuntimeState.Ground.DYNAMIC_WALL;
                    case 'I' -> RuntimeState.Ground.ICE; case 'W' -> RuntimeState.Ground.WALL;
                    case 'F' -> RuntimeState.Ground.FLOOR; default -> throw new IllegalArgumentException(); });
            }
            terrain.add(converted);
        }
        return new RuntimeState(terrain, player, boxes, locks, walls);
    }
    private static RuntimeState.Lock lock(String id, int r, int c) { return new RuntimeState.Lock(id, p(r, c)); }
    private static RuntimeState.DynamicWall wall(String id, int r, int c, String... locks) {
        return new RuntimeState.DynamicWall(id, p(r, c), Set.of(locks));
    }
    private static Position p(int r, int c) { return new Position(r, c); }
    private static void check(boolean condition, String label) {
        if (!condition) { throw new AssertionError(label); }
        passed++; System.out.println("PASS " + label);
    }
    private static void invalid(Runnable action, String label) {
        try { action.run(); } catch (IllegalArgumentException expected) { check(true, label); return; }
        throw new AssertionError("Expected rejection: " + label);
    }
}
