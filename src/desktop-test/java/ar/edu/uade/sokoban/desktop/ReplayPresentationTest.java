package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.replay.MoveCommand;
import ar.edu.uade.sokoban.replay.ReplayTrace;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Real EDT timers and headless components; no claim of native visual QA. */
public final class ReplayPresentationTest {
    private static int passed;
    private ReplayPresentationTest() { }
    private static final class Probe implements GameView {
        GamePresentation screen;
        ReplayTrace trace;
        @Override public void render(GamePresentation value) { screen = value; }
        @Override public void configureReplay(ReplayTrace value) { trace = value; }
        @Override public void showLoadError(String message) { throw new AssertionError(message); }
    }
    public static void main(String[] args) throws Exception {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(Path.of(args[0]).resolve("demo.txt"));
        for (int i = 0; i < 3; i++) { game.move(Direction.RIGHT); }
        ReplayTrace trace = view.trace;
        GamePresentation live = view.screen;
        LevelResult score = game.getLevelResult();
        SwingUtilities.invokeAndWait(() -> {
            ReplayPanel panel = new ReplayPanel(() -> { });
            require(panel.open(trace) && panel.index() == 0
                    && panel.board().shownState() == trace.initialState(), "initial cursor");
            panel.step();
            require(panel.index() == 1 && !panel.isPlaying(), "Step must pause");
            panel.step(); panel.step(); panel.step();
            require(panel.index() == 3 && panel.board().shownState().equals(trace.expectedFinalState())
                    && panel.progressText().contains("PASO 3/3"), "exact final state");
            passed++;
            panel.reset();
            require(panel.index() == 0 && panel.board().shownState() == trace.initialState(), "Reset");
            passed++;
            panel.play();
            Timer old = timer(panel);
            require(panel.isPlaying(), "Play did not start timer");
            panel.pause();
            fire(old);
            require(!panel.isPlaying() && panel.index() == 0, "stale callback after Pause");
            panel.play(); panel.reset(); fire(old);
            require(panel.index() == 0 && !panel.isPlaying(), "Reset did not stop timer");
            passed++;
            panel.play();
            Timer closing = timer(panel);
            panel.close(); fire(closing);
            require(!panel.isPlaying() && panel.index() == 0, "close/navigation leaked timer");
            require(panel.open(trace) && panel.index() == 0, "reopen retained stale cursor");
            fire(closing);
            require(panel.index() == 0, "old timer advanced a new trace");
            passed++;
            BoardPanel realBoard = new BoardPanel();
            realBoard.showState(live.state());
            JPanel root = new JPanel(); root.add(realBoard); root.add(panel);
            VisualThemeController themes = new VisualThemeController(root, realBoard,
                    new PixelAssets(), panel.board());
            panel.board().setScale(3);
            panel.step();
            for (VisualThemeFactory family : VisualThemes.all()) {
                require(themes.select(family), "theme selection");
                require(panel.index() == 1 && realBoard.shownState() == live.state()
                        && panel.board().getScale() == 3, "theme changed game/cursor/zoom");
            }
            panel.close();
            passed++;
            ReplayTrace invalid = new ReplayTrace("invalid", trace.initialState(),
                    List.of(new MoveCommand(Direction.LEFT)), trace.expectedFinalState(), 1, 0);
            require(!panel.open(invalid) && !panel.isPlaying(), "invalid trace accepted");
            passed++;
        });
        boolean rejected = false;
        try { new ReplayPanel(() -> { }).open(trace); }
        catch (IllegalStateException expected) { rejected = true; }
        require(rejected, "off-EDT playback accepted");
        passed++;
        // Exercise actual asynchronous javax.swing.Timer dispatch, not a headless paint.
        CountDownLatch finished = new CountDownLatch(1);
        ReplayPanel[] panels = new ReplayPanel[1];
        Timer[] watchdog = new Timer[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                panels[0] = new ReplayPanel(() -> { });
                panels[0].open(trace); panels[0].play();
                watchdog[0] = new Timer(25, event -> {
                    if (panels[0].index() == 3 && !panels[0].isPlaying()) { finished.countDown(); }
                });
                watchdog[0].start();
            });
            require(finished.await(5, TimeUnit.SECONDS), "EDT Timer did not finish playback");
            require(view.screen == live && game.getLevelResult().equals(score)
                    && game.results().size() == 1, "playback mutated live session");
            passed++;
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                if (watchdog[0] != null) { watchdog[0].stop(); }
                if (panels[0] != null) { panels[0].close(); }
            });
        }
        System.out.println("REPLAY PRESENTATION RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static Timer timer(ReplayPanel panel) {
        try {
            Field field = ReplayPanel.class.getDeclaredField("timer");
            field.setAccessible(true);
            return (Timer) field.get(panel);
        } catch (ReflectiveOperationException failure) { throw new AssertionError(failure); }
    }
    private static void fire(Timer timer) {
        for (var listener : timer.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(timer, ActionEvent.ACTION_PERFORMED, "stale"));
        }
    }
    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
