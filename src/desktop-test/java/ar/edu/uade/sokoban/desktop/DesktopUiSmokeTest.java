package ar.edu.uade.sokoban.desktop;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** One real-display smoke pass through the same Swing window and controller. */
public final class DesktopUiSmokeTest {
    private static int passed;
    private DesktopUiSmokeTest() { }
    public static void main(String[] args) throws Exception {
        if (java.awt.GraphicsEnvironment.isHeadless()) { throw new IllegalStateException("Display requerido"); }
        SwingApplication.main(new String[0]);
        SwingUtilities.invokeAndWait(() -> { });
        JFrame[] result = new JFrame[1];
        SwingUtilities.invokeAndWait(() -> {
            for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
                if (frame instanceof JFrame value && value.isVisible()) { result[0] = value; }
            }
        });
        JFrame frame = result[0]; check(frame != null, "window");
        try {
            SwingUtilities.invokeAndWait(() -> {
                check(!button(frame, "CONTINUAR").isEnabled(), "initial menu");
                button(frame, "NUEVA PARTIDA  /  DEMO").doClick();
                check(button(frame, "R  REINICIAR").isEnabled(), "restart active");
                check(!button(frame, "Z  DESHACER").isEnabled(), "undo initially unavailable");
                check(!button(frame, "GUARDAR PARTIDA").isEnabled(), "save unavailable");
                check(findText(frame, "NIVEL_1"), "first HUD");
                key(frame, "RIGHT");
                check(findText(frame, "PUNTAJE: 9965"), "first victory score");
                check(button(frame, "SIGUIENTE NIVEL").isEnabled(), "next enabled");
                button(frame, "SIGUIENTE NIVEL").doClick();
                check(findText(frame, "NIVEL_2"), "second HUD");
                for (String direction : List.of("RIGHT", "LEFT", "DOWN", "DOWN", "RIGHT")) { key(frame, direction); }
                check(button(frame, "Z  DESHACER").isEnabled(), "undo after five");
                key(frame, "Z");
                check(!button(frame, "Z  DESHACER").isEnabled(), "undo consumed history");
                key(frame, "R");
                check(findText(frame, "0000"), "restart counters");
                for (String direction : List.of("RIGHT", "LEFT", "DOWN", "DOWN", "RIGHT", "RIGHT", "UP", "RIGHT", "UP", "LEFT", "LEFT")) { key(frame, direction); }
                check(findText(frame, "PUNTAJE: 9715"), "campaign score includes undo");
                check(!button(frame, "SIGUIENTE NIVEL").isEnabled(), "campaign end");
            });
            System.out.println("UI SMOKE RESULT: " + passed + " PASS, 0 FAIL");
        } finally { SwingUtilities.invokeAndWait(frame::dispose); }
    }
    private static void key(JFrame frame, String name) {
        Object id = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke(name));
        if (id == null) { throw new AssertionError("missing key " + name); }
        frame.getRootPane().getActionMap().get(id).actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "test"));
    }
    private static JButton button(Component tree, String text) {
        if (tree instanceof JButton b && text.equals(b.getText())) { return b; }
        if (tree instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                try { return button(child, text); } catch (IllegalArgumentException ignored) { }
            }
        }
        throw new IllegalArgumentException("Missing button: " + text);
    }
    private static boolean findText(Component tree, String needle) {
        if (tree instanceof JLabel label && label.isShowing() && label.getText().contains(needle)) { return true; }
        if (tree instanceof Container parent) {
            for (Component child : parent.getComponents()) { if (findText(child, needle)) { return true; } }
        }
        return false;
    }
    private static void check(boolean condition, String label) {
        if (!condition) { throw new AssertionError(label); }
        passed++;
    }
}
