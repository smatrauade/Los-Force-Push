package ar.edu.uade.sokoban.session;

import java.util.Objects;

public record LevelStats(String levelContext, long moves, long pushes, long undoUsesTotal) {
    public LevelStats {
        Objects.requireNonNull(levelContext, "levelContext");
        if (levelContext.isBlank() || moves < 0 || pushes < 0 || pushes > moves || undoUsesTotal < 0) {
            throw new IllegalArgumentException("Invalid level statistics");
        }
    }
    public static LevelStats fromSession(GameSession session) {
        Objects.requireNonNull(session, "session");
        GameSession.Metrics m = session.metrics();
        return new LevelStats(session.levelContext(), m.moves(), m.pushes(), m.undoUsesTotal());
    }
}
