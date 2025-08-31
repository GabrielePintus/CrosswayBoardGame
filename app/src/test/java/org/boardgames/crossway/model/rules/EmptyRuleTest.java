package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.EmptyRule;
import org.boardgames.crossway.model.rules.PatternViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link EmptyRule} class.
 * <p>
 * This class tests the validation logic of the EmptyRule, which ensures that a
 * stone can only be placed on an empty cell on the board.
 * </p>
 */
public class EmptyRuleTest {

    /**
     * Tests that the {@link EmptyRule#validate(Board, Move)} method returns an
     * empty {@link Optional} when the target cell is empty.
     * <p>
     * This represents a valid move according to this rule.
     * </p>
     */
    @Test
    @DisplayName("validate passes when target cell is empty")
    void testEmptyCell() {
        Board board = new Board(new BoardSize(3));
        Move move = new Move(new Point(1, 1), Stone.BLACK);
        EmptyRule rule = new EmptyRule();
        assertTrue(rule.validate(board, move).isEmpty(),
                "Placing on empty cell should be allowed");
    }

    /**
     * Tests that the {@link EmptyRule#validate(Board, Move)} method returns a
     * {@link PatternViolation} when the target cell is already occupied by a stone.
     * <p>
     * This represents an invalid move that should be flagged as a violation.
     * </p>
     */
    @Test
    @DisplayName("validate reports violation when cell is occupied")
    void testOccupiedCell() {
        Board board = new Board(new BoardSize(3));
        Point p = new Point(1, 1);
        board.placeStone(p, Stone.WHITE);
        Move move = new Move(p, Stone.BLACK);
        EmptyRule rule = new EmptyRule();
        Optional<PatternViolation> violation = rule.validate(board, move);
        assertTrue(violation.isPresent(), "Occupied cell should violate empty rule");
        assertEquals("EmptyRule", violation.get().ruleName(),
                "Violation should come from EmptyRule");
    }
}