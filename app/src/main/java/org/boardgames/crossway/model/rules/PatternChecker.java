package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Move;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A utility class for validating game moves against a set of predefined rules.
 *
 * <p>This class acts as an aggregator for a collection of {@link PatternRule} instances.
 * It provides a clean interface for checking a move against all registered rules,
 * returning either the first rule that is violated or a comprehensive list of all
 * violations. This separation of concerns allows for a flexible and extensible
 * rule-checking system, where new rules can be added simply by providing a new
 * implementation of the {@code PatternRule} interface.</p>
 *
 * @author Gabriele Pintus
 */
public final class PatternChecker {

    /**
     * An immutable list of all rules that a move must satisfy.
     */
    private final List<PatternRule> rules;

    /**
     * Constructs a {@code PatternChecker} with a specified list of rules.
     * The list is copied to ensure immutability.
     *
     * @param rules The list of {@link PatternRule} objects to check against.
     */
    public PatternChecker(List<PatternRule> rules) {
        this.rules = List.copyOf(rules);
    }

    /**
     * Checks all registered rules and returns the first violation found for a move.
     *
     * <p>This method is efficient for standard game validation, as it stops
     * checking as soon as the first rule is broken. This is useful for quickly
     * determining if a move is invalid without needing to find all reasons.</p>
     *
     * @param board The current state of the game board.
     * @param move  The move to validate.
     * @return An {@link Optional} containing the first {@link PatternViolation}
     * found, or an empty {@code Optional} if the move is valid against all rules.
     */
    public Optional<PatternViolation> firstViolation(Board board, Move move) {
        return rules.stream()
                .map(rule -> rule.validate(board, move))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    /**
     * Determines whether a move is allowed based on all registered pattern rules.
     *
     * <p>This is a convenience method that simply checks if {@code firstViolation}
     * returns an empty {@code Optional}. It's suitable for a simple boolean check
     * of move legality.</p>
     *
     * @param board The current state of the game board.
     * @param move  The move to validate.
     * @return {@code true} if the move does not violate any rule, {@code false} otherwise.
     */
    public boolean isAllowed(Board board, Move move) {
        return firstViolation(board, move).isEmpty();
    }

    /**
     * Returns a list of all pattern violations for the given move.
     *
     * <p>Unlike {@code firstViolation}, this method continues to check all rules
     * and collects every single violation. This can be useful for providing
     * comprehensive feedback to a user about all the reasons a move is invalid.</p>
     *
     * @param board The current state of the game board.
     * @param move  The move to validate.
     * @return A {@link List} of all {@link PatternViolation} objects for the move.
     * The list is empty if no rules are violated.
     */
    public List<PatternViolation> allViolations(Board board, Move move) {
        return rules.stream()
                .flatMap(rule -> rule.validate(board, move).stream())
                .collect(Collectors.toList());
    }
}

