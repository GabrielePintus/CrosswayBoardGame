package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Point;

import java.util.Optional;

/**
 * A rule that validates if a move is within the board's boundaries.
 *
 * <p>This rule is a fundamental check to ensure that any attempted move
 * is on a valid location on the board. It prevents stones from being
 * placed outside the defined grid. This is a crucial first-step validation
 * for any move in the game.</p>
 *
 * @author Gabriele Pintus
 */
public final class BoundsRule implements PatternRule {

    /**
     * Validates if the point of the given move is on the board.
     *
     * @param board The current state of the game board.
     * @param move The move to validate.
     * @return An {@link Optional} containing a {@link PatternViolation} if the
     * move's point is out of bounds, or an empty {@code Optional} if the
     * move is valid according to this rule.
     */
    @Override
    public Optional<PatternViolation> validate(Board board, Move move) {
        Point point = move.getPoint();
        if (!board.isOnBoard(point)) {
            return Optional.of(new PatternViolation("BoundsRule", "Position out of bounds", point));
        }
        return Optional.empty();
    }
}

