package ar.edu.uade.sokoban.desktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

/** Shared view-only colors, typography and controls; no domain state. */
public final class PixelTheme {
    public static final Color BACKGROUND = new Color(44, 35, 39);
    public static final Color SURFACE = new Color(65, 49, 48);
    public static final Color SURFACE_LIGHT = new Color(86, 65, 59);
    public static final Color COPPER = new Color(226, 159, 106);
    public static final Color CREAM = new Color(244, 226, 190);
    public static final Color MUTED = new Color(196, 172, 150);
    public static final Color AQUA = new Color(135, 207, 200);
    public static final Font TITLE = new Font(Font.MONOSPACED, Font.BOLD, 25);
    public static final Font BODY = new Font(Font.MONOSPACED, Font.BOLD, 14);
    public static final Font SMALL = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private PixelTheme() { }

    public static JButton button(String label) {
        JButton result = new JButton(label);
        result.setFont(BODY);
        result.setOpaque(true);
        result.setBackground(SURFACE_LIGHT);
        result.setForeground(CREAM);
        result.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COPPER, 2),
                BorderFactory.createEmptyBorder(10, 13, 10, 13)));
        result.setFocusPainted(false);
        result.setPreferredSize(new Dimension(210, 43));
        return result;
    }

    public static void install() {
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.messageForeground", CREAM);
        UIManager.put("OptionPane.messageFont", BODY);
    }

    /** Pixel-grid background; only the scene and HUD have visible boundaries. */
    public static JPanel surface() {
        return new JPanel() {
            private static final long serialVersionUID = 1L;
            { setBackground(BACKGROUND); }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(BACKGROUND.equals(getBackground()) ? new Color(53, 43, 47) : getBackground().brighter());
                for (int x = 0; x < getWidth(); x += 32) {
                    g.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += 32) {
                    g.drawLine(0, y, getWidth(), y);
                }
            }
        };
    }

    public static void border(JComponent value) {
        value.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(113, 80, 66), 3),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
    }
}
