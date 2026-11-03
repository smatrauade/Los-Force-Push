package ar.edu.uade.sokoban.level;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** Filesystem boundary; no cached text or previously published Level. */
public final class LevelLoader {
    // Defensive I/O ceiling, independent from the smaller valid-board dimensions.
    public static final int MAX_FILE_BYTES = 65_536;
    private final LevelParser parser;

    public LevelLoader(LevelParser parser) {
        this.parser = Objects.requireNonNull(parser, "parser");
    }

    public Level load(Path path) throws IOException {
        Level candidate = parser.parse(readText(path));
        // D-020: a playable Level has no stable Fragile IDs. Reject the lossy
        // conversion; callers requiring identity must load its definition.
        for (var row : candidate.board().rows()) {
            for (Cell cell : row) {
                if (cell.occupant() == Occupant.FRAGILE_BOX) {
                    throw new LevelFormatException(
                            "Fragile IDs cannot be preserved in playable Level; "
                            + "use loadExperimentalFragile for its definition");
                }
            }
        }
        return candidate;
    }

    /**
     * Loads an experimental initial definition, never a playable Level.
     * Uses the same byte ceiling and strict UTF-8 policy as {@link #load(Path)}.
     * The existing experimental grammar and resistance range are not ratified.
     */
    public ExperimentalFragileLevel loadExperimentalFragile(Path path) throws IOException {
        return new ExperimentalFragileParser().parse(readText(path));
    }

    /** Explicit Key/Lock/Wall extension; does not change historical loading routes. */
    public ExtendedLevelDefinition loadExtended(Path path) throws IOException {
        return new ExtendedLevelParser().parse(readText(path));
    }

    private String readText(Path path) throws IOException {
        byte[] bytes;
        try (InputStream input = Files.newInputStream(path)) {
            bytes = input.readNBytes(MAX_FILE_BYTES + 1);
        }
        if (bytes.length > MAX_FILE_BYTES) {
            throw new LevelFormatException("File exceeds " + MAX_FILE_BYTES + " bytes");
        }
        return StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(bytes)).toString();
    }
}
