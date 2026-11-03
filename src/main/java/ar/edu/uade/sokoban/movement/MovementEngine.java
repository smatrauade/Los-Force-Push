package ar.edu.uade.sokoban.movement;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Position;
import ar.edu.uade.sokoban.level.Terrain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Stateless functional transitions for the normal-box subset only. */
public final class MovementEngine {
    public MoveResult move(Board state, Direction direction) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(direction, "direction");
        Position player = state.playerPosition();
        int targetRow = player.row() + direction.rowDelta();
        int targetColumn = player.column() + direction.columnDelta();
        if (!inside(state, targetRow, targetColumn)) {
            return blocked(state);
        }
        Position target = new Position(targetRow, targetColumn);
        Cell destination = state.cellAt(target);
        if (destination.terrain() == Terrain.WALL) {
            return blocked(state);
        }
        Position boxDestination = null;
        List<Position> boxPath = new ArrayList<>();
        if (pushable(destination.occupant())) {
            int boxRow = targetRow + direction.rowDelta();
            int boxColumn = targetColumn + direction.columnDelta();
            if (!inside(state, boxRow, boxColumn)) {
                return blocked(state);
            }
            boxDestination = new Position(boxRow, boxColumn);
            Cell behindBox = state.cellAt(boxDestination);
            if (behindBox.terrain() == Terrain.WALL || behindBox.occupant() != Occupant.EMPTY) {
                return blocked(state);
            }
            boxPath.add(boxDestination);
            while (state.cellAt(boxDestination).terrain() == Terrain.ICE) {
                int nextRow = boxDestination.row() + direction.rowDelta();
                int nextColumn = boxDestination.column() + direction.columnDelta();
                if (!inside(state, nextRow, nextColumn)) {
                    break;
                }
                Position next = new Position(nextRow, nextColumn);
                Cell nextCell = state.cellAt(next);
                if (nextCell.terrain() == Terrain.WALL
                    || nextCell.occupant() != Occupant.EMPTY) {
                    break;
                }
                boxDestination = next;
                boxPath.add(boxDestination);
            }
        } else if (destination.occupant() != Occupant.EMPTY) {
            return blocked(state);
        }

        // All collision checks precede assembly; the input Board is never mutated.
        List<List<Cell>> rows = new ArrayList<>(state.height());
        for (List<Cell> row : state.rows()) {
            rows.add(new ArrayList<>(row));
        }
        replaceOccupant(rows, player, Occupant.EMPTY);
        replaceOccupant(rows, target, Occupant.PLAYER);
        if (boxDestination != null) {
            // One push costs one resistance point, however far the box then slides.
            Occupant pushed = Cell.afterPush(destination.occupant(), destination.resistance());
            int left = pushed == Occupant.FRAGILE_BOX ? destination.resistance() - 1 : 0;
            replaceOccupant(rows, boxDestination, pushed, left);
        }
        Board next = new Board(rows); // same aggregate invariants as loaded levels
        return new MoveResult(boxDestination == null ? MoveResult.Kind.MOVED
                : MoveResult.Kind.PUSHED, next, boxPath);
    }

    private boolean inside(Board state, int row, int column) {
        return row >= 0 && row < state.height() && column >= 0 && column < state.width();
    }

    private MoveResult blocked(Board state) {
        return new MoveResult(MoveResult.Kind.BLOCKED, state);
    }

    private boolean pushable(Occupant occupant) {
        return occupant == Occupant.BOX || occupant == Occupant.FRAGILE_BOX;
    }

    private void replaceOccupant(List<List<Cell>> rows, Position position, Occupant occupant) {
        replaceOccupant(rows, position, occupant, 0);
    }

    private void replaceOccupant(List<List<Cell>> rows, Position position,
                                 Occupant occupant, int resistance) {
        List<Cell> row = rows.get(position.row());
        Cell old = row.get(position.column());
        row.set(position.column(), new Cell(old.terrain(), occupant, resistance));
    }

}
