package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.level.Terrain;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Golden boards and invariant checks; assertions do not depend on -ea. */
public final class MovementTest {
    private final MovementEngine engine = new MovementEngine();
    private final Path fixtures;
    private int passed;
    private int failed;

    private MovementTest(Path fixtures) {
        this.fixtures = fixtures;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected fixture directory argument");
        }
        MovementTest tests = new MovementTest(Path.of(args[0]));
        tests.execute();
        System.out.println("MOVEMENT RESULT: " + tests.passed + " PASS, " + tests.failed + " FAIL");
        if (tests.failed != 0) {
            throw new AssertionError("Movement tests failed");
        }
    }

    private void execute() {
        test("01 UP onto floor", () -> checkMove("   \n @ \n   ", Direction.UP,
                MoveResult.Kind.MOVED, " @ \n   \n   "));
        test("02 DOWN onto floor", () -> checkMove("   \n @ \n   ", Direction.DOWN,
                MoveResult.Kind.MOVED, "   \n   \n @ "));
        test("03 LEFT onto floor", () -> checkMove("   \n @ \n   ", Direction.LEFT,
                MoveResult.Kind.MOVED, "   \n@  \n   "));
        test("04 RIGHT onto floor", () -> checkMove("   \n @ \n   ", Direction.RIGHT,
                MoveResult.Kind.MOVED, "   \n  @\n   "));
        test("05 enter free goal", () -> checkMove("@.", Direction.RIGHT,
                MoveResult.Kind.MOVED, " +"));
        test("06 walls block every direction", () -> {
            for (Direction direction : Direction.values()) {
                checkMove("###\n#@#\n###", direction, MoveResult.Kind.BLOCKED, "###\n#@#\n###");
            }
        });
        test("07 single-cell edges block every direction", () -> {
            for (Direction direction : Direction.values()) {
                checkMove("@", direction, MoveResult.Kind.BLOCKED, "@");
            }
        });
        test("08 push RIGHT onto floor", () -> checkMove("@$ ", Direction.RIGHT,
                MoveResult.Kind.PUSHED, " @$"));
        test("09 push onto goal", () -> checkMove("@$.", Direction.RIGHT,
                MoveResult.Kind.PUSHED, " @*"));
        test("10 push LEFT onto floor", () -> checkMove(" $@", Direction.LEFT,
                MoveResult.Kind.PUSHED, "$@ "));
        test("11 push UP onto floor", () -> checkMove(" \n$\n@", Direction.UP,
                MoveResult.Kind.PUSHED, "$\n@\n "));
        test("12 push DOWN onto floor", () -> checkMove("@\n$\n ", Direction.DOWN,
                MoveResult.Kind.PUSHED, " \n@\n$"));
        test("13 push against wall blocked in every direction", () -> {
            checkMove("@$#", Direction.RIGHT, MoveResult.Kind.BLOCKED, "@$#");
            checkMove("#$@", Direction.LEFT, MoveResult.Kind.BLOCKED, "#$@");
            checkMove("@\n$\n#", Direction.DOWN, MoveResult.Kind.BLOCKED, "@\n$\n#");
            checkMove("#\n$\n@", Direction.UP, MoveResult.Kind.BLOCKED, "#\n$\n@");
        });
        test("14 push beyond edge blocked in every direction", () -> {
            checkMove("@$", Direction.RIGHT, MoveResult.Kind.BLOCKED, "@$");
            checkMove("$@", Direction.LEFT, MoveResult.Kind.BLOCKED, "$@");
            checkMove("@\n$", Direction.DOWN, MoveResult.Kind.BLOCKED, "@\n$");
            checkMove("$\n@", Direction.UP, MoveResult.Kind.BLOCKED, "$\n@");
        });
        test("15 cannot push two boxes", () -> {
            checkMove("@$$ ", Direction.RIGHT, MoveResult.Kind.BLOCKED, "@$$ ");
            checkMove(" $$@", Direction.LEFT, MoveResult.Kind.BLOCKED, " $$@");
            checkMove("@\n$\n$\n ", Direction.DOWN, MoveResult.Kind.BLOCKED, "@\n$\n$\n ");
            checkMove(" \n$\n$\n@", Direction.UP, MoveResult.Kind.BLOCKED, " \n$\n$\n@");
        });
        test("16 walking away does not pull a box", () -> {
            checkMove(" $@ ", Direction.RIGHT, MoveResult.Kind.MOVED, " $ @");
            checkMove(" @$ ", Direction.LEFT, MoveResult.Kind.MOVED, "@ $ ");
        });
        test("17 player leaves goal without erasing it", () -> checkMove("+ ", Direction.RIGHT,
                MoveResult.Kind.MOVED, ".@"));
        test("18 box leaves goal; player occupies that goal", () -> checkMove("@* ", Direction.RIGHT,
                MoveResult.Kind.PUSHED, " +$"));
        test("19 leave and reenter a goal previously occupied by a box", () -> {
            Board first = board("@*  ");
            Board pushed = engine.move(first, Direction.RIGHT).board();
            MoveResult left = engine.move(pushed, Direction.LEFT);
            check(left.kind() == MoveResult.Kind.MOVED && left.board().equals(board("@.$ ")),
                    "leaving goal erased terrain");
            MoveResult right = engine.move(left.board(), Direction.RIGHT);
            check(right.kind() == MoveResult.Kind.MOVED && right.board().equals(pushed),
                    "reentering previously occupied goal");
            invariants(first, right.board());
        });
        test("20 push across occupied and free goals", () -> checkMove(" +*.", Direction.RIGHT,
                MoveResult.Kind.PUSHED, " .+*"));
        test("21 transition never changes the previous state or loaded Level", () -> {
            Level level = new Level(board(" @ "));
            Board initial = level.board();
            int originalHash = initial.hashCode();
            MoveResult result = engine.move(initial, Direction.RIGHT);
            check(result.board().equals(board("  @")), "next state");
            check(level.board() == initial && initial.equals(board(" @ "))
                    && originalHash == initial.hashCode(), "previous state changed");
        });
        test("22 independent sequences from a shared initial state", () -> {
            Board initial = board("  @  ");
            Board left = engine.move(engine.move(initial, Direction.LEFT).board(), Direction.LEFT).board();
            Board right = engine.move(engine.move(initial, Direction.RIGHT).board(), Direction.RIGHT).board();
            check(left.equals(board("@    ")) && right.equals(board("    @")), "sequences leaked");
            check(initial.equals(board("  @  ")), "initial state changed");
            invariants(initial, left);
            invariants(initial, right);
        });
        test("23 repeated sequence is deterministic with exact outcomes", () -> {
            Board initial = board("#######\n# @$. #\n#     #\n#######");
            List<Direction> inputs = List.of(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT,
                    Direction.DOWN, Direction.LEFT, Direction.UP);
            List<MoveResult> first = sequence(initial, inputs);
            List<MoveResult> second = sequence(initial, inputs);
            check(first.equals(second), "nondeterministic results");
            check(first.stream().map(MoveResult::kind).toList().equals(List.of(
                    MoveResult.Kind.PUSHED, MoveResult.Kind.PUSHED, MoveResult.Kind.BLOCKED,
                    MoveResult.Kind.MOVED, MoveResult.Kind.MOVED, MoveResult.Kind.MOVED)), "outcomes");
            check(first.get(5).board().equals(board("#######\n#  @.$#\n#     #\n#######")), "final board");
            check(initial.equals(board("#######\n# @$. #\n#     #\n#######")), "initial board changed");
        });
        test("24 external fixture -> Level -> initial Board -> movement", () -> {
            Level level = new LevelLoader(new LevelParser()).load(fixtures.resolve("basic.txt"));
            Board initial = level.board();
            MoveResult pushed = engine.move(initial, Direction.RIGHT);
            check(pushed.kind() == MoveResult.Kind.PUSHED
                    && pushed.board().equals(board("#####\n# @*#\n#####")), "external integration");
            MoveResult blocked = engine.move(pushed.board(), Direction.RIGHT);
            check(blocked.kind() == MoveResult.Kind.BLOCKED && blocked.board() == pushed.board(),
                    "wall after external load");
            check(initial.equals(board("#####\n#@$.#\n#####")), "loaded initial state changed");
            invariants(initial, pushed.board());
        });
        test("25 edit external UTF-8 file before moving; no hardcoded state", () -> {
            Path directory = Files.createTempDirectory("movement-tests-");
            Path file = directory.resolve("nivel ñ con espacios.txt");
            try {
                LevelLoader loader = new LevelLoader(new LevelParser());
                Files.writeString(file, "@$. \r\n", StandardCharsets.UTF_8);
                Level first = loader.load(file);
                check(engine.move(first.board(), Direction.RIGHT).kind() == MoveResult.Kind.PUSHED,
                        "first external layout");
                Files.writeString(file, "@$# \n", StandardCharsets.UTF_8);
                Level second = loader.load(file);
                check(engine.move(second.board(), Direction.RIGHT).kind() == MoveResult.Kind.BLOCKED,
                        "modified external layout ignored");
                check(first.board().equals(board("@$. ")), "old loaded level changed");
            } finally {
                Files.deleteIfExists(file);
                Files.delete(directory);
            }
        });
        test("26 blocked transition reuses identical Board and rows", () -> {
            Board initial = board("@$#");
            MoveResult result = engine.move(initial, Direction.RIGHT);
            check(result.kind() == MoveResult.Kind.BLOCKED && result.board() == initial
                    && result.board().rows() == initial.rows(), "blocked publication changed state");
        });
        test("27 successful result remains deeply immutable", () -> {
            MoveResult result = engine.move(board("@ "), Direction.RIGHT);
            expect(UnsupportedOperationException.class, () -> result.board().rows().clear());
            expect(UnsupportedOperationException.class, () -> result.board().rows().get(0).clear());
            check(result.board().equals(board(" @")), "result changed");
        });
        test("28 null API inputs are rejected", () -> {
            expect(NullPointerException.class, () -> engine.move(null, Direction.UP));
            expect(NullPointerException.class, () -> engine.move(board("@"), null));
            expect(NullPointerException.class, () -> new MoveResult(null, board("@")));
            expect(NullPointerException.class, () -> new MoveResult(MoveResult.Kind.BLOCKED, null));
        });
        test("29 movement runs without java.desktop or Swing", () -> {
            check(ModuleLayer.boot().findModule("java.desktop").isEmpty(), "desktop resolved");
            expect(ClassNotFoundException.class, () -> Class.forName("javax.swing.JPanel"));
            check(engine.move(board("@ "), Direction.RIGHT).kind() == MoveResult.Kind.MOVED,
                    "base-only movement");
        });
        test("30 invariants across 2664 bounded transitions", () -> boundedTransitions());
        test("31 maximum board dimensions remain intact", () -> {
            Board initial = board((" ".repeat(100) + "\n").repeat(99) + " ".repeat(99) + "@");
            MoveResult left = engine.move(initial, Direction.LEFT);
            check(left.kind() == MoveResult.Kind.MOVED
                    && left.board().playerPosition().equals(new Position(99, 98)), "max-board move");
            invariants(initial, left.board());
            for (Direction direction : List.of(Direction.RIGHT, Direction.DOWN)) {
                MoveResult blocked = engine.move(initial, direction);
                check(blocked.kind() == MoveResult.Kind.BLOCKED && blocked.board() == initial,
                        "max-board edge");
            }
        });
    }

    private void boundedTransitions() {
        int checked = 0;
        for (int player = 0; player < 9; player++) {
            for (int boxes = 0; boxes < 512; boxes++) {
                if ((boxes & (1 << player)) != 0 || Integer.bitCount(boxes) > 2) {
                    continue;
                }
                for (int variant = 0; variant < 2; variant++) {
                    List<List<Cell>> rows = new ArrayList<>();
                    for (int r = 0; r < 3; r++) {
                        List<Cell> row = new ArrayList<>();
                        for (int c = 0; c < 3; c++) {
                            int index = r * 3 + c;
                            Occupant occupant = index == player ? Occupant.PLAYER
                                    : (boxes & (1 << index)) != 0 ? Occupant.BOX : Occupant.EMPTY;
                            Terrain terrain = variant == 1 && occupant == Occupant.EMPTY && index % 3 == 0
                                    ? Terrain.WALL : index % 2 == 0 ? Terrain.GOAL : Terrain.FLOOR;
                            row.add(new Cell(terrain, occupant));
                        }
                        rows.add(row);
                    }
                    Board initial = new Board(rows);
                    int oldHash = initial.hashCode();
                    for (Direction direction : Direction.values()) {
                        MoveResult result = engine.move(initial, direction);
                        invariants(initial, result.board());
                        check(initial.hashCode() == oldHash, "previous state changed");
                        if (result.kind() == MoveResult.Kind.BLOCKED) {
                            check(result.board() == initial, "blocked state changed");
                        } else {
                            Position delta = result.board().playerPosition();
                            Position start = initial.playerPosition();
                            int dr = switch (direction) { case UP -> -1; case DOWN -> 1; default -> 0; };
                            int dc = switch (direction) { case LEFT -> -1; case RIGHT -> 1; default -> 0; };
                            check(delta.row() - start.row() == dr && delta.column() - start.column() == dc,
                                    "noncardinal displacement");
                            check(result.board() != initial, "successful state reused");
                        }
                        checked++;
                    }
                }
            }
        }
        check(checked == 2664, "bounded enumeration incomplete");
    }

    private List<MoveResult> sequence(Board initial, List<Direction> directions) {
        List<MoveResult> results = new ArrayList<>();
        Board state = initial;
        for (Direction direction : directions) {
            MoveResult result = engine.move(state, direction);
            invariants(state, result.board());
            results.add(result);
            state = result.board();
        }
        return List.copyOf(results);
    }

    private void checkMove(String beforeText, Direction direction, MoveResult.Kind kind, String afterText) {
        Board before = board(beforeText);
        int oldHash = before.hashCode();
        MoveResult result = engine.move(before, direction);
        check(result.kind() == kind, "wrong transition kind: " + result.kind());
        check(result.board().equals(board(afterText)), "wrong resulting board");
        check(before.equals(board(beforeText)) && before.hashCode() == oldHash, "previous state changed");
        check(kind == MoveResult.Kind.BLOCKED ? result.board() == before : result.board() != before,
                "wrong state identity");
        invariants(before, result.board());
    }

    private static Board board(String text) {
        return new LevelParser().parse(text).board();
    }

    private static void invariants(Board before, Board after) {
        check(before.width() == after.width() && before.height() == after.height(), "dimensions changed");
        check(before.boxCount() == after.boxCount(), "box count changed");
        check(before.goalCount() == after.goalCount(), "goal count changed");
        int players = 0;
        for (int r = 0; r < after.height(); r++) {
            for (int c = 0; c < after.width(); c++) {
                Position position = new Position(r, c);
                Cell cell = after.cellAt(position);
                check(before.cellAt(position).terrain() == cell.terrain(), "terrain changed");
                check(cell.terrain() != Terrain.WALL || cell.occupant() == Occupant.EMPTY, "occupied wall");
                if (cell.occupant() == Occupant.PLAYER) {
                    players++;
                }
            }
        }
        check(players == 1, "player count");
    }

    private void test(String name, CheckedRunnable action) {
        try {
            action.run();
            passed++;
            System.out.println("MOVEMENT PASS " + name);
        } catch (Exception | AssertionError error) {
            failed++;
            System.out.println("MOVEMENT FAIL " + name + ": " + error);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expect(Class<? extends Exception> type, CheckedRunnable action) throws Exception {
        try {
            action.run();
        } catch (Exception error) {
            if (type.isInstance(error)) {
                return;
            }
            throw new AssertionError("Expected " + type.getName() + ", got " + error, error);
        }
        throw new AssertionError("Expected " + type.getName());
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
