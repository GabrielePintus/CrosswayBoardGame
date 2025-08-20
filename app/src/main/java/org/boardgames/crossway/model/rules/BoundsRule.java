package org.boardgames.crossway.model.rules;



import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Point;

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