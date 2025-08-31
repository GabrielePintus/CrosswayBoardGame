package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BoundsRule} class.
 * This class verifies that the rule correctly validates moves
 * based on whether they fall within the board's boundaries.
 */
class BoundsRuleTest {

    /**
     * Tests that a move with coordinates inside the board's boundaries
     * is considered valid by the {@link BoundsRule}.
     */
    @Test
    @DisplayName("Move with coordinates inside board boundaries should be valid")
    void moveInsideBoardPassesValidation() {
        Board board = new Board(new BoardSize(3));
        Move move = new Move(new Point(1, 1), Stone.BLACK);
        BoundsRule rule = new BoundsRule();

        assertTrue(rule.validate(board, move).isEmpty());
    }

    /**
     * Tests that a move with coordinates outside the board's boundaries
     * is considered invalid by the {@link BoundsRule}.
     */
    @Test
    @DisplayName("Move with coordinates outside board boundaries should be invalid")
    void moveOutsideBoardFailsValidation() {
        Board board = new Board(new BoardSize(3));
        Move move = new Move(new Point(10, 10), Stone.BLACK); // Out of bounds for a 3x3 board (indices 0-2)
        BoundsRule rule = new BoundsRule();

        assertFalse(rule.validate(board, move).isEmpty());
    }
}