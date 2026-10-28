package ar.edu.uade.sokoban.level;

import java.util.List;
import java.util.Objects;

/** Experimental definition, not a playable Level. Fragile cells are empty in the template. */
public record ExperimentalFragileLevel(Board spatialTemplate, List<FragileBox> fragileBoxes) {
    public ExperimentalFragileLevel {
        Objects.requireNonNull(spatialTemplate, "spatialTemplate");
        fragileBoxes = List.copyOf(fragileBoxes);
    }

    public record FragileBox(String id, Position position, int initialResistance) {
        public FragileBox {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(position, "position");
            if (initialResistance <= 0) {
                throw new IllegalArgumentException("Resistance must be positive");
            }
        }
    }
}
