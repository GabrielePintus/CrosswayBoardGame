package org.boardgames.crossway.model;


import java.util.*;


public final class DiagonalXRule implements PatternRule {

    @Override
    /**
     * Validates a move against the diagonal X rule.
     * A violation occurs if a 2x2 block is formed with stones in a forbidden diagonal pattern.
     *
     * @param board the current game board
     * @param move the move to validate
     * @return an Optional containing a PatternViolation if the rule is broken, otherwise empty
     */
    public Optional<PatternViolation> validate(Board board, Move move) {
        final Point placed = move.getPoint();
        final Stone stone  = move.getStone();
        final Stone other  = stone.opposite();

        // Lookup that treats (placed, stone) as already on the board.
        var at = (java.util.function.Function<Point, Optional<Stone>>) p ->
                p.equals(placed) ? Optional.of(stone) : board.stoneAt(p);

        for (Point tl : Squares.topLeftsAround(board, placed)) {
            // Build 2×2 block
            int x = tl.x(), y = tl.y();
            Point p00 = tl;
            Point p01 = new Point(x,     y + 1);
            Point p10 = new Point(x + 1, y);
            Point p11 = new Point(x + 1, y + 1);

            // Ensure all 4 cells exist and are occupied
            if (!board.isOnBoard(p00) || !board.isOnBoard(p01) ||
                    !board.isOnBoard(p10) || !board.isOnBoard(p11)) continue;

            var s00 = at.apply(p00);
            var s01 = at.apply(p01);
            var s10 = at.apply(p10);
            var s11 = at.apply(p11);
            if (s00.isEmpty() || s01.isEmpty() || s10.isEmpty() || s11.isEmpty()) continue;

            var v00 = s00.get(); var v01 = s01.get();
            var v10 = s10.get(); var v11 = s11.get();

            // Forbidden diagonal “X”
            boolean x1 = (v00 == stone && v11 == stone && v01 == other && v10 == other);
            boolean x2 = (v01 == stone && v10 == stone && v00 == other && v11 == other);
            if (x1 || x2) {
                return Optional.of(new PatternViolation(
                        "DiagonalXRule",
                        "2×2 diagonal X is forbidden",
                        tl
                ));
            }
        }
        return Optional.empty();
    }
}