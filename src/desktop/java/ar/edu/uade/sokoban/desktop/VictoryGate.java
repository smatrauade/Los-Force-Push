package ar.edu.uade.sokoban.desktop;

import java.util.Objects;

/** Defers only the finish screen while the board completes a final move. */
final class VictoryGate {
    private LevelResult pending;

    boolean defer(LevelResult result, boolean animating) {
        Objects.requireNonNull(result, "result");
        if (!animating) {
            pending = null;
            return false;
        }
        pending = result;
        return true;
    }

    LevelResult take() {
        LevelResult result = pending;
        pending = null;
        return result;
    }

    void clear() {
        pending = null;
    }
}
