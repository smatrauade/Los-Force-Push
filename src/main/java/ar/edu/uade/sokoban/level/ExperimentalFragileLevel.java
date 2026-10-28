package ar.edu.uade.sokoban.level;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/** Experimental definition, not a playable Level. Fragile cells are empty in the template. */
public record ExperimentalFragileLevel(Board spatialTemplate, List<FragileBox> fragileBoxes) {
    static final String ID_REGEX = "[a-z][a-z0-9_]{0,31}";
    private static final Pattern VALID_ID = Pattern.compile(ID_REGEX);

    public ExperimentalFragileLevel {
        Objects.requireNonNull(spatialTemplate, "spatialTemplate");
        fragileBoxes = List.copyOf(fragileBoxes);
        Set<String> ids = new HashSet<>();
        Set<Position> positions = new HashSet<>();
        for (FragileBox box : fragileBoxes) {
            if (!ids.add(box.id())) {
                throw new IllegalArgumentException("Duplicate fragile id: " + box.id());
            }
            if (!positions.add(box.position())) {
                throw new IllegalArgumentException("Duplicate fragile position: " + box.position());
            }
            Position position = box.position();
            if (position.row() >= spatialTemplate.height()
                    || position.column() >= spatialTemplate.width()) {
                throw new IllegalArgumentException("Fragile position outside board: " + position);
            }
            Cell cell = spatialTemplate.cellAt(position);
            if ((cell.terrain() != Terrain.FLOOR && cell.terrain() != Terrain.GOAL)
                    || cell.occupant() != Occupant.EMPTY) {
                throw new IllegalArgumentException("Fragile position must be empty floor or goal: " + position);
            }
        }
    }

    public record FragileBox(String id, Position position, int initialResistance) {
        public FragileBox {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(position, "position");
            if (!VALID_ID.matcher(id).matches()) {
                throw new IllegalArgumentException("Invalid fragile id: " + id);
            }
            if (initialResistance <= 0) {
                throw new IllegalArgumentException("Resistance must be positive");
            }
        }
    }
}
