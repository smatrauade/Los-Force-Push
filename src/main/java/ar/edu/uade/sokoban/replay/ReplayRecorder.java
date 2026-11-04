package ar.edu.uade.sokoban.replay;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Bounded recorder. Failure disables replay, never the real game or its Undo. */
public final class ReplayRecorder {
    public static final int MAX_COMMANDS = 10_000;

    private RuntimeState initialState;
    private String levelContext;
    private final List<MoveCommand> commands = new ArrayList<>();
    private boolean invalid;

    public ReplayRecorder(RuntimeState initialState, String levelContext) {
        reset(initialState, levelContext);
    }

    /** Called for a successful new attempt after Restart or level change. */
    public void reset(RuntimeState initialState, String levelContext) {
        Objects.requireNonNull(initialState, "initialState");
        Objects.requireNonNull(levelContext, "levelContext");
        if (levelContext.isBlank()) {
            throw new IllegalArgumentException("Blank replay level context");
        }
        this.initialState = initialState;
        this.levelContext = levelContext;
        commands.clear();
        invalid = false;
    }

    /** Record AFTER GameSession.move; blocked moves are intentionally ignored. */
    public boolean record(Direction direction, RuntimeMoveResult result) {
        Objects.requireNonNull(direction, "direction");
        Objects.requireNonNull(result, "result");
        if (result.kind() == MoveResult.Kind.BLOCKED || invalid) {
            return false;
        }
        if (commands.size() == MAX_COMMANDS) {
            invalidate();
            return false;
        }
        commands.add(new MoveCommand(direction));
        return true;
    }

    /** Must be called only when the authoritative Undo of five moves succeeds. */
    public boolean undoFive() {
        if (invalid) {
            return false;
        }
        if (commands.size() < 5) {
            invalidate();
            return false;
        }
        commands.subList(commands.size() - 5, commands.size()).clear();
        return true;
    }

    public boolean available() {
        return !invalid;
    }

    public int commandCount() {
        return commands.size();
    }

    /** Fail closed on truncated, incoherent, or non-reproducible histories. */
    public Optional<ReplayTrace> verified(RuntimeState finalState, long moves, long pushes) {
        Objects.requireNonNull(finalState, "finalState");
        if (invalid || moves != commands.size() || pushes < 0 || pushes > moves) {
            return Optional.empty();
        }
        try {
            ReplayTrace trace = new ReplayTrace(levelContext, initialState, commands,
                    finalState, moves, pushes);
            return new ReplayPlayer(trace).verify() ? Optional.of(trace) : Optional.empty();
        } catch (RuntimeException verificationFailure) {
            return Optional.empty();
        }
    }

    private void invalidate() {
        invalid = true;
        commands.clear();
    }
}
