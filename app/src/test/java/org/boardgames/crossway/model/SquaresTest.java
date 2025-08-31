package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Squares} utility class, verifying the correctness of its methods related to
 * identifying potential top-left corners of 2x2 squares on a game board.
 */
public class SquaresTest {

    /**
     * Tests the {@link Squares#topLeftsAround(Board, Point)} method for a central point on a 5x5 board.
     * Expects a list of 4 points, corresponding to the top-left corners of the four 2x2 squares
     * that intersect at the given central point.
     */
    @Test
    @DisplayName("topLeftsAround returns 4 for center point")
    void testTopLeftsAroundCenter() {
        Board board = new Board(new BoardSize(5));
        Point p = new Point(2, 2);
        List<Point> tops = Squares.topLeftsAround(board, p);
        assertEquals(4, tops.size(), "Center point should have 4 top-left candidates");
        assertTrue(tops.contains(new Point(1, 1)));
        assertTrue(tops.contains(new Point(1, 2)));
        assertTrue(tops.contains(new Point(2, 1)));
        assertTrue(tops.contains(new Point(2, 2)));
    }

    /**
     * Tests the {@link Squares#topLeftsAround(Board, Point)} method for a corner point (0, 0) on a 5x5 board.
     * Expects a list containing only one point, which is the point itself, as it can only be the top-left
     * corner of a 2x2 square.
     */
    @Test
    @DisplayName("topLeftsAround returns 1 for corner point")
    void testTopLeftsAroundCorner() {
        Board board = new Board(new BoardSize(5));
        Point p = new Point(0, 0);
        List<Point> tops = Squares.topLeftsAround(board, p);
        assertEquals(1, tops.size(), "Corner point should have 1 top-left candidate");
        assertTrue(tops.contains(new Point(0, 0)));
    }

    /**
     * Tests the {@link Squares#topLeftsAround(Board, Point)} method for a point that is out of the board's bounds.
     * Expects an empty list as no 2x2 squares can be formed around an invalid point.
     */
    @Test
    @DisplayName("topLeftsAround returns empty for out of bounds")
    void testTopLeftsAroundOutOfBounds() {
        Board board = new Board(new BoardSize(5));
        Point p = new Point(-1, -1);
        List<Point> tops = Squares.topLeftsAround(board, p);
        assertTrue(tops.isEmpty(), "Out of bounds point should return empty list");
    }

    /**
     * Tests the {@link Squares#topLeftsAround(Board, Point)} method on a very small board where it's impossible
     * to form a 2x2 square.
     * Expects an empty list, as no valid top-left candidates for a 2x2 square exist.
     */
    @Test
    @DisplayName("topLeftsAround on small board where 2x2 not possible")
    void testTopLeftsAroundSmallBoard() {
        Board board = new Board(new BoardSize(1));
        Point p = new Point(0, 0);
        List<Point> tops = Squares.topLeftsAround(board, p);
        assertTrue(tops.isEmpty(), "1x1 board should return empty list for 2x2 squares");
    }
}