package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.replay.ReplayPlayer;
import ar.edu.uade.sokoban.replay.ReplayTrace;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.nio.file.Path;
import java.util.List;

/** Solvable witnesses for the exact catalog opened by NUEVA PARTIDA. */
public final class ReleaseCampaignTest {
    private static final class Probe implements GameView {
        GamePresentation screen;
        LevelResult result;
        ReplayTrace replay;
        UiCommands commands;
        int breaks;
        int slides;
        int locks;
        @Override public void render(GamePresentation value) { screen = value; result = null; }
        @Override public void configureUiCommands(UiCommands value) { commands = value; }
        @Override public void configureReplay(ReplayTrace value) { replay = value; }
        @Override public void showVictory(LevelResult value) { result = value; }
        @Override public void animateBox(BoxAnimation value) {
            if (value.shattered()) { breaks++; }
            if (value.path().size() > 1) { slides++; }
        }
        @Override public void playEffect(SoundManager.Effect value) {
            if (value == SoundManager.Effect.LOCK) { locks++; }
        }
        @Override public void showLoadError(String message) { throw new AssertionError(message); }
    }
    private ReleaseCampaignTest() { }
    public static void main(String[] args) {
        LevelCatalog catalog = DemoCampaign.catalog(Path.of(args[0]));
        require(catalog.levels().size() == 5, "all mechanics must be reachable");
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.loadCatalog(catalog);
        List<List<Direction>> solutions = List.of(
                List.of(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT),
                List.of(Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.DOWN,
                        Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP,
                        Direction.LEFT, Direction.LEFT),
                List.of(Direction.DOWN, Direction.RIGHT),
                List.of(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT),
                List.of(Direction.RIGHT, Direction.DOWN, Direction.RIGHT, Direction.RIGHT,
                        Direction.RIGHT, Direction.RIGHT, Direction.RIGHT));
        long[] scores = {993, 983, 996, 991, 989};
        int passed = 0;
        for (int i = 0; i < solutions.size(); i++) {
            require(view.screen.moves() == 0 && view.replay == null, "Next leaked prior attempt");
            for (Direction direction : solutions.get(i)) { game.move(direction); }
            require(view.result != null && view.result.score() == scores[i], "witness/score failed at " + i);
            require(view.replay != null && new ReplayPlayer(view.replay).verify()
                    && view.replay.expectedFinalState().equals(view.screen.state()), "replay mismatch");
            if (i + 1 < solutions.size()) {
                require(view.commands.nextLevel() != null, "missing Next");
                view.commands.nextLevel().run();
            } else { require(view.commands.nextLevel() == null, "Next beyond campaign"); }
            passed++;
        }
        require(view.breaks == 1 && view.slides == 2 && view.locks == 1, "mechanic witnesses missing");
        require(game.results().size() == 5 && game.results().get(0).metrics().moves() == 3,
                "completed results lost");
        passed++;
        System.out.println("RELEASE CAMPAIGN RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
