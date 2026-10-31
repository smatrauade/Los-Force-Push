package ar.edu.uade.sokoban.desktop;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** One real-display pass through themes, replay and the actual campaign. */
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
                check(findText(frame, "NIVEL_1"), "first HUD");
                key(frame, "ESCAPE"); button(frame, "OPCIONES").doClick();
                selectTheme(frame, "JADE");
                check(board(frame).getBackground().equals(VisualThemes.all().get(2).palette().background()), "jade applied");
                button(frame, "VOLVER").doClick(); button(frame, "CONTINUAR PARTIDA").doClick();
                check(findText(frame, "0000"), "theme did not restart or move");
                key(frame, "RIGHT");
                check(findText(frame, "PUNTAJE: 9965"), "first victory score");
                checkVictoryLayout(frame);
                check(button(frame, "VER REPETICION").isEnabled(), "replay enabled");
                button(frame, "OPCIONES DE VICTORIA").doClick(); selectTheme(frame, "ORIGINAL_COBRE");
                check(board(frame).getBackground().equals(new java.awt.Color(42, 33, 36)), "victory theme changed");
                button(frame, "VOLVER").doClick();
                button(frame, "VER REPETICION").doClick();
                button(frame, "PASO SIGUIENTE").doClick();
                button(frame, "REINICIAR REPETICION").doClick();
                button(frame, "REPRODUCIR").doClick(); button(frame, "REPRODUCIR").doClick();
            });
            Thread.sleep(500);
            SwingUtilities.invokeAndWait(() -> {
                button(frame, "PAUSAR").doClick();
                button(frame, "VOLVER AL RESULTADO").doClick();
                check(findText(frame, "PUNTAJE: 9965"), "replay leaves result intact");
                button(frame, "VER REPETICION").doClick(); button(frame, "REPRODUCIR").doClick();
                button(frame, "VOLVER AL RESULTADO").doClick();
                button(frame, "VER REPETICION").doClick(); button(frame, "PASO SIGUIENTE").doClick();
                button(frame, "VOLVER AL RESULTADO").doClick();
                check(findText(frame, "PUNTAJE: 9965"), "repeated replay navigation preserves result");
                button(frame, "SIGUIENTE NIVEL").doClick();
                check(findText(frame, "NIVEL_2"), "second HUD");
                key(frame, "RIGHT");
                key(frame, "ESCAPE"); button(frame, "OPCIONES").doClick(); selectTheme(frame, "NOCTURNO");
                check(board(frame).getBackground().equals(VisualThemes.all().get(1).palette().background()), "night applied");
                button(frame, "VOLVER").doClick(); button(frame, "CONTINUAR PARTIDA").doClick();
                for (String direction : List.of("LEFT", "DOWN", "DOWN", "RIGHT")) { key(frame, direction); }
                check(button(frame, "Z  DESHACER").isEnabled(), "undo after five");
                key(frame, "Z"); check(!button(frame, "Z  DESHACER").isEnabled(), "undo consumed history");
                key(frame, "R"); check(findText(frame, "0000"), "restart counters");
                for (String direction : List.of("RIGHT", "LEFT", "DOWN", "DOWN", "RIGHT", "RIGHT", "UP", "RIGHT", "UP", "LEFT", "LEFT")) { key(frame, direction); }
                check(findText(frame, "PUNTAJE: 9715"), "campaign score includes undo");
                check(button(frame, "SIGUIENTE NIVEL").isEnabled(), "third level available");
                button(frame, "SIGUIENTE NIVEL").doClick();
                check(findText(frame, "NIVEL_3"), "third HUD");
                key(frame, "RIGHT"); key(frame, "RIGHT");
                check(findText(frame, "PUNTAJE: 9955"), "fragile ice victory");
                check(!button(frame, "SIGUIENTE NIVEL").isEnabled(), "campaign end");
                checkVictoryLayout(frame);
                frame.setSize(frame.getMinimumSize());
                frame.validate();
                checkVictoryLayout(frame);
                frame.setSize(1190, 790);
                frame.validate();
                checkVictoryLayout(frame);
            });
            System.out.println("UI SMOKE RESULT: " + passed + " PASS, 0 FAIL");
        } finally { SwingUtilities.invokeAndWait(frame::dispose); }
    }
    private static void selectTheme(Component tree, String name) {
        if (tree instanceof JComboBox<?> combo && combo.isShowing()) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (name.equals(String.valueOf(combo.getItemAt(i)))) { combo.setSelectedIndex(i); return; }
            }
        }
        if (tree instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                try { selectTheme(child, name); return; } catch (IllegalArgumentException ignored) { }
            }
        }
        throw new IllegalArgumentException("Missing theme: " + name);
    }
    private static void checkVictoryLayout(JFrame frame) {
        System.out.println("LAYOUT frame=" + frame.getSize() + " content=" + frame.getContentPane().getSize()
                + " scale=" + frame.getGraphicsConfiguration().getDefaultTransform());
        for (String name : List.of("SIGUIENTE NIVEL", "VER REPETICION", "OPCIONES DE VICTORIA", "VOLVER AL MENU")) {
            JButton control = button(frame, name);
            control.scrollRectToVisible(new java.awt.Rectangle(0, 0, control.getWidth(), control.getHeight()));
            check(control.getWidth() > 0 && control.getHeight() > 0
                    && control.getVisibleRect().equals(new java.awt.Rectangle(0, 0, control.getWidth(), control.getHeight())),
                    "victory control fully visible: " + name + " actual=" + control.getVisibleRect());
        }
    }
    private static BoardPanel board(Component tree) {
        if (tree instanceof BoardPanel value) { return value; }
        if (tree instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                try { return board(child); } catch (IllegalArgumentException ignored) { }
            }
        }
        throw new IllegalArgumentException("Missing board");
    }
    private static void key(JFrame frame, String name) {
        Object id = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke(name));
        if (id == null) { throw new AssertionError("missing key " + name); }
        frame.getRootPane().getActionMap().get(id).actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "test"));
    }
    private static JButton button(Component tree, String text) {
        if (tree instanceof JButton b && b.isShowing() && text.equals(b.getText())) { return b; }
        if (tree instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                try { return button(child, text); } catch (IllegalArgumentException ignored) { }
            }
        }
        throw new IllegalArgumentException("Missing visible button: " + text);
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
