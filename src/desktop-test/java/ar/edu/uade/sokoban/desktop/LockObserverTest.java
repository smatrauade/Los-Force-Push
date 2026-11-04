package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.movement.Direction;
import ar.edu.uade.sokoban.movement.MoveResult;
import ar.edu.uade.sokoban.movement.RuntimeMovementEngine;
import ar.edu.uade.sokoban.movement.RuntimeState;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Observer receives committed facts; tests assert authoritative walls remain derived. */
public final class LockObserverTest {
    private static int passed;
    private LockObserverTest() { }
    private static final class Probe implements GameView {
        GamePresentation screen;
        final List<LockChangeSubject.Event> events = new ArrayList<>();
        final List<SoundManager.Effect> sounds = new ArrayList<>();
        @Override public void render(GamePresentation value) { screen = value; }
        @Override public void showLoadError(String message) { }
        @Override public void onLockChange(LockChangeSubject.Event value) {
            require(screen != null, "event before presentation");
            for (LockChangeSubject.Delta delta : value.locks()) {
                require(screen.state().lockActive(delta.lockId()) == delta.after(), "uncommitted event");
            }
            events.add(value);
        }
        @Override public void playEffect(SoundManager.Effect value) { sounds.add(value); }
    }
    public static void main(String[] args) {
        Path levels = Path.of(args[0]);
        controllerEvents(levels);
        restartAndLevelScope(levels);
        subscriptions();
        blockedClosure();
        isolationAndExceptions();
        independentGroups();
        System.out.println("LOCK OBSERVER RESULT: " + passed + " PASS, 0 FAIL");
    }
    private static void controllerEvents(Path levels) {
        Probe view = new Probe();
        try (GameController game = new GameController(view)) {
            game.load(levels.resolve("campaign-b.txt"));
            game.undo(); game.restart(); game.move(Direction.UP);
            require(view.events.isEmpty(), "no change emitted event");
            game.move(Direction.RIGHT);
            require(view.events.size() == 1 && view.events.get(0).locks().equals(
                    List.of(new LockChangeSubject.Delta("l", false, true)))
                    && view.events.get(0).wallsChanged(), "activation event");
            require(view.sounds.stream().filter(x -> x == SoundManager.Effect.LOCK).count() == 1,
                    "duplicate lock sound");
            // Reach the far side of the key and deactivate it; Undo restores five moves earlier.
            for (Direction move : List.of(Direction.LEFT, Direction.DOWN, Direction.DOWN,
                    Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.RIGHT, Direction.UP,
                    Direction.LEFT)) { game.move(move); }
            require(view.events.size() == 2 && !view.events.get(1).locks().get(0).after(), "closure event");
            game.undo();
            require(view.events.size() == 3 && view.events.get(2).origin() == LockChangeSubject.Origin.UNDO
                    && view.events.get(2).locks().get(0).after() && game.undoUsesTotal() == 1,
                    "Undo restore event missing");
            require(view.sounds.stream().filter(x -> x == SoundManager.Effect.LOCK).count() == 1
                    && view.sounds.stream().filter(x -> x == SoundManager.Effect.GATE).count() == 1
                    && view.sounds.stream().filter(x -> x == SoundManager.Effect.UNDO).count() == 1,
                    "Undo duplicated domain sounds");
            passed++;
        }
    }
    private static void restartAndLevelScope(Path levels) {
        Probe view = new Probe();
        GameController game = new GameController(view);
        game.load(levels.resolve("campaign-b.txt")); game.move(Direction.RIGHT); game.restart();
        require(view.events.size() == 2 && view.events.get(1).origin() == LockChangeSubject.Origin.RESTART
                && !view.events.get(1).locks().get(0).after(), "Restart event missing");
        game.move(Direction.RIGHT);
        int count = view.events.size();
        game.load(levels.resolve("missing.txt"));
        require(view.events.size() == count, "failed load emitted event");
        game.load(levels.resolve("demo.txt"));
        require(view.events.size() == count, "unrelated level emitted old lock closure");
        game.close();
        game.load(levels.resolve("campaign-b.txt")); game.move(Direction.RIGHT);
        require(view.events.size() == count, "closed controller retained subscribers");
        passed++;
    }
    private static void subscriptions() {
        LockChangeSubject subject = new LockChangeSubject();
        List<LockChangeSubject.Event> events = new ArrayList<>();
        LockChangeSubject.Observer observer = events::add;
        subject.subscribe(observer); subject.subscribe(observer);
        RuntimeState closed = world(false);
        RuntimeState open = new RuntimeMovementEngine().move(closed, Direction.RIGHT).state();
        subject.publish(LockChangeSubject.Origin.MOVE, closed, open);
        require(subject.subscriberCount() == 1 && events.size() == 1, "duplicate subscription");
        try { events.get(0).locks().clear(); throw new AssertionError("mutable payload"); }
        catch (UnsupportedOperationException expected) { /* immutable event */ }
        subject.unsubscribe(observer);
        subject.publish(LockChangeSubject.Origin.RESTART, open, closed);
        require(events.size() == 1 && subject.subscriberCount() == 0, "unsubscribe");
        subject.subscribe(observer); subject.close();
        require(subject.subscriberCount() == 0, "close cleanup");
        passed++;
    }
    private static void blockedClosure() {
        RuntimeState before = world(true);
        RuntimeMovementEngine engine = new RuntimeMovementEngine();
        RuntimeState open = engine.move(before, Direction.RIGHT).state();
        var blocked = engine.move(open, Direction.RIGHT);
        require(blocked.kind() == MoveResult.Kind.BLOCKED && blocked.state() == open, "unsafe closure fixture");
        LockChangeSubject subject = new LockChangeSubject();
        List<LockChangeSubject.Event> events = new ArrayList<>();
        subject.subscribe(events::add);
        subject.publish(LockChangeSubject.Origin.MOVE, open, blocked.state());
        require(events.isEmpty() && open.wallOpen(new Position(0, 3)), "blocked closure notified or closed wall");
        passed++;
    }
    private static void isolationAndExceptions() {
        LockChangeSubject subject = new LockChangeSubject();
        List<LockChangeSubject.Event> events = new ArrayList<>();
        subject.subscribe(event -> { throw new IllegalStateException("presentation failure"); });
        subject.subscribe(events::add);
        RuntimeState closed = world(false);
        RuntimeState open = new RuntimeMovementEngine().move(closed, Direction.RIGHT).state();
        subject.publish(LockChangeSubject.Origin.LEVEL_CHANGE, null, open);
        require(events.size() == 1 && open.wallOpen(new Position(0, 4))
                && !closed.wallOpen(new Position(0, 4)), "listener failure vetoed facts");
        subject.publish(LockChangeSubject.Origin.MOVE, open, open);
        require(events.size() == 1, "no-op published twice");
        passed++;
    }
    private static void independentGroups() {
        RuntimeState base = world(false);
        RuntimeState closed = new RuntimeState(List.of(base.terrain().get(0),
                List.of(RuntimeState.Ground.LOCK, RuntimeState.Ground.DYNAMIC_WALL,
                        RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR)),
                base.player(), base.boxes(),
                List.of(base.locks().get(0), new RuntimeState.Lock("other", new Position(1, 0))),
                List.of(base.walls().get(0), new RuntimeState.DynamicWall("other_wall", new Position(1, 1), Set.of("other"))));
        RuntimeState open = new RuntimeMovementEngine().move(closed, Direction.RIGHT).state();
        List<LockChangeSubject.Event> events = new ArrayList<>();
        LockChangeSubject subject = new LockChangeSubject();
        subject.subscribe(events::add);
        subject.publish(LockChangeSubject.Origin.MOVE, closed, open);
        require(events.size() == 1 && events.get(0).locks().size() == 1
                && events.get(0).locks().get(0).lockId().equals("l")
                && !open.lockActive("other") && !open.wallOpen(new Position(1, 1)), "unrelated group changed");
        subject.close();
        passed++;
    }
    private static RuntimeState world(boolean occupiedClosure) {
        List<RuntimeState.Ground> terrain = occupiedClosure
                ? List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK,
                        RuntimeState.Ground.DYNAMIC_WALL, RuntimeState.Ground.FLOOR)
                : List.of(RuntimeState.Ground.FLOOR, RuntimeState.Ground.FLOOR, RuntimeState.Ground.LOCK,
                        RuntimeState.Ground.FLOOR, RuntimeState.Ground.DYNAMIC_WALL);
        return new RuntimeState(List.of(terrain), new Position(0, 0),
                List.of(RuntimeState.Box.key("k", new Position(0, 1))),
                List.of(new RuntimeState.Lock("l", new Position(0, 2))),
                List.of(new RuntimeState.DynamicWall("w", new Position(0, occupiedClosure ? 3 : 4), Set.of("l"))));
    }
    private static void require(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
