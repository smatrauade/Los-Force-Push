package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelFormatException;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.MovementEngine;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/** Single-threaded coordinator. Swing calls it on the EDT; no movement rules here. */
public final class GameController {
    private static final int HISTORY_LIMIT = 15;
    private static final int MOVES_PER_UNDO = 5;
    private static final int MAX_UNDO_USES = 3;

    private final LevelLoader loader;
    private final MovementEngine movement;
    private final GameView view;

    private final ScoreCalculator scoreCalculator = new ScoreCalculator();

    private final Deque<GamePresentation> history = new ArrayDeque<>();

    private GamePresentation current;
    private GamePresentation initial;

    private int undoUsesTotal;
    private int consecutiveUndoUses;

    public GameController(LevelLoader loader, MovementEngine movement, GameView view) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.movement = Objects.requireNonNull(movement, "movement");
        this.view = Objects.requireNonNull(view, "view");
    }

    /** Publishes only a completely loaded level; failure preserves the previous session. */
    public void load(Path path) {
        Objects.requireNonNull(path, "path");
        try {
            var level = loader.load(path);

            current = new GamePresentation(
                    level.board(), path.getFileName().toString(), 0, 0);

            initial = current;
            history.clear();
            undoUsesTotal = 0;
            consecutiveUndoUses = 0;

            view.render(current);
            refreshUiCommands();

        } catch (IOException | LevelFormatException | SecurityException exception) {
            view.showLoadError("No se pudo cargar el TXT: " + exception.getMessage());
        }
    }

    public void move(Direction direction) {
        Objects.requireNonNull(direction, "direction");

        if (current == null) {
            return;
        }

        MoveResult result = movement.move(current.board(), direction);

        if (result.kind() == MoveResult.Kind.BLOCKED) {
            view.onMove(result.kind());
            return;
        }

        // Guardamos el estado anterior antes de publicar el nuevo.
        history.addLast(current);

        // El historial nunca conserva más de 15 movimientos.
        if (history.size() > HISTORY_LIMIT) {
            history.removeFirst();
        }

        long pushes = current.pushes()
                + (result.kind() == MoveResult.Kind.PUSHED ? 1 : 0);

        current = new GamePresentation(
                result.board(),
                current.levelName(),
                current.moves() + 1,
                pushes
        );

        view.render(current);
        view.onMove(result.kind());
        refreshUiCommands();
    }

    /** Restores the state from five successful moves ago. */
    public void undo() {
        if (!canUndo()) {
            return;
        }

        GamePresentation restored = null;

        for (int i = 0; i < MOVES_PER_UNDO; i++) {
            restored = history.removeLast();
        }

        current = Objects.requireNonNull(restored);

        undoUsesTotal++;
        consecutiveUndoUses++;

        view.render(current);
        refreshUiCommands();
    }

    /** Starts a completely new attempt on the initially loaded board. */
    public void restart() {
        if (initial == null) {
            return;
        }

        current = initial;
        history.clear();

        undoUsesTotal = 0;
        consecutiveUndoUses = 0;

        view.render(current);
        refreshUiCommands();
    }

    private boolean canUndo() {
        return current != null
                && history.size() >= MOVES_PER_UNDO
                && consecutiveUndoUses < MAX_UNDO_USES;
    }

    private void refreshUiCommands() {
        view.configureUiCommands(new UiCommands(
                canUndo() ? this::undo : null,
                current != null ? this::restart : null,
                null,
                null,
                null
        ));
    }

    int undoUsesTotal() {
        return undoUsesTotal;
    }

    int consecutiveUndoUses() {
        return consecutiveUndoUses;
    }

    /**Returns the current level statistics and calculated score.*/
    public LevelResult getLevelResult() {

        if (current == null) {
            throw new IllegalStateException(
                "No hay un nivel cargado"
            );
        }

        long score = scoreCalculator.calculate(current.moves(), current.pushes(), undoUsesTotal);

        return new LevelResult(
                current.levelName(),
                current.moves(),
                current.pushes(),
                undoUsesTotal,
                score
        );
    }
}
