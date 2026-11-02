package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.LevelFormatException;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import ar.edu.uade.sokoban.session.GameSession;
import ar.edu.uade.sokoban.session.LevelCatalog;
import ar.edu.uade.sokoban.session.LevelProgression;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** EDT coordinator; progression owns world state, victory and catalog advance. */
public final class GameController {
    private final GameView view;
    private LevelProgression game;

    public GameController(GameView view) {
        this.view = Objects.requireNonNull(view, "view");
    }

    public void load(Path path) {
        Objects.requireNonNull(path, "path");
        try {
            loadCatalog(singleEntry(path, detectFormat(path)));
        } catch (IOException | IllegalArgumentException | SecurityException failure) {
            view.showLoadError("No se pudo cargar el TXT: " + failure.getMessage());
        }
    }

    public void loadCatalog(LevelCatalog catalog) {
        try {
            game = new LevelProgression(catalog);
            publish();
        } catch (IOException | IllegalArgumentException | SecurityException failure) {
            view.showLoadError("No se pudo cargar el TXT: " + failure.getMessage());
        }
    }

    public void move(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        if (game == null || game.isFinalized()) {
            return;
        }
        RuntimeMoveResult result = game.move(direction);
        view.onMove(result.kind());
        if (result.kind() == MoveResult.Kind.BLOCKED) {
            return;
        }
        if (!result.lockChanges().isEmpty()) {
            boolean opened = result.lockChanges().stream().anyMatch(RuntimeMoveResult.LockChange::active);
            view.playEffect(opened ? SoundManager.Effect.LOCK : SoundManager.Effect.GATE);
        }
        if (result.boxDestination() != null
                && result.state().terrain().get(result.boxDestination().row())
                .get(result.boxDestination().column()) == RuntimeState.Ground.GOAL) {
            view.playEffect(SoundManager.Effect.GOAL);
        }
        publish();
        if (game.victory().won()) {
            LevelProgression.CompletedLevel completed = game.finish().orElseThrow();
            publishCommands();
            GameSession.Metrics metrics = completed.metrics();
            view.showVictory(new LevelResult(game.levelDisplayName(), metrics.moves(), metrics.pushes(), 0, 0));
        }
    }

    public void restart() {
        if (game != null && !game.isFinalized()) {
            game.restart();
            publish();
            view.playEffect(SoundManager.Effect.RESTART);
        }
    }

    public void nextLevel() {
        if (game == null) {
            return;
        }
        LevelProgression.AdvanceResult result = game.advance();
        if (result.status() == LevelProgression.AdvanceStatus.ADVANCED) {
            publish();
        } else if (result.status() == LevelProgression.AdvanceStatus.LOAD_FAILED) {
            view.showLoadError("No se pudo cargar el siguiente nivel: " + result.diagnostic());
        }
    }

    private void publish() {
        GameSession.Metrics metrics = game.metrics();
        view.render(new GamePresentation(game.currentState(), game.levelDisplayName(),
                metrics.moves(), metrics.pushes()));
        publishCommands();
    }

    private void publishCommands() {
        view.configureUiCommands(new UiCommands(null,
                game != null && !game.isFinalized() ? this::restart : null,
                null, null,
                game != null && game.isFinalized() && game.hasNext() ? this::nextLevel : null));
    }

    private static LevelCatalog singleEntry(Path path, LevelCatalog.Format format) {
        return new LevelCatalog(List.of(new LevelCatalog.Entry(safeId(path), path, format)));
    }

    private static LevelCatalog.Format detectFormat(Path path) throws IOException {
        LevelLoader loader = new LevelLoader(new LevelParser());
        try {
            loader.load(path);
            return LevelCatalog.Format.STANDARD;
        } catch (LevelFormatException ignored) {
            loader.loadExtended(path);
            return LevelCatalog.Format.EXTENDED;
        }
    }

    private static String safeId(Path path) {
        String raw = path.getFileName().toString().toLowerCase(Locale.ROOT).replaceAll("\\.[^.]+$", "");
        raw = raw.replaceAll("[^a-z0-9_]", "_");
        return raw.matches("[a-z][a-z0-9_]{0,31}") ? raw : "nivel";
    }
}
