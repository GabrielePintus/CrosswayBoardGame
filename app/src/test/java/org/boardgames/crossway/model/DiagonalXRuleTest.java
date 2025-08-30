package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.DiagonalXRule;
import org.boardgames.crossway.model.rules.PatternChecker;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DiagonalXRuleTest {
    Board board;
    PatternChecker checker;

    @BeforeEach
    void setUp() {
        board = new Board(new BoardSize(6));
        checker = new PatternChecker(List.of(new DiagonalXRule()));
    }

    @Test
    void forbidsDiagonalX() {
        // 2x2 at (2,2) top-left. We'll complete an X for BLACK at (3,3).
        board.placeStone(new Point(2,2), Stone.BLACK);
        board.placeStone(new Point(3,2), Stone.WHITE);
        board.placeStone(new Point(2,3), Stone.WHITE);
        Move m = new Move(new Point(3,3), Stone.BLACK);

        assertTrue(checker.firstViolation(board, m).isPresent(),
                "Completing the diagonal X must be forbidden");
    }

    @Test
    void allowsWhenBlockNotFull() {
        // Missing one corner → no violation
        board.placeStone(new Point(2,2), Stone.BLACK);
        board.placeStone(new Point(3,2), Stone.WHITE);
        Move m = new Move(new Point(3,3), Stone.BLACK);

        assertTrue(checker.firstViolation(board, m).isEmpty(),
                "Not a full 2x2 block → allowed");
    }

    @Test
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
