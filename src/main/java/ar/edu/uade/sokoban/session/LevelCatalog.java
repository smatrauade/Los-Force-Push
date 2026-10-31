package ar.edu.uade.sokoban.session;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Ordered, immutable level references; format is explicit and never auto-detected. */
public record LevelCatalog(List<Entry> levels) {
    public enum Format { STANDARD, EXTENDED }
    public record Entry(String id, Path path, Format format) {
        public Entry {
            Objects.requireNonNull(id, "id"); Objects.requireNonNull(path, "path"); Objects.requireNonNull(format, "format");
            if (!id.matches("[a-z][a-z0-9_]{0,31}") || path.toString().isBlank()) {
                throw new IllegalArgumentException("Invalid catalog entry");
            }
        }
    }
    public LevelCatalog {
        levels = List.copyOf(levels);
        if (levels.isEmpty()) { throw new IllegalArgumentException("Empty catalog"); }
        Set<String> ids = new HashSet<>();
        for (Entry entry : levels) {
            if (!ids.add(entry.id())) { throw new IllegalArgumentException("Duplicate level id"); }
        }
    }
    public Optional<Entry> nextAfter(String id) {
        Objects.requireNonNull(id, "id");
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).id().equals(id)) {
                return i + 1 < levels.size() ? Optional.of(levels.get(i + 1)) : Optional.empty();
            }
        }
        throw new IllegalArgumentException("Unknown level id");
    }
}
