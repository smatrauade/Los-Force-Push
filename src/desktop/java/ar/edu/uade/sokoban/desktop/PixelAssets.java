package ar.edu.uade.sokoban.desktop;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/** Images loaded once from the runtime classpath, never from user TXT paths. */
public final class PixelAssets {
    public static final int NATIVE_SIZE = 32;
    private static final String ROOT = "/assets/sprites/";
    private final Map<String, BufferedImage> cache = new HashMap<>();
    private VisualThemeFactory.SpritePresentation presentation = VisualThemes.all().get(0).sprites();
    public void setTheme(VisualThemeFactory theme) {
        presentation = java.util.Objects.requireNonNull(theme, "theme").sprites();
        cache.clear();
    }

    public BufferedImage image(String name) {
        BufferedImage value = cache.get(name);
        if (value != null) {
            return value;
        }
        try (InputStream input = PixelAssets.class.getResourceAsStream(ROOT + name + ".png")) {
            if (input == null) {
                throw new IllegalStateException("Falta sprite: " + ROOT + name + ".png"
                        + " (revisar classpath de resources)");
            }
            value = ImageIO.read(input);
            if (value == null || value.getWidth() != NATIVE_SIZE || value.getHeight() != NATIVE_SIZE) {
                throw new IllegalStateException("Sprite invalido, debe medir 32x32: " + name);
            }
            value = presentation.apply(value);
            cache.put(name, value);
            return value;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer sprite: " + name, exception);
        }
    }
}
