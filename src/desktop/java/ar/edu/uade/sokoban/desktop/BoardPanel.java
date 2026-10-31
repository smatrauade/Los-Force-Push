package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Level;
import ar.edu.uade.sokoban.level.Position;
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

/** Read-only renderer; the runtime remains the sole authority for occupancy. */
public final class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int MARGIN = 24;
    private final transient PixelAssets sprites;
    private transient RuntimeState state;
    private int scale = 2;
    private Direction facing = Direction.DOWN;
    private long appearanceTick;
    public BoardPanel() { this(new PixelAssets()); }
    BoardPanel(PixelAssets assets) {
        sprites = Objects.requireNonNull(assets, "assets");
        setBackground(new Color(42, 33, 36)); setPreferredSize(new Dimension(560, 380));
        setFocusable(false); setOpaque(true);
    }
    /** Compatibility for ordinary board-only callers, rendered through the same runtime projection. */
    public void showBoard(Board board) { showState(RuntimeState.fromLevel(new Level(board))); }
    public void showState(RuntimeState next) { state = Objects.requireNonNull(next, "state"); updateSize(); repaint(); }
    public void setScale(int next) {
        if (next < 1 || next > 3) { throw new IllegalArgumentException("Escala permitida: 1, 2 o 3"); }
        scale = next; updateSize(); repaint();
    }
    public int getScale() { return scale; }
    public void setFacing(Direction next) { facing = Objects.requireNonNull(next, "direction"); appearanceTick++; repaint(); }
    private void updateSize() {
        if (state != null) {
            int tile = PixelAssets.NATIVE_SIZE * scale;
            setPreferredSize(new Dimension(state.width() * tile + MARGIN * 2, state.height() * tile + MARGIN * 2));
            revalidate();
        }
    }
    static String terrainSprite(RuntimeState state, Position p) {
        return switch (state.terrain().get(p.row()).get(p.column())) {
            case WALL -> "wall_" + Math.floorMod(p.row() * 3 + p.column(), 3);
            case FLOOR -> "floor";
            case GOAL -> state.boxAt(p) == null ? "goal" : "goal_active";
            case ICE -> "ice";
            case LOCK -> state.locks().stream().filter(l -> l.position().equals(p))
                    .anyMatch(l -> state.lockActive(l.id())) ? "lock_active" : "lock";
            case DYNAMIC_WALL -> state.wallOpen(p) ? "gate_open" : "gate_closed";
        };
    }
    static String boxSprite(RuntimeState state, Position p) {
        RuntimeState.Box box = state.boxAt(p);
        if (box == null) { return null; }
        return switch (box.kind()) {
            case NORMAL -> state.terrain().get(p.row()).get(p.column()) == RuntimeState.Ground.GOAL ? "box_goal" : "box";
            case KEY -> "box_key";
            case FRAGILE -> "box_fragile_" + Math.min(3, box.resistance());
        };
    }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            if (state == null) { return; }
            int tile = PixelAssets.NATIVE_SIZE * scale;
            g.setColor(new Color(104, 75, 64)); g.fillRect(MARGIN - 5, MARGIN - 5, state.width() * tile + 10, state.height() * tile + 10);
            g.setColor(new Color(228, 168, 111)); g.fillRect(MARGIN - 3, MARGIN - 3, state.width() * tile + 6, state.height() * tile + 6);
            for (int row = 0; row < state.height(); row++) {
                for (int col = 0; col < state.width(); col++) {
                    Position p = new Position(row, col); int x = MARGIN + col * tile; int y = MARGIN + row * tile;
                    String terrain = terrainSprite(state, p);
                    if (terrain.startsWith("wall_")) { draw(g, terrain, x, y, tile); continue; }
                    draw(g, "floor_" + Math.floorMod(row * 11 + col * 7, 3), x, y, tile);
                    if (!terrain.equals("floor")) { draw(g, terrain, x, y, tile); }
                    String box = boxSprite(state, p); if (box != null) { draw(g, box, x, y, tile); }
                    if (state.player().equals(p)) { draw(g, "player_" + facing.name().toLowerCase(Locale.ROOT)
                            + ((appearanceTick % 2L == 1L) ? "_step" : ""), x, y, tile); }
                }
            }
        } finally { g.dispose(); }
    }
    private void draw(Graphics2D g, String name, int x, int y, int tile) {
        g.drawImage(sprites.image(name), x, y, tile, tile, null);
    }
}
