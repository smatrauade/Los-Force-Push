package ar.edu.uade.sokoban.level;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/** Dependency-free filesystem tests and direct API boundary checks. */
public final class LevelLoadingTest {
    private final LevelLoader loader = new LevelLoader(new LevelParser());
    private final Path fixtures;
    private final Path temporary;
    private int passed;
    private int failed;
    private int sequence;

    private LevelLoadingTest(Path fixtures, Path temporary) {
        this.fixtures = fixtures;
        this.temporary = temporary;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected fixture directory argument");
        }
        Path temporary = Files.createTempDirectory("level-loading-tests-");
        LevelLoadingTest tests = new LevelLoadingTest(Path.of(args[0]), temporary);
        try {
            tests.execute();
        } finally {
            try (var files = Files.list(temporary)) {
                for (Path file : files.toList()) {
                    Files.delete(file);
                }
            }
            Files.delete(temporary);
        }
        System.out.println("RESULT: " + tests.passed + " PASS, " + tests.failed + " FAIL");
        if (tests.failed != 0) {
            throw new AssertionError("Level-loading tests failed");
        }
    }

    private void execute() {
        test("01 valid external TXT, dimensions and exact cells", () -> {
            Board board = loader.load(fixtures.resolve("basic.txt")).board();
            check(board.width() == 5 && board.height() == 3, "dimensions");
            check(board.playerPosition().equals(new Position(1, 1)), "player");
            check(board.boxCount() == 1 && board.goalCount() == 1, "counts");
            check(board.cellAt(new Position(0, 0)).terrain() == Terrain.WALL, "wall");
            check(board.cellAt(new Position(1, 2)).equals(
                    new Cell(Terrain.FLOOR, Occupant.BOX)), "normal box");
        });
        test("02 different external file produces different board", () -> {
            Level a = loader.load(fixtures.resolve("basic.txt"));
            Level b = loader.load(fixtures.resolve("overlays.txt"));
            check(!a.equals(b), "different content ignored");
            check(b.board().width() == 6 && b.board().height() == 4, "second dimensions");
            check(b.board().boxCount() == 2 && b.board().goalCount() == 3, "second counts");
        });
        test("03 edit TXT and reload; previous Level unchanged", () -> {
            Path file = write(Files.readString(fixtures.resolve("basic.txt")));
            Level old = loader.load(file);
            Files.writeString(file, "#####\n#@*.#\n#####\n", StandardCharsets.UTF_8);
            Level changed = loader.load(file);
            check(!old.equals(changed), "loader cached file");
            check(old.board().goalCount() == 1 && changed.board().goalCount() == 2,
                    "file change or old state was lost");
        });
        test("04 player on goal", () -> check(loader.load(fixtures.resolve("overlays.txt"))
                .board().cellAt(new Position(1, 1)).equals(
                        new Cell(Terrain.GOAL, Occupant.PLAYER)), "player overlay"));
        test("05 box on goal", () -> check(loader.load(fixtures.resolve("overlays.txt"))
                .board().cellAt(new Position(1, 3)).equals(
                        new Cell(Terrain.GOAL, Occupant.BOX)), "box overlay"));
        test("06 missing file", () -> expect(NoSuchFileException.class,
                () -> loader.load(temporary.resolve("missing.txt"))));
        test("07 empty file", () -> expect(LevelFormatException.class,
                () -> loader.load(write(""))));
        test("08 unknown symbol includes row and column", () -> {
            LevelFormatException error = expect(LevelFormatException.class,
                    () -> loader.load(write("###\n#@x\n###\n")));
            check(error.getMessage().contains("U+0078 at row 2, column 3"), "diagnostic");
        });
        test("09 no player", () -> expect(LevelFormatException.class,
                () -> loader.load(write("###\n#$.\n###\n"))));
        test("10 multiple players including overlay", () -> expect(LevelFormatException.class,
                () -> loader.load(write("###\n@+#\n###\n"))));
        test("11 inconsistent rows", () -> expect(LevelFormatException.class,
                () -> loader.load(write("####\n#@#\n####\n"))));
        test("12 failure cannot replace published Level; next load recovers", () -> {
            Level valid = loader.load(fixtures.resolve("basic.txt"));
            AtomicReference<Level> published = new AtomicReference<>(valid);
            Path malformed = write("#####\n#@$.#\n####x\n");
            expect(LevelFormatException.class, () -> published.set(loader.load(malformed)));
            check(published.get() == valid, "partial Level escaped");
            expect(LevelFormatException.class, () -> loader.load(write("@@")));
            check(loader.load(fixtures.resolve("basic.txt")).equals(valid), "state leaked");
        });
        test("13 UTF-8 LF, CRLF, mixed endings and absent final newline", () -> {
            String text = Files.readString(fixtures.resolve("basic.txt"));
            Level expected = loader.load(write(text));
            check(loader.load(write(text.replace("\n", "\r\n"))).equals(expected), "CRLF");
            check(loader.load(write("#####\r\n#@$.#\n#####")).equals(expected), "mixed/end");
            check(loader.load(write(text.stripTrailing())).equals(expected), "no final newline");
        });
        test("14 deterministic independent loads", () -> {
            Level first = loader.load(fixtures.resolve("basic.txt"));
            Level second = loader.load(fixtures.resolve("basic.txt"));
            check(first.equals(second) && first.hashCode() == second.hashCode(), "determinism");
            check(first != second && first.board() != second.board(), "cached shared aggregate");
        });
        test("15 executes with java.base only; java.desktop absent", () -> {
            check(ModuleLayer.boot().findModule("java.desktop").isEmpty(), "desktop resolved");
            expect(ClassNotFoundException.class, () -> Class.forName("javax.swing.JPanel"));
            check(ModuleFinder.ofSystem().find("java.base").isPresent(), "base absent");
        });
        test("16 malformed UTF-8 rejected before parsing", () -> {
            Path file = write("@");
            Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});
            expect(MalformedInputException.class, () -> loader.load(file));
        });
        test("17 valid UTF-8 nonalphabet character rejected explicitly", () -> {
            LevelFormatException error = expect(LevelFormatException.class,
                    () -> loader.load(write("@ñ")));
            check(error.getMessage().contains("U+00F1"), "UTF-8 decoded incorrectly");
        });
        test("18 BOM rejected", () -> expect(LevelFormatException.class,
                () -> loader.load(write("\uFEFF@"))));
        test("19 isolated CR rejected", () -> expect(LevelFormatException.class,
                () -> loader.load(write("@\r"))));
        test("20 blank interior row rejected", () -> expect(LevelFormatException.class,
                () -> loader.load(write("###\n\n#@#\n"))));
        test("21 second final newline rejected", () -> expect(LevelFormatException.class,
                () -> loader.load(write("@\n\n"))));
        test("22 leading/trailing floor spaces preserved", () -> {
            Board board = loader.load(write(" @ \n   \n")).board();
            check(board.width() == 3 && board.height() == 2, "whitespace trimmed");
            check(board.cellAt(new Position(0, 2)).terrain() == Terrain.FLOOR, "trailing floor");
        });
        test("23 tab rejected", () -> expect(LevelFormatException.class,
                () -> loader.load(write("@\t"))));
        test("24 dimensional boundaries and empty aggregate", () -> {
            check(loader.load(write("@" + " ".repeat(99))).board().width() == 100, "max width");
            check(loader.load(write("@ \n" + "  \n".repeat(99))).board().height() == 100,
                    "max height");
            expect(LevelFormatException.class, () -> loader.load(write("@" + " ".repeat(100))));
            expect(LevelFormatException.class, () -> loader.load(write("@\n" + " \n".repeat(100))));
            expect(LevelFormatException.class, () -> new Board(List.of()));
        });
        test("25 oversized file rejected specifically by byte ceiling", () -> {
            Path file = write("@" + " ".repeat(LevelLoader.MAX_FILE_BYTES));
            check(Files.size(file) == 65_537, "wrong oversized fixture");
            LevelFormatException error = expect(LevelFormatException.class, () -> loader.load(file));
            check(error.getMessage().equals("File exceeds 65536 bytes"), "wrong rejection cause");
        });
        test("26 immutable Board isolates source collections", () -> {
            List<Cell> row = new ArrayList<>(List.of(new Cell(Terrain.FLOOR, Occupant.PLAYER)));
            List<List<Cell>> rows = new ArrayList<>(List.of(row));
            Board board = new Board(rows);
            row.clear();
            rows.clear();
            check(board.cellAt(new Position(0, 0)).occupant() == Occupant.PLAYER, "alias");
            expect(UnsupportedOperationException.class, () -> board.rows().clear());
            expect(UnsupportedOperationException.class, () -> board.rows().get(0).clear());
        });
        test("27 cell occupancy and coordinate bounds", () -> {
            expect(IllegalArgumentException.class, () -> new Cell(Terrain.WALL, Occupant.BOX));
            expect(IllegalArgumentException.class, () -> new Position(-1, 0));
            Board board = loader.load(fixtures.resolve("basic.txt")).board();
            expect(IndexOutOfBoundsException.class, () -> board.cellAt(new Position(3, 0)));
            expect(IndexOutOfBoundsException.class, () -> board.cellAt(new Position(0, 5)));
        });
        test("28 direct construction shares Board invariants", () -> {
            Cell player = new Cell(Terrain.FLOOR, Occupant.PLAYER);
            Cell floor = new Cell(Terrain.FLOOR, Occupant.EMPTY);
            expect(LevelFormatException.class, () -> new Board(List.of(List.of(floor))));
            expect(LevelFormatException.class, () -> new Board(List.of(List.of(player, player))));
            expect(LevelFormatException.class,
                    () -> new Board(List.of(List.of(player), List.of(floor, floor))));
        });
        test("29 builder publishes once and isolates rows", () -> {
            LevelBuilder builder = new LevelBuilder();
            expect(LevelFormatException.class, builder::build);
            List<Cell> row = new ArrayList<>(List.of(new Cell(Terrain.FLOOR, Occupant.PLAYER)));
            builder.addRow(row);
            row.clear();
            Level level = builder.build();
            check(level.board().width() == 1, "builder alias");
            expect(IllegalStateException.class, builder::build);
            expect(IllegalStateException.class, () -> builder.addRow(List.of()));
        });
        test("30 no unstated box/goal or enclosure rule", () -> {
            Board board = loader.load(write("@$$.")).board();
            check(board.boxCount() == 2 && board.goalCount() == 1, "counts constrained");
            check(loader.load(write("@")).board().goalCount() == 0, "minimum subset rejected");
        });
        test("31 exact byte ceiling reaches structural validation", () -> {
            Path file = write("@" + " ".repeat(LevelLoader.MAX_FILE_BYTES - 1));
            check(Files.size(file) == 65_536, "wrong boundary fixture");
            LevelFormatException error = expect(LevelFormatException.class, () -> loader.load(file));
            check(error.getMessage().equals("Width must be between 1 and 100 at row 1"),
                    "byte boundary incorrectly rejected as oversized");
        });
        test("32 width rejected before decoding a large unknown-symbol row", () -> {
            LevelFormatException error = expect(LevelFormatException.class,
                    () -> new LevelParser().parse("x".repeat(1_000_000)));
            check(error.getMessage().equals("Width must be between 1 and 100 at row 1"),
                    "width validation was not early");
        });
        test("33 height rejected before decoding row 101", () -> {
            LevelFormatException error = expect(LevelFormatException.class,
                    () -> new LevelParser().parse("@\n" + " \n".repeat(99) + "x"));
            check(error.getMessage().equals("Height exceeds 100"), "height validation was not early");
        });
        test("34 rectangularity rejected before decoding malformed short row", () -> {
            LevelFormatException error = expect(LevelFormatException.class,
                    () -> new LevelParser().parse("###\n#@#\nx"));
            check(error.getMessage().equals("Inconsistent width at row 3"), "wrong precedence");
        });
        test("35 public reference parameters reject null", () -> {
            expect(NullPointerException.class, () -> new LevelParser().parse(null));
            expect(NullPointerException.class, () -> new LevelLoader(null));
            expect(NullPointerException.class, () -> loader.load(null));
            expect(NullPointerException.class, () -> new Cell(null, Occupant.EMPTY));
            expect(NullPointerException.class, () -> new Cell(Terrain.FLOOR, null));
            expect(NullPointerException.class, () -> new Level(null));
            expect(NullPointerException.class, () -> new Board(null));
            Board board = loader.load(fixtures.resolve("basic.txt")).board();
            expect(NullPointerException.class, () -> board.cellAt(null));
        });
        test("36 nested null rows and cells rejected", () -> {
            List<List<Cell>> nullRows = new ArrayList<>();
            nullRows.add(null);
            expect(NullPointerException.class, () -> new Board(nullRows));
            List<Cell> nullCells = new ArrayList<>();
            nullCells.add(null);
            expect(NullPointerException.class, () -> new Board(List.of(nullCells)));
        });
        test("37 negative column rejected", () ->
                expect(IllegalArgumentException.class, () -> new Position(0, -1)));
        test("38 byte ceiling counts UTF-8 bytes, not characters", () -> {
            String text = "@" + "ñ".repeat(32_768);
            Path file = write(text);
            check(text.length() < 65_536 && Files.size(file) == 65_537, "wrong UTF-8 fixture");
            LevelFormatException error = expect(LevelFormatException.class, () -> loader.load(file));
            check(error.getMessage().equals("File exceeds 65536 bytes"), "wrong byte-limit cause");
        });
        test("39 external filename with spaces and Unicode; repeated load", () -> {
            Path file = temporary.resolve("nivel con espacios ñ y é.txt");
            Files.writeString(file, " @ \r\n   \r\n", StandardCharsets.UTF_8);
            Level first = loader.load(file);
            check(first.board().width() == 3 && first.board().height() == 2, "path or spaces");
            check(first.board().cellAt(new Position(0, 2)).equals(
                    new Cell(Terrain.FLOOR, Occupant.EMPTY)), "trailing floor");
            Files.writeString(file, " + \n $ \n", StandardCharsets.UTF_8);
            Level second = loader.load(file);
            check(!first.equals(second) && second.board().goalCount() == 1
                    && second.board().boxCount() == 1, "external reload ignored");
            check(first.board().goalCount() == 0, "old Level changed");
        });
        test("40 diagnostics independent of default formatting locale", () -> {
            Locale original = Locale.getDefault(Locale.Category.FORMAT);
            try {
                Locale.setDefault(Locale.Category.FORMAT,
                        Locale.forLanguageTag("ar-EG-u-nu-arab"));
                LevelFormatException error = expect(LevelFormatException.class,
                        () -> loader.load(write("@x")));
                check(error.getMessage().equals("Unknown symbol U+0078 at row 1, column 2"),
                        "locale-dependent diagnostic");
            } finally {
                Locale.setDefault(Locale.Category.FORMAT, original);
            }
        });
    }

    private Path write(String text) throws IOException {
        Path file = temporary.resolve("case-" + (++sequence) + ".txt");
        return Files.writeString(file, text, StandardCharsets.UTF_8);
    }

    private void test(String name, CheckedRunnable action) {
        try {
            action.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Exception | AssertionError error) {
            failed++;
            System.out.println("FAIL " + name + ": " + error);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static <T extends Exception> T expect(Class<T> type, CheckedRunnable action)
            throws Exception {
        try {
            action.run();
        } catch (Exception error) {
            if (type.isInstance(error)) {
                return type.cast(error);
            }
            throw new AssertionError("Expected " + type.getName() + ", got " + error, error);
        }
        throw new AssertionError("Expected " + type.getName() + " but operation succeeded");
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
