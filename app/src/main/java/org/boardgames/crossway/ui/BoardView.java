package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Stone;
import javax.swing.*;
import java.awt.*;

/**
 * View component: draws the board grid and any placed stones.
 */
public class BoardView extends JPanel {
    private final Board board;
    private final int cellSize = 40;

    public BoardView(Board board) {
        this.board = board;
        int size = board.getSize();
        setPreferredSize(new Dimension(size * cellSize, size * cellSize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int size = board.getSize();

        // draw grid lines
        g2.setColor(Color.BLACK);
        for (int i = 0; i <= size; i++) {
            g2.drawLine(0, i * cellSize, size * cellSize, i * cellSize);
            g2.drawLine(i * cellSize, 0, i * cellSize, size * cellSize);
        }

        // draw stones (none for first sprint)
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Stone s = board.getStone(r, c);
                if (s != null) {
                    if (s.color() == Board.Color.BLACK) g2.setColor(Color.BLACK);
                    else g2.setColor(Color.WHITE);
                    int x = c * cellSize + 4;
                    int y = r * cellSize + 4;
                    g2.fillOval(x, y, cellSize - 8, cellSize - 8);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(x, y, cellSize - 8, cellSize - 8);
                }
            }
        }
    }
}
