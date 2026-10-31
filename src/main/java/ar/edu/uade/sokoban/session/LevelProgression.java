package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.level.LevelLoader;
import ar.edu.uade.sokoban.level.LevelParser;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** In-memory campaign coordinator. Once finalized, actions are frozen until explicit advance. */
public final class LevelProgression {
    public enum AdvanceStatus { ADVANCED, NOT_FINALIZED, LOAD_FAILED, CAMPAIGN_COMPLETED }
    public record AdvanceResult(AdvanceStatus status, String diagnostic) {
        public AdvanceResult { Objects.requireNonNull(status, "status"); Objects.requireNonNull(diagnostic, "diagnostic"); }
    }
    public record LevelResult(VictoryEvaluator.VictoryResult victory, LevelStats stats, long score) {
        public LevelResult {
            Objects.requireNonNull(victory, "victory"); Objects.requireNonNull(stats, "stats");
            if (!victory.won() || score < 0) { throw new IllegalArgumentException("Invalid completed result"); }
        }
    }
    private final LevelCatalog catalog;
    private final ScoreStrategy score;
    private final LevelLoader loader = new LevelLoader(new LevelParser());
    private final VictoryEvaluator evaluator = new VictoryEvaluator();
    private final List<LevelResult> results = new ArrayList<>();
    private GameSession session;
    private int index;
    private LevelResult finalized;

    public LevelProgression(LevelCatalog catalog, ScoreStrategy score) throws IOException {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.score = Objects.requireNonNull(score, "score");
        LevelCatalog.Entry first = catalog.levels().get(0);
        session = new GameSession(load(first), first.id());
    }
    private RuntimeState load(LevelCatalog.Entry entry) throws IOException {
        return switch (entry.format()) {
            case STANDARD -> RuntimeState.fromLevel(loader.load(entry.path()));
            case EXTENDED -> RuntimeState.fromExtended(loader.loadExtended(entry.path()));
        };
    }
    public RuntimeState currentState() { return session.currentState(); }
    public GameSession.Metrics metrics() { return session.metrics(); }
    public String levelId() { return session.levelContext(); }
    public List<LevelResult> results() { return List.copyOf(results); }
    public VictoryEvaluator.VictoryResult victory() { return evaluator.evaluate(session.currentState()); }
    public boolean isFinalized() { return finalized != null; }
    public boolean hasNext() { return index + 1 < catalog.levels().size(); }
    public boolean campaignCompleted() { return finalized != null && !hasNext(); }
    private void requireUnfinished() {
        if (finalized != null) { throw new IllegalStateException("Finalized level is frozen until advance"); }
    }
    public RuntimeMoveResult move(Direction direction) { requireUnfinished(); return session.move(direction); }
    public GameSession.UndoResult undoFive() { requireUnfinished(); return session.undoFive(); }
    public void restart() { requireUnfinished(); session.restart(); }
    public Optional<LevelResult> finish() {
        if (finalized != null) { return Optional.of(finalized); }
        VictoryEvaluator.VictoryResult victory = victory();
        if (!victory.won()) { return Optional.empty(); }
        LevelStats stats = LevelStats.fromSession(session);
        LevelResult result = new LevelResult(victory, stats, score.calculate(stats));
        results.add(result);
        finalized = result;
        return Optional.of(result);
    }
    public AdvanceResult advance() {
        if (finalized == null) { return new AdvanceResult(AdvanceStatus.NOT_FINALIZED, "Current level not finalized"); }
        if (!hasNext()) { return new AdvanceResult(AdvanceStatus.CAMPAIGN_COMPLETED, "No next level"); }
        LevelCatalog.Entry next = catalog.levels().get(index + 1);
        final RuntimeState state;
        try { state = load(next); }
        catch (IOException | IllegalArgumentException failure) {
            return new AdvanceResult(AdvanceStatus.LOAD_FAILED, failure.getClass().getSimpleName());
        }
        session.changeLevel(state, next.id());
        index++;
        finalized = null;
        return new AdvanceResult(AdvanceStatus.ADVANCED, "Level loaded");
    }
}
