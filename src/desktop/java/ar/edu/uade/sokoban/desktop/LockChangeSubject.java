package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.movement.RuntimeState;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/** Window-scoped subject publishes differences only after committed transitions. */
public final class LockChangeSubject implements AutoCloseable {
    public enum Origin { MOVE, UNDO, RESTART, LEVEL_CHANGE }
    public record Delta(String lockId, boolean before, boolean after) { }
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
    public void publish(Origin origin, RuntimeState before, RuntimeState after) {
        Objects.requireNonNull(origin, "origin"); Objects.requireNonNull(after, "after");
        Set<String> ids = new TreeSet<>();
        if (before != null) { before.locks().forEach(l -> ids.add(l.id())); }
        after.locks().forEach(l -> ids.add(l.id()));
        List<Delta> changes = new ArrayList<>();
        for (String id : ids) {
            boolean oldValue = before != null && before.lockActive(id); boolean newValue = after.lockActive(id);
            if (oldValue != newValue) { changes.add(new Delta(id, oldValue, newValue)); }
        }
        boolean wallsChanged = before != null && wallsDiffer(before, after);
        if (before == null) { wallsChanged = after.walls().stream().anyMatch(w -> after.wallOpen(w.position())); }
        if (changes.isEmpty() && !wallsChanged) { return; }
        Event event = new Event(origin, changes, wallsChanged);
        for (Observer observer : List.copyOf(observers)) { observer.onLockChange(event); }
    }
    private static boolean wallsDiffer(RuntimeState before, RuntimeState after) {
        var oldWalls = new java.util.HashMap<String, Boolean>();
        var newWalls = new java.util.HashMap<String, Boolean>();
        before.walls().forEach(w -> oldWalls.put(w.id() + "@" + w.position(), before.wallOpen(w.position())));
        after.walls().forEach(w -> newWalls.put(w.id() + "@" + w.position(), after.wallOpen(w.position())));
        return !oldWalls.equals(newWalls);
    }
    @Override public void close() { observers.clear(); }
}
