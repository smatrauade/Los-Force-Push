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

/** Explicit experimental extension; the historical parsers remain unchanged. */
public final class ExtendedLevelParser {
    private static final String ID = ExperimentalFragileLevel.ID_REGEX;
    private static final Pattern SPECIAL = Pattern.compile("\\{(ice|lock:(" + ID + ")|wall:(" + ID + ")|floor|goal)\\|(empty|player|box|key:(" + ID + ")|fragile:(" + ID + "))\\}");
    private static final Pattern WALL = Pattern.compile("wall (" + ID + ") locks=(" + ID + "(?:," + ID + ")*)");

    public ExtendedLevelDefinition parse(String text) {
        Objects.requireNonNull(text, "text");
        if (text.length() > ExperimentalFragileParser.MAX_TEXT_CHARS) { throw error("Text exceeds limit"); }
        String[] lines = text.split("\\n", -1);
        int count = lines.length;
        if (count > 0 && lines[count - 1].isEmpty()) { count--; }
        for (int i = 0; i < count; i++) {
            if (i < lines.length - 1 && lines[i].endsWith("\r")) { lines[i] = lines[i].substring(0, lines[i].length() - 1); }
        }
        if (count < 3 || !lines[0].equals("!sokoban-txt 2") || !lines[1].equals("[grid]")) {
            throw error("Invalid header or grid section");
        }
        Set<Position> ice = new HashSet<>();
        List<ExtendedLevelDefinition.Key> keys = new ArrayList<>();
        List<ExtendedLevelDefinition.Lock> locks = new ArrayList<>();
        Map<String, Position> wallPositions = new HashMap<>();
        StringBuilder normalized = new StringBuilder("!sokoban-txt 2\n[grid]\n");
        int section = 2;
        while (section < count && !lines[section].equals("[attributes]")) {
            String[] tokens = lines[section].split(" ", -1);
            if (section - 2 >= Board.MAX_DIMENSION || tokens.length > Board.MAX_DIMENSION) { throw error("Grid dimensions exceeded"); }
            for (int col = 0; col < tokens.length; col++) {
                Matcher m = SPECIAL.matcher(tokens[col]);
                if (!m.matches()) { continue; }
                String ground = m.group(1); String occupant = m.group(4);
                Position pos = new Position(section - 2, col);
                if (ground.equals("ice")) { ice.add(pos); }
                else if (m.group(2) != null) { locks.add(new ExtendedLevelDefinition.Lock(m.group(2), pos)); }
                else if (m.group(3) != null && wallPositions.putIfAbsent(m.group(3), pos) != null) { throw error("Duplicate wall id"); }
                String base = ground.equals("goal") ? "goal" : "floor";
                if (m.group(5) != null) { keys.add(new ExtendedLevelDefinition.Key(m.group(5), pos)); occupant = "empty"; }
                if (occupant.startsWith("fragile:")) {
                    tokens[col] = "{" + base + "|" + occupant + "}";
                } else {
                    tokens[col] = switch (base + ":" + occupant) {
                        case "floor:empty" -> "_";
                        case "floor:player" -> "@";
                        case "floor:box" -> "$";
                        case "goal:empty" -> ".";
                        case "goal:player" -> "+";
                        case "goal:box" -> "*";
                        default -> throw error("Unsupported occupant");
                    };
                }
            }
            normalized.append(String.join(" ", tokens)).append('\n');
            section++;
        }
        if (section == count) { throw error("Missing attributes section"); }
        normalized.append("[attributes]\n");
        Map<String, Set<String>> associations = new HashMap<>();
        for (int i = section + 1; i < count; i++) {
            Matcher m = WALL.matcher(lines[i]);
            if (m.matches()) {
                Set<String> ids = new HashSet<>();
                for (String id : m.group(2).split(",")) { if (!ids.add(id)) { throw error("Duplicate lock reference"); } }
                if (associations.putIfAbsent(m.group(1), ids) != null) { throw error("Duplicate wall declaration"); }
            } else { normalized.append(lines[i]).append('\n'); }
        }
        // Remove the synthesized final newline so normalization cannot increase the input quota.
        if (normalized.charAt(normalized.length() - 1) == '\n') {
            normalized.setLength(normalized.length() - 1);
        }
        ExperimentalFragileLevel base = new ExperimentalFragileParser().parse(normalized.toString());
        if (!wallPositions.keySet().equals(associations.keySet())) { throw error("Wall declarations must match references"); }
        List<ExtendedLevelDefinition.Wall> walls = wallPositions.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ExtendedLevelDefinition.Wall(e.getKey(), e.getValue(), associations.get(e.getKey())))
                .toList();
        try { return new ExtendedLevelDefinition(base, ice, keys, locks, walls); }
        catch (IllegalArgumentException failure) { throw error("Invalid definition: " + failure.getMessage()); }
    }
    private static LevelFormatException error(String message) { return new LevelFormatException("Extended C2: " + message); }
}
