package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;
import javax.swing.*;
import java.awt.*;

/**
 * View component: renders the board grid and stones, scaling to fit the window
 * while maintaining a square aspect ratio during resize.
 */
public class BoardView extends JPanel {
    private final Board board;

    public BoardView(Board board) {
        this.board = board;
        // Set an initial preferred size proportional to board dimensions
        int size = board.getSize().size();
        setPreferredSize(new Dimension(size * 40, size * 40));
    }

    /**
     * Ensures the panel remains square: clamps its bounds to a square region
     * whenever its size is set by the parent.
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        int side = Math.min(width, height);
        super.setBounds(x, y, side, side);
    }

    /**
     * Computes the current cell size based on the square panel dimensions.
     * @return size in pixels of one board cell (square)
     */
    public int getCellSize() {
        int N = board.getSize().size();
        int side = Math.min(getWidth(), getHeight());
        if (N <= 0) return 0;
        return Math.max(1, side / N);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int N = board.getSize().size();
        int cellSize = getCellSize();
        int boardSide = cellSize * N;

        // Center the board in the square panel
        int xOffset = (getWidth() - boardSide) / 2;
        int yOffset = (getHeight() - boardSide) / 2;

        // Draw grid lines
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= N; i++) {
            int pos = i * cellSize;
            // vertical line
            g2.drawLine(xOffset + pos, yOffset, xOffset + pos, yOffset + boardSide);
            // horizontal line
            g2.drawLine(xOffset, yOffset + pos, xOffset + boardSide, yOffset + pos);
        }

        // Draw stones
        int margin = cellSize / 10;
        int diameter = cellSize - 2 * margin;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                Point p = new Point(col, row);
                Stone s = board.getStone(p);
                if (s != null) {
                    int x = xOffset + col * cellSize + margin;
                    int y = yOffset + row * cellSize + margin;
                    g2.setColor(s == Stone.BLACK ? Color.BLACK : Color.WHITE);
                    g2.fillOval(x, y, diameter, diameter);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(x, y, diameter, diameter);
                }
            }
        }
    }
}