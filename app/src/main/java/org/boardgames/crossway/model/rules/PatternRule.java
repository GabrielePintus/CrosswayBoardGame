package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Move;

import java.util.Optional;

/**
 * An interface for a rule that validates a proposed move in the Crossway game.
 *
 * <p>Each implementation of this interface represents a single, self-contained rule
 * of the game. The primary method, {@code validate}, checks if a move conforms to
 * a specific pattern or condition. This design allows for a modular and extensible
 * rule-checking system, where rules can be added, removed, or combined without
 * affecting the core game logic.</p>
 *
 * <p>The rules are validated on a "what-if" basis; the {@code validate} method
 * assumes the move has already been made and checks for any resulting illegal
 * patterns. If a violation is found, it returns an {@link Optional} containing
 * a {@link PatternViolation} object with details about the rule broken and the
 * location of the infringement. If the move is valid with respect to the rule,
 * an empty {@code Optional} is returned.</p>
 *
 * @author Gabriele Pintus
 */
public interface PatternRule {

    /**
     * Validates a hypothetical move against a specific game rule.
     *
     * <p>This method checks for a violation by analyzing the board state as if
     * the specified move has just been completed. It should not modify the board
     * state itself.</p>
     *
     * @param board The current game board.
     * @param move  The proposed move to validate.
     * @return An {@link Optional} containing a {@link PatternViolation} if the
     * move breaks the rule, or an empty {@code Optional} if the move is valid.
     */
    Optional<PatternViolation> validate(Board board, Move move);
}

