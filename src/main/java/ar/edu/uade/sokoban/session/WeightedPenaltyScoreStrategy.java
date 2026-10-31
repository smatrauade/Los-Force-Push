package ar.edu.uade.sokoban.session;

import java.util.Objects;

/** Saturating nonnegative weighted penalties, without overflow. */
public record WeightedPenaltyScoreStrategy(long base, long movePenalty, long pushPenalty, long undoPenalty)
        implements ScoreStrategy {
    public WeightedPenaltyScoreStrategy() { this(10_000, 10, 25, 100); }
    public WeightedPenaltyScoreStrategy {
        if (base < 0 || movePenalty < 0 || pushPenalty < 0 || undoPenalty < 0) {
            throw new IllegalArgumentException("Negative score configuration");
        }
    }
    @Override public long calculate(LevelStats stats) {
        Objects.requireNonNull(stats, "stats");
        long value = subtract(base, movePenalty, stats.moves());
        value = subtract(value, pushPenalty, stats.pushes());
        return subtract(value, undoPenalty, stats.undoUsesTotal());
    }
    private static long subtract(long remaining, long weight, long count) {
        if (weight == 0 || count == 0) { return remaining; }
        if (count > remaining / weight) { return 0; }
        return remaining - weight * count;
    }
}
