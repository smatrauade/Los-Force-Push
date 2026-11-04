package ar.edu.uade.sokoban.session;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.Objects;

/**
 * Pure victory query. KeyBox on Lock is not a Goal (E05). Empty or leftover
 * boxes cannot win. Individual hypothesis for OQ-01 details.
 */
public final class VictoryEvaluator {
    public record VictoryResult(boolean won, int goals, int boxes, int coveredGoals) { }

    public VictoryResult evaluate(RuntimeState state) {
        Objects.requireNonNull(state, "state");
        int goals = 0;
        int covered = 0;
        for (int row = 0; row < state.height(); row++) {
            for (int col = 0; col < state.width(); col++) {
                if (state.terrain().get(row).get(col) == RuntimeState.Ground.GOAL) {
                    goals++;
                    if (state.boxAt(new Position(row, col)) != null) {
                        covered++;
                    }
                }
            }
        }
        int boxes = state.boxes().size();
        return new VictoryResult(goals > 0 && boxes > 0 && covered == goals && boxes == goals,
                goals, boxes, covered);
    }
}
