package ar.edu.uade.sokoban.level;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MovementEngine;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** Separate executable suite; preserves the historical Loading 40 / Movement 31 CLI totals. */
public final class C2LevelLoadingTest {
    private final LevelParser parser = new LevelParser();
    private final LevelLoader loader = new LevelLoader(parser);
    private final Path fixtures;
    private final Path temporary;
    private int passed;
    private int failed;
    private int sequence;

    private C2LevelLoadingTest(Path fixtures, Path temporary) {
        this.fixtures = fixtures;
        this.temporary = temporary;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected fixture directory argument");
        }
        Path owned = Files.createTempDirectory("c2-level-loading-tests-");
        C2LevelLoadingTest tests = new C2LevelLoadingTest(Path.of(args[0]), owned);
        try {
            tests.execute();
        } finally {
            try (var paths = Files.walk(owned)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
        System.out.println("C2 RESULT: " + tests.passed + " PASS, " + tests.failed + " FAIL");
        if (tests.failed != 0) {
            throw new AssertionError("C2-L7 tests failed");
        }
    }

    private void execute() {
        test("01 external legacy fixture equals C2 envelope", () -> {
            Level expected = loader.load(fixtures.resolve("basic.txt"));
            check(loader.load(write(envelope("# # # # #\n# @ $ . #\n# # # # #")))
                    .equals(expected), "fixture equivalence");
        });
        test("02 all seven aliases have exact semantics", () -> {
            String[] aliases = {"#", "_", ".", "@", "$", "+", "*"};
            Cell[] cells = sevenCells();
            for (int i = 0; i < aliases.length; i++) {
                String row = aliases[i] + (cells[i].occupant() == Occupant.PLAYER ? "" : " @");
                check(parser.parse(envelope(row)).board().cellAt(new Position(0, 0))
                        .equals(cells[i]), "alias " + aliases[i]);
            }
        });
        test("03 all seven long forms have exact semantics", () -> {
            String[] forms = {"{wall|empty}", "{floor|empty}", "{goal|empty}",
                    "{floor|player}", "{floor|box}", "{goal|player}", "{goal|box}"};
            Cell[] cells = sevenCells();
            for (int i = 0; i < forms.length; i++) {
                String row = forms[i] + (cells[i].occupant() == Occupant.PLAYER ? "" : " @");
                check(parser.parse(envelope(row)).board().cellAt(new Position(0, 0))
                        .equals(cells[i]), "long form " + forms[i]);
            }
        });
        test("04 aliases and long forms can mix", () ->
                check(parser.parse(envelope("@ {goal|box} _ {wall|empty} ."))
                        .equals(parser.parse("@* #.")), "mixed semantics"));
        test("05 player position and overlays", () -> {
            Board board = parser.parse(envelope("# _ #\n_ {goal|player} {goal|box}")).board();
            check(board.playerPosition().equals(new Position(1, 1)), "logical player position");
            check(board.goalCount() == 2 && board.boxCount() == 1, "overlays lost");
        });
        test("06 rectangularity uses cells not physical characters", () -> {
            Board board = parser.parse(envelope("@ _\n{floor|empty} {goal|box}")).board();
            check(board.width() == 2 && board.height() == 2, "physical character width used");
        });
        test("07 unequal cell counts reject before decoding short row", () ->
                reject(envelope("@ _\nx"), "DIMENSION"));
        test("08 exact header and reserved prefix reject without fallback", () -> {
            for (String header : List.of("!sokoban-txt 99", "!sokoban-txt", "!sokoban-txt2",
                    "!sokoban-txt 2 ", "!sokoban-txt 02", "!sokoban-txt\t2")) {
                reject(header + "\n[grid]\n@\n[attributes]", "HEADER");
            }
        });
        test("09 missing duplicated and disordered sections", () -> {
            for (String text : List.of("!sokoban-txt 2", "!sokoban-txt 2\n@\n[attributes]",
                    "!sokoban-txt 2\n[attributes]\n@\n[grid]", "!sokoban-txt 2\n[grid]\n@",
                    envelope("[grid]\n@"), envelope("@\n[unknown]"),
                    envelope("@") + "\n[grid]", envelope("@") + "\n[attributes]")) {
                reject(text, "SECTION");
            }
        });
        test("10 attributes are never ignored", () -> {
            for (String record : List.of("fragile f1 resistance=2", "wall w1 initial=closed",
                    "link l1 -> w1", "unknown=value", "# comment", " ")) {
                reject(envelope("@") + "\n" + record, "UNSUPPORTED_FEATURE");
            }
        });
        test("11 strict single ASCII separator", () -> {
            for (String row : List.of(" @", "@ ", "@  _", "@\t_", "@\r_")) {
                reject(envelope(row), "SEPARATOR");
            }
        });
        test("12 LF CRLF mixed and single optional final newline", () -> {
            String text = envelope("@ _\n_ .");
            Level expected = parser.parse(text);
            for (String variant : List.of(text + "\n", text.replace("\n", "\r\n"),
                    text.replace("\n", "\r\n") + "\r\n",
                    "!sokoban-txt 2\r\n[grid]\n@ _\r\n_ .\n[attributes]")) {
                check(parser.parse(variant).equals(expected), "EOL changed semantics");
            }
        });
        test("13 blank rows and second final newline rejected", () -> {
            reject(envelope("@\n\n_"), "DIMENSION");
            reject(envelope("@") + "\n\n", "SECTION");
            reject(envelope(""), "DIMENSION");
            reject("!sokoban-txt 2\n[grid]\n[attributes]", "DIMENSION");
        });
        test("14 BOM remains legacy input and fails through Loader", () -> {
            LevelFormatException error = expect(LevelFormatException.class,
                    () -> loader.load(write("\uFEFF" + envelope("@"))));
            check(error.getMessage().equals("Unknown symbol U+FEFF at row 1, column 1"),
                    "BOM was removed or activated C2");
        });
        test("15 invalid UTF-8 fails before C2 parsing", () -> {
            Path file = write(envelope("@"));
            Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28}, StandardOpenOption.APPEND);
            expect(MalformedInputException.class, () -> loader.load(file));
        });
        test("16 maximum logical dimensions load from external file", () -> {
            Board board = loader.load(write(maximumGrid(false))).board();
            check(board.width() == 100 && board.height() == 100, "maximum dimensions");
        });
        test("17 width and height 101 reject", () -> {
            reject(envelope("@" + " _".repeat(100)), "DIMENSION");
            reject(envelope("@\n" + "_\n".repeat(99) + "_"), "DIMENSION");
        });
        test("18 Loader byte ceiling and exact boundary", () -> {
            String prefix = "!sokoban-txt 2\n[grid]\n";
            String exact = prefix + "x".repeat(LevelLoader.MAX_FILE_BYTES - prefix.length());
            Path boundary = write(exact);
            check(Files.size(boundary) == 65_536, "boundary fixture");
            LevelFormatException shape = expect(LevelFormatException.class, () -> loader.load(boundary));
            check(shape.getMessage().contains("C2-L7 TOKEN_LIMIT"), "wrong boundary cause");
            LevelFormatException size = expect(LevelFormatException.class,
                    () -> loader.load(write(exact + "x")));
            check(size.getMessage().equals("File exceeds 65536 bytes"), "wrong byte ceiling");
        });
        test("19 token quota before unbounded token allocation", () -> {
            reject(envelope("@ " + "x".repeat(128)), "TOKEN at");
            reject(envelope("@ " + "x".repeat(129)), "TOKEN_LIMIT");
            reject(envelope("@ " + "x".repeat(1_000_000)), "TOKEN_LIMIT");
        });
        test("20 special entities fail explicitly", () -> {
            for (String token : List.of("{floor|fragile:f1}", "{goal|fragile:f2}",
                    "{floor|key:k1}", "{ice|empty}", "{lock:l1|empty}", "{wall:w1|empty}")) {
                reject(envelope("@ " + token), "UNSUPPORTED_FEATURE");
            }
        });
        test("21 unclosed and unrecognized combinations reject", () -> {
            for (String token : List.of("{floor|empty", "{wall|player}", "{wall|box}",
                    "{goal|wall}", "{floor|box}}", "{FLOOR|empty}", "~", "//comment")) {
                reject(envelope("@ " + token), "TOKEN");
            }
        });
        test("22 late error cannot replace a published Level", () -> {
            Level valid = parser.parse(envelope("@ _"));
            AtomicReference<Level> published = new AtomicReference<>(valid);
            expect(LevelFormatException.class, () -> published.set(parser.parse(
                    envelope("@ _\n_ .") + "\nfragile f1 resistance=2")));
            check(published.get() == valid, "late failure escaped");
            check(parser.parse(envelope("@ _")).equals(valid), "failed parser retained state");
        });
        test("23 interleaved legacy and C2 calls are independent", () -> {
            Level first = parser.parse(envelope("@ _"));
            reject(envelope("@ {ice|empty}"), "UNSUPPORTED_FEATURE");
            check(parser.parse("@ ").equals(first), "legacy polluted");
            Level second = parser.parse(envelope("@ _"));
            check(first.equals(second) && first != second && first.board() != second.board(),
                    "cached aggregate or nondeterministic load");
        });
        test("24 edit and reload external Unicode filename", () -> {
            Path file = temporary.resolve("nivel C2 con espacios ñ.txt");
            Files.writeString(file, envelope("@ _"), StandardCharsets.UTF_8);
            Level first = loader.load(file);
            Files.writeString(file, envelope("_ +").replace("\n", "\r\n"), StandardCharsets.UTF_8);
            Level second = loader.load(file);
            check(first.board().playerPosition().equals(new Position(0, 0)), "old state changed");
            check(second.board().playerPosition().equals(new Position(0, 1))
                    && second.board().goalCount() == 1, "reload ignored changed bytes");
        });
        test("25 one player invariant still protects extended input", () -> {
            expect(LevelFormatException.class, () -> parser.parse(envelope("_ .")));
            expect(LevelFormatException.class, () -> parser.parse(envelope("@ +")));
        });
        test("26 extended Board remains deeply immutable", () -> {
            Board board = parser.parse(envelope("@ _")).board();
            expect(UnsupportedOperationException.class, () -> board.rows().clear());
            expect(UnsupportedOperationException.class, () -> board.rows().get(0).clear());
        });
        test("27 prefix selection is exact and never trims", () -> {
            LevelFormatException leading = expect(LevelFormatException.class,
                    () -> parser.parse(" " + envelope("@")));
            check(leading.getMessage().equals("Unknown symbol U+0021 at row 1, column 2"),
                    "trim or implicit header recognition");
            LevelFormatException absent = expect(LevelFormatException.class, () -> parser.parse("_ @"));
            check(absent.getMessage().equals("Unknown symbol U+005F at row 1, column 1"),
                    "C2 token accepted without header");
        });
        test("28 parse String has no new byte ceiling", () -> {
            String text = maximumGrid(true);
            check(text.getBytes(StandardCharsets.UTF_8).length > LevelLoader.MAX_FILE_BYTES,
                    "large valid grid fixture too short");
            check(parser.parse(text).board().width() == 100, "String API gained byte ceiling");
            LevelFormatException error = expect(LevelFormatException.class, () -> loader.load(write(text)));
            check(error.getMessage().equals("File exceeds 65536 bytes"), "Loader lost guard");
        });
        test("29 null caller contract remains unchanged", () ->
                expect(NullPointerException.class, () -> parser.parse(null)));
        test("30 extended tokens must be printable ASCII", () -> {
            for (String token : List.of("ñ", "\u00A0", "\u0000", "\u007F", "😀")) {
                reject(envelope("@ " + token), "TOKEN");
            }
        });
        test("31 resulting model is safe for existing normal movement", () -> {
            MovementEngine movement = new MovementEngine();
            Board legacy = parser.parse("@$.").board();
            Board extended = parser.parse(envelope("@ {floor|box} .")).board();
            check(movement.move(extended, Direction.RIGHT).equals(movement.move(legacy, Direction.RIGHT)),
                    "existing consumer saw different semantics");
        });
        test("32 deterministic errors after valid and invalid calls", () -> {
            String bad = envelope("@ {wall:w1|empty}");
            String first = expect(LevelFormatException.class, () -> parser.parse(bad)).getMessage();
            parser.parse(envelope("+ *"));
            check(expect(LevelFormatException.class, () -> parser.parse(bad)).getMessage().equals(first),
                    "diagnostic retained per-call state");
        });
    }

    private static Cell[] sevenCells() {
        return new Cell[] {new Cell(Terrain.WALL, Occupant.EMPTY), new Cell(Terrain.FLOOR, Occupant.EMPTY),
                new Cell(Terrain.GOAL, Occupant.EMPTY), new Cell(Terrain.FLOOR, Occupant.PLAYER),
                new Cell(Terrain.FLOOR, Occupant.BOX), new Cell(Terrain.GOAL, Occupant.PLAYER),
                new Cell(Terrain.GOAL, Occupant.BOX)};
    }

    private static String envelope(String rows) {
        return "!sokoban-txt 2\n[grid]\n" + rows + "\n[attributes]";
    }

    private static String maximumGrid(boolean longForms) {
        String empty = longForms ? "{floor|empty}" : "_";
        String player = longForms ? "{floor|player}" : "@";
        String row = String.join(" ", java.util.Collections.nCopies(100, empty));
        return envelope(player + (" " + empty).repeat(99) + "\n" + (row + "\n").repeat(98) + row);
    }

    private Path write(String text) throws Exception {
        return Files.writeString(temporary.resolve("case-" + (++sequence) + ".txt"),
                text, StandardCharsets.UTF_8);
    }

    private void reject(String text, String code) throws Exception {
        LevelFormatException error = expect(LevelFormatException.class, () -> parser.parse(text));
        check(error.getMessage().contains("C2-L7 " + code), "wrong cause: " + error.getMessage());
    }

    private void test(String name, Check action) {
        try {
            action.run();
            passed++;
            System.out.println("C2 PASS " + name);
        } catch (Exception | AssertionError error) {
            failed++;
            System.out.println("C2 FAIL " + name + ": " + error);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }

    private static <T extends Exception> T expect(Class<T> type, Check action) throws Exception {
        try {
            action.run();
        } catch (Exception error) {
            if (type.isInstance(error)) { return type.cast(error); }
            throw new AssertionError("Expected " + type.getName() + ", got " + error, error);
        }
        throw new AssertionError("Expected " + type.getName());
    }

    @FunctionalInterface
    private interface Check { void run() throws Exception; }
}
