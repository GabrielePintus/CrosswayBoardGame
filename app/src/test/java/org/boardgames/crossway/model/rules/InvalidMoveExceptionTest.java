package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.InvalidMoveException;
import org.boardgames.crossway.model.rules.PatternViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link InvalidMoveException} class.
 * <p>
 * These tests ensure that the exception correctly stores and provides
 * access to the {@link PatternViolation} details and formats its message
 * as expected.
 * </p>
 */
class InvalidMoveExceptionTest {

    /**
     * Tests that the exception's message is correctly formatted with
     * details from the {@link PatternViolation} and that the
     * {@code getViolation()} method returns the correct object.
     */
    @Test
    @DisplayName("Exception message includes violation details and getViolation returns the correct object")
    void testExceptionMessageAndGetter() {
        // Create a sample PatternViolation to be wrapped by the exception.
        PatternViolation violation = new PatternViolation("SomeRule", "Not allowed", new Point(1, 2));

        // Create an instance of the exception with the violation.
        InvalidMoveException ex = new InvalidMoveException(violation);

        // Assert that the getViolation() method returns the original violation object.
        assertEquals(violation, ex.getViolation(), "getViolation should return the provided violation");

        // Assert that the exception's message is correctly formatted.
        // It should contain the violation's message and the coordinates of the invalid move.
        assertEquals("Not allowed at Point[x=1, y=2]", ex.getMessage(),
                "Exception message should include violation message and point");
    }
}