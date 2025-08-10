package org.boardgames.crossway.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Board class.
 */
public class BoardTest {
    private Board board;
    private BoardSize size;

    @BeforeEach
    void setUp() {
        size = new BoardSize(4);
        board = new Board(size);
    }

    @Test
    @DisplayName("Empty board returns null for any coordinate")
    void testGetStoneEmpty() {
        Point p = new Point(2, 3);
        assertNull(board.stoneAt(p), "New board should have no stones placed");
    }

    @Test
    @DisplayName("Placing and retrieving stones (Optional API)")
    void testPlaceAndStoneAt() {
        Point p = new Point(1, 1);

        board.placeStone(p, Stone.BLACK);
        var r1 = board.stoneAt(p);
        assertTrue(r1.isPresent(), "Stone should be present after placement");
        assertEquals(Stone.BLACK, r1.get(), "Stone placed should be retrievable");

        // Overwrite existing stone
        board.placeStone(p, Stone.WHITE);
        var r2 = board.stoneAt(p);
        assertTrue(r2.isPresent(), "Stone should still be present after overwrite");
        assertEquals(Stone.WHITE, r2.get(), "Placing a new stone should overwrite previous one");
    }

    @Test
    @DisplayName("isOnBoard detects valid and invalid points")
    void testIsOnBoard() {
        // Valid points
        assertTrue(board.isOnBoard(new Point(0, 0)));
        assertTrue(board.isOnBoard(new Point(3, 3)));

        // Invalid points
        assertFalse(board.isOnBoard(new Point(-1, 0)));
        assertFalse(board.isOnBoard(new Point(0, -1)));
        assertFalse(board.isOnBoard(new Point(4, 2)));
        assertFalse(board.isOnBoard(new Point(2, 4)));
    }

    @Test
    @DisplayName("getSize returns correct board size")
    void testGetSize() {
        BoardSize returned = board.getSize();
        assertSame(size, returned, "getSize should return the same BoardSize instance passed in constructor");
        assertEquals(4, returned.size(), "BoardSize.size() should match dimension");
    }

    @Test
    @DisplayName("Placing stone outside board throws exception")
    void testPlaceStoneOutOfBounds() {
        Point outside = new Point(10, 10);
        assertThrows(IllegalArgumentException.class,
                () -> board.placeStone(outside, Stone.BLACK),
                "Placing a stone outside board should throw IllegalArgumentException");
    }
}