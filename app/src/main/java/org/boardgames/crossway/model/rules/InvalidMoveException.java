package org.boardgames.crossway.model.rules;


/**
 * An exception thrown to indicate that a requested move is illegal according to the game's rules.
 *
 * <p>This custom exception extends {@link IllegalArgumentException} and is used by the
 * game logic to signal that a proposed move violates one or more of the game's
 * {@link PatternRule rules}. It encapsulates a {@link PatternViolation} object
 * which provides specific details about the nature and location of the rule
 * infringement.</p>
 *
 * <p>The exception's message is automatically generated from the violation's details,
 * making it ready for display to the user or for logging purposes.</p>
 *
 * @author Gabriele Pintus
 */
public final class InvalidMoveException extends IllegalArgumentException {

    /**
     * The specific violation that caused this exception.
     */
    private final PatternViolation violation;

    /**
     * Constructs a new {@code InvalidMoveException} with details from a
     * {@link PatternViolation}.
     *
     * @param violation The specific rule violation that occurred.
     */
    public InvalidMoveException(PatternViolation violation) {
        // Construct the exception message using the violation's details.
        super(violation.message() + " at " + violation.at());
        this.violation = violation;
    }

    /**
     * Retrieves the {@link PatternViolation} object that provides the specific
     * details about why the move was invalid.
     *
     * @return The encapsulated {@link PatternViolation}.
     */
    public PatternViolation getViolation() {
        return violation;
    }
}

