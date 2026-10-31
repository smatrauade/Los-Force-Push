package ar.edu.uade.sokoban.session;

/** Pluggable scoring policy; the caller checks victory before invoking it. */
@FunctionalInterface
public interface ScoreStrategy {
    long calculate(LevelStats stats);
}
