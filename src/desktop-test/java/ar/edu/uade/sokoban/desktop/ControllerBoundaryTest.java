package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.replay.ReplayTrace;
import ar.edu.uade.sokoban.session.LevelCatalog;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.SwingUtilities;

/** Loaded victory must be recognized even when no valid movement exists. */
public final class ControllerBoundaryTest {
    private static final class Probe implements GameView {
        LevelResult victory;
        ReplayTrace replay;
        UiCommands commands;
        int victories;
        @Override public void render(GamePresentation value) { victory = null; }
        @Override public void showLoadError(String message) { throw new AssertionError(message); }
        @Override public void showVictory(LevelResult value) { victory = value; victories++; }
        @Override public void configureReplay(ReplayTrace value) { replay = value; }
        @Override public void configureUiCommands(UiCommands value) { commands = value; }
    }
    private ControllerBoundaryTest() { }
    public static void main(String[] args) throws Exception {
        Path solved = Files.createTempFile("pr17-solved-", ".txt");
        try {
            Files.writeString(solved, "#####\n#@#*#\n#####\n");
            Probe view = new Probe();
            try (GameController game = new GameController(view)) {
                game.load(solved);
                require(view.victory != null && view.victory.moves() == 0
                        && view.victory.score() == 1000 && game.results().size() == 1,
                        "already solved immobile TXT did not finalize");
                require(view.replay != null && view.replay.commands().isEmpty(), "empty completed replay");
                game.move(Direction.RIGHT); game.undo(); game.restart();
                require(view.victories == 1 && game.getLevelResult().score() == 1000,
                        "frozen actions finalized twice");
            }
            SwingUtilities.invokeAndWait(() -> {
                ReplayPanel panel = new ReplayPanel(() -> { });
                require(panel.open(view.replay), "empty trace rejected by Swing");
                panel.play(); panel.step(); panel.reset();
                require(panel.index() == 0 && !panel.isPlaying(), "empty playback started a timer");
                panel.close();
            });
            Probe campaign = new Probe();
            try (GameController game = new GameController(campaign)) {
                game.loadCatalog(new LevelCatalog(List.of(
                        new LevelCatalog.Entry("demo", Path.of(args[0]).resolve("demo.txt"), LevelCatalog.Format.STANDARD),
                        new LevelCatalog.Entry("solved", solved, LevelCatalog.Format.STANDARD))));
                for (int i = 0; i < 3; i++) { game.move(Direction.RIGHT); }
                campaign.commands.nextLevel().run();
                require(campaign.victory != null && campaign.victory.moves() == 0
                        && game.results().size() == 2 && campaign.commands.nextLevel() == null,
                        "Next did not finalize an initially solved level");
            }
            System.out.println("CONTROLLER BOUNDARY RESULT: 3 PASS, 0 FAIL");
        } finally { Files.deleteIfExists(solved); }
    }
    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
