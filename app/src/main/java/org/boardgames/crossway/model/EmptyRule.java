package org.boardgames.crossway.model;

import java.util.Optional;

public class EmptyRule implements PatternRule {

    @Override
    public Optional<PatternViolation> validate(Board board, Move move) {
        Point point = move.getPoint();
        if (!board.isEmpty(point)) {
            return Optional.of(new PatternViolation("EmptyRule", "Position already occupied", point));
        }
        return Optional.empty();
    }
}