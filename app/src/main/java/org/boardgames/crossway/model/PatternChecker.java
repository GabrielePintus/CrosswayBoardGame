package org.boardgames.crossway.model;

import java.util.*;

public final class PatternChecker {
    private final List<PatternRule> rules;

    public PatternChecker(List<PatternRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public Optional<PatternViolation> firstViolation(Board board, Move move) {
        for (PatternRule r : rules) {
            var v = r.validate(board, move);
            if (v.isPresent()) return v;
        }
        return Optional.empty();
    }

    public boolean isAllowed(Board board, Move move) {
        return firstViolation(board, move).isEmpty();
    }

    public List<PatternViolation> allViolations(Board board, Move move) {
        List<PatternViolation> out = new ArrayList<>();
        for (PatternRule r : rules) r.validate(board, move).ifPresent(out::add);
        return out;
    }
}