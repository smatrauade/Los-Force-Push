package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.nio.file.Path;
import java.util.List;

/** Acceptance of the actual shipped TXT through the same controller used by Swing. */
public final class ShippedLevelsIntegrationTest {
    private static int passed;

    private ShippedLevelsIntegrationTest() { }

    private static final class Probe implements GameView {
        GamePresentation screen;
        UiCommands commands = UiCommands.EMPTY;
        LevelResult victory;
        String error;
        MoveResult.Kind lastMove;
        int victories;
        @Override public void render(GamePresentation value) { screen = value; }
        @Override public void configureUiCommands(UiCommands value) { commands = value; }
        @Override public void showVictory(LevelResult value) { victory = value; victories++; }
        @Override public void showLoadError(String message) { error = message; }
        @Override public void onMove(MoveResult.Kind kind) { lastMove = kind; }
    }

    public static void main(String[] args) throws Exception {
        require(args.length == 1, "expected one shipped-level directory");
        Path levels = Path.of(args[0]);
        demoToKeyCampaign(levels);
        fragileDemoIsSolvable(levels);
        iceDemoIsSolvable(levels);
        System.out.println("CAMPAIGN RESULT: " + passed + " PASS, 0 FAIL");
    }

    private static void demoToKeyCampaign(Path levels) throws Exception {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.loadCatalog(new LevelCatalog(List.of(
                new LevelCatalog.Entry("demo", levels.resolve("demo.txt"), LevelCatalog.Format.STANDARD),
                new LevelCatalog.Entry("campaign_b", levels.resolve("campaign-b.txt"),
                        LevelCatalog.Format.EXTENDED))));
        require(view.error == null && view.screen != null, "demo catalog failed to load");
        require(view.screen.levelName().equals("demo.txt")
                && view.screen.moves() == 0 && view.screen.pushes() == 0, "wrong first level");
        move(game, view, List.of(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT));
        require(view.victories == 1 && view.victory != null, "demo victory missing");
        require(view.victory.moves() == 3 && view.victory.pushes() == 2
                && view.victory.undoUses() == 0 && view.victory.score() == 993,
                "demo score, pushes or moves incorrect");
        require(view.commands.nextLevel() != null && view.commands.restart() == null,
                "next-level action not available after victory");
        view.commands.nextLevel().run();
        require(view.screen.levelName().equals("campaign-b.txt")
                && view.screen.moves() == 0 && view.screen.pushes() == 0,
                "next campaign level did not reset current metrics");
        require(game.results().size() == 1
                && game.results().get(0).metrics().moves() == 3,
                "completed demo was not retained");
        move(game, view, List.of(Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN,
                Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP,
                Direction.LEFT, Direction.LEFT));
        require(view.victories == 2 && view.victory != null, "key/lock campaign victory missing");
        require(view.victory.moves() == 11 && view.victory.pushes() == 3
                && view.victory.undoUses() == 0 && view.victory.score() == 983,
                "key/lock campaign score incorrect");
        require(view.commands.nextLevel() == null && game.results().size() == 2,
                "finished campaign exposed a nonexistent next level");
        require(game.results().get(0).metrics().moves() == 3,
                "advancing a campaign mutated historical results");
    }

    private static void fragileDemoIsSolvable(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levels.resolve("fragile_demo.txt"));
        require(view.error == null && view.screen != null, "fragile TXT rejected");
        RuntimeState.Box before = view.screen.state().boxAt(new Position(1, 2));
        require(before != null && before.kind() == RuntimeState.BoxKind.FRAGILE
                && "f1".equals(before.id()) && before.resistance() == 4,
                "fragile ID or resistance lost");
        move(game, view, List.of(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT));
        require(view.victories == 1 && view.victory != null, "fragile demo is not solvable");
        RuntimeState.Box finalBox = view.screen.state().boxAt(new Position(1, 5));
        require(finalBox != null && finalBox.kind() == RuntimeState.BoxKind.FRAGILE
                && "f1".equals(finalBox.id()) && finalBox.resistance() == 1,
                "fragile final state lost ID, position or remaining resistance");
        require(view.victory.score() == 991 && view.victory.pushes() == 3,
                "fragile victory scoring incorrect");
        GamePresentation stable = view.screen;
        LevelResult victory = view.victory;
        game.load(levels.resolve("missing-integration-level.txt"));
        require(view.error != null && view.screen == stable && view.victory == victory,
                "failed TXT load corrupted a finalized level");
    }

    private static void iceDemoIsSolvable(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levels.resolve("ice_demo.txt"));
        require(view.error == null && view.screen != null, "ICE demo TXT rejected");
        move(game, view, List.of(Direction.DOWN, Direction.RIGHT));
        require(view.victories == 1 && view.victory != null, "ICE demo is not solvable");
        RuntimeState.Box box = view.screen.state().boxAt(new Position(2, 7));
        require(box != null && box.kind() == RuntimeState.BoxKind.NORMAL,
                "ICE slide did not reach the goal");
        require(view.victory.moves() == 2 && view.victory.pushes() == 1
                && view.victory.score() == 996, "ICE push must count once");
    }

    private static void move(GameController game, Probe view, List<Direction> path) {
        for (Direction direction : path) {
            game.move(direction);
            require(view.lastMove != MoveResult.Kind.BLOCKED, "shipped witness path was blocked");
        }
    }

    private static void require(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
        passed++;
    }
}
