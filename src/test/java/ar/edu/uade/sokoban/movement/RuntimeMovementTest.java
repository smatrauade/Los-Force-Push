package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.ExperimentalFragileLevel;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.level.Position;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Runtime integration and golden transitions; assertions run without -ea. */
public final class RuntimeMovementTest {
    private static final RuntimeMovementEngine ENGINE = new RuntimeMovementEngine();
    private static int passed;
    private RuntimeMovementTest() { }
    public static void main(String[] args) throws Exception {
        Path fixtures = Path.of(args[0]);
        LevelLoader loader = new LevelLoader(new LevelParser());
        ExperimentalFragileLevel definition = loader.loadExperimentalFragile(fixtures.resolve("experimental-fragile.txt"));
        RuntimeState state = RuntimeState.fromDefinition(definition);
        check(state.boxes().size() == 3 && state.boxAt(p(1, 2)).id().equals("f1")
                && state.boxAt(p(1, 2)).resistance() == 3 && state.boxAt(p(1, 3)).id().equals("f2")
                && state.boxAt(p(1, 3)).resistance() == 12
                && state.boxAt(p(1, 4)).kind() == RuntimeState.BoxKind.NORMAL
                && state.terrain().get(1).get(3) == RuntimeState.Ground.GOAL, "TXT full definition");
        check(definition.fragileBoxes().size() == 2 && definition.spatialTemplate().boxCount() == 1,
                "definition unchanged");
        RuntimeState ordinary = RuntimeState.fromLevel(loader.load(fixtures.resolve("basic.txt")));
        check(ordinary.boxes().size() == 1, "legacy adapter");
        RuntimeState c2 = RuntimeState.fromDefinition(new ar.edu.uade.sokoban.level.ExperimentalFragileParser()
                .parse("!sokoban-txt 2\n[grid]\n@ *\n[attributes]\n"));
        check(c2.boxes().size() == 1 && c2.terrain().get(0).get(1) == RuntimeState.Ground.GOAL,
                "ordinary C2 adapter");
        RuntimeState normalGoal = line("FFG", RuntimeState.Box.normal(p(0, 1)));
        RuntimeMoveResult normalPush = ENGINE.move(normalGoal, Direction.RIGHT);
        check(normalPush.state().boxAt(p(0, 2)).kind() == RuntimeState.BoxKind.NORMAL
                && normalPush.state().terrain().get(0).get(2) == RuntimeState.Ground.GOAL, "normal push onto goal");
        RuntimeState normalExit = line("FFIIF", RuntimeState.Box.normal(p(0, 1)));
        check(ENGINE.move(normalExit, Direction.RIGHT).boxDestination().equals(p(0, 4)), "normal ice exit");
        RuntimeState iceEdge = line("FFII", RuntimeState.Box.normal(p(0, 1)));
        check(ENGINE.move(iceEdge, Direction.RIGHT).boxDestination().equals(p(0, 3)), "ice edge stop");
        RuntimeState noPull = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR)), p(0, 1), List.of(RuntimeState.Box.normal(p(0, 0))));
        check(ENGINE.move(noPull, Direction.RIGHT).state().boxAt(p(0, 0)) != null, "no pulling");
        RuntimeState f = line("FIIIIG", RuntimeState.Box.fragile("f1", p(0, 1), 2));
        RuntimeMoveResult slide = ENGINE.move(f, Direction.RIGHT);
        check(slide.kind() == MoveResult.Kind.PUSHED && slide.boxDistance() == 4
                && slide.boxDestination().equals(p(0, 5)) && slide.state().boxAt(p(0, 5)).resistance() == 1
                && slide.state().boxAt(p(0, 5)).id().equals("f1"), "fragile ice exit and identity");
        check(f.boxAt(p(0, 1)).resistance() == 2 && f.player().equals(p(0, 0)), "input immutable");
        check(slide.equals(ENGINE.move(f, Direction.RIGHT)), "deterministic transitions");
        RuntimeState breakIce = line("FIIIIG", RuntimeState.Box.fragile("a", p(0, 1), 1));
        RuntimeMoveResult broken = ENGINE.move(breakIce, Direction.RIGHT);
        check(broken.broken() && broken.boxDistance() == 4 && broken.state().boxes().isEmpty()
                && broken.state().terrain().get(0).get(5) == RuntimeState.Ground.GOAL, "break after slide on goal");
        RuntimeState breakFloor = line("FFG", RuntimeState.Box.fragile("a", p(0, 1), 1));
        check(ENGINE.move(breakFloor, Direction.RIGHT).broken()
                && ENGINE.move(breakFloor, Direction.RIGHT).state().boxes().isEmpty(), "break without ice");
        RuntimeState wall = line("FIIW", RuntimeState.Box.normal(p(0, 1)));
        check(ENGINE.move(wall, Direction.RIGHT).boxDestination().equals(p(0, 2)), "ice wall stop");
        RuntimeState boxStop = line("FIIIF", RuntimeState.Box.normal(p(0, 1)), RuntimeState.Box.normal(p(0, 3)));
        check(ENGINE.move(boxStop, Direction.RIGHT).boxDestination().equals(p(0, 2)), "ice box stop");
        RuntimeState blocked = line("FFW", RuntimeState.Box.fragile("a", p(0, 1), 1));
        RuntimeMoveResult blockedResult = ENGINE.move(blocked, Direction.RIGHT);
        check(blockedResult.kind() == MoveResult.Kind.BLOCKED && blockedResult.state() == blocked
                && blocked.boxAt(p(0, 1)).resistance() == 1, "blocked push atomic");
        RuntimeState chain = line("FFFF", RuntimeState.Box.normal(p(0, 1)), RuntimeState.Box.normal(p(0, 2)));
        check(ENGINE.move(chain, Direction.RIGHT).state() == chain, "no chains");
        RuntimeState playerIce = line("FIF");
        check(ENGINE.move(playerIce, Direction.RIGHT).state().player().equals(p(0, 1)), "player no slide");
        check(ENGINE.move(playerIce, Direction.LEFT).state() == playerIce, "boundary blocked");
        RuntimeState left = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR)), p(0, 1), List.of());
        check(ENGINE.move(left, Direction.LEFT).state().player().equals(p(0, 0)), "left");
        RuntimeState vertical = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR), List.of(RuntimeState.Ground.FLOOR)), p(1, 0), List.of());
        RuntimeState up = ENGINE.move(vertical, Direction.UP).state();
        check(up.player().equals(p(0, 0)) && ENGINE.move(up, Direction.DOWN).state().equals(vertical), "up and down");
        invalid(() -> new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR)), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 0)))), "overlap");
        invalid(() -> new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR)), p(1, 0), List.of()), "out of bounds");
        invalid(() -> new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR)), p(0, 0), List.of(RuntimeState.Box.fragile("a", p(0, 1), 1), RuntimeState.Box.fragile("a", p(0, 2), 2))), "duplicate ids");
        invalid(() -> RuntimeState.Box.fragile("a", p(0, 1), 0), "zero resistance");
        List<RuntimeState.Ground> mutable = new ArrayList<>(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.ICE));
        List<RuntimeState.Box> mutableBoxes = new ArrayList<>();
        RuntimeState snapshot = new RuntimeState(List.of(mutable), p(0, 0), mutableBoxes);
        mutable.set(1, RuntimeState.Ground.WALL); mutableBoxes.add(RuntimeState.Box.normal(p(0, 1)));
        check(snapshot.terrain().get(0).get(1) == RuntimeState.Ground.ICE && snapshot.boxes().isEmpty(), "defensive copies");
        System.out.println("RUNTIME RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static RuntimeState line(String cells, RuntimeState.Box... boxes) {
        List<RuntimeState.Ground> row = new ArrayList<>();
        for (char ch : cells.toCharArray()) {
            row.add(switch (ch) { case 'I' -> RuntimeState.Ground.ICE; case 'W' -> RuntimeState.Ground.WALL;
                case 'G' -> RuntimeState.Ground.GOAL; case 'F', '.' -> RuntimeState.Ground.FLOOR;
                default -> throw new IllegalArgumentException(); });
        }
        return new RuntimeState(List.of(row), p(0, 0), List.of(boxes));
    }
    private static Position p(int r, int c) { return new Position(r, c); }
    private static void check(boolean ok, String label) {
        if (!ok) { throw new AssertionError(label); }
        passed++; System.out.println("PASS " + label);
    }
    private static void invalid(Runnable action, String label) {
        try { action.run(); } catch (IllegalArgumentException expected) { check(true, label); return; }
        throw new AssertionError("Expected rejection: " + label);
    }
}
