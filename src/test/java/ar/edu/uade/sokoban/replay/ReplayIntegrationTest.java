package ar.edu.uade.sokoban.replay;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import ar.edu.uade.sokoban.movement.RuntimeState.Box;
import ar.edu.uade.sokoban.movement.RuntimeState.DynamicWall;
import ar.edu.uade.sokoban.movement.RuntimeState.Ground;
import ar.edu.uade.sokoban.movement.RuntimeState.Lock;
import ar.edu.uade.sokoban.session.GameSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Deterministic Java-base contract tests. No Swing, filesystem or external TXT. */
public final class ReplayIntegrationTest {
    private static int passed;
    private static final RuntimeMovementEngine ENGINE = new RuntimeMovementEngine();

    private ReplayIntegrationTest() { }

    public static void main(String[] args) {
        check("successful normal push and exact counters", ReplayIntegrationTest::normalPush);
        check("blocked movement is not recorded", ReplayIntegrationTest::blockedIgnored);
        check("ICE trajectory replays from the authoritative engine", ReplayIntegrationTest::ice);
        check("Fragile shatters after ICE and identity is preserved", ReplayIntegrationTest::fragile);
        check("KeyBox activates Lock and DynamicWall", ReplayIntegrationTest::keyLockWall);
        check("Undo 5 removes discarded commands", ReplayIntegrationTest::undoBranch);
        check("Undo mismatch invalidates replay without throwing", ReplayIntegrationTest::undoMismatch);
        check("GameSession authoritative Undo reconciles Replay", ReplayIntegrationTest::realSessionUndo);
        check("GameSession authoritative Restart resets Replay", ReplayIntegrationTest::realSessionRestart);
        check("Restart resets bounded history and attempt identity", ReplayIntegrationTest::restart);
        check("10_001st command invalidates rather than truncates", ReplayIntegrationTest::overflow);
        check("tampered final state fails verification", ReplayIntegrationTest::tamperedFinal);
        check("incorrect pushes fail verification", ReplayIntegrationTest::incorrectPushCount);
        check("trace is an immutable defensive copy", ReplayIntegrationTest::immutableTrace);
        check("trace rejects invalid metadata", ReplayIntegrationTest::invalidTrace);
        check("playback cursor is independent of verification", ReplayIntegrationTest::cursorIndependence);
        check("replay reset preserves initial state and counters", ReplayIntegrationTest::cursorReset);
        check("blocked command fails closed with unchanged cursor", ReplayIntegrationTest::blockedTrace);
        check("replay completion cannot silently advance", ReplayIntegrationTest::completion);
        check("initial empty attempt is valid only on exact state", ReplayIntegrationTest::emptyAttempt);
        System.out.printf("REPLAY RESULT: %d PASS, 0 FAIL%n", passed);
    }

    private static void check(String label, Runnable task) {
        try {
            task.run();
            passed++;
        } catch (RuntimeException | AssertionError failure) {
            throw new AssertionError("Replay test failed: " + label, failure);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void rejects(Runnable task) {
        try {
            task.run();
        } catch (IllegalStateException | IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected input or state rejection");
    }

    private static RuntimeState board(String... lines) {
        List<List<Ground>> grid = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();
        Position player = null;
        for (int row = 0; row < lines.length; row++) {
            List<Ground> cells = new ArrayList<>();
            for (int col = 0; col < lines[row].length(); col++) {
                char cell = lines[row].charAt(col);
                Position here = new Position(row, col);
                cells.add(switch (cell) {
                    case '#' -> Ground.WALL;
                    case '.' -> Ground.GOAL;
                    case '~' -> Ground.ICE;
                    default -> Ground.FLOOR;
                });
                if (cell == '@') {
                    player = here;
                } else if (cell == '$') {
                    boxes.add(Box.normal(here));
                } else if (cell == 'f') {
                    boxes.add(Box.fragile("f1", here, 1));
                }
            }
            grid.add(cells);
        }
        return new RuntimeState(grid, player, boxes);
    }

    private static RuntimeState singlePush() {
        return board("#######", "#@$.  #", "#     #", "#######");
    }

    private static RuntimeState emptyBoard() {
        return board("#########", "#       #", "# @     #", "#       #", "#########");
    }

    private static RuntimeMoveResult move(ReplayRecorder recorder, RuntimeState before, Direction direction) {
        RuntimeMoveResult result = ENGINE.move(before, direction);
        recorder.record(direction, result);
        return result;
    }

    private static void normalPush() {
        RuntimeState initial = singlePush();
        ReplayRecorder recorder = new ReplayRecorder(initial, "normal");
        RuntimeMoveResult result = move(recorder, initial, Direction.RIGHT);
        require(result.kind() == MoveResult.Kind.PUSHED, "push missing");
        ReplayTrace trace = recorder.verified(result.state(), 1, 1).orElseThrow();
        ReplayPlayer player = new ReplayPlayer(trace);
        require(player.verify() && player.currentState().equals(initial), "verification mutated cursor");
        player.step();
        require(player.currentState().equals(result.state()) && player.pushes() == 1, "push mismatch");
    }

    private static void blockedIgnored() {
        RuntimeState initial = board("#####", "#@###", "#####");
        ReplayRecorder recorder = new ReplayRecorder(initial, "blocked");
        RuntimeMoveResult blocked = ENGINE.move(initial, Direction.RIGHT);
        require(blocked.kind() == MoveResult.Kind.BLOCKED, "fixture is not blocked");
        require(!recorder.record(Direction.RIGHT, blocked), "blocked move got recorded");
        require(recorder.commandCount() == 0 && recorder.available(), "blocked changed recorder");
        require(recorder.verified(initial, 0, 0).isPresent(), "empty history mismatch");
    }

    private static void ice() {
        RuntimeState initial = board("########", "#@$~~. #", "#      #", "########");
        ReplayRecorder recorder = new ReplayRecorder(initial, "ice");
        RuntimeMoveResult result = move(recorder, initial, Direction.RIGHT);
        require(result.boxPath().size() == 3 && !result.boxShattered(), "ice path mismatch");
        require(recorder.verified(result.state(), 1, 1).isPresent(), "ice replay failed");
    }

    private static void fragile() {
        RuntimeState initial = board("########", "#@f~~. #", "#      #", "########");
        ReplayRecorder recorder = new ReplayRecorder(initial, "fragile");
        RuntimeMoveResult result = move(recorder, initial, Direction.RIGHT);
        require(result.boxShattered() && result.boxPath().size() == 3, "fragile break order");
        require(result.state().boxes().isEmpty(), "fragile not destroyed");
        require(recorder.verified(result.state(), 1, 1).isPresent(), "fragile replay diverged");
    }

    private static void keyLockWall() {
        List<List<Ground>> grid = List.of(
                List.of(Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL),
                List.of(Ground.WALL, Ground.FLOOR, Ground.FLOOR, Ground.LOCK,
                        Ground.DYNAMIC_WALL, Ground.GOAL, Ground.WALL),
                List.of(Ground.WALL, Ground.FLOOR, Ground.FLOOR, Ground.FLOOR,
                        Ground.FLOOR, Ground.FLOOR, Ground.WALL),
                List.of(Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL, Ground.WALL));
        RuntimeState initial = new RuntimeState(grid, new Position(1, 1),
                List.of(Box.key("k1", new Position(1, 2))),
                List.of(new Lock("l1", new Position(1, 3))),
                List.of(new DynamicWall("w1", new Position(1, 4), Set.of("l1"))));
        ReplayRecorder recorder = new ReplayRecorder(initial, "locks");
        RuntimeMoveResult result = move(recorder, initial, Direction.RIGHT);
        require(result.state().lockActive("l1") && result.state().wallOpen(new Position(1, 4)),
                "lock did not activate wall");
        require(recorder.verified(result.state(), 1, 1).isPresent(), "key/lock replay failed");
    }

    private static void undoBranch() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "undo");
        RuntimeState state = initial;
        Direction[] moves = {Direction.RIGHT, Direction.RIGHT, Direction.LEFT,
                Direction.LEFT, Direction.RIGHT, Direction.RIGHT};
        RuntimeState stateAfterFirst = null;
        for (Direction direction : moves) {
            state = move(recorder, state, direction).state();
            if (stateAfterFirst == null) {
                stateAfterFirst = state;
            }
        }
        require(recorder.commandCount() == 6, "expected six moves");
        require(recorder.undoFive() && recorder.commandCount() == 1, "five commands not trimmed");
        state = move(recorder, stateAfterFirst, Direction.DOWN).state();
        ReplayTrace trace = recorder.verified(state, 2, 0).orElseThrow();
        require(trace.commands().size() == 2 && trace.commands().get(1).direction() == Direction.DOWN,
                "discarded branch survived undo");
    }

    private static void undoMismatch() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "bad-undo");
        move(recorder, initial, Direction.RIGHT);
        require(!recorder.undoFive() && !recorder.available(), "invalid undo did not disable recorder");
        require(recorder.commandCount() == 0 && recorder.verified(initial, 0, 0).isEmpty(),
                "invalid recorder exposed a replay");
    }

    private static void realSessionUndo() {
        RuntimeState initial = emptyBoard();
        GameSession game = new GameSession(initial, "session");
        ReplayRecorder recorder = new ReplayRecorder(game.initialState(), game.levelContext());
        for (Direction direction : List.of(Direction.RIGHT, Direction.RIGHT, Direction.LEFT,
                Direction.LEFT, Direction.RIGHT, Direction.RIGHT)) {
            RuntimeMoveResult result = game.move(direction);
            require(result.kind() != MoveResult.Kind.BLOCKED && recorder.record(direction, result),
                    "valid session move not recorded");
        }
        require(game.canUndo() && game.undo() && recorder.undoFive(), "session Undo/Replay mismatch");
        GameSession.Metrics metrics = game.metrics();
        require(metrics.moves() == 1 && metrics.pushes() == 0 && game.undoUsesTotal() == 1,
                "authoritative Undo metrics changed");
        require(recorder.verified(game.currentState(), metrics.moves(), metrics.pushes()).isPresent(),
                "session Undo replay did not match full restored world");
        RuntimeMoveResult branch = game.move(Direction.DOWN);
        require(recorder.record(Direction.DOWN, branch), "divergent branch not recorded");
        metrics = game.metrics();
        require(metrics.moves() == 2 && recorder.verified(game.currentState(), metrics.moves(),
                metrics.pushes()).isPresent(), "replayed branch failed after real Undo");
    }

    private static void realSessionRestart() {
        RuntimeState initial = emptyBoard();
        GameSession game = new GameSession(initial, "session-restart");
        ReplayRecorder recorder = new ReplayRecorder(game.initialState(), game.levelContext());
        recorder.record(Direction.RIGHT, game.move(Direction.RIGHT));
        recorder.record(Direction.RIGHT, game.move(Direction.RIGHT));
        game.restart();
        recorder.reset(game.initialState(), game.levelContext());
        require(game.metrics().moves() == 0 && game.undoUsesTotal() == 0 &&
                game.consecutiveUndoUses() == 0, "Restart reset wrong game metrics");
        require(recorder.commandCount() == 0 && recorder.available(), "Restart retained old commands");
        require(recorder.record(Direction.DOWN, game.move(Direction.DOWN)), "new attempt failed");
        require(recorder.verified(game.currentState(), game.metrics().moves(),
                game.metrics().pushes()).isPresent(), "real session restart replay failed");
    }

    private static void restart() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "first");
        move(recorder, initial, Direction.RIGHT);
        recorder.reset(initial, "second");
        RuntimeState finalState = move(recorder, initial, Direction.DOWN).state();
        ReplayTrace trace = recorder.verified(finalState, 1, 0).orElseThrow();
        require(trace.levelContext().equals("second") && trace.commands().size() == 1
                && trace.commands().get(0).direction() == Direction.DOWN, "restart preserved old commands");
    }

    private static void overflow() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "bounded");
        RuntimeMoveResult artificialSuccessfulMove = new RuntimeMoveResult(MoveResult.Kind.MOVED, initial);
        for (int i = 0; i < ReplayRecorder.MAX_COMMANDS; i++) {
            require(recorder.record(Direction.RIGHT, artificialSuccessfulMove), "premature overflow");
        }
        require(!recorder.record(Direction.RIGHT, artificialSuccessfulMove), "overflow not rejected");
        require(!recorder.available() && recorder.commandCount() == 0,
                "truncated replay exposed as complete");
        require(recorder.verified(initial, 0, 0).isEmpty(), "overflow replay unexpectedly available");
        recorder.reset(initial, "new-attempt");
        require(recorder.available() && recorder.commandCount() == 0, "restart did not reset overflow");
    }

    private static void tamperedFinal() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "tamper");
        RuntimeState realFinal = move(recorder, initial, Direction.RIGHT).state();
        require(!realFinal.equals(initial), "fixture not moved");
        require(recorder.verified(initial, 1, 0).isEmpty(), "wrong final state accepted");
        require(recorder.verified(realFinal, 1, 0).isPresent(), "right final state rejected");
    }

    private static void incorrectPushCount() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "wrong-push-count");
        RuntimeState result = move(recorder, initial, Direction.RIGHT).state();
        require(recorder.verified(result, 1, 1).isEmpty(), "false push count accepted");
        require(recorder.verified(result, 1, 0).isPresent(), "correct non-push count rejected");
    }

    private static void immutableTrace() {
        RuntimeState initial = emptyBoard();
        List<MoveCommand> mutable = new ArrayList<>();
        mutable.add(new MoveCommand(Direction.RIGHT));
        RuntimeState finalState = ENGINE.move(initial, Direction.RIGHT).state();
        ReplayTrace trace = new ReplayTrace("immutable", initial, mutable, finalState, 1, 0);
        mutable.clear();
        require(trace.commands().size() == 1 && new ReplayPlayer(trace).verify(),
                "trace points to mutable caller list");
        try {
            trace.commands().clear();
            throw new AssertionError("Immutable trace list was writable");
        } catch (UnsupportedOperationException expected) {
            // List.copyOf must prevent modification.
        }
    }

    private static void invalidTrace() {
        RuntimeState initial = emptyBoard();
        rejects(() -> new ReplayTrace("", initial, List.of(), initial, 0, 0));
        rejects(() -> new ReplayTrace("ok", initial, List.of(), initial, 1, 0));
        rejects(() -> new ReplayTrace("ok", initial, List.of(), initial, 0, 1));
        try {
            new MoveCommand(null);
            throw new AssertionError("Null direction accepted");
        } catch (NullPointerException expected) {
            // Command arguments are mandatory.
        }
    }

    private static void cursorIndependence() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "cursor");
        RuntimeState state = move(recorder, initial, Direction.RIGHT).state();
        state = move(recorder, state, Direction.DOWN).state();
        ReplayPlayer player = new ReplayPlayer(recorder.verified(state, 2, 0).orElseThrow());
        player.step();
        RuntimeState afterOne = player.currentState();
        require(player.verify(), "copy verification failed");
        require(player.index() == 1 && player.currentState().equals(afterOne), "verification mutated cursor");
    }

    private static void cursorReset() {
        RuntimeState initial = singlePush();
        ReplayRecorder recorder = new ReplayRecorder(initial, "reset");
        RuntimeState finalState = move(recorder, initial, Direction.RIGHT).state();
        ReplayPlayer player = new ReplayPlayer(recorder.verified(finalState, 1, 1).orElseThrow());
        player.step();
        player.reset();
        require(player.currentState().equals(initial) && player.index() == 0 && player.pushes() == 0,
                "reset did not clear playback counters");
    }

    private static void blockedTrace() {
        RuntimeState initial = board("#####", "#@###", "#####");
        ReplayTrace trace = new ReplayTrace("blocked-tamper", initial,
                List.of(new MoveCommand(Direction.RIGHT)), initial, 1, 0);
        ReplayPlayer player = new ReplayPlayer(trace);
        require(!player.verify(), "invalid blocked trace passed");
        rejects(player::step);
        require(player.index() == 0 && player.currentState().equals(initial), "blocked step mutated cursor");
    }

    private static void completion() {
        RuntimeState initial = emptyBoard();
        ReplayPlayer player = new ReplayPlayer(new ReplayTrace("empty", initial, List.of(), initial, 0, 0));
        require(!player.hasNext(), "empty history has next");
        rejects(player::step);
        rejects(player::nextDirection);
    }

    private static void emptyAttempt() {
        RuntimeState initial = emptyBoard();
        ReplayRecorder recorder = new ReplayRecorder(initial, "new");
        Optional<ReplayTrace> trace = recorder.verified(initial, 0, 0);
        require(trace.isPresent() && new ReplayPlayer(trace.orElseThrow()).verify(), "empty replay rejected");
        require(recorder.verified(initial, 1, 0).isEmpty(), "missing move accepted");
    }
}
