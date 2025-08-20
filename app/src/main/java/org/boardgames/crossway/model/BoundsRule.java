package org.boardgames.crossway.model;



import java.util.Optional;

public class BoundsRule implements PatternRule {

    @Override
    public Optional<PatternViolation> validate(Board board, Move move) {
        Point point = move.getPoint();
        if (!board.isOnBoard(point)) {
            return Optional.of(new PatternViolation("BoundsRule", "Position out of bounds", point));
        }
        return Optional.empty();
    }
}