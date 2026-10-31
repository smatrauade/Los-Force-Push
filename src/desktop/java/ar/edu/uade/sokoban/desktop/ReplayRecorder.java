package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Bounded whole-trajectory log: never exposes a truncated replay as valid. */
public final class ReplayRecorder {
    public static final int MAX_COMMANDS = 10_000;
    private RuntimeState initial;
    private String context;
    private final List<MoveCommand> commands = new ArrayList<>();
    private boolean overflow;
    public ReplayRecorder(RuntimeState initial, String context) { reset(initial, context); }
    public void reset(RuntimeState state, String levelContext) {
        initial = Objects.requireNonNull(state, "state"); context = Objects.requireNonNull(levelContext, "context");
        commands.clear(); overflow = false;
    }
    public void record(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        if (overflow) { return; }
        if (commands.size() == MAX_COMMANDS) { overflow = true; commands.clear(); return; }
        commands.add(new MoveCommand(direction));
    }
    public void undoFive() {
        if (overflow) { return; }
        if (commands.size() < 5) { throw new IllegalStateException("Replay history mismatch"); }
        commands.subList(commands.size() - 5, commands.size()).clear();
    }
    public Optional<ReplayTrace> verified(RuntimeState finalState) {
        if (overflow) { return Optional.empty(); }
        ReplayTrace trace = new ReplayTrace(context, initial, commands, finalState);
        try { return new ReplayPlayer(trace).verify() ? Optional.of(trace) : Optional.empty(); }
        catch (RuntimeException failure) { return Optional.empty(); }
    }
}
