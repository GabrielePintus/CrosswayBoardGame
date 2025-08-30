package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for History class.
 */
public class HistoryTest {

    @Test
    @DisplayName("constructor creates empty history")
    void testConstructor() {
        History history = new History();
        assertTrue(history.getPastMoves().isEmpty(), "Past moves should be empty initially");
        assertTrue(history.getFutureMoves().isEmpty(), "Future moves should be empty initially");
    }

    @Test
    @DisplayName("commit adds move to past")
    void testCommit() {
        History history = new History();
        Move move = new Move(new Point(0, 0), Stone.BLACK);
        history.commit(move);
        assertEquals(List.of(move), history.getPastMoves(), "Committed move should be in past moves");
        assertTrue(history.getFutureMoves().isEmpty(), "Future moves should remain empty after commit");
    }

    @Test
    @DisplayName("commit throws exception for null move")
    void testCommitNull() {
        History history = new History();
        assertThrows(IllegalArgumentException.class, () -> history.commit(null),
                "Commit should throw IllegalArgumentException for null move");
    }

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

    @Test
    @DisplayName("undo on empty history returns null")
    void testUndoEmpty() {
        History history = new History();
        assertNull(history.undo(), "Undo on empty history should return null");
    }

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

    @Test
    @DisplayName("redo on empty future returns null")
    void testRedoEmpty() {
        History history = new History();
        assertNull(history.redo(), "Redo on empty future should return null");
    }

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