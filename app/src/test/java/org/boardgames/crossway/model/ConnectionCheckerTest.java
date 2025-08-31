package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.ConnectionChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ConnectionChecker} class.
 * <p>
 * These tests verify that the connection detection logic correctly identifies
 * winning conditions for both Black and White stones on a game board.
 * </p>
 */
public class ConnectionCheckerTest {

    /**
     * Tests whether the {@link ConnectionChecker} correctly detects a winning
     * vertical connection for the black player.
     * <p>
     * A winning connection for black is a continuous path of black stones
     * from the top (north) edge of the board to the bottom (south) edge.
     * </p>
     */
    @Test
    @DisplayName("Detects a vertical connection for the black player")
    void testBlackWin() {
        Board board = new Board(new BoardSize(3));
        board.placeStone(new Point(1, 0), Stone.BLACK);
        board.placeStone(new Point(1, 1), Stone.BLACK);
        board.placeStone(new Point(1, 2), Stone.BLACK);
        ConnectionChecker checker = new ConnectionChecker(board);
        assertTrue(checker.hasWon(Stone.BLACK), "Black should connect north to south");
        assertFalse(checker.hasWon(Stone.WHITE), "White should not have a winning path");
    }

    /**
     * Tests whether the {@link ConnectionChecker} correctly detects a winning
     * horizontal connection for the white player.
     * <p>
     * A winning connection for white is a continuous path of white stones
     * from the left (west) edge of the board to the right (east) edge.
     * </p>
     */
    @Test
    @DisplayName("Detects a horizontal connection for the white player")
    void testWhiteWin() {
        Board board = new Board(new BoardSize(3));
        board.placeStone(new Point(0, 1), Stone.WHITE);
        board.placeStone(new Point(1, 1), Stone.WHITE);
        board.placeStone(new Point(2, 1), Stone.WHITE);
        ConnectionChecker checker = new ConnectionChecker(board);
        assertTrue(checker.hasWon(Stone.WHITE), "White should connect west to east");
        assertFalse(checker.hasWon(Stone.BLACK), "Black should not have a winning path");
    }

    /**
     * Tests that the {@link ConnectionChecker} returns false when no
     * winning connection exists for either player.
     * <p>
     * This test places disconnected stones on the board to ensure that a
     * path is only detected if the stones form a continuous chain.
     * </p>
     */
    @Test
    @DisplayName("Returns false when no winning connection exists")
    void testNoWin() {
        Board board = new Board(new BoardSize(3));
        board.placeStone(new Point(0, 0), Stone.BLACK);
        board.placeStone(new Point(2, 2), Stone.BLACK);
        ConnectionChecker checker = new ConnectionChecker(board);
        assertFalse(checker.hasWon(Stone.BLACK), "Disconnected stones should not win");
        assertFalse(checker.hasWon(Stone.WHITE), "No white stones present; no win");
    }
}