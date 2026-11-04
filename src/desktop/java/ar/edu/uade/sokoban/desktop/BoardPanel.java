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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Read-only renderer. The runtime remains the sole authority for occupancy. */
public final class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int MARGIN = 24;

    /** One deterministic presentation frame; a null sprite means the box is gone. */
    record BoxFrame(Position position, String sprite) {
        BoxFrame {
            Objects.requireNonNull(position, "position");
        }
    }
    private final transient PixelAssets sprites;
    private transient VisualThemeFactory theme = VisualThemes.original();
    private transient RuntimeState state;
    // Presentation-only frames: never used as the authoritative gameplay state.
    private transient Board overlay;
    private transient Timer slideTimer;
    private transient BoxAnimation boxAnimation;
    private transient List<BoxFrame> animationFrames = List.of();
    private transient int animationFrame;
    private transient Timer animationTimer;
    private transient Runnable animationCompletion;
    private transient long animationGeneration;
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
        stopAnimations();
        state = Objects.requireNonNull(next, "state");
        updateSize();
        repaint();
    }

    /** Read-only identity check used by headless presentation tests. */
    RuntimeState shownState() {
        return state;
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
        return boxAnimation != null || (slideTimer != null && slideTimer.isRunning());
    }

    /** Package-private frame accessors keep headless tests deterministic. */
    List<BoxFrame> animationFrames() {
        return List.copyOf(animationFrames);
    }

    int animationFrameIndex() {
        return animationFrame;
    }

    BoxFrame projectedBoxFrame() {
        return boxAnimation == null || animationFrame >= animationFrames.size()
                ? null : animationFrames.get(animationFrame);
    }

    boolean hasAnimationCompletion() {
        return animationCompletion != null;
    }

    Rectangle cellBounds(Position position) {
        int tile = PixelAssets.NATIVE_SIZE * scale;
        return new Rectangle(MARGIN + position.column() * tile,
                MARGIN + position.row() * tile, tile, tile);
    }

    /**
     * Starts one presentation-only trajectory. The final RuntimeState must
     * already be published; only the moving box is drawn from these frames.
     */
    void playBoxAnimation(BoxAnimation animation, int millisPerCell, Runnable onComplete) {
        Objects.requireNonNull(animation, "animation");
        if (millisPerCell <= 0) {
            throw new IllegalArgumentException("Intervalo de animacion invalido");
        }
        stopAnimations();
        if (state == null) {
            return;
        }
        List<BoxFrame> frames = planBoxAnimation(state, animation);
        boxAnimation = animation;
        animationFrames = frames;
        animationFrame = 0;
        animationCompletion = onComplete;
        long generation = ++animationGeneration;
        animationTimer = new Timer(millisPerCell, event -> {
            if (generation != animationGeneration) {
                return;
            }
            advanceAnimationFrame();
        });
        animationTimer.start();
        repaint();
    }

    /** Advances one frame; false means the sequence is finished or cancelled. */
    boolean advanceAnimationFrame() {
        if (boxAnimation == null) {
            return false;
        }
        animationFrame++;
        if (animationFrame >= animationFrames.size()) {
            finishAnimation();
            return false;
        }
        repaint();
        return true;
    }

    /** Cancels without completing: used by pause, close, load, restart and undo. */
    boolean cancelAnimation() {
        boolean active = isAnimating();
        stopAnimations();
        if (active) {
            repaint();
        }
        return active;
    }

    static List<BoxFrame> planBoxAnimation(RuntimeState world, BoxAnimation animation) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(animation, "animation");
        List<BoxFrame> frames = new ArrayList<>();
        for (Position position : animation.path()) {
            validatePosition(world, position);
            frames.add(new BoxFrame(position, boxSprite(world, animation.box(), position)));
        }
        if (animation.shattered()) {
            Position last = animation.path().get(animation.path().size() - 1);
            frames.add(new BoxFrame(last, "box_broken"));
            frames.add(new BoxFrame(last, null));
        }
        return List.copyOf(frames);
    }

    private static void validatePosition(RuntimeState world, Position position) {
        if (position.row() < 0 || position.column() < 0
                || position.row() >= world.height() || position.column() >= world.width()) {
            throw new IllegalArgumentException("Posicion de animacion fuera del tablero");
        }
    }

    private void finishAnimation() {
        animationGeneration++;
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        boxAnimation = null;
        animationFrames = List.of();
        animationFrame = 0;
        Runnable completion = animationCompletion;
        animationCompletion = null;
        repaint();
        if (completion != null) {
            completion.run();
        }
    }

    private void stopBoxAnimation() {
        animationGeneration++;
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        boxAnimation = null;
        animationFrames = List.of();
        animationFrame = 0;
        animationCompletion = null;
    }

    private void stopAnimations() {
        stopSlide();
        stopBoxAnimation();
    }

    /**
     * Transitional Board-frame adapter for the legacy animation contribution.
     * Not called by the current RuntimeState controller. Its connection to
     * authoritative ICE/Fragile events belongs to I1.B/I3.
     */
    public void playSlide(List<Board> frames, int millisPerCell) {
        stopAnimations();
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
        return box == null ? null : boxSprite(world, box, position);
    }

    /** Uses the animation's own box metadata, never the final world's replacement. */
    static String boxSprite(RuntimeState world, RuntimeState.Box box, Position position) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(box, "box");
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
            BoxFrame frame = boxAnimation == null || animationFrame >= animationFrames.size()
                    ? null : animationFrames.get(animationFrame);
            Position suppressed = boxAnimation == null ? null : boxAnimation.destination();
            paintRuntime(g, state, suppressed, frame);
        } finally {
            g.dispose();
        }
    }

    private void paintRuntime(Graphics2D g, RuntimeState world, Position suppressedBox,
                              BoxFrame animated) {
        int tile = PixelAssets.NATIVE_SIZE * scale;
        g.setColor(theme.palette().boardFrameOuter());
        g.fillRect(MARGIN - 5, MARGIN - 5, world.width() * tile + 10, world.height() * tile + 10);
        g.setColor(theme.palette().boardFrameInner());
        g.fillRect(MARGIN - 3, MARGIN - 3, world.width() * tile + 6, world.height() * tile + 6);
        for (int row = 0; row < world.height(); row++) {
            for (int col = 0; col < world.width(); col++) {
                Position position = new Position(row, col);
                int x = MARGIN + col * tile;
                int y = MARGIN + row * tile;
                String terrain = terrainSprite(world, position);
                if (terrain.startsWith("wall_")) {
                    draw(g, terrain, x, y, tile);
                    continue;
                }
                draw(g, "floor_" + Math.floorMod(row * 11 + col * 7, 3), x, y, tile);
                if (!terrain.equals("floor")) {
                    draw(g, terrain, x, y, tile);
                }
                String box = boxSprite(world, position);
                if (box != null && !position.equals(suppressedBox)) {
                    draw(g, box, x, y, tile);
                }
                if (animated != null && animated.position().equals(position)
                        && animated.sprite() != null) {
                    draw(g, animated.sprite(), x, y, tile);
                }
                if (world.player().equals(position)) {
                    draw(g, "player_" + facing.name().toLowerCase(Locale.ROOT)
                            + ((appearanceTick % 2L == 1L) ? "_step" : ""), x, y, tile);
                }
            }
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
