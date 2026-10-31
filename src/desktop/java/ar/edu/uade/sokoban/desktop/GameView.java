package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.MoveResult;

/** Boundary for Swing and screen-free integration tests. */
public interface GameView {
    void render(GamePresentation presentation);
    void showLoadError(String message);

    /** A successful load or advance may navigate to the game screen. */
    default void onLevelStarted() { }

    /** Optional event so SFX does not execute during paint/repaint. */
    default void onMove(MoveResult.Kind kind) { }

    /** Displays a result calculated by the core; the UI does not infer win or score. */
    default void showVictory(LevelResult result) { }

    /** Presentation callbacks supplied according to the current session state. */
    default void configureUiCommands(UiCommands commands) { }

    /** Allows future gameplay events to request sound without owning audio devices. */
    default void playEffect(SoundManager.Effect effect) { }
}
