package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BoardSize} record, verifying its immutability and
 * the correctness of its utility methods for validating coordinates.
 */
public class BoardSizeTest {

    /**
     * Tests the {@link BoardSize#size()} method.
     * Verifies that it returns the correct dimension of the board.
     */
    @Test
    @DisplayName("size() returns the correct dimension")
    void testSizeMethod() {
        BoardSize size = new BoardSize(6);
        assertEquals(6, size.size(), "size() should return the constructor value");
    }

    /**
     * Tests the {@link BoardSize#isInBounds(Point)} method with points that are
     * within the board's boundaries, including corner points.
     */
    @Test
    @DisplayName("isInBounds detects points inside the board")
    void testIsInBoundsValid() {
        BoardSize size = new BoardSize(4);
        assertTrue(size.isInBounds(new Point(0, 0)), "(0,0) should be in bounds");
        assertTrue(size.isInBounds(new Point(3, 3)), "(3,3) should be in bounds");
        assertTrue(size.isInBounds(new Point(2, 1)), "(2,1) should be in bounds");
    }

    /**
     * Tests the {@link BoardSize#isInBounds(Point)} method with points that are
     * outside the board's boundaries.
     */
    @Test
    @DisplayName("isInBounds detects points outside the board")
    void testIsInBoundsInvalid() {
        BoardSize size = new BoardSize(4);
        assertFalse(size.isInBounds(new Point(-1, 0)), "(-1,0) should be out of bounds");
        assertFalse(size.isInBounds(new Point(0, -1)), "(0,-1) should be out of bounds");
        assertFalse(size.isInBounds(new Point(4, 2)), "(4,2) should be out of bounds");
        assertFalse(size.isInBounds(new Point(2, 4)), "(2,4) should be out of bounds");
    }

    /**
     * Tests the {@link BoardSize#toInt()} method.
     * Verifies that it correctly returns the board size as an integer.
     */
    @Test
    @DisplayName("toInt returns the board size as an integer")
    void testToInt() {
        BoardSize size = new BoardSize(5);
        assertEquals(5, size.toInt(), "toInt() should return the board size as an integer");
    }
}