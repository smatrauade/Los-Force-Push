package ar.edu.uade.sokoban.desktop;

import java.util.Objects;

/** Sound is emitted for committed moves only; restored states use undo/restart sounds. */
public final class LockSoundObserver implements LockChangeSubject.Observer {
    private final GameView view;
    public LockSoundObserver(GameView view) { this.view = Objects.requireNonNull(view, "view"); }
    @Override public void onLockChange(LockChangeSubject.Event event) {
        if (event.origin() != LockChangeSubject.Origin.MOVE) { return; }
        if (!event.locks().isEmpty()) { view.playEffect(SoundManager.Effect.LOCK); }
        if (event.wallsChanged()) { view.playEffect(SoundManager.Effect.GATE); }
    }
}
