package ar.edu.uade.sokoban.desktop;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

/** Three concrete families, without modifying or redistributing the underlying assets. */
public final class VisualThemes {
    private VisualThemes() { }
    private abstract static class Family implements VisualThemeFactory {
        private final String name;
        private final UiPalette palette;
        private final int red;
        private final int green;
        private final int blue;
        Family(String name, UiPalette palette, int red, int green, int blue) {
            this.name = name; this.palette = palette; this.red = red; this.green = green; this.blue = blue;
        }
        @Override public String name() { return name; }
        @Override public UiPalette palette() { return palette; }
        @Override public SpritePresentation sprites() { return source -> tint(source, red, green, blue); }
        @Override public String toString() { return name; }
    }
    private static final class Copper extends Family {
        Copper() { super("ORIGINAL_COBRE", new UiPalette(PixelTheme.BACKGROUND, PixelTheme.SURFACE,
                PixelTheme.SURFACE_LIGHT, PixelTheme.CREAM, PixelTheme.COPPER, new Color(42, 33, 36)), 255, 255, 255); }
    }
    private static final class Night extends Family {
        Night() { super("NOCTURNO", new UiPalette(new Color(20, 24, 46), new Color(35, 43, 73),
                new Color(53, 66, 104), new Color(225, 232, 255), new Color(155, 184, 255), new Color(20, 24, 46)), 165, 195, 255); }
    }
    private static final class Jade extends Family {
        Jade() { super("JADE", new UiPalette(new Color(19, 42, 38), new Color(31, 63, 53),
                new Color(48, 91, 72), new Color(225, 246, 219), new Color(140, 228, 173), new Color(19, 42, 38)), 175, 255, 190); }
    }
    public static List<VisualThemeFactory> all() { return List.of(new Copper(), new Night(), new Jade()); }
    private static BufferedImage tint(BufferedImage source, int red, int green, int blue) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int pixel = source.getRGB(x, y); int alpha = pixel >>> 24;
                int r = ((pixel >>> 16) & 255) * red / 255;
                int g = ((pixel >>> 8) & 255) * green / 255;
                int b = (pixel & 255) * blue / 255;
                result.setRGB(x, y, alpha << 24 | r << 16 | g << 8 | b);
            }
        }
        return result;
    }
}
