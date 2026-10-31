package ar.edu.uade.sokoban.desktop;

import java.util.Objects;

/** Requests presentation refresh; never owns wall state. */
public final class GatePresentationObserver implements LockChangeSubject.Observer {
    private final GameView view;
    public GatePresentationObserver(GameView view) { this.view = Objects.requireNonNull(view, "view"); }
    @Override public void onLockChange(LockChangeSubject.Event event) { view.onGateChanged(event); }
}
