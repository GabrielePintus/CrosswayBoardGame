package org.boardgames.crossway.model;

import java.util.*;

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
        for (PatternRule r : rules) {
            var v = r.validate(board, move);
            if (v.isPresent()) return v;
        }
        return Optional.empty();
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
        List<PatternViolation> out = new ArrayList<>();
        for (PatternRule r : rules) r.validate(board, move).ifPresent(out::add);
        return out;
    }
}