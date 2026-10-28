package ar.edu.uade.sokoban.level;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Separate executable regression for the experimental, non-playable definition. */
public final class ExperimentalFragileParserTest {
    private static int passed;
    private static int failed;
    private static final ExperimentalFragileParser PARSER = new ExperimentalFragileParser();

    private ExperimentalFragileParserTest() { }

    public static void main(String[] args) throws Exception {
        check("one fragile", () -> {
            var value = PARSER.parse(level("@ {floor|fragile:f1}", "fragile f1 resistance=3"));
            assertTrue(value.fragileBoxes().get(0).equals(new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), 3)));
            assertTrue(value.spatialTemplate().cellAt(new Position(0, 1)).occupant() == Occupant.EMPTY);
        });
        check("two independent values and declaration order", () -> {
            String grid = "@ {floor|fragile:f1} {floor|fragile:f2}";
            var first = PARSER.parse(level(grid, "fragile f1 resistance=1\nfragile f2 resistance=42"));
            var second = PARSER.parse(level(grid, "fragile f2 resistance=42\nfragile f1 resistance=1"));
            assertTrue(first.equals(second) && first.fragileBoxes().get(1).initialResistance() == 42);
        });
        check("fragile on goal", () -> {
            var value = PARSER.parse(level("@ {goal|fragile:f1}", "fragile f1 resistance=1"));
            assertTrue(value.spatialTemplate().cellAt(new Position(0, 1)).terrain() == Terrain.GOAL);
        });
        check("missing attribute", () -> reject(level("@ {floor|fragile:f1}", "")));
        check("duplicate spatial id", () -> reject(level("@ {floor|fragile:f1} {goal|fragile:f1}", "fragile f1 resistance=1")));
        check("duplicate attribute", () -> reject(level("@ {floor|fragile:f1}", "fragile f1 resistance=1\nfragile f1 resistance=2")));
        check("unknown id", () -> reject(level("@", "fragile f1 resistance=1")));
        check("zero resistance", () -> reject(level("@ {floor|fragile:f1}", "fragile f1 resistance=0")));
        check("negative resistance", () -> reject(level("@ {floor|fragile:f1}", "fragile f1 resistance=-1")));
        check("integer overflow", () -> reject(level("@ {floor|fragile:f1}", "fragile f1 resistance=2147483648")));
        check("integer maximum", () -> assertTrue(PARSER.parse(level("@ {floor|fragile:f1}", "fragile f1 resistance=2147483647")).fragileBoxes().get(0).initialResistance() == Integer.MAX_VALUE));
        check("unknown parameter", () -> reject(level("@ {floor|fragile:f1}", "fragile f1 resistance=1 extra=2")));
        check("bad separators", () -> reject(level("@  {floor|fragile:f1}", "fragile f1 resistance=1")));
        check("non ASCII id", () -> reject(level("@ {floor|fragile:ñ}", "fragile ñ resistance=1")));
        check("identifier length boundary", () -> {
            String id = "a".repeat(32);
            PARSER.parse(level("@ {floor|fragile:" + id + "}", "fragile " + id + " resistance=1"));
            reject(level("@ {floor|fragile:" + id + "a}", "fragile " + id + "a resistance=1"));
        });
        check("dimension and token limits", () -> {
            reject(level("@" + " _".repeat(100), ""));
            reject(level("@ " + "x".repeat(129), ""));
            reject("x".repeat(ExperimentalFragileParser.MAX_TEXT_CHARS + 1));
        });
        check("maximum grid preserves fragile position", () -> {
            String row = "_ ".repeat(99) + "_";
            String grid = "@ " + "_ ".repeat(98) + "_\n" + (row + "\n").repeat(98)
                    + "_ ".repeat(99) + "{goal|fragile:last}";
            var value = PARSER.parse(level(grid, "fragile last resistance=9"));
            assertTrue(value.spatialTemplate().width() == 100 && value.spatialTemplate().height() == 100);
            assertTrue(value.fragileBoxes().get(0).position().equals(new Position(99, 99)));
        });
        check("independent calls", () -> {
            String input = level("@ {floor|fragile:f1}", "fragile f1 resistance=2");
            var first = PARSER.parse(input);
            reject(level("@ {floor|fragile:f1}", ""));
            var second = PARSER.parse(input);
            assertTrue(first.equals(second) && first != second && first.spatialTemplate() != second.spatialTemplate());
        });
        check("deep immutability", () -> {
            var value = PARSER.parse(level("@ {floor|fragile:f1}", "fragile f1 resistance=2"));
            expectUnsupported(() -> value.fragileBoxes().clear());
            expectUnsupported(() -> value.spatialTemplate().rows().get(0).clear());
            var source = new ArrayList<>(value.fragileBoxes());
            var copy = new ExperimentalFragileLevel(value.spatialTemplate(), source);
            source.clear();
            assertTrue(copy.fragileBoxes().size() == 1);
        });
        check("late failure cannot publish", () -> {
            var original = PARSER.parse(level("@", ""));
            var holder = new ExperimentalFragileLevel[] {original};
            try { holder[0] = PARSER.parse(level("@ {floor|fragile:f1}", "fragile f1 resistance=2\nextra")); }
            catch (LevelFormatException expected) { assertTrue(holder[0] == original); return; }
            throw new AssertionError("late error accepted");
        });
        check("normal loader rejects same file", () -> {
            Path file = Files.createTempFile("fragile-test-", ".txt");
            try {
                Files.writeString(file, level("@ {goal|fragile:f1}", "fragile f1 resistance=4"), StandardCharsets.UTF_8);
                PARSER.parse(Files.readString(file, StandardCharsets.UTF_8));
                try { new LevelLoader(new LevelParser()).load(file); }
                catch (LevelFormatException expected) { return; }
                throw new AssertionError("playable loader accepted fragile");
            } finally { Files.delete(file); }
        });
        check("CRLF accepted; unexpected section rejected", () -> {
            PARSER.parse(level("@ {floor|fragile:f1}", "fragile f1 resistance=2").replace("\n", "\r\n"));
            reject(level("@", "[extra]"));
        });
        check("direct model rejects malformed ids", () -> {
            for (String id : List.of("", "F1", "ñ", "a".repeat(33))) {
                rejectConstruction(() -> new ExperimentalFragileLevel.FragileBox(id, new Position(0, 1), 1));
            }
        });
        check("direct model rejects duplicate ids", () -> {
            Board board = PARSER.parse(level("@ _ .", "")).spatialTemplate();
            rejectConstruction(() -> new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), 2),
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 2), 3))));
        });
        check("direct model rejects duplicate positions", () -> {
            Board board = PARSER.parse(level("@ _", "")).spatialTemplate();
            rejectConstruction(() -> new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), 2),
                    new ExperimentalFragileLevel.FragileBox("f2", new Position(0, 1), 3))));
        });
        check("direct model rejects out-of-bounds positions", () -> {
            Board board = PARSER.parse(level("@ _", "")).spatialTemplate();
            rejectConstruction(() -> new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 2), 1))));
            rejectConstruction(() -> new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(1, 1), 1))));
        });
        check("direct model rejects fragile on walls", () -> {
            Board board = PARSER.parse(level("@ #", "")).spatialTemplate();
            rejectConstruction(() -> new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), 1))));
        });
        check("direct model rejects fragile on player", () -> {
            Board board = PARSER.parse(level("@ _", "")).spatialTemplate();
            rejectConstruction(() -> new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 0), 1))));
        });
        check("direct model rejects fragile on normal box", () -> {
            Board board = PARSER.parse(level("@ $", "")).spatialTemplate();
            rejectConstruction(() -> new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), 1))));
        });
        check("direct model accepts independent floor and goal", () -> {
            Board board = PARSER.parse(level("@ _ .", "")).spatialTemplate();
            var model = new ExperimentalFragileLevel(board, List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), 1),
                    new ExperimentalFragileLevel.FragileBox("f2", new Position(0, 2), Integer.MAX_VALUE)));
            assertTrue(model.fragileBoxes().size() == 2);
        });
        check("direct model rejects null inputs and nonpositive resistance", () -> {
            Board board = PARSER.parse(level("@ _", "")).spatialTemplate();
            expectNull(() -> new ExperimentalFragileLevel.FragileBox(null, new Position(0, 1), 1));
            expectNull(() -> new ExperimentalFragileLevel.FragileBox("f1", null, 1));
            expectNull(() -> new ExperimentalFragileLevel(null, List.of()));
            expectNull(() -> new ExperimentalFragileLevel(board, null));
            expectNull(() -> new ExperimentalFragileLevel(board, java.util.Arrays.asList((ExperimentalFragileLevel.FragileBox) null)));
            rejectConstruction(() -> new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), 0));
            rejectConstruction(() -> new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 1), -1));
        });
        System.out.println("FRAGILE RESULT: " + passed + " PASS, " + failed + " FAIL");
        if (failed != 0) { System.exit(1); }
    }

    private static String level(String grid, String attributes) {
        return "!sokoban-txt 2\n[grid]\n" + grid + "\n[attributes]" + (attributes.isEmpty() ? "" : "\n" + attributes);
    }
    private static void reject(String text) {
        try { PARSER.parse(text); } catch (LevelFormatException expected) { return; }
        throw new AssertionError("invalid input accepted");
    }
    private static void expectNull(Runnable operation) {
        try { operation.run(); } catch (NullPointerException expected) { return; }
        throw new AssertionError("null accepted");
    }
    private static void rejectConstruction(Runnable operation) {
        try { operation.run(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("invalid direct model construction accepted");
    }
    private static void expectUnsupported(Runnable operation) {
        try { operation.run(); } catch (UnsupportedOperationException expected) { return; }
        throw new AssertionError("mutable collection");
    }
    private static void assertTrue(boolean condition) { if (!condition) { throw new AssertionError(); } }
    private static void check(String name, Checked action) {
        try { action.run(); passed++; System.out.println("PASS " + name); }
        catch (Exception | AssertionError error) { failed++; System.out.println("FAIL " + name + ": " + error); }
    }
    @FunctionalInterface private interface Checked { void run() throws Exception; }
}
