package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Point record.
 */
public class PointTest {

    @Test
    @DisplayName("isNeighbourOf detects self as neighbor")
    void testIsNeighbourOfSelf() {
        Point p = new Point(0, 0);
        assertTrue(p.isNeighbourOf(p), "A point should be a neighbor of itself");
    }

    @Test
    @DisplayName("isNeighbourOf detects adjacent points")
    void testIsNeighbourOfAdjacent() {
        Point p = new Point(1, 1);
        // Horizontal
        assertTrue(p.isNeighbourOf(new Point(0, 1)));
        assertTrue(p.isNeighbourOf(new Point(2, 1)));
        // Vertical
        assertTrue(p.isNeighbourOf(new Point(1, 0)));
        assertTrue(p.isNeighbourOf(new Point(1, 2)));
        // Diagonal
        assertTrue(p.isNeighbourOf(new Point(0, 0)));
        assertTrue(p.isNeighbourOf(new Point(0, 2)));
        assertTrue(p.isNeighbourOf(new Point(2, 0)));
        assertTrue(p.isNeighbourOf(new Point(2, 2)));
    }

    @Test
    @DisplayName("isNeighbourOf detects non-adjacent points")
    void testIsNeighbourOfDistant() {
        Point p = new Point(0, 0);
        assertFalse(p.isNeighbourOf(new Point(2, 0)), "Points two units apart should not be neighbors");
        assertFalse(p.isNeighbourOf(new Point(0, 2)));
        assertFalse(p.isNeighbourOf(new Point(2, 2)));
    }
}