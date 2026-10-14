package ar.edu.uade.sokoban.level;

/** Explicit failure of the experimental level contract. */
public final class LevelFormatException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public LevelFormatException(String message) {
        super(message);
    }
}
