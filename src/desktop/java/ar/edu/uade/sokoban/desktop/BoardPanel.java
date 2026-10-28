package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Terrain;
import ar.edu.uade.sokoban.movement.Direction;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.JPanel;

/** Read-only view of the core Board. No movement, collision or victory rules. */
public final class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int MARGIN = 24;
    private final transient PixelAssets sprites;
    private transient Board board;
    private int scale = 2;
    private Direction facing = Direction.DOWN;
    private long appearanceTick;

    public BoardPanel() {
        this(new PixelAssets());
    }

    BoardPanel(PixelAssets assets) {
        sprites = Objects.requireNonNull(assets, "assets");
        setBackground(new Color(42, 33, 36));
        setPreferredSize(new Dimension(560, 380));
        setFocusable(false);
        setOpaque(true);
    }

    public void showBoard(Board next) {
        board = Objects.requireNonNull(next, "board");
        updateSize();
        repaint();
    }

    public void setScale(int next) {
        if (next < 1 || next > 3) {
            throw new IllegalArgumentException("Escala permitida: 1, 2 o 3");
        }
        scale = next;
        updateSize();
        repaint();
    }

    public int getScale() {
        return scale;
    }

    public void setFacing(Direction next) {
        facing = Objects.requireNonNull(next, "direction");
        appearanceTick++;
        repaint();
    }

    private void updateSize() {
        if (board != null) {
            int tile = PixelAssets.NATIVE_SIZE * scale;
            setPreferredSize(new Dimension(board.width() * tile + MARGIN * 2,
                    board.height() * tile + MARGIN * 2));
            revalidate();
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            if (board == null) {
                return;
            }
            int tile = PixelAssets.NATIVE_SIZE * scale;
            g.setColor(new Color(104, 75, 64));
            g.fillRect(MARGIN - 5, MARGIN - 5,
                    board.width() * tile + 10, board.height() * tile + 10);
            g.setColor(new Color(228, 168, 111));
            g.fillRect(MARGIN - 3, MARGIN - 3,
                    board.width() * tile + 6, board.height() * tile + 6);
            for (int row = 0; row < board.height(); row++) {
                for (int col = 0; col < board.width(); col++) {
                    Cell cell = board.rows().get(row).get(col);
                    drawCell(g, cell, MARGIN + col * tile, MARGIN + row * tile,
                            tile, row, col);
                }
            }
        } finally {
            g.dispose();
        }
    }

    private void drawCell(Graphics2D g, Cell cell, int x, int y,
                          int tile, int row, int col) {
        String terrain = spriteForTerrain(cell.terrain(), row, col);
        if (terrain.startsWith("wall_")) {
            draw(g, terrain, x, y, tile);
            return;
        }
        draw(g, "floor_" + Math.floorMod(row * 11 + col * 7, 3), x, y, tile);
        if (!terrain.equals("floor")) {
            draw(g, terrain, x, y, tile);
        }
        String occupant = spriteForOccupant(cell.occupant(), cell.terrain());
        if (occupant != null) {
            draw(g, occupant, x, y, tile);
        }
    }

    /** Adapter point for future Terrain values: the domain keeps its own rules. */
    static String spriteForTerrain(Terrain terrain, int row, int col) {
        return switch (terrain.name()) {
            case "WALL" -> "wall_" + Math.floorMod(row * 3 + col, 3);
            case "GOAL", "DESTINATION" -> "goal";
            case "ICE", "SLIPPERY", "SLIPPERY_FLOOR" -> "ice";
            case "LOCK", "LOCK_CLOSED", "LOCK_TILE" -> "lock";
            case "LOCK_OPEN", "LOCK_ACTIVE" -> "lock_active";
            case "CLOSED_WALL", "GATE_CLOSED", "WALL_CLOSED" -> "gate_closed";
            case "OPEN_WALL", "GATE_OPEN", "WALL_OPEN" -> "gate_open";
            default -> "floor";
        };
    }

    /** Future names are presentation hints, not definitions of future gameplay rules. */
    static String spriteForOccupant(Occupant occupant, Terrain terrain) {
        return switch (occupant.name()) {
            case "PLAYER" -> "player_down";
            case "BOX" -> (terrain.name().equals("GOAL")
                    || terrain.name().equals("DESTINATION")) ? "box_goal" : "box";
            case "KEY_BOX", "BOX_KEY" -> "box_key";
            case "FRAGILE_BOX", "BOX_FRAGILE" -> "box_fragile_3";
            default -> null;
        };
    }

    private void draw(Graphics2D g, String name, int x, int y, int tile) {
        if (name.equals("player_down")) {
            String direction = facing.name().toLowerCase(java.util.Locale.ROOT);
            name = "player_" + direction + ((appearanceTick % 2L == 1L) ? "_step" : "");
        }
        BufferedImage image = sprites.image(name);
        g.drawImage(image, x, y, tile, tile, null);
    }
}
