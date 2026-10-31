package ar.edu.uade.sokoban.session;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;

/** Bounded caretaker; it never inspects snapshots or applies game rules. */
final class UndoManager {
    private static final int CAPACITY = 15;
    private final Deque<GameMemento> history = new ArrayDeque<>();
    void record(GameMemento snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        if (history.size() == CAPACITY) { history.removeFirst(); }
        history.addLast(snapshot);
    }
    int size() { return history.size(); }
    GameMemento peekBack(int steps) {
        if (steps < 1 || steps > history.size()) { throw new IllegalArgumentException("Invalid history depth"); }
        Iterator<GameMemento> iterator = history.descendingIterator();
        GameMemento result = null;
        for (int i = 0; i < steps; i++) { result = iterator.next(); }
        return result;
    }
    void discardBack(int steps) {
        if (steps < 1 || steps > history.size()) { throw new IllegalArgumentException("Invalid history depth"); }
        for (int i = 0; i < steps; i++) { history.removeLast(); }
    }
    void clear() { history.clear(); }
}
