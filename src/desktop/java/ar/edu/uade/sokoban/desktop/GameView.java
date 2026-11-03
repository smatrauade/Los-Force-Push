package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.movement.MoveResult;
import java.util.List;

/** Boundary for Swing and screen-free integration tests. */
public interface GameView {
    void render(GamePresentation presentation);
    void showLoadError(String message);

    /** Optional slide animation; headless probes keep the default no-op. */
    default void animateBoxSlide(List<Board> frames) { }

    /** Optional event so SFX does not execute during paint/repaint. */
    default void onMove(MoveResult.Kind kind) { }

    /** Called by future victory/session code. This UI does not infer win or score. */
    default void showVictory(LevelResult result) { }

    /** Presentation-only callbacks injected when companion modules exist. */
    default void configureUiCommands(UiCommands commands) { }

    /** Allows future gameplay events to request sound without owning audio devices. */
    default void playEffect(SoundManager.Effect effect) { }
}
