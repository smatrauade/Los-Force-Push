
package ar.edu.uade.sokoban.desktop;

/**
 * Calculates the final score obtained during a level.
 */
public final class ScoreCalculator {

    private static final long INITIAL_SCORE = 1000;
    private static final long MOVE_PENALTY = 1;
    private static final long PUSH_PENALTY = 2;
    private static final long UNDO_PENALTY = 10;

    public long calculate(long moves, long pushes, int undoUses) {

        if (moves < 0 || pushes < 0 || undoUses < 0) {
            throw new IllegalArgumentException(
                    "Las estadisticas no pueden ser negativas"
            );
        }

        long score = INITIAL_SCORE
                - MOVE_PENALTY * moves
                - PUSH_PENALTY * pushes
                - UNDO_PENALTY * undoUses;

        return Math.max(0, score);
    }
}
