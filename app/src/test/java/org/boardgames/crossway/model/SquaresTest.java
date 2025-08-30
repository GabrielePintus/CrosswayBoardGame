package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Squares utility class.
 */
public class SquaresTest {

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

    @Test
    @DisplayName("topLeftsAround returns 1 for corner point")
    void testTopLeftsAroundCorner() {
        Board board = new Board(new BoardSize(5));
        Point p = new Point(0, 0);
        List<Point> tops = Squares.topLeftsAround(board, p);
        assertEquals(1, tops.size(), "Corner point should have 1 top-left candidate");
        assertTrue(tops.contains(new Point(0, 0)));
    }

    @Test
    @DisplayName("topLeftsAround returns empty for out of bounds")
    void testTopLeftsAroundOutOfBounds() {
        Board board = new Board(new BoardSize(5));
        Point p = new Point(-1, -1);
        List<Point> tops = Squares.topLeftsAround(board, p);
        assertTrue(tops.isEmpty(), "Out of bounds point should return empty list");
    }

    @Test
    @DisplayName("topLeftsAround on small board where 2x2 not possible")
    void testTopLeftsAroundSmallBoard() {
        Board board = new Board(new BoardSize(1));
        Point p = new Point(0, 0);
        List<Point> tops = Squares.topLeftsAround(board, p);
        assertTrue(tops.isEmpty(), "1x1 board should return empty list for 2x2 squares");
    }
}