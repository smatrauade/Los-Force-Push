package ar.edu.uade.sokoban.desktop;

import java.awt.Color;
import java.awt.image.BufferedImage;

/** Abstract factory: a matching palette and sprite presentation form one visual family. */
public interface VisualThemeFactory {
    record UiPalette(Color background, Color surface, Color button, Color text, Color accent, Color boardBackground) { }
    @FunctionalInterface interface SpritePresentation { BufferedImage apply(BufferedImage source); }
    String name();
    UiPalette palette();
    SpritePresentation sprites();
}
