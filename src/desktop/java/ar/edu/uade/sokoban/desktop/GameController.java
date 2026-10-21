package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelFormatException;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.MovementEngine;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/** Single-threaded coordinator. Swing calls it on the EDT; no movement rules here. */
public final class GameController {
    private final LevelLoader loader;
    private final MovementEngine movement;
    private final GameView view;
    private GamePresentation current;

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
            current = new GamePresentation(level.board(), path.getFileName().toString(), 0, 0);
            view.render(current);
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
            return;
        }
        long pushes = current.pushes() + (result.kind() == MoveResult.Kind.PUSHED ? 1 : 0);
        current = new GamePresentation(result.board(), current.levelName(), current.moves() + 1, pushes);
        view.render(current);
    }
}
