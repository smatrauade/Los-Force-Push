package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.level.Terrain;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Read-only renderer. The runtime remains the sole authority for occupancy. */
public final class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int MARGIN = 24;
    private final transient PixelAssets sprites;
    private transient VisualThemeFactory theme = VisualThemes.original();
    private transient RuntimeState state;
    // Presentation-only frames: never used as the authoritative gameplay state.
    private transient Board overlay;
    private transient Timer slideTimer;
    private int scale = 2;
    private Direction facing = Direction.DOWN;
    private long appearanceTick;

    public BoardPanel() {
        this(new PixelAssets());
    }

    BoardPanel(PixelAssets assets) {
        sprites = Objects.requireNonNull(assets, "assets");
        setBackground(theme.palette().boardBackground());
        setPreferredSize(new Dimension(560, 380));
        setFocusable(false);
        setOpaque(true);
    }

    /** Presentation-only family change; the runtime state and zoom are untouched. */
    void setTheme(VisualThemeFactory next) {
        Objects.requireNonNull(next, "theme");
        theme = next;
        sprites.setTheme(next);
        setBackground(theme.palette().boardBackground());
        repaint();
    }

    public void showBoard(Board board) {
        showState(RuntimeState.fromLevel(new Level(board)));
    }

    public void showState(RuntimeState next) {
        stopSlide();
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

    /** The overlay is visual only; the session's RuntimeState remains authoritative. */
    public boolean isAnimating() {
        return slideTimer != null && slideTimer.isRunning();
    }

    /**
     * Transitional Board-frame adapter for the legacy animation contribution.
     * Not called by the current RuntimeState controller. Its connection to
     * authoritative ICE/Fragile events belongs to I1.B/I3.
     */
    public void playSlide(List<Board> frames, int millisPerCell) {
        stopSlide();
        if (state == null || frames == null || frames.size() < 2) {
            return;
        }
        if (millisPerCell <= 0) {
            throw new IllegalArgumentException("Intervalo de animacion invalido");
        }
        // A legacy Board cannot represent Lock/DynamicWall metadata faithfully.
        if (!state.locks().isEmpty() || !state.walls().isEmpty()) {
            return;
        }
        List<Board> sequence = List.copyOf(frames);
        for (Board frame : sequence) {
            if (frame.width() != state.width() || frame.height() != state.height()) {
                throw new IllegalArgumentException("Dimensiones de animacion incompatibles");
            }
        }
        int[] index = {0};
        overlay = sequence.get(0);
        repaint();
        slideTimer = new Timer(millisPerCell, event -> {
            index[0]++;
            if (index[0] >= sequence.size() - 1) {
                stopSlide();
            } else {
                overlay = sequence.get(index[0]);
            }
            repaint();
        });
        slideTimer.start();
    }

    private void stopSlide() {
        if (slideTimer != null) {
            slideTimer.stop();
            slideTimer = null;
        }
        overlay = null;
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
            case ICE -> "ice";
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
            // Pixel assets only have three intact resistance states; higher values share level 3.
            case FRAGILE -> "box_fragile_" + Math.min(3, box.resistance());
        };
    }

    /** Adapter point for historical Board-only callers. */
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
            if (overlay != null) {
                paintOverlay(g, overlay);
                return;
            }
            int tile = PixelAssets.NATIVE_SIZE * scale;
            g.setColor(theme.palette().boardFrameOuter());
            g.fillRect(MARGIN - 5, MARGIN - 5, state.width() * tile + 10, state.height() * tile + 10);
            g.setColor(theme.palette().boardFrameInner());
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

    /** Renders legacy frames without publishing or mutating the runtime. */
    private void paintOverlay(Graphics2D g, Board frame) {
        int tile = PixelAssets.NATIVE_SIZE * scale;
        g.setColor(theme.palette().boardFrameOuter());
        g.fillRect(MARGIN - 5, MARGIN - 5, frame.width() * tile + 10,
                frame.height() * tile + 10);
        g.setColor(theme.palette().boardFrameInner());
        g.fillRect(MARGIN - 3, MARGIN - 3, frame.width() * tile + 6,
                frame.height() * tile + 6);
        for (int row = 0; row < frame.height(); row++) {
            for (int col = 0; col < frame.width(); col++) {
                var cell = frame.rows().get(row).get(col);
                int x = MARGIN + col * tile;
                int y = MARGIN + row * tile;
                String terrain = spriteForTerrain(cell.terrain(), row, col);
                if (terrain.startsWith("wall_")) {
                    draw(g, terrain, x, y, tile);
                    continue;
                }
                draw(g, "floor_" + Math.floorMod(row * 11 + col * 7, 3), x, y, tile);
                if (!terrain.equals("floor")) {
                    draw(g, terrain, x, y, tile);
                }
                String occupant = switch (cell.occupant()) {
                    case EMPTY -> null;
                    case FRAGILE_BOX -> "box_fragile_" + Math.min(Math.max(cell.resistance(), 0), 3);
                    default -> spriteForOccupant(cell.occupant(), cell.terrain());
                };
                if (occupant != null) {
                    if (occupant.equals("player_down")) {
                        occupant = "player_" + facing.name().toLowerCase(Locale.ROOT)
                                + ((appearanceTick % 2L == 1L) ? "_step" : "");
                    }
                    draw(g, occupant, x, y, tile);
                }
            }
        }
    }

    private void draw(Graphics2D g, String name, int x, int y, int tile) {
        g.drawImage(sprites.image(name), x, y, tile, tile, null);
    }
}
