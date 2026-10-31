package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.session.GameSession;
import ar.edu.uade.sokoban.session.LevelCatalog;
import ar.edu.uade.sokoban.session.LevelProgression;
import ar.edu.uade.sokoban.session.WeightedPenaltyScoreStrategy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/** EDT coordinator; progression owns all game state and rules. */
public final class GameController {
    private final GameView view;
    private LevelProgression game;
    public GameController(GameView view) { this.view = Objects.requireNonNull(view, "view"); }
    public void load(Path path) { load(path, LevelCatalog.Format.STANDARD); }
    public void load(Path path, LevelCatalog.Format format) {
        Objects.requireNonNull(path, "path"); Objects.requireNonNull(format, "format");
        loadCatalog(new LevelCatalog(List.of(new LevelCatalog.Entry("nivel", path, format))));
    }
    public void loadCatalog(LevelCatalog catalog) {
        try {
            LevelProgression candidate = new LevelProgression(catalog, new WeightedPenaltyScoreStrategy());
            game = candidate;
            publish(); view.onLevelStarted();
        } catch (IOException | IllegalArgumentException | SecurityException failure) {
            view.showLoadError("No se pudo cargar el TXT: " + failure.getMessage());
        }
    }
    public void move(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        if (game == null || game.isFinalized()) { return; }
        var before = game.currentState();
        RuntimeMoveResult result = game.move(direction);
        view.onMove(result.kind());
        if (result.kind() == MoveResult.Kind.BLOCKED) { return; }
        if (result.boxDistance() > 1) { view.playEffect(SoundManager.Effect.ICE); }
        if (result.broken()) { view.playEffect(SoundManager.Effect.FRAGILE); }
        if (!result.lockChanges().isEmpty()) {
            view.playEffect(SoundManager.Effect.LOCK);
            if (before.walls().stream().anyMatch(w -> before.wallOpen(w.position())
                    != result.state().wallOpen(w.position()))) {
                view.playEffect(SoundManager.Effect.GATE);
            }
        }
        if (result.boxDestination() != null && !result.broken()
                && result.state().terrain().get(result.boxDestination().row()).get(result.boxDestination().column())
                        == ar.edu.uade.sokoban.movement.RuntimeState.Ground.GOAL) {
            view.playEffect(SoundManager.Effect.GOAL);
        }
        publish();
        if (game.victory().won()) {
            var resultLevel = game.finish().orElseThrow();
            publishCommands();
            var stats = resultLevel.stats();
            view.showVictory(new LevelResult(stats.levelContext(), stats.moves(), stats.pushes(),
                    stats.undoUsesTotal(), resultLevel.score()));
        }
    }
    public void undo() {
        if (game != null && !game.isFinalized() && game.undoFive() == GameSession.UndoResult.RESTORED) {
            publish(); view.playEffect(SoundManager.Effect.UNDO);
        }
    }
    public void restart() {
        if (game != null && !game.isFinalized()) { game.restart(); publish(); view.playEffect(SoundManager.Effect.RESTART); }
    }
    public void nextLevel() {
        if (game == null) { return; }
        var result = game.advance();
        if (result.status() == LevelProgression.AdvanceStatus.ADVANCED) { publish(); view.onLevelStarted(); }
        else if (result.status() == LevelProgression.AdvanceStatus.LOAD_FAILED) { view.showLoadError("No se pudo cargar el siguiente nivel: " + result.diagnostic()); }
    }
    private void publish() {
        var m = game.metrics();
        view.render(new GamePresentation(game.currentState(), game.levelId(), m.moves(), m.pushes(),
                game.undoUsesRemaining(), game.historyAvailable()));
        publishCommands();
    }
    private void publishCommands() {
        view.configureUiCommands(new UiCommands(game.canUndo() ? this::undo : null,
                !game.isFinalized() ? this::restart : null, null, null,
                game.isFinalized() && game.hasNext() ? this::nextLevel : null));
    }
}
