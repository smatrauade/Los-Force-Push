package ar.edu.uade.sokoban.level;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** External loading and Key/Lock/Wall integration without a test framework. */
public final class ExtendedLevelTest {
    private static int passed;
    private static final LevelLoader LOADER = new LevelLoader(new LevelParser());
    private static final ExtendedLevelParser PARSER = new ExtendedLevelParser();

    private ExtendedLevelTest() { }

    public static void main(String[] args) throws Exception {
        Path fixtures = Path.of(args[0]);
        Path file = fixtures.resolve("extended-runtime.txt");
        String text = Files.readString(file, StandardCharsets.UTF_8);
        ExtendedLevelDefinition definition = LOADER.loadExtended(file);
        RuntimeState initial = RuntimeState.fromExtended(definition);
        check(initial.player().equals(p(1, 1)) && initial.boxes().size() == 2
                && initial.boxAt(p(1, 2)).id().equals("k1")
                && initial.boxAt(p(2, 1)).kind() == RuntimeState.BoxKind.NORMAL
                && !initial.wallOpen(p(1, 6)), "complete external state");
        RuntimeMovementEngine engine = new RuntimeMovementEngine();
        RuntimeState approached = engine.move(initial, Direction.RIGHT).state();
        RuntimeState opened = engine.move(approached, Direction.RIGHT).state();
        check(opened.lockActive("l1") && opened.wallOpen(p(1, 6))
                && opened.boxAt(p(1, 4)).id().equals("k1"), "key activates lock");
        RuntimeState leave = engine.move(opened, Direction.RIGHT).state();
        RuntimeState closed = engine.move(leave, Direction.RIGHT).state();
        check(!closed.lockActive("l1") && !closed.wallOpen(p(1, 6)), "key leaves lock and closes wall");
        check(engine.move(closed, Direction.RIGHT).kind() == MoveResult.Kind.BLOCKED
                && opened.wallOpen(p(1, 6))
                && initial.boxAt(p(1, 2)).id().equals("k1"), "blocked and past states unchanged");
        check(RuntimeState.fromExtended(LOADER.loadExtended(fixtures.resolve("extended-ordinary.txt")))
                .boxes().size() == 1, "ordinary external");
        check(LOADER.loadExtended(file).equals(PARSER.parse(text)), "deterministic reload");
        String reordered = text.replace("wall w1 locks=l1\n", "wall w1 locks=l1\n");
        check(PARSER.parse(reordered).equals(definition), "reload equal");
        String multi = wrap("@ {goal|key:k} _ {lock:l|key:q} {wall:w|empty} {wall:x|empty}",
                "wall x locks=l\nwall w locks=l");
        RuntimeState many = RuntimeState.fromExtended(PARSER.parse(multi));
        check(many.lockActive("l") && many.wallOpen(p(0, 4)) && many.wallOpen(p(0, 5))
                && many.boxAt(p(0, 1)).kind() == RuntimeState.BoxKind.KEY
                && many.terrain().get(0).get(1) == RuntimeState.Ground.GOAL, "goal key and two walls open");
        String twoLocks = wrap("@ {lock:a|key:k} {lock:b|empty} {wall:w|empty}", "wall w locks=b,a");
        check(RuntimeState.fromExtended(PARSER.parse(twoLocks)).wallOpen(p(0, 3)), "two locks one wall");
        try {
            definition.keys().clear();
            throw new AssertionError("mutable keys");
        } catch (UnsupportedOperationException expected) {
            check(true, "definition immutable");
        }
        bad(text.replace("!sokoban-txt 2", "!sokoban-txt 3"), "header");
        bad(text.replace("[attributes]", "[missing]"), "missing section");
        bad("\ufeff" + text, "BOM");
        bad(text.replace("key:k1", "key:BAD"), "invalid id");
        bad(text.replace("locks=l1", "locks=missing"), "missing lock reference");
        bad(text.replace("wall w1 locks=l1", ""), "missing wall metadata");
        bad(text + "\nwall w1 locks=l1\n", "duplicate wall metadata");
        bad(text + "unknown data\n", "unknown metadata");
        bad(wrap("@ {floor|key:a} {floor|key:a}", ""), "duplicate key");
        bad(wrap("@ {lock:l|empty} {wall:w|player}", "wall w locks=l"), "closed occupied wall");
        bad(wrap("@ {lock:l|empty} {wall:w|box}", "wall w locks=l"), "closed occupied box");
        bad(wrap("@ {lock:l|empty} {wall:w|empty}", "wall w locks=l,l"), "duplicate lock association");
        bad(wrap("@ {ice|empty}", ""), "ice rejected");
        bad(wrap("@ {floor|fragile:a}", "fragile a resistance=1"), "fragile rejected");
        try {
            LOADER.load(file);
            throw new AssertionError("legacy loader accepted extended");
        } catch (LevelFormatException expected) {
            check(true, "historical ordinary rejects extension");
        }
        try {
            LOADER.loadExperimentalFragile(file);
            throw new AssertionError("fragile loader accepted extension");
        } catch (LevelFormatException expected) {
            check(true, "historical fragile rejects extension");
        }
        System.out.println("EXTENDED RESULT: " + passed + " PASS, 0 FAIL");
    }

    private static Position p(int row, int col) {
        return new Position(row, col);
    }

    private static String wrap(String row, String attributes) {
        return "!sokoban-txt 2\n[grid]\n" + row + "\n[attributes]\n" + attributes + "\n";
    }

    private static void bad(String text, String label) {
        try {
            PARSER.parse(text);
        } catch (LevelFormatException expected) {
            check(true, label);
            return;
        }
        throw new AssertionError("Invalid accepted: " + label);
    }

    private static void check(boolean value, String label) {
        if (!value) {
            throw new AssertionError(label);
        }
        passed++;
        System.out.println("PASS " + label);
    }
}
