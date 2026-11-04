package ar.edu.uade.sokoban.desktop;

import java.awt.Component;
import java.util.Objects;

/**
 * Applies one visual family to the board, the menu art and the Swing tree.
 * It is a presentation coordinator: it reads no gameplay state and writes only
 * view colors, sprite caches and repaint requests.
 */
final class VisualThemeController {
    private final Component root;
    private final BoardPanel board;
    private final PixelAssets menuArt;
    private final VisualThemeApplier applier = new VisualThemeApplier();
    private VisualThemeFactory current = VisualThemes.original();

    VisualThemeController(Component root, BoardPanel board, PixelAssets menuArt) {
        this.root = Objects.requireNonNull(root, "root");
        this.board = Objects.requireNonNull(board, "board");
        this.menuArt = Objects.requireNonNull(menuArt, "menuArt");
    }

    VisualThemeFactory current() {
        return current;
    }

    /**
     * Null is rejected without touching any component, so an empty combo
     * selection cannot leave a half-applied palette.
     */
    boolean select(VisualThemeFactory theme) {
        if (theme == null) {
            return false;
        }
        current = theme;
        board.setTheme(theme);
        menuArt.setTheme(theme);
        applier.apply(root, theme);
        root.repaint();
        return true;
    }
}
