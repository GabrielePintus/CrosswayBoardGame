package org.boardgames.crossway.model;

public class InvalidMoveException extends IllegalArgumentException {

    private final PatternViolation violation;

    public InvalidMoveException(PatternViolation violation) {
        super(violation.message() + " at " + violation.at());
        this.violation = violation;
    }

    public PatternViolation getViolation() {
        return violation;
    }
}