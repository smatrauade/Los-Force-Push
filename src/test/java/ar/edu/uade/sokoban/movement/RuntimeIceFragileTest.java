package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.ExperimentalFragileLevel;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.session.GameSession;
import ar.edu.uade.sokoban.session.LevelCatalog;
import ar.edu.uade.sokoban.session.LevelProgression;
import ar.edu.uade.sokoban.session.VictoryEvaluator;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/** INT-01..04 targeted integration checks, no Swing or network. */
public final class RuntimeIceFragileTest {
    private static final RuntimeMovementEngine ENGINE = new RuntimeMovementEngine();
    private static final LevelLoader LOADER = new LevelLoader(new LevelParser());
    private static int passed;

    private RuntimeIceFragileTest() { }

    public static void main(String[] args) throws Exception {
        Path fixtures = Path.of(args[0]);
        testIds(fixtures);
        testIce(fixtures);
        testFragileIce();
        testExistingKeyWall();
        testFailClose(fixtures);
        System.out.println("RUNTIME ICE FRAGILE RESULT: " + passed + " PASS, 0 FAIL");
    }

    private static void testIds(Path fixtures) throws Exception {
        Path special = Files.createTempFile("sokoban-i1b1-fragile-", ".txt");
        try {
            Files.writeString(special, "!sokoban-txt 2\n[grid]\n"
                    + "# # # # # # # #\n"
                    + "# @ {floor|fragile:a} _ _ {goal|fragile:b} . #\n"
                    + "# # # # # # # #\n[attributes]\n"
                    + "fragile a resistance=2\nfragile b resistance=3", StandardCharsets.UTF_8);
            ExperimentalFragileLevel definition = LOADER.loadExperimentalFragile(special);
            RuntimeState initial = RuntimeState.fromExperimentalFragile(definition);
            RuntimeState.Box a = initial.boxAt(p(1, 2));
            RuntimeState.Box b = initial.boxAt(p(1, 5));
            check(a.kind() == RuntimeState.BoxKind.FRAGILE && a.id().equals("a")
                    && a.resistance() == 2, "INT-01 first identity/resistance TXT-to-runtime");
            check(b.kind() == RuntimeState.BoxKind.FRAGILE && b.id().equals("b")
                    && b.resistance() == 3, "INT-01 second identity/resistance TXT-to-runtime");
            check(initial.terrain().get(1).get(5) == RuntimeState.Ground.GOAL,
                    "INT-01 fragile on goal retains goal terrain");
            GameSession session = new GameSession(initial, "fragile_test");
            RuntimeMoveResult first = session.move(Direction.RIGHT);
            check(first.kind() == MoveResult.Kind.PUSHED && first.boxDestination().equals(p(1, 3))
                    && first.state().boxAt(p(1, 3)).id().equals("a")
                    && first.state().boxAt(p(1, 3)).resistance() == 1,
                    "INT-01 push retains identity and deducts one");
            check(first.state().boxAt(p(1, 5)).resistance() == 3 && initial.boxAt(p(1, 2)) == a,
                    "INT-01 other boxes and initial state remain unchanged");
            RuntimeMoveResult second = session.move(Direction.RIGHT);
            check(second.kind() == MoveResult.Kind.PUSHED && second.boxShattered()
                    && second.boxDestination().equals(p(1, 4))
                    && second.state().boxAt(p(1, 4)) == null
                    && second.state().boxAt(p(1, 5)).id().equals("b")
                    && second.state().boxAt(p(1, 5)).resistance() == 3
                    && session.metrics().pushes() == 2,
                    "INT-01 two pushes consume a:2, preserve b:3 and leave no ghost box");
            session.restart();
            check(session.currentState() == initial && session.metrics().pushes() == 0,
                    "INT-01 restart restores initial identities and resistance");
            LevelProgression progression = new LevelProgression(new LevelCatalog(List.of(
                    new LevelCatalog.Entry("fragile", special, LevelCatalog.Format.EXPERIMENTAL_FRAGILE))));
            check(progression.currentState().boxAt(p(1, 2)).id().equals("a")
                    && progression.move(Direction.RIGHT).state().boxAt(p(1, 3)).resistance() == 1,
                    "INT-01 explicit catalog path to playable runtime and movement");
        } finally {
            Files.deleteIfExists(special);
        }
        // Preserve the fail-close legacy entry point even after introducing the new adapter.
        boolean rejected = false;
        try {
            LOADER.load(fixtures.resolve("fragile.txt"));
        } catch (ar.edu.uade.sokoban.level.LevelFormatException expected) {
            rejected = true;
        }
        check(rejected, "INT-01 standard loader still rejects lossy fragile");
    }

    private static void testIce(Path fixtures) throws Exception {
        RuntimeState initial = RuntimeState.fromLevel(LOADER.load(fixtures.resolve("ice.txt")));
        check(initial.terrain().get(1).get(3) == RuntimeState.Ground.ICE,
                "INT-02 ICE TXT loads into runtime without valueOf failure");
        GameSession session = new GameSession(initial, "ice");
        RuntimeMoveResult result = session.move(Direction.RIGHT);
        check(result.kind() == MoveResult.Kind.PUSHED && result.boxDestination().equals(p(1, 6))
                && result.boxPath().equals(List.of(p(1, 3), p(1, 4), p(1, 5), p(1, 6))),
                "INT-02 ICE slide follows the authoritative path onto ordinary goal");
        check(result.state().boxAt(p(1, 6)) != null && session.metrics().pushes() == 1
                && session.metrics().moves() == 1 && initial.boxAt(p(1, 2)) != null,
                "INT-02 one push, immutable initial state");
        LevelProgression campaign = new LevelProgression(new LevelCatalog(List.of(
                new LevelCatalog.Entry("ice", fixtures.resolve("ice.txt"), LevelCatalog.Format.STANDARD))));
        RuntimeMoveResult campaignMove = campaign.move(Direction.RIGHT);
        check(campaignMove.boxDestination().equals(p(1, 6))
                && campaignMove.boxPath().equals(result.boxPath())
                && campaign.metrics().pushes() == 1,
                "INT-02 ICE crosses actual LevelProgression loading and gameplay");
        RuntimeState blockedExit = RuntimeState.fromLevel(LOADER.load(fixtures.resolve("ice_blocked_exit.txt")));
        RuntimeMoveResult blocked = ENGINE.move(blockedExit, Direction.RIGHT);
        check(blocked.boxDestination().equals(p(1, 4))
                && blocked.boxPath().equals(List.of(p(1, 3), p(1, 4))),
                "INT-02 E03 wall exit stops at last free ICE");
        RuntimeState noEntry = RuntimeState.fromLevel(LOADER.load(fixtures.resolve("ice_no_entry.txt")));
        RuntimeMoveResult walking = ENGINE.move(noEntry, Direction.RIGHT);
        check(walking.boxDestination().equals(p(1, 3))
                && walking.boxPath().equals(List.of(p(1, 3))),
                "INT-02 entering normal floor does not start slide");
        RuntimeState simple = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.ICE, RuntimeState.Ground.ICE)),
                p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1))));
        check(ENGINE.move(simple, Direction.RIGHT).boxDestination().equals(p(0, 3)),
                "INT-02 E03 boundary stops on last free ICE");
        RuntimeState obstructed = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.ICE, RuntimeState.Ground.ICE)),
                p(0, 0), List.of(RuntimeState.Box.normal(p(0, 1)), RuntimeState.Box.normal(p(0, 3))));
        check(ENGINE.move(obstructed, Direction.RIGHT).boxDestination().equals(p(0, 2)),
                "INT-02 occupied next ICE stops at last free ICE");
    }

    private static void testFragileIce() {
        RuntimeState initial = fragileIce(2);
        GameSession session = new GameSession(initial, "fragile_ice");
        RuntimeMoveResult first = session.move(Direction.RIGHT);
        check(first.boxDestination().equals(p(0, 4)) && first.boxPath().equals(
                List.of(p(0, 2), p(0, 3), p(0, 4))) && !first.boxShattered(),
                "INT-03 fragile completes slide before resistance reduction");
        check(first.state().boxAt(p(0, 4)).id().equals("f1")
                && first.state().boxAt(p(0, 4)).resistance() == 1,
                "INT-03 fragile retains identity and one remaining resistance");
        check(initial.boxAt(p(0, 1)).resistance() == 2 && session.metrics().pushes() == 1,
                "INT-03 immutable source and one push across three cells");
        GameSession breakSession = new GameSession(fragileIce(1), "break_once");
        RuntimeMoveResult playedBreak = breakSession.move(Direction.RIGHT);
        check(playedBreak.boxShattered() && breakSession.currentState().boxes().isEmpty()
                && breakSession.metrics().moves() == 1 && breakSession.metrics().pushes() == 1,
                "INT-03 gameplay shatter preserves session state and metrics without restart");
        RuntimeMoveResult broken = ENGINE.move(fragileIce(1), Direction.RIGHT);
        check(broken.kind() == MoveResult.Kind.PUSHED && broken.boxShattered()
                && broken.boxDestination().equals(p(0, 4)) && broken.boxPath().size() == 3,
                "INT-03 E04 breaks only after full slide");
        check(broken.state().boxes().isEmpty() && broken.state().boxAt(p(0, 4)) == null
                && !new VictoryEvaluator().evaluate(broken.state()).won(),
                "INT-03 shattered fragile leaves no occupant or false victory");
        RuntimeState stuck = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.WALL)),
                p(0, 0), List.of(RuntimeState.Box.fragile("f1", p(0, 1), 1)));
        RuntimeMoveResult noPush = ENGINE.move(stuck, Direction.RIGHT);
        check(noPush.kind() == MoveResult.Kind.BLOCKED && noPush.state() == stuck,
                "INT-03 blocked push cannot damage fragile");
    }

    private static RuntimeState fragileIce(int resistance) {
        return new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.ICE, RuntimeState.Ground.ICE,
                RuntimeState.Ground.GOAL, RuntimeState.Ground.WALL)),
                p(0, 0), List.of(RuntimeState.Box.fragile("f1", p(0, 1), resistance)));
    }

    private static void testExistingKeyWall() {
        RuntimeState state = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.DYNAMIC_WALL)),
                p(0, 0), List.of(RuntimeState.Box.key("key", p(0, 1))),
                List.of(new RuntimeState.Lock("lock", p(0, 2))),
                List.of(new RuntimeState.DynamicWall("wall", p(0, 4), Set.of("lock"))));
        RuntimeMoveResult onLock = ENGINE.move(state, Direction.RIGHT);
        check(onLock.state().lockActive("lock") && onLock.state().wallOpen(p(0, 4))
                && onLock.lockChanges().size() == 1 && onLock.lockChanges().get(0).active(),
                "INT-04 key on lock opens dynamic wall and emits change");
        RuntimeState closureRisk = new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK,
                RuntimeState.Ground.DYNAMIC_WALL, RuntimeState.Ground.FLOOR)),
                p(0, 0), List.of(RuntimeState.Box.key("key", p(0, 1))),
                List.of(new RuntimeState.Lock("lock", p(0, 2))),
                List.of(new RuntimeState.DynamicWall("wall", p(0, 3), Set.of("lock"))));
        RuntimeMoveResult keyOnLock = ENGINE.move(closureRisk, Direction.RIGHT);
        RuntimeMoveResult rejectClosure = ENGINE.move(keyOnLock.state(), Direction.RIGHT);
        check(keyOnLock.state().wallOpen(p(0, 3))
                && rejectClosure.kind() == MoveResult.Kind.BLOCKED
                && rejectClosure.state() == keyOnLock.state()
                && rejectClosure.state().lockActive("lock")
                && rejectClosure.state().wallOpen(p(0, 3)),
                "INT-04 occupied dynamic wall closes only atomically or blocks change");
        RuntimeMoveResult offLock = ENGINE.move(onLock.state(), Direction.RIGHT);
        check(!offLock.state().lockActive("lock") && !offLock.state().wallOpen(p(0, 4))
                && offLock.lockChanges().size() == 1 && !offLock.lockChanges().get(0).active(),
                "INT-04 E06 key leaving lock closes wall");
    }

    private static void testFailClose(Path fixtures) throws Exception {
        boolean failed = false;
        try {
            RuntimeState.fromLevel(new LevelParser().parse(Files.readString(
                    fixtures.resolve("fragile.txt"), StandardCharsets.UTF_8)));
        } catch (IllegalArgumentException expected) {
            failed = true;
        }
        check(failed, "lossy runtime construction from C2 fragile Level rejected");
        failed = false;
        try {
            RuntimeState.Box.fragile("f1", p(0, 1), 0);
        } catch (IllegalArgumentException expected) {
            failed = true;
        }
        check(failed, "zero resistance rejected");
        failed = false;
        try {
            new RuntimeState(List.of(List.of(RuntimeState.Ground.FLOOR,
                    RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR)), p(0, 0),
                    List.of(RuntimeState.Box.fragile("id", p(0, 1), 1),
                            RuntimeState.Box.fragile("id", p(0, 2), 2)));
        } catch (IllegalArgumentException expected) {
            failed = true;
        }
        check(failed, "duplicate fragile ids rejected");
        check(RuntimeState.fromLevel(LOADER.load(fixtures.resolve("ice.txt")))
                .toBoard().cellAt(p(1, 3)).terrain() == ar.edu.uade.sokoban.level.Terrain.ICE,
                "ICE projection preserves terrain for display-only Board consumers");
        RuntimeState.Box fragile = fragileIce(12).boxAt(p(0, 1));
        check(fragileIce(12).toBoard().cellAt(p(0, 1)).resistance() == 12
                && fragile.id().equals("f1"),
                "fragile projection preserves resistance, authoritative ID stays in runtime");
        // D-020 already permits ICE empty tiles adjacent to a floor Fragile;
        // only a fragile token placed directly on ICE is outside its grammar.
        RuntimeState fromTxt = RuntimeState.fromExperimentalFragile(
                LOADER.loadExperimentalFragile(fixtures.resolve("fragile_on_ice.txt")));
        RuntimeMoveResult fromTxtPush = ENGINE.move(fromTxt, Direction.RIGHT);
        check(fromTxt.terrain().get(1).get(3) == RuntimeState.Ground.ICE
                && fromTxtPush.boxDestination().equals(p(1, 6))
                && fromTxtPush.state().boxAt(p(1, 6)).id().equals("f1")
                && fromTxtPush.state().boxAt(p(1, 6)).resistance() == 1,
                "INT-03 existing TXT fragile-on-floor plus ICE path preserves identity");
        failed = false;
        String unratified = Files.readString(fixtures.resolve("fragile_on_ice.txt"), StandardCharsets.UTF_8)
                .replace("{floor|fragile:f1}", "{ice|fragile:f1}");
        Path unsupported = Files.createTempFile("sokoban-i1b1-unsupported-", ".txt");
        try {
            Files.writeString(unsupported, unratified, StandardCharsets.UTF_8);
            try {
                LOADER.loadExperimentalFragile(unsupported);
            } catch (ar.edu.uade.sokoban.level.LevelFormatException expected) {
                failed = true;
            }
        } finally {
            Files.deleteIfExists(unsupported);
        }
        check(failed, "D-020 experimental definition rejects unratified initial Fragile-on-ICE token");
    }

    private static Position p(int row, int col) {
        return new Position(row, col);
    }

    private static void check(boolean actual, String description) {
        if (!actual) {
            throw new AssertionError(description);
        }
        passed++;
        System.out.println("PASS " + description);
    }
}
