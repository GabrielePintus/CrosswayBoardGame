package org.boardgames.crossway.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import org.boardgames.crossway.model.Board.Color;


public class BoardTest {

    private Board board;

    @BeforeEach
    void setup() {
        board = new Board((short) 6);
    }

    @Test
    @DisplayName("Board should return the correct size")
    void testBoardSize() {
        assertEquals(6, board.getSize());
    }

    @Test
    @DisplayName("Adding and retrieving a stone works correctly")
    void testAddAndRetrieveStone() {
        Stone stone = new Stone(2, 3, Color.WHITE);
        Point point = new Point(2, 3);

        board.addStone(stone);

        Stone retrieved = board.getStone(point);
        assertNotNull(retrieved);
        assertEquals(stone, retrieved);
    }

    @Test
    @DisplayName("Adding a stone out of bounds throws exception")
    void testAddStoneOutOfBounds() {
        Stone outOfBoundsStone1 = new Stone(1, 10, Color.BLACK);
        Stone outOfBoundsStone2 = new Stone(20, 5, Color.BLACK); // board size is 6
        Stone outOfBoundsStone3 = new Stone(0, 6, Color.WHITE); // board size is 6

        assertThrows(IllegalArgumentException.class, () -> board.addStone(outOfBoundsStone1));
        assertThrows(IllegalArgumentException.class, () -> board.addStone(outOfBoundsStone2));
        assertThrows(IllegalArgumentException.class, () -> board.addStone(outOfBoundsStone3));
    }

    @Test
    @DisplayName("Creating a stone with negative coordinates throws exception")
    void testCreateStoneWithNegativeCoordinates() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Stone(-1, -1, Color.WHITE));
        assertEquals("Invalid position", exception.getMessage());
    }

    @Test
    @DisplayName("Retrieving a non-existent stone returns null")
    void testGetStoneNotPresent() {
        Stone result = board.getStone(new Point(1, 1));
        assertNull(result);
    }

    @Test
    @DisplayName("Board should not allow boardSize less than 2")
    void testBoardSizeAssertion() {
        AssertionError error = assertThrows(AssertionError.class, () -> new Board((short) 1));
        assertEquals("Board size must be greater than 1", error.getMessage());
    }
}