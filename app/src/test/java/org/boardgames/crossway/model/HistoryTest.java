package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link History} class, which manages the history of moves in a game.
 * It tests the core functionalities of committing, undoing, redoing, and swapping move colors.
 */
class HistoryTest {

    /**
     * Tests the default constructor of the {@link History} class.
     * Verifies that a new history object is created with empty lists for past and future moves.
     */
    @Test
    @DisplayName("constructor creates empty history")
    void testConstructor() {
        History history = new History();
        assertTrue(history.getPastMoves().isEmpty(), "Past moves should be empty initially");
        assertTrue(history.getFutureMoves().isEmpty(), "Future moves should be empty initially");
    }

    /**
     * Tests the {@link History#commit(Move)} method.
     * Verifies that a committed move is correctly added to the list of past moves and that the future moves list remains empty.
     */
    @Test
    @DisplayName("commit adds move to past")
    void testCommit() {
        History history = new History();
        Move move = new Move(new Point(0, 0), Stone.BLACK);
        history.commit(move);
        assertEquals(List.of(move), history.getPastMoves(), "Committed move should be in past moves");
        assertTrue(history.getFutureMoves().isEmpty(), "Future moves should remain empty after commit");
    }

    /**
     * Tests the {@link History#commit(Move)} method's handling of a null move.
     * Verifies that it throws an {@link IllegalArgumentException} as expected.
     */
    @Test
    @DisplayName("commit throws exception for null move")
    void testCommitNull() {
        History history = new History();
        assertThrows(IllegalArgumentException.class, () -> history.commit(null),
                "Commit should throw IllegalArgumentException for null move");
    }

    /**
     * Tests the {@link History#undo()} method.
     * Verifies that the last move from the past is correctly moved to the future and is returned by the method.
     */
    @Test
    @DisplayName("undo moves from past to future")
    void testUndo() {
        History history = new History();
        Move move = new Move(new Point(0, 0), Stone.BLACK);
        history.commit(move);
        Move undone = history.undo();
        assertEquals(move, undone, "Undone move should match the committed move");
        assertTrue(history.getPastMoves().isEmpty(), "Past moves should be empty after undo");
        assertEquals(List.of(move), history.getFutureMoves(), "Undone move should be in future moves");
    }

    /**
     * Tests the {@link History#undo()} method on an empty history.
     * Verifies that it returns null when there are no moves to undo.
     */
    @Test
    @DisplayName("undo on empty history returns null")
    void testUndoEmpty() {
        History history = new History();
        assertNull(history.undo(), "Undo on empty history should return null");
    }

    /**
     * Tests the {@link History#redo()} method.
     * Verifies that the last move from the future is correctly moved back to the past and is returned by the method.
     */
    @Test
    @DisplayName("redo moves from future to past")
    void testRedo() {
        History history = new History();
        Move move = new Move(new Point(0, 0), Stone.BLACK);
        history.commit(move);
        history.undo();
        Move redone = history.redo();
        assertEquals(move, redone, "Redone move should match the original move");
        assertEquals(List.of(move), history.getPastMoves(), "Redone move should be in past moves");
        assertTrue(history.getFutureMoves().isEmpty(), "Future moves should be empty after redo");
    }

    /**
     * Tests the {@link History#redo()} method on a history with an empty future.
     * Verifies that it returns null when there are no moves to redo.
     */
    @Test
    @DisplayName("redo on empty future returns null")
    void testRedoEmpty() {
        History history = new History();
        assertNull(history.redo(), "Redo on empty future should return null");
    }

    /**
     * Tests the behavior of {@link History#commit(Move)} after a call to {@link History#undo()}.
     * Verifies that a new commit clears the list of future moves.
     */
    @Test
    @DisplayName("commit after undo clears future")
    void testCommitAfterUndo() {
        History history = new History();
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(1, 0), Stone.WHITE);
        history.commit(m1);
        history.undo();
        history.commit(m2);
        assertEquals(List.of(m2), history.getPastMoves(), "New commit after undo should replace past");
        assertTrue(history.getFutureMoves().isEmpty(), "Future should be cleared after commit following undo");
    }

    /**
     * Tests the {@link History#swapColors()} method.
     * Verifies that the stone colors of all moves in the past history are correctly flipped.
     */
    @Test
    @DisplayName("swapColors flips stones in all moves")
    void testSwapColors() {
        History history = new History();
        Move m1 = new Move(new Point(0, 0), Stone.BLACK);
        Move m2 = new Move(new Point(1, 0), Stone.WHITE);
        history.commit(m1);
        history.commit(m2);
        history.swapColors();
        List<Move> past = history.getPastMoves();
        assertEquals(Stone.WHITE, past.get(0).getStone(), "First move stone should be flipped");
        assertEquals(Stone.BLACK, past.get(1).getStone(), "Second move stone should be flipped");
    }

    /**
     * Tests that the {@link History#getPastMoves()} method returns a copy of the list.
     * Verifies that external modifications to the returned list do not affect the internal state of the history.
     */
    @Test
    @DisplayName("getPastMoves returns a copy")
    void testGetPastMovesCopy() {
        History history = new History();
        Move move = new Move(new Point(0, 0), Stone.BLACK);
        history.commit(move);
        List<Move> past = history.getPastMoves();
        past.clear();
        assertFalse(history.getPastMoves().isEmpty(), "Modifying returned list should not affect internal state");
    }

    /**
     * Tests that the {@link History#getFutureMoves()} method returns a copy of the list.
     * Verifies that external modifications to the returned list do not affect the internal state of the history.
     */
    @Test
    @DisplayName("getFutureMoves returns a copy")
    void testGetFutureMovesCopy() {
        History history = new History();
        Move move = new Move(new Point(0, 0), Stone.BLACK);
        history.commit(move);
        history.undo();
        List<Move> future = history.getFutureMoves();
        future.clear();
        assertFalse(history.getFutureMoves().isEmpty(), "Modifying returned list should not affect internal state");
    }
}