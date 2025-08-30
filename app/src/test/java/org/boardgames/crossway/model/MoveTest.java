package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Move class.
 */
public class MoveTest {

    @Test
    @DisplayName("constructor sets point and stone correctly")
    void testConstructor() {
        Point point = new Point(0, 0);
        Stone stone = Stone.BLACK;
        Move move = new Move(point, stone);
        assertEquals(point, move.getPoint(), "getPoint should return the constructed point");
        assertEquals(stone, move.getStone(), "getStone should return the constructed stone");
    }

    @Test
    @DisplayName("constructor throws exception for null point")
    void testConstructorNullPoint() {
        assertThrows(IllegalArgumentException.class, () -> new Move(null, Stone.BLACK),
                "Constructor should throw IllegalArgumentException for null point");
    }

    @Test
    @DisplayName("constructor throws exception for null stone")
    void testConstructorNullStone() {
        assertThrows(IllegalArgumentException.class, () -> new Move(new Point(0, 0), null),
                "Constructor should throw IllegalArgumentException for null stone");
    }

    @Test
    @DisplayName("equals returns true for identical moves")
    void testEqualsSame() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(0, 0), Stone.BLACK);
        assertEquals(m1, m2, "Identical moves should be equal");
    }

    @Test
    @DisplayName("equals returns false for different points")
    void testEqualsDifferentPoint() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(1, 0), Stone.BLACK);
        assertNotEquals(m1, m2, "Moves with different points should not be equal");
    }

    @Test
    @DisplayName("equals returns false for different stones")
    void testEqualsDifferentStone() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(0, 0), Stone.WHITE);
        assertNotEquals(m1, m2, "Moves with different stones should not be equal");
    }

    @Test
    @DisplayName("hashCode is consistent with equals")
    void testHashCode() {
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(0, 0), Stone.BLACK);
        assertEquals(m1.hashCode(), m2.hashCode(), "Equal moves should have the same hashCode");
    }
}