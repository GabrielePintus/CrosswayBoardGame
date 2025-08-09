package org.boardgames.crossway.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main game logic for Crossway. Uses a Union-Find structure to
 * track connectivity for efficient win detection.
 */
public class Game {
    private Board board;
    private DisjointSet<Point> uf = new DisjointSet<Point>();
    private ArrayList<Point> history = new ArrayList<Point>();

    // Virtual border nodes for Union-Find
    private static final Point WHITE_WEST  = new Point(-1, -1);
    private static final Point WHITE_EAST  = new Point(-2, -2);
    private static final Point BLACK_NORTH = new Point(-3, -3);
    private static final Point BLACK_SOUTH = new Point(-4, -4);

    // All 8 neighbor directions
    private static final List<int[]> DIRECTIONS = Arrays.asList(
            new int[]{-1, -1}, new int[]{-1,  0}, new int[]{-1,  1},
            new int[]{ 0, -1},                   new int[]{ 0,  1},
            new int[]{ 1, -1}, new int[]{ 1,  0}, new int[]{ 1,  1}
    );

    /**
     * Constructs a new game on the given board.
     * Initializes Union-Find sets for virtual border nodes.
     *
     * @param board the game board
     */
    public Game(Board board) {
        this.board = board;
        // Register virtual border nodes
        uf.makeSet(WHITE_WEST);
        uf.makeSet(WHITE_EAST);
        uf.makeSet(BLACK_NORTH);
        uf.makeSet(BLACK_SOUTH);
    }

    public Game(BoardSize boardSize) {
        this(new Board(boardSize));
    }


    public void reset() {
        board.clear();
        this.uf = new DisjointSet<Point>(); // Reset Union-Find structure
        // Re-register virtual border nodes
        uf.makeSet(WHITE_WEST);
        uf.makeSet(WHITE_EAST);
        uf.makeSet(BLACK_NORTH);
        uf.makeSet(BLACK_SOUTH);

    }

    public void undoLastMove() {
        if (history.isEmpty()) {
            throw new IllegalStateException("No moves to undo.");
        }

        // Remove the last move from history and board
        Point last = history.remove(history.size() - 1);
        board.clearCell(last);

        // Rebuild the Union-Find structure from scratch
        uf = new DisjointSet<>();
        uf.makeSet(WHITE_WEST);
        uf.makeSet(WHITE_EAST);
        uf.makeSet(BLACK_NORTH);
        uf.makeSet(BLACK_SOUTH);

        // Replay all remaining moves in order
        for (Point p : history) {
            Stone s = board.getStone(p);
            uf.makeSet(p);
            unionWithNeighbors(p, s);
            unionWithBorders(p, s);
        }
    }

    /**
     * @return the underlying board
     */
    public Board getBoard() {
        return board;
    }

    public void setBoardSize(BoardSize boardSize) {this.board = new Board(boardSize);}

    /**
     * Places a stone of the given color at the specified point.
     * Unions it with adjacent same-colored stones and border nodes.
     *
     * @param point the board coordinate
     * @param stone the stone color
     * @throws IllegalArgumentException if point is out of bounds
     */
    public void placeStone(Point point, Stone stone) {
        boolean valid = validateMove(point, stone);
        if (!valid) {
            throw new IllegalArgumentException("Invalid point for placing stone: " + point);
        }
        board.placeStone(point, stone);
        uf.makeSet(point);

        unionWithNeighbors(point, stone);
        unionWithBorders(point, stone);

        history.add(point); // Track the move for potential undo
    }

    /**
     * Checks if the given color has formed a connected path across.
     *
     * @param stone the stone color to check
     * @return true if that color has won
     */
    public boolean hasWon(Stone stone) {
        return (stone == Stone.WHITE)
                ? uf.connected(WHITE_WEST, WHITE_EAST)
                : uf.connected(BLACK_NORTH, BLACK_SOUTH);
    }

    // ------ Helper Methods ------

    /**
     * Validates that the point can be placed on the board at point
     */
    private boolean validatePoint(Point point) {
        boolean inBounds = board.isOnBoard(point);
        boolean isEmpty = board.isEmpty(point);
        return inBounds && isEmpty;
    }

    private boolean validateMove(Point point, Stone stone) {
        // Check if the point is on the board and empty
        boolean isPointValid = validatePoint(point);
        // Check for forbidden 2x2 patterns
        boolean isPatternViolated = enforceNoForbiddenPattern(point, stone);

        return isPointValid && !isPatternViolated;
    }

    /**
     * Unions the newly placed stone with all adjacent stones of the same color.
     */
    private void unionWithNeighbors(Point point, Stone stone) {
        for (int[] dir : DIRECTIONS) {
            Point neighbor = new Point(point.x() + dir[0], point.y() + dir[1]);
            if (board.isOnBoard(neighbor) && stone.equals(board.getStone(neighbor))) {
                uf.makeSet(neighbor);
                uf.union(point, neighbor);
            }
        }
    }

    /**
     * Unions the stone with the appropriate virtual border nodes.
     */
    private void unionWithBorders(Point point, Stone stone) {
        int N = board.getSize().size();  // square board
        if (stone == Stone.WHITE) {
            if (point.x() == 0)     uf.union(point, WHITE_WEST);
            if (point.x() == N - 1) uf.union(point, WHITE_EAST);
        } else {
            if (point.y() == 0)     uf.union(point, BLACK_NORTH);
            if (point.y() == N - 1) uf.union(point, BLACK_SOUTH);
        }
    }

    /**
     * Enforces that no forbidden 2x2 pattern (as in Fig.2) is formed by this placement.
     */
    private boolean enforceNoForbiddenPattern(Point point, Stone stone) {
        List<Point> blocks = getTopLeftCorners(point);
        for (Point topLeft : blocks) {
            if (isForbiddenBlock(topLeft, point, stone)) {
                return true; // Pattern is violated
            }
        }
        return false; // No forbidden pattern found
    }

    /**
     * Returns all top-left corners of 2x2 blocks that include the point.
     */
    private List<Point> getTopLeftCorners(Point point) {
        int x = point.x(), y = point.y();
        return Arrays.stream(new int[][]{{-1,-1},{-1,0},{0,-1},{0,0}})
                .map(offset -> new Point(x + offset[0], y + offset[1]))
                .filter(p -> isWithinBlock(p))
                .collect(Collectors.toList());
    }
    /**
     * Checks if a 2x2 block with top-left corner p fits on the board.
     */
    private boolean isWithinBlock(Point p) {
        int N = board.getSize().size();
        return p.x() >= 0 && p.y() >= 0 && p.x() + 1 < N && p.y() + 1 < N;
    }
    /**
     * Determines if placing 'stone' at 'point' completes the forbidden pattern
     * within the 2x2 block whose top-left corner is 'topLeft'.
     */
    private boolean isForbiddenBlock(Point topLeft, Point point, Stone stone) {
        Stone other = stone.opposite();
        Point p00 = topLeft;
        Point p01 = new Point(topLeft.x(), topLeft.y() + 1);
        Point p10 = new Point(topLeft.x() + 1, topLeft.y());
        Point p11 = new Point(topLeft.x() + 1, topLeft.y() + 1);
        Stone s00 = p00.equals(point) ? stone : board.getStone(p00);
        Stone s01 = p01.equals(point) ? stone : board.getStone(p01);
        Stone s10 = p10.equals(point) ? stone : board.getStone(p10);
        Stone s11 = p11.equals(point) ? stone : board.getStone(p11);

        if (s00 == null || s01 == null || s10 == null || s11 == null) {
            return false;
        }
        // Diagonal patterns
        return (s00 == stone && s11 == stone && s01 == other && s10 == other)
                || (s01 == stone && s10 == stone && s00 == other && s11 == other);
    }

}