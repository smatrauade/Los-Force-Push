package ar.edu.uade.sokoban.level;

import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
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
        filesystemChecks(args.length == 0 ? Path.of("src/test/resources/levels") : Path.of(args[0]));
        System.out.println("FRAGILE RESULT: " + passed + " PASS, " + failed + " FAIL");
        if (failed != 0) { System.exit(1); }
    }

    private static void filesystemChecks(Path fixtures) {
        LevelLoader loader = new LevelLoader(new LevelParser());
        check("32 external definition preserves both identities resistance and terrain", () -> {
            var value = loader.loadExperimentalFragile(fixtures.resolve("experimental-fragile.txt"));
            assertTrue(value.fragileBoxes().equals(List.of(
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(1, 2), 3),
                    new ExperimentalFragileLevel.FragileBox("f2", new Position(1, 3), 12))));
            assertTrue(value.spatialTemplate().equals(new LevelParser().parse("######\n#@ .*#\n######").board()));
            expectUnsupported(() -> value.fragileBoxes().clear());
            expectUnsupported(() -> value.spatialTemplate().rows().get(1).clear());
            expect(LevelFormatException.class, () -> loader.load(fixtures.resolve("experimental-fragile.txt")));
        });
        check("33 edit external Unicode path and reload without changing old definition", () -> withFile(file -> {
            String original = level("@ {floor|fragile:f1} {goal|fragile:f2} #",
                    "fragile f1 resistance=3\nfragile f2 resistance=12");
            Files.writeString(file, original, StandardCharsets.UTF_8);
            var first = loader.loadExperimentalFragile(file);
            String edited = level("@ {goal|fragile:f2} {floor|fragile:f1} $",
                    "fragile f2 resistance=8\nfragile f1 resistance=5");
            Files.writeString(file, edited.replace("\n", "\r\n") + "\r\n", StandardCharsets.UTF_8);
            var second = loader.loadExperimentalFragile(file);
            assertTrue(second.fragileBoxes().equals(List.of(
                    new ExperimentalFragileLevel.FragileBox("f2", new Position(0, 1), 8),
                    new ExperimentalFragileLevel.FragileBox("f1", new Position(0, 2), 5))));
            assertTrue(second.spatialTemplate().cellAt(new Position(0, 1)).terrain() == Terrain.GOAL);
            assertTrue(second.spatialTemplate().cellAt(new Position(0, 3)).occupant() == Occupant.BOX);
            assertTrue(first.fragileBoxes().get(0).initialResistance() == 3
                    && first.fragileBoxes().get(0).position().equals(new Position(0, 1))
                    && first.spatialTemplate().cellAt(new Position(0, 3)).terrain() == Terrain.WALL);
            // Declaration order is independent of spatial order, including mixed line endings.
            Files.writeString(file, level("@ {goal|fragile:f2} {floor|fragile:f1} $",
                    "fragile f1 resistance=5\r\nfragile f2 resistance=8"), StandardCharsets.UTF_8);
            var third = loader.loadExperimentalFragile(file);
            assertTrue(third.equals(second) && third != second && third.spatialTemplate() != second.spatialTemplate());
        }));
        check("34 late file error preserves published definition and reload recovers", () -> withFile(file -> {
            String valid = level("@ {goal|fragile:f1}", "fragile f1 resistance=7");
            Files.writeString(file, valid, StandardCharsets.UTF_8);
            var first = loader.loadExperimentalFragile(file);
            var holder = new ExperimentalFragileLevel[] {first};
            Files.writeString(file, valid + "\ninvalid", StandardCharsets.UTF_8);
            var error = expect(LevelFormatException.class, () -> holder[0] = loader.loadExperimentalFragile(file));
            assertTrue(error.getMessage().equals("Invalid fragile attribute at line 6") && holder[0] == first);
            Files.write(file, new byte[] {(byte) 0xC3});
            expect(MalformedInputException.class, () -> holder[0] = loader.loadExperimentalFragile(file));
            assertTrue(holder[0] == first && first.fragileBoxes().get(0).initialResistance() == 7);
            Files.writeString(file, valid, StandardCharsets.UTF_8);
            assertTrue(loader.loadExperimentalFragile(file).equals(first));
        }));
        check("35 malformed and truncated UTF-8 reject before syntax", () -> withFile(file -> {
            for (byte[] bytes : List.of(new byte[] {(byte) 0xC3, (byte) 0x28},
                    new byte[] {(byte) 0xE2, (byte) 0x82})) {
                Files.write(file, bytes);
                expect(MalformedInputException.class, () -> loader.loadExperimentalFragile(file));
            }
        }));
        check("36 oversized file rejects by bytes before decoding", () -> withFile(file -> {
            byte[] bytes = new byte[LevelLoader.MAX_FILE_BYTES + 1];
            java.util.Arrays.fill(bytes, (byte) 'x');
            bytes[0] = (byte) 0xFF;
            Files.write(file, bytes);
            var error = expect(LevelFormatException.class, () -> loader.loadExperimentalFragile(file));
            assertTrue(error.getMessage().equals("File exceeds 65536 bytes"));
        }));
        check("37 exact byte ceiling reaches experimental structural validation", () -> withFile(file -> {
            String prefix = "!sokoban-txt 2\n[grid]\n";
            String text = prefix + "x".repeat(LevelLoader.MAX_FILE_BYTES - prefix.length());
            Files.writeString(file, text, StandardCharsets.UTF_8);
            assertTrue(Files.size(file) == LevelLoader.MAX_FILE_BYTES);
            var error = expect(LevelFormatException.class, () -> loader.loadExperimentalFragile(file));
            assertTrue(error.getMessage().equals("Missing [attributes]"));
        }));
        check("38 experimental filesystem ceiling counts UTF-8 bytes not Java chars", () -> withFile(file -> {
            String text = "@" + "ñ".repeat(32_768);
            assertTrue(text.length() < ExperimentalFragileParser.MAX_TEXT_CHARS);
            Files.writeString(file, text, StandardCharsets.UTF_8);
            assertTrue(Files.size(file) == LevelLoader.MAX_FILE_BYTES + 1);
            var error = expect(LevelFormatException.class, () -> loader.loadExperimentalFragile(file));
            assertTrue(error.getMessage().equals("File exceeds 65536 bytes"));
        }));
        check("39 experimental file missing and null remain explicit failures", () -> withFile(file -> {
            expect(NullPointerException.class, () -> loader.loadExperimentalFragile(null));
            Files.delete(file);
            expect(NoSuchFileException.class, () -> loader.loadExperimentalFragile(file));
        }));
        check("40 experimental file does not normalize BOM empty or legacy input", () -> withFile(file -> {
            for (String text : List.of("", "@", "\uFEFF" + level("@", ""))) {
                Files.writeString(file, text, StandardCharsets.UTF_8);
                var error = expect(LevelFormatException.class, () -> loader.loadExperimentalFragile(file));
                assertTrue(error.getMessage().equals("Experimental C2 header or grid section invalid"));
            }
        }));
        check("41 ordinary C2 works in both APIs without sharing cached state", () -> withFile(file -> {
            Files.writeString(file, level("@ $ .", ""), StandardCharsets.UTF_8);
            Level playable = loader.load(file);
            var definition = loader.loadExperimentalFragile(file);
            assertTrue(definition.fragileBoxes().isEmpty() && definition.spatialTemplate().equals(playable.board()));
            Files.writeString(file, "@$.", StandardCharsets.UTF_8);
            expect(LevelFormatException.class, () -> loader.loadExperimentalFragile(file));
            assertTrue(loader.load(file).board().boxCount() == 1);
        }));
    }

    private static void withFile(FileCheck action) throws Exception {
        Path directory = Files.createTempDirectory("fragile-loading-");
        Path file = directory.resolve("nivel Fragile ñ con espacios.txt");
        Files.createFile(file);
        try { action.run(file); }
        finally { Files.deleteIfExists(file); Files.delete(directory); }
    }

    private static <T extends Exception> T expect(Class<T> type, Checked action) throws Exception {
        try { action.run(); }
        catch (Exception error) {
            if (type.isInstance(error)) { return type.cast(error); }
            throw new AssertionError("Expected " + type.getName() + ", got " + error, error);
        }
        throw new AssertionError("Expected " + type.getName());
    }

    @FunctionalInterface private interface FileCheck { void run(Path file) throws Exception; }

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
