package ar.edu.uade.sokoban.desktop;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Abstract Factory for view-only visual families. Each concrete family supplies
 * a coherent Swing palette and the sprite presentation used by {@link PixelAssets}.
 * It never exposes or mutates gameplay state, boards or stored asset files.
 */
public interface VisualThemeFactory {
    /** Stable identifier used by tests and by the original-theme comparison. */
    String id();

    /** Spanish label shown in the options screen. */
    String displayName();

    UiPalette palette();

    SpritePresentation sprites();

    /** Colors shared by every view surface of one family. */
    record UiPalette(Color background, Color surface, Color surfaceLight, Color text,
                     Color accent, Color muted, Color status, Color grid,
                     Color boardBackground, Color boardFrameOuter, Color boardFrameInner,
                     Color border) {
        public UiPalette {
            Objects.requireNonNull(background, "background");
            Objects.requireNonNull(surface, "surface");
            Objects.requireNonNull(surfaceLight, "surfaceLight");
            Objects.requireNonNull(text, "text");
            Objects.requireNonNull(accent, "accent");
            Objects.requireNonNull(muted, "muted");
            Objects.requireNonNull(status, "status");
            Objects.requireNonNull(grid, "grid");
            Objects.requireNonNull(boardBackground, "boardBackground");
            Objects.requireNonNull(boardFrameOuter, "boardFrameOuter");
            Objects.requireNonNull(boardFrameInner, "boardFrameInner");
            Objects.requireNonNull(border, "border");
        }
    }

    /**
     * Deterministic, alpha-preserving pixel transform. The original family
     * returns its source unchanged so its output stays pixel-identical.
     */
    @FunctionalInterface
    interface SpritePresentation {
        BufferedImage apply(BufferedImage source);
    }
}
