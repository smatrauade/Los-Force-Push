package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.level.Terrain;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JPanel;

/** Read-only renderer. The runtime remains the sole authority for occupancy. */
public final class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int MARGIN = 24;
    private final transient PixelAssets sprites;
    private transient RuntimeState state;
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

    public void showBoard(Board board) {
        showState(RuntimeState.fromLevel(new Level(board)));
    }

    public void showState(RuntimeState next) {
        state = Objects.requireNonNull(next, "state");
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
        if (state != null) {
            int tile = PixelAssets.NATIVE_SIZE * scale;
            setPreferredSize(new Dimension(state.width() * tile + MARGIN * 2,
                    state.height() * tile + MARGIN * 2));
            revalidate();
        }
    }

    static String terrainSprite(RuntimeState world, Position position) {
        return switch (world.terrain().get(position.row()).get(position.column())) {
            case WALL -> "wall_" + Math.floorMod(position.row() * 3 + position.column(), 3);
            case FLOOR -> "floor";
            case GOAL -> world.boxAt(position) == null ? "goal" : "goal_active";
            case LOCK -> world.lockActive(world.locks().stream()
                    .filter(lock -> lock.position().equals(position))
                    .map(RuntimeState.Lock::id).findFirst().orElse("")) ? "lock_active" : "lock";
            case DYNAMIC_WALL -> world.wallOpen(position) ? "gate_open" : "gate_closed";
        };
    }

    static String boxSprite(RuntimeState world, Position position) {
        RuntimeState.Box box = world.boxAt(position);
        if (box == null) {
            return null;
        }
        return switch (box.kind()) {
            case NORMAL -> world.terrain().get(position.row()).get(position.column())
                    == RuntimeState.Ground.GOAL ? "box_goal" : "box";
            case KEY -> "box_key";
        };
    }

    /** Adapter point for historical Board-only callers. */
    static String spriteForTerrain(Terrain terrain, int row, int col) {
        return switch (terrain.name()) {
            case "WALL" -> "wall_" + Math.floorMod(row * 3 + col, 3);
            case "GOAL", "DESTINATION" -> "goal";
            case "LOCK", "LOCK_CLOSED", "LOCK_TILE" -> "lock";
            case "LOCK_OPEN", "LOCK_ACTIVE" -> "lock_active";
            case "CLOSED_WALL", "GATE_CLOSED", "WALL_CLOSED" -> "gate_closed";
            case "OPEN_WALL", "GATE_OPEN", "WALL_OPEN" -> "gate_open";
            default -> "floor";
        };
    }

    static String spriteForOccupant(Occupant occupant, Terrain terrain) {
        return switch (occupant.name()) {
            case "PLAYER" -> "player_down";
            case "BOX" -> (terrain.name().equals("GOAL")
                    || terrain.name().equals("DESTINATION")) ? "box_goal" : "box";
            case "KEY_BOX", "BOX_KEY" -> "box_key";
            default -> null;
        };
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            if (state == null) {
                return;
            }
            int tile = PixelAssets.NATIVE_SIZE * scale;
            g.setColor(new Color(104, 75, 64));
            g.fillRect(MARGIN - 5, MARGIN - 5, state.width() * tile + 10, state.height() * tile + 10);
            g.setColor(new Color(228, 168, 111));
            g.fillRect(MARGIN - 3, MARGIN - 3, state.width() * tile + 6, state.height() * tile + 6);
            for (int row = 0; row < state.height(); row++) {
                for (int col = 0; col < state.width(); col++) {
                    Position position = new Position(row, col);
                    int x = MARGIN + col * tile;
                    int y = MARGIN + row * tile;
                    String terrain = terrainSprite(state, position);
                    if (terrain.startsWith("wall_")) {
                        draw(g, terrain, x, y, tile);
                        continue;
                    }
                    draw(g, "floor_" + Math.floorMod(row * 11 + col * 7, 3), x, y, tile);
                    if (!terrain.equals("floor")) {
                        draw(g, terrain, x, y, tile);
                    }
                    String box = boxSprite(state, position);
                    if (box != null) {
                        draw(g, box, x, y, tile);
                    }
                    if (state.player().equals(position)) {
                        draw(g, "player_" + facing.name().toLowerCase(Locale.ROOT)
                                + ((appearanceTick % 2L == 1L) ? "_step" : ""), x, y, tile);
                    }
                }
            }
        } finally {
            g.dispose();
        }
    }

    private void draw(Graphics2D g, String name, int x, int y, int tile) {
        g.drawImage(sprites.image(name), x, y, tile, tile, null);
    }
}
