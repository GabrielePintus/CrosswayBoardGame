package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.model.BoardChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * View component that renders the board grid and stones, scaling to fit the window
 * while maintaining a square aspect ratio during resize.
 *
 * <p>This component automatically centers the board within its bounds and ensures
 * the board remains square regardless of the parent container's dimensions.</p>
 */
public class BoardView extends JPanel implements BoardChangeListener {

    /** The board model to render. */
    private final Board board;

    /** Callback invoked when a board coordinate is clicked. */
    private Consumer<Point> clickCallback;

    /** Initial cell size multiplier for preferred size calculation. */
    private static final int INITIAL_CELL_SIZE = 40;

    /** Margin factor for stone rendering (1/10 of cell size). */
    private static final int MARGIN_DIVISOR = 10;

    /** Minimum cell size to prevent rendering issues. */
    private static final int MIN_CELL_SIZE = 1;

    /**
     * Creates a new BoardView for the specified board.
     *
     * @param board the board model to render
     */
    public BoardView(Board board) {
        this.board = board;
        initializePreferredSize();

        addMouseListener(new MouseAdapter() {
            /**
             * Handles the mouse release event.
             *
             * @param e the mouse event
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (clickCallback != null) {
                    clickCallback.accept(mouseEventToPoint(e));
                }
            }
        });
    }

    /**
     * Initializes the preferred size based on board dimensions.
     */
    private void initializePreferredSize() {
        int boardSize = board.getSize().size();
        int preferredDimension = boardSize * INITIAL_CELL_SIZE;
        setPreferredSize(new Dimension(preferredDimension, preferredDimension));
    }

    /**
     * Ensures the panel remains square by clamping its bounds to a square region
     * whenever its size is set by the parent container.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the desired width
     * @param height the desired height
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        int squareSide = Math.min(width, height);
        super.setBounds(x, y, squareSide, squareSide);
    }

    /**
     * Computes the current cell size based on the square panel dimensions.
     *
     * @return size in pixels of one board cell (guaranteed to be at least 1)
     */
    public int getCellSize() {
        int boardSize = board.getSize().size();

        if (boardSize <= 0) {
            return 0;
        }

        int panelSide = Math.min(getWidth(), getHeight());
        return Math.max(MIN_CELL_SIZE, panelSide / boardSize);
    }

    /**
     * Converts a {@link MouseEvent}'s pixel location to a logical board coordinate.
     *
     * @param mouseEvent the mouse event to convert
     * @return the corresponding board {@link Point}
     */
    public Point mouseEventToPoint(MouseEvent mouseEvent) {
        int boardSize = board.getSize().size();
        int cellSize = getCellSize();
        int boardPixelSize = cellSize * boardSize;
        int xOffset = (getWidth() - boardPixelSize) / 2;
        int yOffset = (getHeight() - boardPixelSize) / 2;

        int boardX = (mouseEvent.getX() - xOffset) / cellSize;
        int boardY = (mouseEvent.getY() - yOffset) / cellSize;
        return new Point(boardX, boardY);
    }

    /**
     * Registers a callback to be invoked when a board coordinate is clicked.
     *
     * @param callback the callback consumer receiving the board {@link Point}
     */
    public void setBoardClickCallback(Consumer<Point> callback) {
        this.clickCallback = callback;
    }

    /**
     * Renders the board component including grid lines and stones.
     *
     * @param g the graphics context to render into
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // Enable anti-aliasing for smoother rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            drawBoard(g2d);
        } finally {
            g2d.dispose();
        }
    }

    /**
     * Draws the complete board including grid and stones.
     *
     * @param g2d the graphics context
     */
    private void drawBoard(Graphics2D g2d) {
        int boardSize = board.getSize().size();
        int cellSize = getCellSize();
        int boardPixelSize = cellSize * boardSize;

        // Calculate offsets to center the board
        int xOffset = (getWidth() - boardPixelSize) / 2;
        int yOffset = (getHeight() - boardPixelSize) / 2;

        drawGrid(g2d, boardSize, cellSize, xOffset, yOffset, boardPixelSize);
        drawStones(g2d, boardSize, cellSize, xOffset, yOffset);
    }

    /**
     * Draws the grid lines for the board.
     *
     * @param g2d the graphics context
     * @param boardSize the size of the board (N x N)
     * @param cellSize the size of each cell in pixels
     * @param xOffset horizontal offset for centering
     * @param yOffset vertical offset for centering
     * @param boardPixelSize total board size in pixels
     */
    private void drawGrid(Graphics2D g2d, int boardSize, int cellSize,
                          int xOffset, int yOffset, int boardPixelSize) {
        g2d.setColor(Color.LIGHT_GRAY);

        // Draw vertical and horizontal grid lines
        for (int i = 0; i <= boardSize; i++) {
            int linePosition = i * cellSize;

            // Vertical line
            g2d.drawLine(xOffset + linePosition, yOffset,
                    xOffset + linePosition, yOffset + boardPixelSize);

            // Horizontal line
            g2d.drawLine(xOffset, yOffset + linePosition,
                    xOffset + boardPixelSize, yOffset + linePosition);
        }
    }

    /**
     * Draws all stones on the board.
     *
     * @param g2d the graphics context
     * @param boardSize the size of the board (N x N)
     * @param cellSize the size of each cell in pixels
     * @param xOffset horizontal offset for centering
     * @param yOffset vertical offset for centering
     */
    private void drawStones(Graphics2D g2d, int boardSize, int cellSize,
                            int xOffset, int yOffset) {
        int stoneMargin = cellSize / MARGIN_DIVISOR;
        int stoneDiameter = cellSize - (2 * stoneMargin);

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Point position = new Point(col, row);
                Optional<Stone> stone = board.stoneAt(position);

                if (stone.isPresent()) {
                    drawStone(g2d, stone.get(), col, row, cellSize,
                            xOffset, yOffset, stoneMargin, stoneDiameter);
                }
            }
        }
    }

    /**
     * Draws a single stone at the specified position.
     *
     * @param g2d the graphics context
     * @param stone the stone to draw
     * @param col the column position
     * @param row the row position
     * @param cellSize the size of each cell
     * @param xOffset horizontal offset for centering
     * @param yOffset vertical offset for centering
     * @param margin the margin around the stone
     * @param diameter the diameter of the stone
     */
    private void drawStone(Graphics2D g2d, Stone stone, int col, int row,
                           int cellSize, int xOffset, int yOffset,
                           int margin, int diameter) {
        int x = xOffset + col * cellSize + margin;
        int y = yOffset + row * cellSize + margin;

        // Fill the stone with appropriate color
        Color stoneColor = (stone == Stone.BLACK) ? Color.BLACK : Color.WHITE;
        g2d.setColor(stoneColor);
        g2d.fillOval(x, y, diameter, diameter);

        // Draw the stone border
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y, diameter, diameter);
    }

    /**
     * Updates the view when the board model changes, triggering a repaint.
     *
     * @param board the board that has changed
     */
    @Override
    public void onBoardChange(Board board) {
        repaint();
    }
}