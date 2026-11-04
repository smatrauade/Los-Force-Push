package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Position;
import java.util.List;
import java.util.Set;

/** Key/Lock/Wall movement cases independent of Swing. */
public final class DynamicWorldTest {
    private static int passed;
    private static final RuntimeMovementEngine ENGINE = new RuntimeMovementEngine();

    private DynamicWorldTest() { }

    public static void main(String[] args) {
        RuntimeState closed = world(List.of(
                List.of(g(RuntimeState.Ground.FLOOR), g(RuntimeState.Ground.LOCK),
                        g(RuntimeState.Ground.FLOOR), g(RuntimeState.Ground.DYNAMIC_WALL))),
                p(0, 0),
                List.of(RuntimeState.Box.key("k", p(0, 2))),
                List.of(new RuntimeState.Lock("l", p(0, 1))),
                List.of(new RuntimeState.DynamicWall("w", p(0, 3), Set.of("l"))));
        check(!closed.lockActive("l") && !closed.wallOpen(p(0, 3)), "empty lock starts closed");
        RuntimeState onLock = ENGINE.move(closed, Direction.RIGHT).state();
        check(onLock.player().equals(p(0, 1)) && !onLock.lockActive("l"), "player can stand on lock");
        RuntimeState pushedOff = ENGINE.move(
                world(List.of(List.of(g(RuntimeState.Ground.FLOOR), g(RuntimeState.Ground.FLOOR),
                                g(RuntimeState.Ground.LOCK), g(RuntimeState.Ground.FLOOR),
                                g(RuntimeState.Ground.DYNAMIC_WALL))),
                        p(0, 0),
                        List.of(RuntimeState.Box.key("k", p(0, 1))),
                        List.of(new RuntimeState.Lock("l", p(0, 2))),
                        List.of(new RuntimeState.DynamicWall("w", p(0, 4), Set.of("l")))),
                Direction.RIGHT).state();
        check(pushedOff.lockActive("l") && pushedOff.wallOpen(p(0, 4)), "push key onto lock");
        RuntimeState closedAgain = ENGINE.move(pushedOff, Direction.RIGHT).state();
        check(!closedAgain.lockActive("l") && !closedAgain.wallOpen(p(0, 4)), "E06 wall closes");
        check(ENGINE.move(closedAgain, Direction.RIGHT).kind() == MoveResult.Kind.BLOCKED, "closed wall blocks");
        RuntimeState normalOnLock = world(List.of(
                List.of(g(RuntimeState.Ground.FLOOR), g(RuntimeState.Ground.LOCK),
                        g(RuntimeState.Ground.DYNAMIC_WALL))),
                p(0, 0),
                List.of(RuntimeState.Box.normal(p(0, 1))),
                List.of(new RuntimeState.Lock("l", p(0, 1))),
                List.of(new RuntimeState.DynamicWall("w", p(0, 2), Set.of("l"))));
        check(!normalOnLock.lockActive("l") && !normalOnLock.wallOpen(p(0, 2)), "normal box does not open");
        System.out.println("DYNAMIC WORLD RESULT: " + passed + " PASS, 0 FAIL");
    }

    private static RuntimeState.Ground g(RuntimeState.Ground ground) {
        return ground;
    }

    private static RuntimeState world(List<List<RuntimeState.Ground>> terrain, Position player,
            List<RuntimeState.Box> boxes, List<RuntimeState.Lock> locks,
            List<RuntimeState.DynamicWall> walls) {
        return new RuntimeState(terrain, player, boxes, locks, walls);
    }

    private static Position p(int row, int col) {
        return new Position(row, col);
    }

    private static void check(boolean value, String label) {
        if (!value) {
            throw new AssertionError(label);
        }
        passed++;
        System.out.println("PASS " + label);
    }
}
