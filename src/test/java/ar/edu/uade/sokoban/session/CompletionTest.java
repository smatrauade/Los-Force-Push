package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class CompletionTest {
    private static final VictoryEvaluator EVAL = new VictoryEvaluator();
    private static int passed;
    private CompletionTest() { }
    public static void main(String[] args) throws Exception {
        Path dir = Path.of(args[0]);
        RuntimeState solved = state(List.of("FG"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1))));
        check(EVAL.evaluate(solved).won(), "normal solved");
        check(!EVAL.evaluate(state(List.of("FF"), p(0, 0), List.of())).won(), "no goals and no boxes");
        check(!EVAL.evaluate(state(List.of("FF"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1))))).won(), "no goal");
        check(!EVAL.evaluate(state(List.of("FG"), p(0, 0), List.of())).won(), "no box");
        check(!EVAL.evaluate(state(List.of("FGG"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1))))).won(), "extra goal");
        check(!EVAL.evaluate(state(List.of("FGF"), p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1)), RuntimeState.Box.normal(p(0, 2))))).won(), "extra box");
        check(EVAL.evaluate(state(List.of("FG"), p(0, 0), List.of(RuntimeState.Box.fragile("f", p(0, 1), 1)))).won(), "fragile goal");
        check(EVAL.evaluate(state(List.of("FG"), p(0, 0), List.of(RuntimeState.Box.key("k", p(0, 1))))).won(), "key goal");
        RuntimeState lock = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK,
                RuntimeState.Ground.GOAL, RuntimeState.Ground.DYNAMIC_WALL)), p(0, 0),
                List.of(RuntimeState.Box.key("k", p(0, 1))), List.of(new RuntimeState.Lock("l", p(0, 1))),
                List.of(new RuntimeState.DynamicWall("w", p(0, 3), Set.of("l"))));
        check(lock.wallOpen(p(0, 3)) && !EVAL.evaluate(lock).won(), "E05 lock does not satisfy goal");
        check(!EVAL.evaluate(state(List.of("FG"), p(0, 0), List.of())).won(), "broken fragile cannot satisfy goal");
        check(EVAL.evaluate(solved).equals(EVAL.evaluate(solved)), "pure repeatable victory");
        WeightedPenaltyScoreStrategy score = new WeightedPenaltyScoreStrategy();
        check(score.calculate(new LevelStats("a", 0, 0, 0)) == 10_000, "score base");
        check(score.calculate(new LevelStats("a", 4, 2, 1)) == 9810, "score three penalties");
        check(score.calculate(new LevelStats("a", 1000, 0, 0)) == 0, "score zero floor");
        check(score.calculate(new LevelStats("a", Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE)) == 0, "score overflow safe");
        check(new WeightedPenaltyScoreStrategy(Long.MAX_VALUE, Long.MAX_VALUE, 0, 0)
                .calculate(new LevelStats("a", 1, 0, 0)) == 0, "maximum weights safe");
        check(new WeightedPenaltyScoreStrategy(17, 0, 0, 0).calculate(new LevelStats("a", Long.MAX_VALUE, 0, 0)) == 17, "zero penalties");
        invalid(() -> new WeightedPenaltyScoreStrategy(1, -1, 0, 0), "negative weight rejected");
        invalid(() -> new LevelStats("a", 1, 2, 0), "invalid stats rejected");
        LevelCatalog.Entry a = new LevelCatalog.Entry("a", dir.resolve("completion-a.txt"), LevelCatalog.Format.STANDARD);
        LevelCatalog.Entry b = new LevelCatalog.Entry("b", dir.resolve("completion-b.txt"), LevelCatalog.Format.EXTENDED);
        LevelCatalog catalog = new LevelCatalog(List.of(a, b));
        check(catalog.nextAfter("a").orElseThrow().equals(b) && catalog.nextAfter("b").isEmpty(), "ordered catalog");
        invalid(() -> catalog.nextAfter("missing"), "unknown catalog id");
        invalid(() -> new LevelCatalog(List.of(a, a)), "duplicate catalog id");
        invalid(() -> new LevelCatalog.Entry("BAD", Path.of("x"), LevelCatalog.Format.STANDARD), "invalid catalog id");
        invalid(() -> new LevelCatalog(List.of()), "empty catalog");
        LevelProgression game = new LevelProgression(catalog, score);
        check(!game.victory().won() && game.finish().isEmpty()
                && game.advance().status() == LevelProgression.AdvanceStatus.NOT_FINALIZED, "cannot finish or advance early");
        check(game.move(Direction.RIGHT).kind() == MoveResult.Kind.PUSHED && game.victory().won(), "fixture A solved");
        LevelProgression.LevelResult first = game.finish().orElseThrow();
        check(first.score() == 9965 && first.stats().moves() == 1 && first.stats().pushes() == 1, "first result score");
        check(game.finish().orElseThrow() == first && game.results().size() == 1, "finish idempotent");
        invalidState(() -> game.move(Direction.LEFT), "move frozen after finish");
        invalidState(game::restart, "restart frozen after finish");
        invalidState(game::undoFive, "undo frozen after finish");
        check(game.advance().status() == LevelProgression.AdvanceStatus.ADVANCED && game.levelId().equals("b")
                && game.metrics().moves() == 0 && game.results().size() == 1 && !game.isFinalized(), "advance resets session");
        check(!game.victory().won() && game.currentState().boxAt(p(1, 3)).id().equals("k"), "fixture B initial");
        game.move(Direction.RIGHT);
        check(game.currentState().lockActive("l") && game.currentState().wallOpen(p(2, 4)) && !game.victory().won(), "key activates lock but is not winner");
        for (Direction d : List.of(Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT, Direction.RIGHT,
                Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.LEFT)) {
            check(game.move(d).kind() != MoveResult.Kind.BLOCKED, "fixture B valid step");
        }
        check(game.victory().won() && !game.currentState().lockActive("l")
                && game.currentState().boxAt(p(1, 2)).kind() == RuntimeState.BoxKind.KEY, "fixture B key reaches goal");
        LevelProgression.LevelResult second = game.finish().orElseThrow();
        check(second.score() == 9815 && second.stats().moves() == 11 && second.stats().pushes() == 3
                && game.results().size() == 2 && game.campaignCompleted(), "second result and campaign");
        check(game.advance().status() == LevelProgression.AdvanceStatus.CAMPAIGN_COMPLETED
                && game.results().size() == 2, "end idempotent");
        LevelProgression alternate = new LevelProgression(new LevelCatalog(List.of(a)), ignored -> 7);
        alternate.move(Direction.RIGHT);
        check(alternate.finish().orElseThrow().score() == 7, "alternative strategy injected");
        LevelProgression undoDirect = new LevelProgression(new LevelCatalog(List.of(a)), score);
        for (Direction d : List.of(Direction.RIGHT, Direction.LEFT, Direction.RIGHT, Direction.LEFT, Direction.RIGHT)) { undoDirect.move(d); }
        undoDirect.undoFive(); undoDirect.move(Direction.RIGHT);
        check(undoDirect.finish().orElseThrow().score() == 9865, "undo score uses restored trajectory and accumulated uses");
        LevelProgression undo = new LevelProgression(new LevelCatalog(List.of(a)), score);
        for (Direction d : List.of(Direction.RIGHT, Direction.LEFT, Direction.RIGHT, Direction.LEFT, Direction.RIGHT)) { undo.move(d); }
        check(undo.victory().won() && undo.undoFive() == GameSession.UndoResult.RESTORED && !undo.victory().won()
                && undo.metrics().undoUsesTotal() == 1 && undo.metrics().moves() == 0, "undo removes derived victory");
        undo.restart(); undo.move(Direction.RIGHT);
        check(undo.finish().orElseThrow().score() == 9865, "restart preserves undo penalty");
        Path nextFile = Files.createTempFile("sokoban-next-", ".txt");
        try {
            Files.delete(nextFile);
            LevelCatalog badCatalog = new LevelCatalog(List.of(a, new LevelCatalog.Entry("bad", nextFile, LevelCatalog.Format.EXTENDED)));
            LevelProgression failure = new LevelProgression(badCatalog, score);
            failure.move(Direction.RIGHT); LevelProgression.LevelResult retained = failure.finish().orElseThrow();
            check(failure.advance().status() == LevelProgression.AdvanceStatus.LOAD_FAILED
                    && failure.levelId().equals("a") && failure.results().equals(List.of(retained)), "missing next atomic");
            Files.writeString(nextFile, "bad", StandardCharsets.UTF_8);
            check(failure.advance().status() == LevelProgression.AdvanceStatus.LOAD_FAILED && failure.isFinalized(), "invalid next atomic");
            Files.write(nextFile, new byte[] {(byte) 0xc3, (byte) 0x28});
            check(failure.advance().status() == LevelProgression.AdvanceStatus.LOAD_FAILED, "invalid UTF-8 next");
            Files.write(nextFile, new byte[65_537]);
            check(failure.advance().status() == LevelProgression.AdvanceStatus.LOAD_FAILED, "oversize next");
            Files.copy(dir.resolve("completion-a.txt"), nextFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            check(failure.advance().status() == LevelProgression.AdvanceStatus.LOAD_FAILED, "wrong explicit format");
            Files.copy(dir.resolve("completion-b.txt"), nextFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            check(failure.advance().status() == LevelProgression.AdvanceStatus.ADVANCED && failure.results().size() == 1,
                    "repair next and preserve results");
        } finally { Files.deleteIfExists(nextFile); }
        LevelLoader loader = new LevelLoader(new LevelParser());
        check(EVAL.evaluate(RuntimeState.fromLevel(loader.load(dir.resolve("completion-a.txt")))).boxes() == 1, "external initial oracle");
        System.out.println("COMPLETION RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static RuntimeState state(List<String> rows, Position player, List<RuntimeState.Box> boxes) {
        List<List<RuntimeState.Ground>> terrain = new ArrayList<>();
        for (String row : rows) {
            List<RuntimeState.Ground> line = new ArrayList<>();
            for (char c : row.toCharArray()) { line.add(c == 'G' ? RuntimeState.Ground.GOAL : RuntimeState.Ground.FLOOR); }
            terrain.add(line);
        }
        return new RuntimeState(terrain, player, boxes);
    }
    private static Position p(int row, int col) { return new Position(row, col); }
    private static void invalid(Runnable action, String label) {
        try { action.run(); } catch (IllegalArgumentException expected) { check(true, label); return; }
        throw new AssertionError("Expected invalid: " + label);
    }
    private static void invalidState(Runnable action, String label) {
        try { action.run(); } catch (IllegalStateException expected) { check(true, label); return; }
        throw new AssertionError("Expected frozen: " + label);
    }
    private static void check(boolean value, String label) {
        if (!value) { throw new AssertionError(label); }
        passed++; System.out.println("PASS " + label);
    }
}
