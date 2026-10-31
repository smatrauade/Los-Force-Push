package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Theme, Command replay and Observer collaboration tests without a display. */
public final class DesktopExtensionsTest {
    private static int passed;
    private DesktopExtensionsTest() { }
    private static final class Probe implements GameView {
        GamePresentation state;
        UiCommands commands = UiCommands.EMPTY;
        LevelResult result;
        final List<LockChangeSubject.Event> events = new ArrayList<>();
        final List<SoundManager.Effect> effects = new ArrayList<>();
        @Override public void render(GamePresentation value) { state = value; }
        @Override public void showLoadError(String value) { throw new AssertionError(value); }
        @Override public void configureUiCommands(UiCommands value) { commands = value; }
        @Override public void showVictory(LevelResult value) { result = value; }
        @Override public void onGateChanged(LockChangeSubject.Event event) { events.add(event); }
        @Override public void playEffect(SoundManager.Effect effect) { effects.add(effect); }
    }
    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]); Path levels = root.resolve("src/desktop/resources/levels");
        Path fixture = root.resolve("src/test/resources/levels/extended-runtime.txt");
        List<VisualThemeFactory> themes = VisualThemes.all();
        check(themes.size() == 3 && !themes.get(0).palette().background().equals(themes.get(1).palette().background())
                && !themes.get(1).palette().background().equals(themes.get(2).palette().background()), "three palettes");
        PixelAssets assets = new PixelAssets();
        BufferedImage copper = assets.image("box"); assets.setTheme(themes.get(1)); BufferedImage night = assets.image("box");
        assets.setTheme(themes.get(2)); BufferedImage jade = assets.image("box");
        boolean nightDiff = false; boolean jadeDiff = false; boolean alphaEqual = true;
        for (int y = 0; y < 32; y++) { for (int x = 0; x < 32; x++) {
            int a = copper.getRGB(x, y), b = night.getRGB(x, y), c = jade.getRGB(x, y);
            nightDiff |= a != b; jadeDiff |= a != c; alphaEqual &= (a >>> 24) == (b >>> 24) && (a >>> 24) == (c >>> 24);
        } }
        check(nightDiff && jadeDiff && alphaEqual && jade.getWidth() == 32 && jade.getHeight() == 32, "distinct sprites preserve alpha and dimensions");
        assets.setTheme(themes.get(0)); check(assets.image("box").getRGB(10, 10) == copper.getRGB(10, 10), "original restored");
        RuntimeState initial = RuntimeState.fromExtended(new LevelLoader(new LevelParser()).loadExtended(fixture));
        BoardPanel panel = new BoardPanel(); panel.showState(initial); panel.setScale(3); panel.setFacing(Direction.LEFT);
        var size = panel.getPreferredSize(); panel.setSize(size);
        BufferedImage beforeTheme = paint(panel);
        panel.setTheme(themes.get(2)); panel.setTheme(themes.get(1)); panel.setTheme(themes.get(0));
        check(panel.getScale() == 3 && panel.getPreferredSize().equals(size) && initial.boxAt(new Position(1, 2)).id().equals("k1"), "theme preserves world and zoom");
        BufferedImage afterTheme = paint(panel);
        check(java.util.Arrays.equals(beforeTheme.getRGB(0, 0, size.width, size.height, null, 0, size.width),
                afterTheme.getRGB(0, 0, size.width, size.height, null, 0, size.width)), "orientation and original pixels survive theme roundtrip");
        ReplayRecorder recorder = new ReplayRecorder(initial, "fixture"); RuntimeMovementEngine engine = new RuntimeMovementEngine();
        RuntimeState current = initial;
        for (Direction d : List.of(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT)) {
            var result = new MoveCommand(d).execute(engine, current);
            check(result.kind() != MoveResult.Kind.BLOCKED, "command executes real engine");
            recorder.record(d); current = result.state();
        }
        check(recorder.verified(initial).isEmpty(), "mismatched replay rejected");
        ReplayTrace trace = recorder.verified(current).orElseThrow();
        ReplayPlayer player = new ReplayPlayer(trace);
        check(player.verify(), "complete state equivalence");
        while (player.hasNext()) { player.step(); }
        check(player.currentState().equals(current) && player.index() == 3, "playback final world");
        player.reset(); check(player.currentState().equals(initial) && player.index() == 0, "playback reset");
        ReplayRecorder limited = new ReplayRecorder(initial, "fixture");
        for (int i = 0; i <= ReplayRecorder.MAX_COMMANDS; i++) { limited.record(Direction.RIGHT); }
        check(limited.verified(initial).isEmpty(), "limit disables replay without truncation");
        limited.reset(initial, "fixture"); check(limited.verified(initial).isPresent(), "restart resets replay limit");
        Probe probe = new Probe(); GameController game = new GameController(probe);
        game.load(fixture, LevelCatalog.Format.EXTENDED); int eventsBefore = probe.events.size();
        game.move(Direction.UP); check(probe.events.size() == eventsBefore, "blocked emits no observer event");
        game.move(Direction.RIGHT); check(probe.events.get(probe.events.size() - 1).origin() == LockChangeSubject.Origin.MOVE
                && probe.effects.contains(SoundManager.Effect.LOCK) && probe.effects.contains(SoundManager.Effect.GATE), "move observer and audio");
        game.move(Direction.LEFT); game.move(Direction.DOWN); game.move(Direction.UP); game.move(Direction.RIGHT);
        check(probe.commands.undo() != null, "undo available"); probe.commands.undo().run();
        check(probe.events.get(probe.events.size() - 1).origin() == LockChangeSubject.Origin.UNDO
                && !probe.state.state().lockActive("l1"), "undo observer reconciles lock");
        game.move(Direction.RIGHT); int sounds = count(probe.effects, SoundManager.Effect.LOCK);
        game.restart(); check(probe.events.get(probe.events.size() - 1).origin() == LockChangeSubject.Origin.RESTART
                && count(probe.effects, SoundManager.Effect.LOCK) == sounds, "restart observer without lock sound");
        game.move(Direction.RIGHT); game.load(levels.resolve("campaign-a.txt"));
        check(probe.events.get(probe.events.size() - 1).origin() == LockChangeSubject.Origin.LEVEL_CHANGE,
                "level change observer");
        game.move(Direction.UP); // blocked: must not be recorded
        game.move(Direction.RIGHT); ReplayTrace completed = game.replay().orElseThrow();
        check(new ReplayPlayer(completed).verify() && probe.result.score() == 9965
                && completed.commands().size() == 1, "completed replay ignores blocked movement and preserves score");
        game.move(Direction.RIGHT); check(game.replay().orElseThrow().equals(completed), "finished replay stable");
        Probe branchView = new Probe(); GameController branch = new GameController(branchView);
        branch.load(levels.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED);
        for (Direction d : List.of(Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT)) { branch.move(d); }
        branch.undo();
        for (Direction d : List.of(Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT,
                Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.LEFT)) { branch.move(d); }
        ReplayTrace branchTrace = branch.replay().orElseThrow();
        check(branchTrace.commands().size() == 11 && new ReplayPlayer(branchTrace).verify()
                && branchView.result.score() == 9715, "undo removes abandoned commands and preserves score");
        Probe restartView = new Probe(); GameController restartGame = new GameController(restartView);
        restartGame.load(levels.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED);
        restartGame.move(Direction.RIGHT); restartGame.restart();
        for (Direction d : List.of(Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.RIGHT,
                Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT, Direction.LEFT)) { restartGame.move(d); }
        check(restartGame.replay().orElseThrow().commands().size() == 11 && restartView.result.score() == 9815,
                "restart clears previous commands");
        ReplayRecorder fragileRecorder = new ReplayRecorder(initial, "fragile"); RuntimeState fragileState = initial;
        for (Direction d : List.of(Direction.DOWN, Direction.RIGHT, Direction.RIGHT)) {
            var move = new MoveCommand(d).execute(engine, fragileState); fragileRecorder.record(d); fragileState = move.state();
        }
        check(fragileState.boxAt(new Position(2, 4)) == null
                && fragileRecorder.verified(fragileState).isPresent(), "fragile decrement and break replay");
        Probe campaignView = new Probe(); GameController campaign = new GameController(campaignView);
        campaign.loadCatalog(new LevelCatalog(List.of(
                new LevelCatalog.Entry("a", levels.resolve("campaign-a.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("b", levels.resolve("campaign-b.txt"), LevelCatalog.Format.EXTENDED))));
        campaign.move(Direction.RIGHT); ReplayTrace firstReplay = campaign.replay().orElseThrow();
        campaign.nextLevel();
        check(campaign.replay().orElseThrow().equals(firstReplay) && campaignView.state.levelName().equals("b"),
                "completed replay survives level advance");
        campaign.close(); branch.close(); restartGame.close();
        LockChangeSubject subject = new LockChangeSubject(); List<String> seen = new ArrayList<>();
        LockChangeSubject.Observer one = event -> seen.add("one"); LockChangeSubject.Observer two = event -> seen.add("two");
        subject.subscribe(one); subject.subscribe(two); subject.subscribe(one);
        var active = engine.move(initial, Direction.RIGHT).state();
        subject.publish(LockChangeSubject.Origin.MOVE, initial, active);
        check(seen.equals(List.of("one", "two")), "two observers ordered once");
        subject.unsubscribe(one); subject.publish(LockChangeSubject.Origin.UNDO, active, initial);
        check(seen.equals(List.of("one", "two", "two")), "unsubscribe");
        subject.publish(LockChangeSubject.Origin.MOVE, initial, initial);
        check(seen.size() == 3, "no event without difference");
        var row = List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK, RuntimeState.Ground.FLOOR,
                RuntimeState.Ground.LOCK, RuntimeState.Ground.DYNAMIC_WALL);
        var locks = List.of(new RuntimeState.Lock("a", new Position(0, 1)), new RuntimeState.Lock("b", new Position(0, 3)));
        var walls = List.of(new RuntimeState.DynamicWall("w", new Position(0, 4), java.util.Set.of("a", "b")));
        RuntimeState a = new RuntimeState(List.of(row), new Position(0, 0),
                List.of(RuntimeState.Box.key("k", new Position(0, 1))), locks, walls);
        RuntimeState b = new RuntimeState(List.of(row), new Position(0, 0),
                List.of(RuntimeState.Box.key("k", new Position(0, 3))), locks, walls);
        List<LockChangeSubject.Event> multiple = new ArrayList<>();
        subject.subscribe(multiple::add); subject.publish(LockChangeSubject.Origin.LEVEL_CHANGE, a, b);
        check(multiple.size() == 1 && multiple.get(0).locks().equals(List.of(
                new LockChangeSubject.Delta("a", true, false), new LockChangeSubject.Delta("b", false, true)))
                && !multiple.get(0).wallsChanged(), "multiple lock deltas are ordered and wall remains open");
        subject.close(); check(subject.subscriberCount() == 0, "subject close releases observers");
        game.close();
        System.out.println("EXTENSIONS RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static BufferedImage paint(BoardPanel panel) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var graphics = image.createGraphics();
        try { panel.paint(graphics); } finally { graphics.dispose(); }
        return image;
    }
    private static int count(List<SoundManager.Effect> values, SoundManager.Effect target) {
        return (int) values.stream().filter(v -> v == target).count();
    }
    private static void check(boolean value, String label) {
        if (!value) { throw new AssertionError(label); }
        passed++; System.out.println("PASS " + label);
    }
}
