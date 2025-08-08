package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;
import javax.swing.*;
import java.awt.*;

/**
 * View component: renders the board grid and stones.
 */
public class BoardView extends JPanel {
    private final Board board;
    private final int cellSize = 40;

    public BoardView(Board board) {
        this.board = board;
        int size = board.getSize().size();
        setPreferredSize(new Dimension(size * cellSize, size * cellSize));
    }

    public int getCellSize() {
        return cellSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int N = board.getSize().size();
        // Draw grid lines
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= N; i++) {
            int pos = i * cellSize;
            g2.drawLine(pos, 0, pos, N * cellSize);
            g2.drawLine(0, pos, N * cellSize, pos);
        }

        // Draw stones
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                Point p = new Point(col, row);
                Stone s = board.getStone(p);
                if (s != null) {
                    int x = col * cellSize + 4;
                    int y = row * cellSize + 4;
                    int d = cellSize - 8;

                    // Set fill color based on stone
                    if (s == Stone.BLACK) {
                        g2.setColor(Color.BLACK);
                    } else {
                        g2.setColor(Color.WHITE);
                    }
                    g2.fillOval(x, y, d, d);

                    // Outline
                    g2.setColor(Color.BLACK);
                    g2.drawOval(x, y, d, d);
                }
            }
        }
    }
}
