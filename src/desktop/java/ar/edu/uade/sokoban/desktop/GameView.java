package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.MoveResult;

/** Boundary for Swing and screen-free integration tests. */
public interface GameView {
    void render(GamePresentation presentation);
    void showLoadError(String message);

    /** Optional event so SFX does not execute during paint/repaint. */
    default void onMove(MoveResult.Kind kind) { }

    /** Called by future victory/session code. This UI does not infer win or score. */
    default void showVictory(LevelResult result) { }

    /** Presentation-only callbacks injected when companion modules exist. */
    default void configureUiCommands(UiCommands commands) { }

    /** Allows future gameplay events to request sound without owning audio devices. */
    default void playEffect(SoundManager.Effect effect) { }
}
