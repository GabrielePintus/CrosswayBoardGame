package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.*;

import java.util.*;
import java.util.function.Function;

/**
 * Validates a move against the "Diagonal X" rule.
 *
 * <p>This rule, also known as the "Crosscut Rule", is a crucial constraint in
 * the Crossway game. It forbids the formation of a 2x2 block of stones where
 * two opposite corners are occupied by one player and the other two corners
 * are occupied by the opponent. Such a configuration would create a diagonal
 * "X" pattern, which is considered an illegal move. The validation check
 * is performed relative to the newly placed stone, examining all possible
 * 2x2 squares that the new stone could complete.</p>
 *
 * @author Gabriele Pintus
 */
public final class DiagonalXRule implements PatternRule {

    /**
     * Validates a move against the diagonal X rule.
     *
     * <p>The method checks all four potential 2x2 squares that the new stone's
     * placement could form. For each potential square, it verifies if all four
     * cells are occupied and if they form the forbidden diagonal "X" pattern.
     * The new stone is treated as already on the board for this check.</p>
     *
     * @param board The current state of the game board.
     * @param move  The move to validate, containing the point and stone color.
     * @return An {@link Optional} containing a {@link PatternViolation} if the rule
     * is broken by the move, or an empty {@code Optional} if the move is valid
     * with respect to this rule.
     */
    @Override
    public Optional<PatternViolation> validate(Board board, Move move) {
        final Point placed = move.getPoint();
        final Stone stone  = move.getStone();
        final Stone other  = stone.opposite();

        // A helper function to look up stones, treating the new move's point
        // as if it is already occupied.
        Function<Point, Optional<Stone>> at = p ->
                p.equals(placed) ? Optional.of(stone) : board.stoneAt(p);

        // Check all four possible 2x2 top-left corners that the new stone
        // could be a part of.
        for (Point tl : Squares.topLeftsAround(board, placed)) {
            // Define the four corners of the 2x2 square.
            int x = tl.x(), y = tl.y();
            Point p00 = tl;
            Point p01 = new Point(x,     y + 1);
            Point p10 = new Point(x + 1, y);
            Point p11 = new Point(x + 1, y + 1);

            // Ensure all four points of the square are within the board's bounds.
            if (!board.isOnBoard(p00) || !board.isOnBoard(p01) ||
                    !board.isOnBoard(p10) || !board.isOnBoard(p11)) {
                continue;
            }

            // Get the stones at each corner, using the 'at' helper function.
            var s00 = at.apply(p00);
            var s01 = at.apply(p01);
            var s10 = at.apply(p10);
            var s11 = at.apply(p11);

            // Check if all four points are occupied.
            if (s00.isEmpty() || s01.isEmpty() || s10.isEmpty() || s11.isEmpty()) {
                continue;
            }

            // Extract the stone values.
            var v00 = s00.get(); var v01 = s01.get();
            var v10 = s10.get(); var v11 = s11.get();

            // Check for the forbidden diagonal "X" pattern.
            // Pattern 1: Same player's stones at top-left and bottom-right.
            boolean x1 = (v00 == stone && v11 == stone && v01 == other && v10 == other);
            // Pattern 2: Same player's stones at top-right and bottom-left.
            boolean x2 = (v01 == stone && v10 == stone && v00 == other && v11 == other);

            if (x1 || x2) {
                return Optional.of(new PatternViolation(
                        "DiagonalXRule",
                        "A 2x2 diagonal X pattern is forbidden.",
                        tl
                ));
            }
        }
        return Optional.empty();
    }
}

