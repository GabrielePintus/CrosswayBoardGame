package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Board} class, verifying its state management,
 * stone placement, and utility methods like coordinate validation and
 * serialization.
 */
class BoardTest {

    /**
     * Tests the constructor of the {@link Board} class when provided with a {@link BoardSize}.
     * Verifies that the board is correctly initialized with the given size and is initially empty.
     */
    @Test
    @DisplayName("constructor with size creates empty board")
    void testConstructorWithSize() {
        Board board = new Board(BoardSize.SMALL);
        assertEquals(BoardSize.SMALL, board.getSize(), "Board size should match the provided size");
        assertTrue(board.getStones().isEmpty(), "New board should have no stones");
    }

    /**
     * Tests the {@link Board#clear()} method.
     * Verifies that all stones are removed from the board, making it empty.
     */
    @Test
    @DisplayName("clear removes all stones")
    void testClear() {
        Board board = new Board(BoardSize.SMALL);
        board.placeStone(new Point(0, 0), Stone.BLACK);
        board.clear();
        assertTrue(board.getStones().isEmpty(), "Board should be empty after clear");
    }

    /**
     * Tests the {@link Board#swapColors()} method.
     * Verifies that the color of every stone on the board is flipped (e.g., BLACK becomes WHITE and vice-versa).
     */
    @Test
    @DisplayName("swapColors flips all stone colors")
    void testSwapColors() {
        Board board = new Board(BoardSize.SMALL);
        board.placeStone(new Point(0, 0), Stone.BLACK);
        board.placeStone(new Point(1, 0), Stone.WHITE);
        board.swapColors();
        assertEquals(Optional.of(Stone.WHITE), board.stoneAt(new Point(0, 0)), "BLACK should become WHITE");
        assertEquals(Optional.of(Stone.BLACK), board.stoneAt(new Point(1, 0)), "WHITE should become BLACK");
    }

    /**
     * Tests the {@link Board#placeStone(Point, Stone)} method with a valid point and stone.
     * Verifies that the stone is correctly placed and can be retrieved from the board.
     */
    @Test
    @DisplayName("placeStone adds stone at point")
    void testPlaceStoneValid() {
        Board board = new Board(BoardSize.SMALL);
        Point point = new Point(0, 0);
        board.placeStone(point, Stone.BLACK);
        assertEquals(Optional.of(Stone.BLACK), board.stoneAt(point), "Stone should be placed at the point");
    }

    /**
     * Tests the {@link Board#placeStone(Point, Stone)} method's handling of an out-of-bounds point.
     * Verifies that it throws an {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("placeStone throws exception for out of bounds")
    void testPlaceStoneOutOfBounds() {
        Board board = new Board(BoardSize.SMALL);
        Point point = new Point(-1, 0);
        assertThrows(IllegalArgumentException.class, () -> board.placeStone(point, Stone.BLACK),
                "placeStone should throw IllegalArgumentException for out of bounds");
    }

    /**
     * Tests the {@link Board#clearCell(Point)} method with a valid, occupied cell.
     * Verifies that the stone is removed and the cell becomes empty.
     */
    @Test
    @DisplayName("clearCell removes stone")
    void testClearCellValid() {
        Board board = new Board(BoardSize.SMALL);
        Point point = new Point(0, 0);
        board.placeStone(point, Stone.BLACK);
        board.clearCell(point);
        assertEquals(Optional.empty(), board.stoneAt(point), "Cell should be empty after clear");
    }

    /**
     * Tests the {@link Board#clearCell(Point)} method's handling of an empty cell.
     * Verifies that it throws an {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("clearCell throws exception for empty cell")
    void testClearCellEmpty() {
        Board board = new Board(BoardSize.SMALL);
        Point point = new Point(0, 0);
        assertThrows(IllegalArgumentException.class, () -> board.clearCell(point),
                "clearCell should throw IllegalArgumentException for empty cell");
    }

    /**
     * Tests the {@link Board#stoneAt(Point)} method for an unoccupied cell.
     * Verifies that it returns an empty {@link Optional}.
     */
    @Test
    @DisplayName("stoneAt returns empty for unoccupied")
    void testStoneAtEmpty() {
        Board board = new Board(BoardSize.SMALL);
        assertEquals(Optional.empty(), board.stoneAt(new Point(0, 0)), "Unoccupied cell should return empty Optional");
    }

    /**
     * Tests the {@link Board#isEmpty(Point)} method for an empty cell within board boundaries.
     * Verifies that it returns true.
     */
    @Test
    @DisplayName("isEmpty returns true for empty in bounds")
    void testIsEmptyTrue() {
        Board board = new Board(BoardSize.SMALL);
        assertTrue(board.isEmpty(new Point(0, 0)), "Empty cell in bounds should return true");
    }

    /**
     * Tests the {@link Board#isEmpty(Point)} method for an occupied cell.
     * Verifies that it returns false.
     */
    @Test
    @DisplayName("isEmpty returns false for occupied")
    void testIsEmptyFalseOccupied() {
        Board board = new Board(BoardSize.SMALL);
        Point point = new Point(0, 0);
        board.placeStone(point, Stone.BLACK);
        assertFalse(board.isEmpty(point), "Occupied cell should return false");
    }

    /**
     * Tests the {@link Board#isEmpty(Point)} method for a point outside the board boundaries.
     * Verifies that it returns false.
     */
    @Test
    @DisplayName("isEmpty returns false for out of bounds")
    void testIsEmptyFalseOutOfBounds() {
        Board board = new Board(BoardSize.SMALL);
        assertFalse(board.isEmpty(new Point(9, 0)), "Out of bounds should return false");
    }

    /**
     * Tests the {@link Board#isOnBoard(Point)} method for a point within the board boundaries.
     * Verifies that it returns true.
     */
    @Test
    @DisplayName("isOnBoard returns true for in bounds")
    void testIsOnBoardTrue() {
        Board board = new Board(BoardSize.SMALL);
        assertTrue(board.isOnBoard(new Point(0, 0)), "Point in bounds should return true");
    }

    /**
     * Tests the {@link Board#isOnBoard(Point)} method for a point outside the board boundaries.
     * Verifies that it returns false.
     */
    @Test
    @DisplayName("isOnBoard returns false for out of bounds")
    void testIsOnBoardFalse() {
        Board board = new Board(BoardSize.SMALL);
        assertFalse(board.isOnBoard(new Point(-1, 0)), "Point out of bounds should return false");
    }

    /**
     * Tests the {@link Board#getStones()} method.
     * Verifies that the returned list of moves is correctly sorted by x-coordinate, then by y-coordinate.
     */
    @Test
    @DisplayName("getStones returns sorted list")
    void testGetStonesSorted() {
        Board board = new Board(BoardSize.SMALL);
        board.placeStone(new Point(1, 0), Stone.BLACK);
        board.placeStone(new Point(0, 1), Stone.WHITE);
        board.placeStone(new Point(0, 0), Stone.BLACK);
        List<Move> stones = board.getStones();
        assertEquals(new Point(0, 0), stones.get(0).getPoint(), "First stone should be sorted by x then y");
        assertEquals(new Point(0, 1), stones.get(1).getPoint());
        assertEquals(new Point(1, 0), stones.get(2).getPoint());
    }

    /**
     * Tests the {@link Board#toJson()} method.
     * Verifies that the method correctly serializes the board's state into a JSON string.
     */
    @Test
    @DisplayName("toJson serializes board state")
    void testToJson() {
        Board board = new Board(BoardSize.SMALL);
        board.placeStone(new Point(0, 0), Stone.BLACK);
        String json = board.toJson();
        assertTrue(json.contains("\"size\":9"), "JSON should contain board size");
        assertTrue(json.contains("\"x\":0,\"y\":0"), "JSON should contain point coordinates");
        assertTrue(json.contains("\"stone\":\"BLACK\""), "JSON should contain stone type");
    }

    /**
     * Tests the {@link Board#fromJson(String)} method.
     * Verifies that the method correctly deserializes a JSON string to restore a board's state.
     */
    @Test
    @DisplayName("fromJson deserializes board state")
    void testFromJson() {
        String json = "{\"size\":9,\"stones\":[{\"point\":{\"x\":0,\"y\":0},\"stone\":\"BLACK\"}]}";
        Board board = Board.fromJson(json);
        assertEquals(BoardSize.SMALL, board.getSize(), "Deserialized board size should match");
        assertEquals(Optional.of(Stone.BLACK), board.stoneAt(new Point(0, 0)), "Deserialized stone should match");
    }
}