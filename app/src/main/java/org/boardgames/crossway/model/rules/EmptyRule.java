package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Point;

import java.util.Optional;

/**
 * A rule that validates if a move is made on an empty cell.
 *
 * <p>This rule is fundamental to turn-based board games where a player
 * can only place a piece on an unoccupied spot. It checks the target
 * coordinates of a move and returns a violation if a stone already exists
 * at that location.</p>
 *
 * @author Gabriele Pintus
 */
public final class EmptyRule implements PatternRule {

    /**
     * Validates if the point of the given move is currently empty on the board.
     *
     * @param board The current state of the game board.
     * @param move The move to validate.
     * @return An {@link Optional} containing a {@link PatternViolation} if the
     * move's point is already occupied, or an empty {@code Optional} if the
     * cell is empty and the move is valid according to this rule.
     */
    @Override
    public Optional<PatternViolation> validate(Board board, Move move) {
        Point point = move.getPoint();
        if (!board.isEmpty(point)) {
            return Optional.of(new PatternViolation("EmptyRule", "Position already occupied", point));
        }
        return Optional.empty();
    }
}

