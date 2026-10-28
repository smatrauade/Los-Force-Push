package ar.edu.uade.sokoban.desktop;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** Optional real-display UI smoke test; run with Xvfb on Linux or a desktop on Windows. */
public final class DesktopUiSmokeTest {
    private static int passed;
    private DesktopUiSmokeTest() { }

    public static void main(String[] args) throws Exception {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            throw new IllegalStateException("La prueba de ventanas requiere un escritorio o Xvfb");
        }
        SwingApplication.main(new String[0]);
        SwingUtilities.invokeAndWait(() -> { });
        final JFrame[] frames = new JFrame[1];
        SwingUtilities.invokeAndWait(() -> {
            for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
                if (frame instanceof JFrame value && value.isVisible()) {
                    frames[0] = value;
                }
            }
        });
        JFrame frame = frames[0];
        check(frame != null, "la ventana no se abrio");
        Path previews = args.length == 1 ? Path.of(args[0]) : null;
        if (previews != null) {
            Files.createDirectories(previews);
        }
        AtomicInteger restartClicks = new AtomicInteger();
        AtomicInteger undoClicks = new AtomicInteger();
        try {
            SwingUtilities.invokeAndWait(() -> {
                JButton newGame = button(frame, "NUEVA PARTIDA  /  DEMO");
                check(newGame.isEnabled(), "menu inicial");
                check(!button(frame, "CONTINUAR").isEnabled(), "no hay partida al inicio");
                check(!button(frame, "CARGAR PARTIDA").isEnabled(), "cargar sin modulo");
            });
            if (previews != null) { screenshot(frame, previews.resolve("PANTALLA_MENU.png")); }
            SwingUtilities.invokeAndWait(() -> button(frame, "NUEVA PARTIDA  /  DEMO").doClick());
            SwingUtilities.invokeAndWait(() -> {
                check(!button(frame, "R  REINICIAR").isEnabled(), "restart sin modulo");
                check(!button(frame, "Z  DESHACER").isEnabled(), "undo sin modulo");
                check(!button(frame, "GUARDAR PARTIDA").isEnabled(), "save sin modulo");
                check(findText(frame, "DEMO.TXT"), "HUD nivel");
                check(findText(frame, "0000"), "HUD contador");
            });
            if (previews != null) { screenshot(frame, previews.resolve("PANTALLA_JUEGO.png")); }
            SwingUtilities.invokeAndWait(() -> {
                Object key = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                        .get(KeyStroke.getKeyStroke("RIGHT"));
                check(key != null, "binding RIGHT");
                frame.getRootPane().getActionMap().get(key).actionPerformed(
                        new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, "test"));
                check(findText(frame, "0001"), "contador tras movimiento");
                GameView view = (GameView) frame;
                view.configureUiCommands(new UiCommands(undoClicks::incrementAndGet,
                        restartClicks::incrementAndGet, null, null, null));
                check(button(frame, "Z  DESHACER").isEnabled(), "undo habilitable");
                check(button(frame, "R  REINICIAR").isEnabled(), "restart habilitable");
                button(frame, "Z  DESHACER").doClick();
                button(frame, "R  REINICIAR").doClick();
                check(undoClicks.get() == 1 && restartClicks.get() == 1,
                        "callbacks delegados y sin reglas falsas");
                view.configureUiCommands(UiCommands.EMPTY);
                check(!button(frame, "R  REINICIAR").isEnabled(), "deshabilitacion dinamica");
                button(frame, "ESC  PAUSA").doClick();
                check(!button(frame, "REINICIAR NIVEL").isEnabled(), "pausa restart pendiente");
            });
            if (previews != null) { screenshot(frame, previews.resolve("PANTALLA_PAUSA.png")); }
            SwingUtilities.invokeAndWait(() -> {
                button(frame, "CONTINUAR PARTIDA").doClick();
                ((GameView) frame).showVictory(new LevelResult("demo.txt", 17, 3, 1, 900));
                check(!button(frame, "SIGUIENTE NIVEL").isEnabled(), "nivel siguiente pendiente");
            });
            if (previews != null) { screenshot(frame, previews.resolve("PANTALLA_VICTORIA_DEMO.png")); }
            System.out.println("UI SMOKE RESULT: " + passed
                    + " PASS, 0 FAIL (ventana real, navegacion y callbacks)");
        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }

    private static void screenshot(JFrame frame, Path path) throws Exception {
        final BufferedImage[] result = new BufferedImage[1];
        SwingUtilities.invokeAndWait(() -> {
            Container content = frame.getContentPane();
            BufferedImage image = new BufferedImage(content.getWidth(), content.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            try {
                content.paint(graphics);
            } finally {
                graphics.dispose();
            }
            result[0] = image;
        });
        ImageIO.write(result[0], "png", path.toFile());
    }

    private static JButton button(Component tree, String text) {
        if (tree instanceof JButton value && text.equals(value.getText())) {
            return value;
        }
        if (tree instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                try {
                    return button(child, text);
                } catch (IllegalArgumentException ignored) {
                    // Keep looking at sibling components.
                }
            }
        }
        throw new IllegalArgumentException("No se encontro boton: " + text);
    }

    private static boolean findText(Component tree, String needle) {
        if (tree instanceof JLabel label && label.isShowing()
                && label.getText().contains(needle)) {
            return true;
        }
        if (tree instanceof Container parent) {
            for (Component child : parent.getComponents()) {
                if (findText(child, needle)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void check(boolean condition, String problem) {
        if (!condition) {
            throw new AssertionError(problem);
        }
        passed++;
    }
}
