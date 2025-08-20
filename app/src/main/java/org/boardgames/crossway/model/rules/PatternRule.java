package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Move;

import java.util.Optional;

public interface PatternRule {
    /** Return a violation if this move breaks the rule (treating the move as hypothetically placed). */
    Optional<PatternViolation> validate(Board board, Move move);
}
