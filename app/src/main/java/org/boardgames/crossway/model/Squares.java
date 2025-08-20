package org.boardgames.crossway.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for operations related to 2x2 squares on the game board.
 * Provides methods for calculating the top-left coordinates of valid square regions.
 *
 * @author Gabriele Pintus
 */
public final class Squares {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Squares() {}

    /**
     * Calculates the top-left coordinates of all valid 2x2 blocks that include the given point.
     *
     * <p>A 2x2 block is considered "valid" if all four of its constituent points are
     * within the boundaries of the game board. This method checks four potential
     * top-left candidates around the given point {@code p}:
     * <ul>
     * <li>(p.x - 1, p.y - 1)</li>
     * <li>(p.x - 1, p.y)</li>
     * <li>(p.x, p.y - 1)</li>
     * <li>(p.x, p.y)</li>
     * </ul>
     * Only those candidates that can form a complete 2x2 square entirely on the board
     * are included in the final list.
     *
     * @param board The game board instance used to check boundaries.
     * @param p The reference point around which to find the 2x2 blocks.
     * @return A list of {@link Point} objects representing the top-left coordinates
     * of the valid 2x2 blocks containing the point {@code p}.
     */
    public static List<Point> topLeftsAround(Board board, Point p) {
        int x = p.x();
        int y = p.y();

        // Candidates for the top-left corner of a 2x2 block that contains (x,y).
        List<Point> candidates = List.of(
                new Point(x - 1, y - 1),
                new Point(x - 1, y),
                new Point(x, y - 1),
                new Point(x, y)
        );

        // Filter the candidates to include only those that form a full 2x2 square on the board.
        List<Point> result = new ArrayList<>();
        for (Point topLeft : candidates) {
            int tx = topLeft.x();
            int ty = topLeft.y();

            // Check if all four points of the 2x2 square are on the board.
            boolean isSquareOnBoard = board.isOnBoard(topLeft) &&
                    board.isOnBoard(new Point(tx + 1, ty)) &&
                    board.isOnBoard(new Point(tx, ty + 1)) &&
                    board.isOnBoard(new Point(tx + 1, ty + 1));

            if (isSquareOnBoard) {
                result.add(topLeft);
            }
        }
        return result;
    }
}

