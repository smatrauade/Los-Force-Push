package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.replay.ReplayTrace;
import java.util.List;

/** Boundary for Swing and screen-free integration tests. */
public interface GameView {
    void render(GamePresentation presentation);
    void showLoadError(String message);

    /** Optional slide animation; headless probes keep the default no-op. */
    default void animateBoxSlide(List<Board> frames) { }

    /** Optional authoritative ICE/Fragile trajectory; never recomputed by the view. */
    default void animateBox(BoxAnimation animation) { }

    /** Optional event so SFX does not execute during paint/repaint. */
    default void onMove(MoveResult.Kind kind) { }

    /** Called by future victory/session code. This UI does not infer win or score. */
    default void showVictory(LevelResult result) { }

    /** Presentation-only callbacks injected when companion modules exist. */
    default void configureUiCommands(UiCommands commands) { }

    /** Null clears availability; only complete, verified attempts may be offered. */
    default void configureReplay(ReplayTrace trace) { }

    /** Confirmed facts for presentation; subscribers never control dynamic walls. */
    default void onLockChange(LockChangeSubject.Event event) { }

    /** Allows future gameplay events to request sound without owning audio devices. */
    default void playEffect(SoundManager.Effect effect) { }
}
