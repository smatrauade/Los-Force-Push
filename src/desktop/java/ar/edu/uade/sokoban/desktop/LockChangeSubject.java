package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/** Window-scoped Observer subject. Publishes committed facts; never stores or changes the world. */
public final class LockChangeSubject implements AutoCloseable {
    public enum Origin { MOVE, UNDO, RESTART, LEVEL_CHANGE }
    public record Delta(String lockId, boolean before, boolean after) {
        public Delta { Objects.requireNonNull(lockId, "lockId"); }
    }
    public record Event(Origin origin, List<Delta> locks, boolean wallsChanged) {
        public Event { Objects.requireNonNull(origin, "origin"); locks = List.copyOf(locks); }
    }
    @FunctionalInterface public interface Observer { void onLockChange(Event event); }
    private final List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer observer) {
        Objects.requireNonNull(observer, "observer");
        if (!observers.contains(observer)) { observers.add(observer); }
    }
    public void unsubscribe(Observer observer) { observers.remove(observer); }
    public int subscriberCount() { return observers.size(); }

    /** LEVEL_CHANGE uses a null baseline; IDs from unrelated levels are never compared. */
    public void publish(Origin origin, RuntimeState before, RuntimeState after) {
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(after, "after");
        Set<String> ids = new TreeSet<>();
        if (before != null) { before.locks().forEach(lock -> ids.add(lock.id())); }
        after.locks().forEach(lock -> ids.add(lock.id()));
        List<Delta> changes = new ArrayList<>();
        for (String id : ids) {
            boolean oldValue = before != null && before.lockActive(id);
            boolean newValue = after.lockActive(id);
            if (oldValue != newValue) { changes.add(new Delta(id, oldValue, newValue)); }
        }
        boolean wallsChanged = before == null
                ? after.walls().stream().anyMatch(wall -> after.wallOpen(wall.position()))
                : !wallStates(before).equals(wallStates(after));
        if (changes.isEmpty() && !wallsChanged) { return; }
        Event event = new Event(origin, changes, wallsChanged);
        for (Observer observer : List.copyOf(observers)) {
            try { observer.onLockChange(event); }
            catch (RuntimeException failure) {
                // A presentation subscriber cannot veto an already committed move/victory.
                System.err.println("No se pudo notificar un cambio de cerrojo");
            }
        }
    }
    private static Map<String, Boolean> wallStates(RuntimeState state) {
        Map<String, Boolean> result = new HashMap<>();
        state.walls().forEach(wall -> result.put(wall.id() + "@" + wall.position(),
                state.wallOpen(wall.position())));
        return result;
    }
    @Override public void close() { observers.clear(); }
}
