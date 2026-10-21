package ar.edu.uade.sokoban.desktop;

import ar.edu.uade.sokoban.level.Board;
import ar.edu.uade.sokoban.level.Cell;
import ar.edu.uade.sokoban.level.Occupant;
import ar.edu.uade.sokoban.level.Terrain;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Development placeholders drawn from the core cells, with no rules or mutable grid. */
public final class BoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int TILE = 56;
    private static final int MARGIN = 20;
    private transient Board board;

    public BoardPanel() {
        setBackground(new Color(22, 31, 42));
        setPreferredSize(new Dimension(600, 300));
        setFocusable(false);
    }

    public void showBoard(Board value) {
        board = value;
        setPreferredSize(new Dimension(value.width() * TILE + 2 * MARGIN,
                value.height() * TILE + 2 * MARGIN));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (board == null) {
            return;
        }
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int row = 0; row < board.height(); row++) {
                for (int col = 0; col < board.width(); col++) {
                    paintCell(g, board.rows().get(row).get(col),
                            MARGIN + col * TILE, MARGIN + row * TILE);
                }
            }
        } finally {
            g.dispose();
        }
    }

    private static void paintCell(Graphics2D g, Cell cell, int x, int y) {
        g.setStroke(new BasicStroke(1));
        if (cell.terrain() == Terrain.WALL) {
            g.setColor(new Color(76, 96, 116));
            g.fillRoundRect(x + 2, y + 2, TILE - 4, TILE - 4, 9, 9);
            g.setColor(new Color(108, 127, 145));
            g.drawLine(x + 9, y + 10, x + TILE - 10, y + 10);
            return;
        }
        g.setColor(new Color(226, 221, 205));
        g.fillRect(x, y, TILE, TILE);
        g.setColor(new Color(204, 199, 182));
        g.drawRect(x, y, TILE, TILE);
        if (cell.terrain() == Terrain.GOAL) {
            g.setColor(new Color(36, 132, 96));
            g.setStroke(new BasicStroke(3));
            g.drawOval(x + 10, y + 10, TILE - 20, TILE - 20);
            g.fillOval(x + 23, y + 23, 10, 10);
        }
        if (cell.occupant() == Occupant.BOX) {
            g.setColor(cell.terrain() == Terrain.GOAL
                    ? new Color(54, 153, 109) : new Color(201, 135, 54));
            g.fillRoundRect(x + 8, y + 8, TILE - 16, TILE - 16, 7, 7);
            g.setColor(new Color(103, 73, 37));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x + 8, y + 8, TILE - 16, TILE - 16, 7, 7);
            g.drawLine(x + 16, y + 16, x + TILE - 16, y + TILE - 16);
            g.drawLine(x + TILE - 16, y + 16, x + 16, y + TILE - 16);
        } else if (cell.occupant() == Occupant.PLAYER) {
            g.setColor(new Color(16, 105, 141));
            g.fillOval(x + 9, y + 9, TILE - 18, TILE - 18);
            g.setColor(Color.WHITE);
            g.fillOval(x + 19, y + 21, 5, 5);
            g.fillOval(x + 32, y + 21, 5, 5);
            g.drawArc(x + 19, y + 27, 18, 12, 190, 160);
        }
    }
}
