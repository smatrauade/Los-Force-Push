package ar.edu.uade.sokoban.level;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** External loading, validation and full-runtime integration without a test framework. */
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
        check(initial.player().equals(p(1, 1)) && initial.boxes().size() == 3
                && initial.boxAt(p(1, 2)).id().equals("k1") && initial.boxAt(p(2, 2)).resistance() == 2
                && initial.boxAt(p(2, 1)).kind() == RuntimeState.BoxKind.NORMAL
                && initial.terrain().get(1).get(3) == RuntimeState.Ground.ICE
                && initial.terrain().get(2).get(3) == RuntimeState.Ground.GOAL && !initial.wallOpen(p(1, 6)), "complete external state");
        RuntimeMovementEngine engine = new RuntimeMovementEngine();
        RuntimeState opened = engine.move(initial, Direction.RIGHT).state();
        check(opened.lockActive("l1") && opened.wallOpen(p(1, 6)) && opened.boxAt(p(1, 4)).id().equals("k1"), "ice key activates lock");
        RuntimeState approach = engine.move(opened, Direction.RIGHT).state();
        RuntimeState closed = engine.move(approach, Direction.RIGHT).state();
        check(!closed.lockActive("l1") && !closed.wallOpen(p(1, 6)), "key leaves lock and closes wall");
        check(engine.move(closed, Direction.RIGHT).kind() == MoveResult.Kind.BLOCKED && opened.wallOpen(p(1, 6))
                && initial.boxAt(p(1, 2)).id().equals("k1"), "blocked and past states unchanged");
        RuntimeState down = engine.move(initial, Direction.DOWN).state();
        RuntimeState pushed = engine.move(down, Direction.RIGHT).state();
        check(pushed.boxAt(p(2, 3)).resistance() == 1 && pushed.terrain().get(2).get(3) == RuntimeState.Ground.GOAL, "fragile loses one on goal");
        RuntimeState broken = engine.move(pushed, Direction.RIGHT).state();
        check(broken.boxAt(p(2, 4)) == null && pushed.boxAt(p(2, 3)).id().equals("f1"), "fragile breaks and snapshot remains");
        check(RuntimeState.fromExtended(LOADER.loadExtended(fixtures.resolve("extended-ordinary.txt"))).boxes().size() == 1, "ordinary external");
        check(LOADER.loadExtended(file).equals(PARSER.parse(text)), "deterministic reload");
        check(LOADER.loadExtended(fixtures.resolve("experimental-fragile.txt")).base()
                .equals(LOADER.loadExperimentalFragile(fixtures.resolve("experimental-fragile.txt"))),
                "historical fragile compatibility");
        String reordered = text.replace("wall w1 locks=l1\nfragile f1 resistance=2", "fragile f1 resistance=2\nwall w1 locks=l1");
        check(PARSER.parse(reordered).equals(definition), "attribute order independent");
        String multi = wrap("@ {goal|key:k} {ice|fragile:a} {ice|box} {lock:l|key:q} {wall:w|empty} {wall:x|empty}",
                "fragile a resistance=1\nwall x locks=l\nwall w locks=l");
        RuntimeState many = RuntimeState.fromExtended(PARSER.parse(multi));
        check(many.lockActive("l") && many.wallOpen(p(0, 5)) && many.wallOpen(p(0, 6))
                && many.boxAt(p(0, 1)).kind() == RuntimeState.BoxKind.KEY
                && many.terrain().get(0).get(1) == RuntimeState.Ground.GOAL, "goal key and two walls open");
        String twoLocks = wrap("@ {lock:a|key:k} {lock:b|empty} {wall:w|empty}", "wall w locks=b,a");
        check(RuntimeState.fromExtended(PARSER.parse(twoLocks)).wallOpen(p(0, 3)), "two locks one wall");
        String twoFragile = wrap("@ {floor|fragile:a} {goal|fragile:b}", "fragile b resistance=12\nfragile a resistance=1");
        check(PARSER.parse(twoFragile).base().fragileBoxes().size() == 2, "two fragile reordered");
        RuntimeState occupiedIce = RuntimeState.fromExtended(PARSER.parse(wrap("{ice|player} {ice|key:k} {lock:l|box} {wall:w|empty}", "wall w locks=l")));
        check(occupiedIce.terrain().get(0).get(0) == RuntimeState.Ground.ICE
                && occupiedIce.boxAt(p(0, 1)).kind() == RuntimeState.BoxKind.KEY
                && !occupiedIce.lockActive("l"), "ice player and key; normal on lock");
        RuntimeState openOccupied = RuntimeState.fromExtended(PARSER.parse(wrap("@ {lock:l|key:k} {wall:w|fragile:f}",
                "fragile f resistance=2\nwall w locks=l")));
        check(openOccupied.wallOpen(p(0, 2)) && openOccupied.boxAt(p(0, 2)).id().equals("f"), "open wall occupied");
        try { definition.keys().clear(); throw new AssertionError("mutable keys"); }
        catch (UnsupportedOperationException expected) { check(true, "definition immutable"); }
        bad(text.replace("!sokoban-txt 2", "!sokoban-txt 3"), "header");
        bad(text.replace("[attributes]", "[missing]"), "missing section");
        bad(text.replace("# @", "#  @"), "separator");
        bad("\ufeff" + text, "BOM");
        bad(text.replace("# @", "# ?"), "unknown token");
        bad(text.replace("{ice|empty}", "{ice|goal}"), "unsupported combination");
        bad(text.replace("key:k1", "key:BAD"), "invalid id");
        bad(text.replace("resistance=2", "resistance=0"), "zero resistance");
        bad(text.replace("resistance=2", "resistance=2147483648"), "overflow resistance");
        bad(text.replace("fragile f1 resistance=2\n", ""), "missing resistance");
        bad(text + "fragile f1 resistance=2\n", "duplicate resistance");
        bad(text.replace("locks=l1", "locks=missing"), "missing lock reference");
        bad(text.replace("wall w1 locks=l1\n", ""), "missing wall metadata");
        bad(text + "wall w1 locks=l1\n", "duplicate wall metadata");
        bad(text + "unknown data\n", "unknown metadata");
        bad(text.substring(0, text.indexOf("[attributes]")), "truncated");
        bad(text.replace("# @", "# _"), "missing player");
        bad(text.replace("# @", "# @ @"), "irregular row");
        bad(wrap("@ @", ""), "duplicate player");
        bad("!sokoban-txt 2\n[grid]\n" + "@\n".repeat(101) + "[attributes]", "height limit");
        bad("@", "no legacy fallback");
        bad(wrap("@ {floor|key:a} {floor|key:a}", ""), "duplicate key");
        bad(wrap("@ {lock:l|empty} {wall:w|player}", "wall w locks=l"), "duplicate player or closed occupied wall");
        bad(wrap("@ {lock:l|empty} {wall:w|box}", "wall w locks=l"), "closed occupied wall");
        bad(wrap("@ {lock:l|empty} {wall:w|empty}", "wall w locks=l,l"), "duplicate lock association");
        bad(wrap("@ {floor|fragile:a} {floor|key:a}", "fragile a resistance=1"), "duplicate box id");
        bad(wrap("@ {lock:l|empty} {lock:l|empty}", ""), "duplicate lock id");
        bad(wrap("@ {wall:w|empty} {wall:w|empty}", "wall w locks=l"), "duplicate wall id");
        bad(wrap("@ " + "_ ".repeat(100) + "_", ""), "width limit");
        Path temp = Files.createTempFile("sokoban-extended-", ".txt");
        try {
            Files.writeString(temp, text, StandardCharsets.UTF_8);
            ExtendedLevelDefinition original = LOADER.loadExtended(temp);
            Files.writeString(temp, text.replace("resistance=2", "resistance=3"), StandardCharsets.UTF_8);
            check(LOADER.loadExtended(temp).base().fragileBoxes().get(0).initialResistance() == 3
                    && original.base().fragileBoxes().get(0).initialResistance() == 2, "external edit reload");
            Files.writeString(temp, text.replace("resistance=2", "resistance=0"), StandardCharsets.UTF_8);
            try { LOADER.loadExtended(temp); throw new AssertionError("invalid reload accepted"); }
            catch (LevelFormatException expected) { check(original.equals(definition), "invalid reload atomic"); }
            Files.write(temp, new byte[] {(byte) 0xc3, (byte) 0x28});
            try { LOADER.loadExtended(temp); throw new AssertionError("invalid UTF-8 accepted"); }
            catch (IOException expected) { check(true, "invalid UTF-8"); }
            Files.write(temp, new byte[65_537]);
            try { LOADER.loadExtended(temp); throw new AssertionError("oversize accepted"); }
            catch (LevelFormatException expected) { check(true, "byte limit"); }
        } finally { Files.deleteIfExists(temp); }
        try { LOADER.load(file); throw new AssertionError("legacy loader accepted extended"); }
        catch (LevelFormatException expected) { check(true, "historical ordinary rejects extension"); }
        try { LOADER.loadExperimentalFragile(file); throw new AssertionError("fragile loader accepted extension"); }
        catch (LevelFormatException expected) { check(true, "historical fragile rejects extension"); }
        System.out.println("EXTENDED RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static Position p(int r, int c) { return new Position(r, c); }
    private static String wrap(String row, String attributes) { return "!sokoban-txt 2\n[grid]\n" + row + "\n[attributes]\n" + attributes + "\n"; }
    private static void bad(String text, String label) {
        try { PARSER.parse(text); } catch (LevelFormatException expected) { check(true, label); return; }
        throw new AssertionError("Invalid accepted: " + label);
    }
    private static void check(boolean value, String label) {
        if (!value) { throw new AssertionError(label); }
        passed++; System.out.println("PASS " + label);
    }
}
