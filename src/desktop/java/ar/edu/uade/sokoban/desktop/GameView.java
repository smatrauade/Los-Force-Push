package ar.edu.uade.sokoban.desktop;

/** Presentation boundary shared by Swing and screen-free integration tests. */
public interface GameView {
    void render(GamePresentation presentation);
    void showLoadError(String message);
}
