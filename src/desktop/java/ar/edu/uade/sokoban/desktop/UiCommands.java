package ar.edu.uade.sokoban.desktop;

/**
 * Optional UI commands. The session/logic modules own implementations; Swing only
 * exposes buttons and invokes the supplied callbacks on the Swing EDT.
 * Null means not yet implemented, so the corresponding control stays disabled.
 */
public record UiCommands(Runnable undo, Runnable restart, Runnable save,
                         Runnable loadSaved, Runnable nextLevel) {
    public static final UiCommands EMPTY = new UiCommands(null, null, null, null, null);
}
