package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Move} class, verifying its functionality including constructor behavior,
 * equality checks, and hash code consistency.
 */
class MoveTest {

    /**
     * Tests the constructor of the {@link Move} class to ensure that it correctly
     * initializes the point and stone properties.
     */
    @Test
    @DisplayName("constructor sets point and stone correctly")
    void testConstructor() {
        Point point = new Point(0, 0);
        Stone stone = Stone.BLACK;
        Move move = new Move(point, stone);
        assertEquals(point, move.getPoint(), "getPoint should return the constructed point");
        assertEquals(stone, move.getStone(), "getStone should return the constructed stone");
    }

    /**
     * Tests the constructor's handling of a null point, expecting it to throw an
     * {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("constructor throws exception for null point")
    void testConstructorNullPoint() {
        assertThrows(IllegalArgumentException.class, () -> new Move(null, Stone.BLACK),
                "Constructor should throw IllegalArgumentException for null point");
    }

    /**
     * Tests the constructor's handling of a null stone, expecting it to throw an
     * {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("constructor throws exception for null stone")
    void testConstructorNullStone() {
        assertThrows(IllegalArgumentException.class, () -> new Move(new Point(0, 0), null),
                "Constructor should throw IllegalArgumentException for null stone");
    }

    /**
     * Tests the {@link Move#equals(Object)} method to ensure it returns true for two
     * {@link Move} objects that have the same point and stone.
     */
    @Test
    @DisplayName("equals returns true for identical moves")
    void testEqualsSame() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(0, 0), Stone.BLACK);
        assertEquals(m1, m2, "Identical moves should be equal");
    }

    /**
     * Tests the {@link Move#equals(Object)} method to ensure it returns false for two
     * {@link Move} objects that have the same stone but different points.
     */
    @Test
    @DisplayName("equals returns false for different points")
    void testEqualsDifferentPoint() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(1, 0), Stone.BLACK);
        assertNotEquals(m1, m2, "Moves with different points should not be equal");
    }

    /**
     * Tests the {@link Move#equals(Object)} method to ensure it returns false for two
     * {@link Move} objects that have the same point but different stones.
     */
    @Test
    @DisplayName("equals returns false for different stones")
    void testEqualsDifferentStone() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(0, 0), Stone.WHITE);
        assertNotEquals(m1, m2, "Moves with different stones should not be equal");
    }

    /**
     * Tests the {@link Move#hashCode()} method to ensure that equal objects return
     * the same hash code, as per the contract of {@link Object#hashCode()}.
     */
    @Test
    @DisplayName("hashCode is consistent with equals")
    void testHashCode() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(0, 0), Stone.BLACK);
        assertEquals(m1.hashCode(), m2.hashCode(), "Equal moves should have the same hashCode");
    }
}