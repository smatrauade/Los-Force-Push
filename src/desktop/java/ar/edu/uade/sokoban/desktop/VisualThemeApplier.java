package ar.edu.uade.sokoban.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/**
 * Presentation-only palette switch. It remembers each component's original
 * colors and border once, so returning to {@link VisualThemes#original()}
 * restores exactly the previous Swing state. No gameplay object is touched.
 */
final class VisualThemeApplier {
    private final Map<Component, Color[]> originalColors = new IdentityHashMap<>();
    private final Map<JComponent, Border> originalBorders = new IdentityHashMap<>();

    void apply(Component root, VisualThemeFactory theme) {
        if (root == null || theme == null) {
            return;
        }
        boolean original = VisualThemes.ORIGINAL_ID.equals(theme.id());
        applyComponent(root, theme.palette(), original);
    }

    private void applyComponent(Component component, VisualThemeFactory.UiPalette palette,
                                boolean original) {
        if (!(component instanceof BoardPanel)) {
            // The board owns its background through its own theme, never through
            // this generic widget palette, so restore order cannot stale it.
            Color[] saved = originalColors.computeIfAbsent(component,
                    key -> new Color[]{key.getBackground(), key.getForeground()});
            component.setBackground(original ? saved[0] : background(component, saved[0], palette));
            component.setForeground(original ? saved[1] : foreground(component, saved[1], palette));
        }
        if (component instanceof JComponent widget) {
            Border savedBorder = originalBorders.computeIfAbsent(widget, JComponent::getBorder);
            widget.setBorder(original ? savedBorder : border(savedBorder, palette));
        }
        if (component instanceof PixelTheme.Surface surface) {
            surface.setPalette(original ? VisualThemes.original().palette() : palette);
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyComponent(child, palette, original);
            }
        }
    }

    private static Color background(Component component, Color saved,
                                    VisualThemeFactory.UiPalette palette) {
        if (saved != null) {
            if (saved.equals(PixelTheme.BACKGROUND)) {
                return palette.background();
            }
            if (saved.equals(PixelTheme.SURFACE)) {
                return palette.surface();
            }
            if (saved.equals(PixelTheme.SURFACE_LIGHT)) {
                return palette.surfaceLight();
            }
        }
        if (component instanceof JButton || component instanceof JComboBox<?>) {
            return palette.surfaceLight();
        }
        if (component instanceof JCheckBox) {
            return palette.surface();
        }
        return saved;
    }

    private static Color foreground(Component component, Color saved,
                                    VisualThemeFactory.UiPalette palette) {
        if (saved != null) {
            if (saved.equals(PixelTheme.CREAM)) {
                return palette.text();
            }
            if (saved.equals(PixelTheme.COPPER)) {
                return palette.accent();
            }
            if (saved.equals(PixelTheme.MUTED)) {
                return palette.muted();
            }
            if (saved.equals(PixelTheme.AQUA)) {
                return palette.status();
            }
        }
        if (component instanceof JButton || component instanceof JComboBox<?>
                || component instanceof JCheckBox) {
            return palette.text();
        }
        return saved;
    }

    private static Border border(Border saved, VisualThemeFactory.UiPalette palette) {
        if (saved instanceof CompoundBorder compound
                && compound.getOutsideBorder() instanceof LineBorder line) {
            Color color = line.getLineColor().equals(PixelTheme.COPPER) ? palette.accent()
                    : line.getLineColor().equals(PixelTheme.BORDER) ? palette.border()
                    : line.getLineColor();
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, line.getThickness()),
                    compound.getInsideBorder());
        }
        return saved;
    }
}
