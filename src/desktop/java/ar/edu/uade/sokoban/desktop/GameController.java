package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelFormatException;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.MovementEngine;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
            view.onMove(result.kind());
            return;
        }
        long pushes = current.pushes() + (result.kind() == MoveResult.Kind.PUSHED ? 1 : 0);
        current = new GamePresentation(result.board(), current.levelName(), current.moves() + 1, pushes);
        view.animateBoxSlide(slideFrames(result.board(), result.boxPath()));
        view.render(current);
        view.onMove(result.kind());
    }

    /** Presentation-only: intermediate boards so the view can show the slide. */
    private List<Board> slideFrames(Board finalBoard, List<Position> path) {
        if (path.size() < 2) {
            return List.of();
        }
        Position arrival = path.get(path.size() - 1);
        List<Board> frames = new ArrayList<>(path.size());
        for (Position step : path) {
            frames.add(withBoxAt(finalBoard, arrival, step));
        }
        return frames;
    }

    private Board withBoxAt(Board base, Position from, Position to) {
        if (from.equals(to)) {
            return base;
        }
        List<List<Cell>> rows = new ArrayList<>(base.height());
        for (List<Cell> row : base.rows()) {
            rows.add(new ArrayList<>(row));
        }
        Cell moving = rows.get(from.row()).get(from.column());
        rows.get(from.row()).set(from.column(), new Cell(moving.terrain(), Occupant.EMPTY));
        Cell landing = rows.get(to.row()).get(to.column());
        rows.get(to.row()).set(to.column(),
                new Cell(landing.terrain(), moving.occupant(), moving.resistance()));
        return new Board(rows);
    }
}
