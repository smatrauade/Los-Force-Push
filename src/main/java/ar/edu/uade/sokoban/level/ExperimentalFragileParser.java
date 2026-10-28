package ar.edu.uade.sokoban.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Isolated C2 experiment. Its output cannot be passed off as a playable Level. */
public final class ExperimentalFragileParser {
    public static final int MAX_TEXT_CHARS = 65_536;
    private static final String ID = ExperimentalFragileLevel.ID_REGEX;
    private static final Pattern CELL = Pattern.compile("\\{(floor|goal)\\|fragile:(" + ID + ")\\}");
    private static final Pattern ATTRIBUTE = Pattern.compile("fragile (" + ID + ") resistance=([1-9][0-9]*)");

    private record Reference(String id, Position position) { }

    public ExperimentalFragileLevel parse(String text) {
        Objects.requireNonNull(text, "text");
        if (text.length() > MAX_TEXT_CHARS) {
            throw new LevelFormatException("Experimental text exceeds " + MAX_TEXT_CHARS + " characters");
        }
        String[] lines = text.split("\\n", -1);
        int count = lines.length;
        if (count > 0 && lines[count - 1].isEmpty()) { count--; }
        for (int i = 0; i < count; i++) {
            // Only CR immediately followed by LF belongs to CRLF.
            if (i < lines.length - 1 && lines[i].endsWith("\r")) {
                lines[i] = lines[i].substring(0, lines[i].length() - 1);
            }
        }
        if (count < 3 || !lines[0].equals("!sokoban-txt 2") || !lines[1].equals("[grid]")) {
            throw new LevelFormatException("Experimental C2 header or grid section invalid");
        }
        List<Reference> references = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        StringBuilder basic = new StringBuilder("!sokoban-txt 2\n[grid]\n");
        int section = 2;
        while (section < count && !lines[section].equals("[attributes]")) {
            String[] tokens = lines[section].split(" ", -1);
            for (int col = 0; col < tokens.length; col++) {
                Matcher matcher = CELL.matcher(tokens[col]);
                if (matcher.matches()) {
                    String id = matcher.group(2);
                    if (!ids.add(id)) { throw new LevelFormatException("Duplicate fragile reference: " + id); }
                    if (section - 2 >= Board.MAX_DIMENSION || col >= Board.MAX_DIMENSION) {
                        throw new LevelFormatException("Experimental grid exceeds dimensions");
                    }
                    references.add(new Reference(id, new Position(section - 2, col)));
                    tokens[col] = matcher.group(1).equals("goal") ? "." : "_";
                }
            }
            basic.append(String.join(" ", tokens)).append('\n');
            section++;
        }
        if (section == count) { throw new LevelFormatException("Missing [attributes]"); }
        basic.append("[attributes]");
        // Existing strict C2 parser validates all spatial tokens, separators and invariants.
        Board template = new LevelParser().parse(basic.toString()).board();
        Map<String, Integer> attributes = new HashMap<>();
        for (int i = section + 1; i < count; i++) {
            Matcher matcher = ATTRIBUTE.matcher(lines[i]);
            if (!matcher.matches()) { throw new LevelFormatException("Invalid fragile attribute at line " + (i + 1)); }
            String id = matcher.group(1);
            if (!ids.contains(id)) { throw new LevelFormatException("Unknown fragile id: " + id); }
            int resistance;
            try { resistance = Integer.parseInt(matcher.group(2)); }
            catch (NumberFormatException failure) { throw new LevelFormatException("Resistance exceeds integer range"); }
            if (attributes.putIfAbsent(id, resistance) != null) {
                throw new LevelFormatException("Duplicate fragile attribute: " + id);
            }
        }
        List<ExperimentalFragileLevel.FragileBox> boxes = new ArrayList<>();
        for (Reference reference : references) {
            Integer resistance = attributes.get(reference.id());
            if (resistance == null) { throw new LevelFormatException("Missing fragile attribute: " + reference.id()); }
            boxes.add(new ExperimentalFragileLevel.FragileBox(reference.id(), reference.position(), resistance));
        }
        return new ExperimentalFragileLevel(template, boxes);
    }
}
