package org.boardgames.crossway.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main game logic for Crossway. Uses a Union-Find structure to
 * track connectivity for efficient win detection.
 */
public class Game {
    private Board board;
    private DisjointSet<Point> uf = new DisjointSet<Point>();
    private Deque<Move> history = new ArrayDeque<Move>();

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
        Move lastMove = history.removeLast();
        board.clearCell(lastMove.getPoint());

        // Rebuild the Union-Find structure from scratch
        uf = new DisjointSet<>();
        uf.makeSet(WHITE_WEST);
        uf.makeSet(WHITE_EAST);
        uf.makeSet(BLACK_NORTH);
        uf.makeSet(BLACK_SOUTH);

        // Replay all remaining moves in order
        for (Move m : history) {
            Optional<Stone> s = board.stoneAt(m.getPoint());
            s.ifPresentOrElse(
                stone -> {
                    board.placeStone(m.getPoint(), stone); // Re-place the stone
                    uf.makeSet(m.getPoint()); // Register the point in Union-Find
                    unionWithNeighbors(m.getPoint(), stone);
                    unionWithBorders(m.getPoint(), stone);
                },
                () -> board.clearCell(m.getPoint()) // Clear if no stone was present
            );
        }
    }

    /**
     * @return the underlying board
     */
    public Board getBoard() {
        return board;
    }

    public void makeMove(Move move) {
        if (!validateMove(move)) {
            throw new IllegalArgumentException("Invalid point for placing stone: " + move.getPoint());
        }

        board.placeStone(move.getPoint(), move.getStone());

        // Register the point in Union-Find
        uf.makeSet(move.getPoint());
        unionWithNeighbors(move.getPoint(), move.getStone());
        unionWithBorders(move.getPoint(), move.getStone());

        history.add(move); // Track the move for potential undo
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

    private boolean validateMove(Move move) {
        // Check if the point is on the board and empty
        boolean isPointValid = validatePoint(move.getPoint());
        // Check for forbidden 2x2 patterns
        boolean isPatternViolated = enforceNoForbiddenPattern(move);

        return isPointValid && !isPatternViolated;
    }

    /**
     * Unions the newly placed stone with all adjacent stones of the same color.
     */
    private void unionWithNeighbors(Point point, Stone stone) {
        for (int[] dir : DIRECTIONS) {
            Point neighbor = new Point(point.x() + dir[0], point.y() + dir[1]);
            Optional<Stone> s = board.stoneAt(point);
            if (board.isOnBoard(neighbor) && s.isPresent() && stone.equals(s.get())) {
                uf.makeSet(neighbor); // Ensure the neighbor is registered in Union-Find
                uf.union(point, neighbor); // Union the current point with the neighbor
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
    private boolean enforceNoForbiddenPattern(Move move) {
        List<Point> blocks = getTopLeftCorners(move.getPoint());
        for (Point topLeft : blocks) {
            if (isForbiddenBlock(topLeft, move.getPoint(), move.getStone())) {
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
    private boolean isForbiddenBlock(Point topLeft, Point placed, Stone stone) {
        final Stone other = stone.opposite();

        // 2×2 block coordinates
        final int x = topLeft.x();
        final int y = topLeft.y();
        final Point p00 = topLeft;
        final Point p01 = new Point(x,     y + 1);
        final Point p10 = new Point(x + 1, y);
        final Point p11 = new Point(x + 1, y + 1);

        // Early-out: if the 2×2 spills outside the board, it can't form the pattern
        if (!board.isOnBoard(p00) || !board.isOnBoard(p01) || !board.isOnBoard(p10) || !board.isOnBoard(p11)) {
            return false;
        }

        // Lookup that treats (placed) as if the move were already on the board
        java.util.function.Function<Point, java.util.Optional<Stone>> atWithPlaced =
                p -> p.equals(placed) ? java.util.Optional.of(stone) : board.stoneAt(p);

        var s00 = atWithPlaced.apply(p00);
        var s01 = atWithPlaced.apply(p01);
        var s10 = atWithPlaced.apply(p10);
        var s11 = atWithPlaced.apply(p11);

        // Pattern only applies when all four cells are occupied
        if (s00.isEmpty() || s01.isEmpty() || s10.isEmpty() || s11.isEmpty()) return false;

        var v00 = s00.get();
        var v01 = s01.get();
        var v10 = s10.get();
        var v11 = s11.get();

        // Diagonal "X" patterns
        return (v00 == stone && v11 == stone && v01 == other && v10 == other)
                || (v01 == stone && v10 == stone && v00 == other && v11 == other);
    }


}