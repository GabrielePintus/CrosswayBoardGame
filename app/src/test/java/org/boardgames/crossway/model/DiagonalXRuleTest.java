package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.DiagonalXRule;
import org.boardgames.crossway.model.rules.PatternChecker;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DiagonalXRule} which is part of the game's rules engine.
 * These tests ensure that the rule correctly identifies and forbids moves that would
 * create a 2x2 square with alternating stones in a diagonal 'X' pattern.
 */
class DiagonalXRuleTest {
    Board board;
    PatternChecker checker;

    /**
     * Initializes a new board and a {@link PatternChecker} with the {@link DiagonalXRule}
     * before each test method runs.
     */
    @BeforeEach
    void setUp() {
        board = new Board(new BoardSize(6));
        checker = new PatternChecker(List.of(new DiagonalXRule()));
    }

    /**
     * Tests that the {@link DiagonalXRule} correctly forbids a move that completes
     * a 2x2 square with a diagonal 'X' pattern of stones.
     */
    @Test
    @DisplayName("forbidsDiagonalX")
    void forbidsDiagonalX() {
        // 2x2 at (2,2) top-left. We'll complete an X for BLACK at (3,3).
        board.placeStone(new Point(2,2), Stone.BLACK);
        board.placeStone(new Point(3,2), Stone.WHITE);
        board.placeStone(new Point(2,3), Stone.WHITE);
        Move m = new Move(new Point(3,3), Stone.BLACK);

        assertTrue(checker.firstViolation(board, m).isPresent(),
                "Completing the diagonal X must be forbidden");
    }

    /**
     * Tests that the {@link DiagonalXRule} allows a move when the 2x2 square is not
     * fully formed (i.e., one of the four corners is empty).
     */
    @Test
    @DisplayName("allowsWhenBlockNotFull")
    void allowsWhenBlockNotFull() {
        // Missing one corner -> no violation
        board.placeStone(new Point(2,2), Stone.BLACK);
        board.placeStone(new Point(3,2), Stone.WHITE);
        Move m = new Move(new Point(3,3), Stone.BLACK);

        assertTrue(checker.firstViolation(board, m).isEmpty(),
                "Not a full 2x2 block -> allowed");
    }

    /**
     * Tests that the {@link DiagonalXRule} only checks for the pattern in squares
     * immediately adjacent to the proposed move. It should not check for patterns
     * that are far away from the move's point.
     */
    @Test
    @DisplayName("checksOnlyNearbySquares")
    void checksOnlyNearbySquares() {
        // Put a diagonal X far from the placed point; rule should not trigger
        board.placeStone(new Point(0,0), Stone.BLACK);
        board.placeStone(new Point(1,1), Stone.BLACK);
        board.placeStone(new Point(0,1), Stone.WHITE);
        // Leave (1,0) empty so it's not a full 2x2 anyway
        Move m = new Move(new Point(5,5), Stone.WHITE);

        assertTrue(checker.firstViolation(board, m).isEmpty(),
                "Placement far away must not scan unrelated squares");
    }
}