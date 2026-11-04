package ar.edu.uade.sokoban.desktop;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * The three concrete visual families. Sprite files are only read from the
 * classpath: each alternative family derives a tinted copy in memory and the
 * original family returns the decoded image untouched.
 */
public final class VisualThemes {
    public static final String ORIGINAL_ID = "original_cobre";
    public static final String NIGHT_ID = "nocturno";
    public static final String JADE_ID = "jade";

    private static final List<VisualThemeFactory> ALL =
            List.of(new Copper(), new Night(), new Jade());

    private VisualThemes() { }

    /** Immutable catalog consumed by the options screen and its tests. */
    public static List<VisualThemeFactory> all() {
        return ALL;
    }

    public static VisualThemeFactory original() {
        return ALL.get(0);
    }

    public static VisualThemeFactory byId(String id) {
        Objects.requireNonNull(id, "id");
        for (VisualThemeFactory theme : ALL) {
            if (theme.id().equals(id)) {
                return theme;
            }
        }
        throw new IllegalArgumentException("Tema visual desconocido: " + id);
    }

    private abstract static class Family implements VisualThemeFactory {
        private final String id;
        private final String displayName;
        private final UiPalette palette;
        private final int tintRed;
        private final int tintGreen;
        private final int tintBlue;

        Family(String id, String displayName, UiPalette palette,
               int tintRed, int tintGreen, int tintBlue) {
            this.id = id;
            this.displayName = displayName;
            this.palette = palette;
            this.tintRed = tintRed;
            this.tintGreen = tintGreen;
            this.tintBlue = tintBlue;
        }

        @Override
        public final String id() {
            return id;
        }

        @Override
        public final String displayName() {
            return displayName;
        }

        @Override
        public final UiPalette palette() {
            return palette;
        }

        @Override
        public SpritePresentation sprites() {
            return source -> tint(source, tintRed, tintGreen, tintBlue);
        }

        @Override
        public final String toString() {
            return displayName;
        }
    }

    /** Original brick-and-copper look; reuses the exact pre-selector colors. */
    private static final class Copper extends Family {
        Copper() {
            super(ORIGINAL_ID, "ORIGINAL COBRE", new UiPalette(
                    PixelTheme.BACKGROUND, PixelTheme.SURFACE, PixelTheme.SURFACE_LIGHT,
                    PixelTheme.CREAM, PixelTheme.COPPER, PixelTheme.MUTED,
                    PixelTheme.AQUA, new Color(53, 43, 47),
                    new Color(42, 33, 36), new Color(104, 75, 64),
                    new Color(228, 168, 111), PixelTheme.BORDER), 0, 0, 0);
        }

        @Override
        public SpritePresentation sprites() {
            return source -> source;
        }
    }

    private static final class Night extends Family {
        Night() {
            super(NIGHT_ID, "NOCTURNO", new UiPalette(
                    new Color(20, 24, 46), new Color(35, 43, 73),
                    new Color(53, 66, 104), new Color(225, 232, 255),
                    new Color(155, 184, 255), new Color(173, 184, 216),
                    new Color(135, 214, 226), new Color(43, 50, 82),
                    new Color(20, 24, 46), new Color(43, 53, 92),
                    new Color(110, 138, 210), new Color(70, 92, 150)), 120, 155, 235);
        }
    }

    private static final class Jade extends Family {
        Jade() {
            super(JADE_ID, "JADE", new UiPalette(
                    new Color(19, 42, 38), new Color(31, 63, 53),
                    new Color(48, 91, 72), new Color(225, 246, 219),
                    new Color(140, 228, 173), new Color(166, 205, 178),
                    new Color(138, 226, 196), new Color(28, 58, 51),
                    new Color(19, 42, 38), new Color(34, 74, 61),
                    new Color(96, 172, 124), new Color(60, 116, 88)), 110, 220, 140);
        }
    }

    /**
     * Blends 55% of the original pixel with 45% of the family tint while keeping
     * the source alpha channel byte-for-byte. Integer math keeps it deterministic.
     */
    private static BufferedImage tint(BufferedImage source, int tintRed, int tintGreen, int tintBlue) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = source.getRGB(x, y);
                int alpha = pixel >>> 24;
                int red = (pixel >>> 16) & 0xFF;
                int green = (pixel >>> 8) & 0xFF;
                int blue = pixel & 0xFF;
                int mixedRed = (red * 55 + tintRed * 45) / 100;
                int mixedGreen = (green * 55 + tintGreen * 45) / 100;
                int mixedBlue = (blue * 55 + tintBlue * 45) / 100;
                result.setRGB(x, y, alpha << 24 | mixedRed << 16 | mixedGreen << 8 | mixedBlue);
            }
        }
        return result;
    }
}
