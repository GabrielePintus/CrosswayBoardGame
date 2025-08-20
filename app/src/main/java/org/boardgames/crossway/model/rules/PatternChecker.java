package org.boardgames.crossway.model.rules;


import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Move;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PatternChecker {
    private final List<PatternRule> rules;

    public PatternChecker(List<PatternRule> rules) {
        this.rules = List.copyOf(rules);
    }

    /**
     * Checks all registered rules and returns the first violation found for a move.
     *
     * @param board the game board
     * @param move the move to validate
     * @return an Optional containing the first PatternViolation found, or empty if none
     */
    public Optional<PatternViolation> firstViolation(Board board, Move move) {
        return rules.stream()
                .map(r -> r.validate(board, move))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    /**
     * Determines whether a move is allowed based on all registered pattern rules.
     *
     * @param board the game board
     * @param move the move to validate
     * @return true if the move does not violate any rule, false otherwise
     */
    public boolean isAllowed(Board board, Move move) {
        return firstViolation(board, move).isEmpty();
    }

    /**
     * Returns a list of all pattern violations for the given move.
     *
     * @param board the game board
     * @param move the move to validate
     * @return a list of all PatternViolation objects for the move
     */
    public List<PatternViolation> allViolations(Board board, Move move) {
        return rules.stream()
                .flatMap(r -> r.validate(board, move).stream())
                .collect(Collectors.toList());
    }
}