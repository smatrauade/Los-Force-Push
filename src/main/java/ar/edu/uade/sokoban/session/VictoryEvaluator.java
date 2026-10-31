package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/** Pure victory query; the equality and nonempty requirements are individual hypotheses. */
public final class VictoryEvaluator {
    public record VictoryResult(boolean won, int goals, int boxes, int coveredGoals) { }
    public VictoryResult evaluate(RuntimeState state) {
        Objects.requireNonNull(state, "state");
        int goals = 0;
        int covered = 0;
        for (int r = 0; r < state.height(); r++) {
            for (int c = 0; c < state.width(); c++) {
                if (state.terrain().get(r).get(c) == RuntimeState.Ground.GOAL) {
                    goals++;
                    if (state.boxAt(new Position(r, c)) != null) { covered++; }
                }
            }
        }
        int boxes = state.boxes().size();
        return new VictoryResult(goals > 0 && boxes > 0 && covered == goals && boxes == goals,
                goals, boxes, covered);
    }
}
