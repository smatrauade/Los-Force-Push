package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.LevelFormatException;
import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import ar.edu.uade.sokoban.replay.ReplayRecorder;
import ar.edu.uade.sokoban.replay.ReplayTrace;
import ar.edu.uade.sokoban.session.GameSession;
import ar.edu.uade.sokoban.session.LevelCatalog;
import ar.edu.uade.sokoban.session.LevelProgression;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/** EDT coordinator; progression owns world state, victory and catalog advance. */
public final class GameController implements AutoCloseable {
    private final GameView view;
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private LevelProgression game;
    private ReplayRecorder recorder;
    private ReplayTrace completedReplay;
    private final LockChangeSubject lockChanges = new LockChangeSubject();

    public GameController(GameView view) {
        this.view = Objects.requireNonNull(view, "view");
        lockChanges.subscribe(view::onLockChange);
        lockChanges.subscribe(event -> {
            if (event.origin() == LockChangeSubject.Origin.MOVE) {
                boolean activated = event.locks().stream().anyMatch(LockChangeSubject.Delta::after);
                view.playEffect(activated ? SoundManager.Effect.LOCK : SoundManager.Effect.GATE);
            }
        });
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
            startRecording();
            publish();
            lockChanges.publish(LockChangeSubject.Origin.LEVEL_CHANGE, null, game.currentState());
        } catch (IOException | IllegalArgumentException | SecurityException failure) {
            view.showLoadError("No se pudo cargar el TXT: " + failure.getMessage());
        }
    }

    public void move(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        if (game == null || game.isFinalized()) {
            return;
        }
        RuntimeState before = game.currentState();
        RuntimeMoveResult result = game.move(direction);
        view.onMove(result.kind());
        if (result.kind() == MoveResult.Kind.BLOCKED) {
            return;
        }
        recorder.record(direction, result);
        // E03 only extends boxPath past its first destination on ICE, so a
        // longer path is authoritative evidence of a slide.
        boolean slidOnIce = result.boxPath().size() > 1;
        if (slidOnIce) {
            view.playEffect(SoundManager.Effect.ICE);
        }
        if (result.boxShattered()) {
            view.playEffect(SoundManager.Effect.FRAGILE);
        }
        if (result.boxDestination() != null && !result.boxShattered()
                && result.state().terrain().get(result.boxDestination().row())
                .get(result.boxDestination().column()) == RuntimeState.Ground.GOAL) {
            view.playEffect(SoundManager.Effect.GOAL);
        }
        publish();
        lockChanges.publish(LockChangeSubject.Origin.MOVE, before, result.state());
        // The final runtime is already visible; this only projects its path.
        if (result.kind() == MoveResult.Kind.PUSHED) {
            BoxAnimation animation = BoxAnimation.from(result);
            if (animation.needsFrames()) {
                view.animateBox(animation);
            }
        }
        if (game.victory().won()) {
            game.finish().orElseThrow();
            GameSession.Metrics metrics = game.metrics();
            completedReplay = recorder.verified(game.currentState(), metrics.moves(),
                    metrics.pushes()).orElse(null);
            publishCommands();
            view.showVictory(getLevelResult());
        }
    }

    public void restart() {
        if (game != null && !game.isFinalized()) {
            RuntimeState before = game.currentState();
            game.restart();
            startRecording();
            publish();
            lockChanges.publish(LockChangeSubject.Origin.RESTART, before, game.currentState());
            view.playEffect(SoundManager.Effect.RESTART);
        }
    }

    public void undo() {
        RuntimeState before = game == null ? null : game.currentState();
        if (game != null && game.canUndo() && game.undo()) {
            recorder.undoFive();
            publish();
            lockChanges.publish(LockChangeSubject.Origin.UNDO, before, game.currentState());
            view.playEffect(SoundManager.Effect.UNDO);
        }
    }

    int undoUsesTotal() {
        return game == null ? 0 : game.undoUsesTotal();
    }

    int consecutiveUndoUses() {
        return game == null ? 0 : game.consecutiveUndoUses();
    }

    public List<LevelProgression.CompletedLevel> results() {
        return game == null ? List.of() : game.results();
    }

    /** Only the current completed attempt is offered; playback owns no live session. */
    public Optional<ReplayTrace> completedReplay() {
        return Optional.ofNullable(completedReplay);
    }

    private void startRecording() {
        recorder = new ReplayRecorder(game.currentState(), game.levelId());
        completedReplay = null;
    }

    public LevelResult getLevelResult() {
        if (game == null) {
            throw new IllegalStateException("No hay un nivel cargado");
        }
        if (game.isFinalized()) {
            LevelProgression.CompletedLevel completed = game.finish().orElseThrow();
            return new LevelResult(game.levelDisplayName(), completed.metrics().moves(),
                    completed.metrics().pushes(), completed.undoUsesTotal(),
                    scoreCalculator.calculate(completed.metrics().moves(), completed.metrics().pushes(),
                            completed.undoUsesTotal()));
        }
        GameSession.Metrics metrics = game.metrics();
        int uses = game.undoUsesTotal();
        return new LevelResult(game.levelDisplayName(), metrics.moves(), metrics.pushes(), uses,
                scoreCalculator.calculate(metrics.moves(), metrics.pushes(), uses));
    }

    public void nextLevel() {
        if (game == null) {
            return;
        }
        LevelProgression.AdvanceResult result = game.advance();
        if (result.status() == LevelProgression.AdvanceStatus.ADVANCED) {
            startRecording();
            publish();
            lockChanges.publish(LockChangeSubject.Origin.LEVEL_CHANGE, null, game.currentState());
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
        view.configureReplay(completedReplay);
        view.configureUiCommands(new UiCommands(game != null && game.canUndo() ? this::undo : null,
                game != null && !game.isFinalized() ? this::restart : null,
                null, null,
                game != null && game.isFinalized() && game.hasNext() ? this::nextLevel : null));
    }

    /** Releases window-scoped presentation subscribers. No domain state is changed. */
    @Override public void close() {
        lockChanges.close();
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
            try {
                loader.loadExtended(path);
                return LevelCatalog.Format.EXTENDED;
            } catch (LevelFormatException extendedFailure) {
                try {
                    loader.loadExperimentalFragile(path);
                    return LevelCatalog.Format.EXPERIMENTAL_FRAGILE;
                } catch (LevelFormatException fragileFailure) {
                    extendedFailure.addSuppressed(fragileFailure);
                    throw extendedFailure;
                }
            }
        }
    }

    private static String safeId(Path path) {
        String raw = path.getFileName().toString().toLowerCase(Locale.ROOT).replaceAll("\\.[^.]+$", "");
        raw = raw.replaceAll("[^a-z0-9_]", "_");
        return raw.matches("[a-z][a-z0-9_]{0,31}") ? raw : "nivel";
    }
}
