package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PatternChecker} class.
 * <p>
 * This class verifies the behavior of the `PatternChecker`, which is responsible for
 * validating moves against a set of predefined game rules.
 * </p>
 */
class PatternCheckerTest {

    Board board;
    PatternChecker checker;

    /**
     * Sets up the test environment before each test.
     * <p>
     * Initializes a new 4x4 board and a `PatternChecker` with a list of rules:
     * `BoundsRule`, `EmptyRule`, and `DiagonalXRule`.
     * </p>
     */
    @BeforeEach
    void setUp() {
        board = new Board(new BoardSize(4));
        checker = new PatternChecker(List.of(new BoundsRule(), new EmptyRule(), new DiagonalXRule()));
    }

    /**
     * Tests that the {@link PatternChecker#firstViolation(Board, Move)} method
     * correctly returns the first rule violation it encounters.
     * <p>
     * In this test, an `EmptyRule` violation is expected to be found first
     * because the target cell is already occupied.
     * </p>
     */
    @Test
    @DisplayName("firstViolation returns the first failing rule")
    void testFirstViolation() {
        Point p = new Point(1, 1);
        board.placeStone(p, Stone.WHITE); // Occupy the cell to trigger EmptyRule
        Move m = new Move(p, Stone.BLACK);
        var violation = checker.firstViolation(board, m);
        assertTrue(violation.isPresent(), "Should find a violation");
        assertEquals("EmptyRule", violation.get().ruleName(), "EmptyRule should be the first violation found");
    }

    /**
     * Tests that the {@link PatternChecker#isAllowed(Board, Move)} method
     * correctly checks all rules and returns the appropriate boolean value.
     * <p>
     * One move is a valid, in-bounds move, while the other is an invalid,
     * out-of-bounds move.
     * </p>
     */
    @Test
    @DisplayName("isAllowed checks all rules")
    void testIsAllowed() {
        Move ok = new Move(new Point(2, 2), Stone.BLACK);
        assertTrue(checker.isAllowed(board, ok), "An empty in-bounds move should be allowed");
        Move bad = new Move(new Point(5, 5), Stone.WHITE);
        assertFalse(checker.isAllowed(board, bad), "An out-of-bounds move should be disallowed");
    }

    /**
     * Tests that the {@link PatternChecker#allViolations(Board, Move)} method
     * correctly collects all rule violations for a given move.
     * <p>
     * The test sets up a board state where a move violates both the `EmptyRule`
     * (the cell is occupied) and the `DiagonalXRule` (a specific pattern is
     * formed with surrounding stones).
     * </p>
     */
    @Test
    @DisplayName("allViolations collects every failing rule")
    void testAllViolations() {
        // Setup board to have an occupied cell and a diagonal X pattern around (1,1)
        board.placeStone(new Point(0, 0), Stone.BLACK);
        board.placeStone(new Point(1, 0), Stone.WHITE);
        board.placeStone(new Point(0, 1), Stone.WHITE);
        board.placeStone(new Point(1, 1), Stone.WHITE); // Occupied with opposite color

        Move m = new Move(new Point(1, 1), Stone.BLACK);
        List<PatternViolation> violations = checker.allViolations(board, m);
        assertEquals(2, violations.size(), "Should report both EmptyRule and DiagonalXRule violations");
        assertTrue(violations.stream().anyMatch(v -> v.ruleName().equals("EmptyRule")),
                "EmptyRule should be among the violations");
        assertTrue(violations.stream().anyMatch(v -> v.ruleName().equals("DiagonalXRule")),
                "DiagonalXRule should be among the violations");
    }
}