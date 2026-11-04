package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.RuntimeState;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Native window acceptance for Replay, the real menu catalog and failure navigation. */
public final class ReleaseUiSmokeTest {
    private static int passed;
    private ReleaseUiSmokeTest() { }
    public static void main(String[] args) throws Exception {
        if (java.awt.GraphicsEnvironment.isHeadless()) { throw new IllegalStateException("Display required"); }
        SwingApplication.main(new String[0]);
        SwingUtilities.invokeAndWait(() -> { });
        JFrame[] selected = new JFrame[1];
        SwingUtilities.invokeAndWait(() -> {
            for (Frame value : Frame.getFrames()) {
                if (value instanceof JFrame frame && frame.isVisible()) { selected[0] = frame; }
            }
        });
        JFrame frame = selected[0];
        require(frame != null, "window missing");
        ReplayPanel panel = field(frame, "replayPanel", ReplayPanel.class);
        BoardPanel board = field(frame, "board", BoardPanel.class);
        GameController game = field(frame, "controller", GameController.class);
        try {
            SwingUtilities.invokeAndWait(() -> {
                button(frame, "NUEVA PARTIDA  /  DEMO").doClick();
                keys(frame, "RIGHT", "RIGHT", "RIGHT");
                require(active(frame).equals("FINISH") && button(frame, "VER REPLAY").isEnabled(), "real victory/replay");
                RuntimeState live = board.shownState();
                LevelResult score = game.getLevelResult();
                button(frame, "VER REPLAY").doClick();
                require(active(frame).equals("REPLAY") && panel.board() != board, "separate board");
                button(frame, "PLAY").doClick();
                button(frame, "PAUSE").doClick();
                button(frame, "STEP").doClick();
                require(panel.index() == 1, "native Step");
                keys(frame, "LEFT", "Z", "R");
                require(board.shownState() == live && game.getLevelResult().equals(score)
                        && game.results().size() == 1, "Replay allowed gameplay input");
                button(frame, "RESET").doClick();
                require(panel.index() == 0 && !panel.isPlaying(), "native Reset");
                passed++;
                button(frame, "BACK").doClick();
                require(active(frame).equals("FINISH"), "Back destination");
                button(frame, "VOLVER AL MENU").doClick();
                button(frame, "CONTINUAR").doClick();
                require(active(frame).equals("FINISH"), "Continue opened frozen gameplay");
                passed++;
                button(frame, "VOLVER AL MENU").doClick();
                button(frame, "OPCIONES").doClick();
                JComboBox<?> themes = field(frame, "themeSelector", JComboBox.class);
                for (int i = 0; i < 3; i++) { themes.setSelectedIndex(i); }
                button(frame, "VOLVER").doClick();
                button(frame, "CONTINUAR").doClick();
                button(frame, "VER REPLAY").doClick();
                button(frame, "STEP").doClick();
                require(panel.index() == 1 && panel.board().getScale() == board.getScale(), "theme/zoom replay");
                keys(frame, "ESCAPE");
                require(active(frame).equals("FINISH") && !panel.isPlaying(), "ESC cleanup");
                passed++;
                button(frame, "SIGUIENTE NIVEL").doClick();
                keys(frame, "RIGHT", "LEFT", "DOWN", "DOWN", "RIGHT", "RIGHT", "UP", "RIGHT", "UP", "LEFT", "LEFT");
                require(active(frame).equals("FINISH") && game.getLevelResult().score() == 983, "key/lock campaign");
                passed++;
                button(frame, "SIGUIENTE NIVEL").doClick();
                keys(frame, "DOWN", "RIGHT");
                require(board.isAnimating() && active(frame).equals("GAME"), "ICE victory not deferred");
                // Dismiss only this test's modal error; openLevel is the same entry as JFileChooser.
                Timer dismiss = new Timer(50, event -> {
                    for (java.awt.Window window : java.awt.Window.getWindows()) {
                        if (window instanceof JDialog dialog && dialog.isVisible()) { dialog.dispose(); }
                    }
                });
                dismiss.start();
                try {
                    Method open = frame.getClass().getDeclaredMethod("openLevel", Path.class);
                    open.setAccessible(true);
                    open.invoke(frame, Path.of(System.getProperty("java.io.tmpdir"), "pr17-no-such-level.txt"));
                } catch (ReflectiveOperationException failure) { throw new AssertionError(failure); }
                finally { dismiss.stop(); }
                require(active(frame).equals("FINISH") && !board.isAnimating()
                        && button(frame, "VER REPLAY").isEnabled() && game.getLevelResult().score() == 996,
                        "invalid load lost pending victory");
                passed++;
                button(frame, "SIGUIENTE NIVEL").doClick();
                keys(frame, "RIGHT", "RIGHT", "RIGHT");
                require(active(frame).equals("FINISH") && game.getLevelResult().score() == 991, "Fragile campaign");
                button(frame, "SIGUIENTE NIVEL").doClick();
                keys(frame, "RIGHT");
                require(board.isAnimating() && board.shownState().boxes().size() == 1, "Fragile break presentation");
                keys(frame, "R");
                require(!board.isAnimating() && board.shownState().boxes().size() == 2
                        && game.getLevelResult().moves() == 0, "Restart during break animation");
                passed++;
            });
            SwingUtilities.invokeAndWait(() -> keys(frame, "RIGHT"));
            awaitAnimation(board);
            SwingUtilities.invokeAndWait(() -> {
                keys(frame, "DOWN", "RIGHT", "RIGHT", "RIGHT", "RIGHT", "RIGHT");
                require(active(frame).equals("FINISH") && game.results().size() == 5
                        && game.getLevelResult().score() == 989 && !button(frame, "SIGUIENTE NIVEL").isEnabled(),
                        "final campaign/result");
                button(frame, "VER REPLAY").doClick();
                button(frame, "PLAY").doClick();
                require(panel.isPlaying(), "play before close");
                frame.dispose();
            });
            SwingUtilities.invokeAndWait(() -> {
                require(!panel.isPlaying() && !board.isAnimating() && !frame.isDisplayable(), "window resource cleanup");
                passed++;
            });
            System.out.println("RELEASE UI RESULT: " + passed + " PASS, 0 FAIL (native " + System.getProperty("os.name") + " window; no audio listening)");
        } finally { SwingUtilities.invokeAndWait(frame::dispose); }
    }
    private static void awaitAnimation(BoardPanel board) throws Exception {
        long deadline = System.nanoTime() + 5_000_000_000L;
        boolean[] busy = {true};
        while (busy[0] && System.nanoTime() < deadline) {
            SwingUtilities.invokeAndWait(() -> busy[0] = board.isAnimating());
            if (busy[0]) { Thread.sleep(30); }
        }
        require(!busy[0], "animation timed out");
    }
    private static String active(JFrame frame) { return field(frame, "active", String.class); }
    private static <T> T field(Object target, String name, Class<T> type) {
        try {
            Field field = target.getClass().getDeclaredField(name); field.setAccessible(true);
            return type.cast(field.get(target));
        } catch (ReflectiveOperationException failure) { throw new AssertionError(failure); }
    }
    private static void keys(JFrame frame, String... names) {
        for (String name : names) {
            Object action = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .get(KeyStroke.getKeyStroke(name));
            frame.getRootPane().getActionMap().get(action).actionPerformed(
                    new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "release-test"));
        }
    }
    private static JButton button(Component root, String text) {
        if (root instanceof JButton button && text.equals(button.getText()) && button.isShowing()) { return button; }
        if (root instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                JButton found = findButton(child, text);
                if (found != null) { return found; }
            }
        }
        throw new AssertionError("Visible button missing: " + text);
    }
    private static JButton findButton(Component root, String text) {
        if (root instanceof JButton button && text.equals(button.getText()) && button.isShowing()) { return button; }
        if (root instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                JButton found = findButton(child, text); if (found != null) { return found; }
            }
        }
        return null;
    }
    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
