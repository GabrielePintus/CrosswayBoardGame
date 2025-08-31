package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.PatternViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PatternViolation} record.
 * <p>
 * This class verifies that the immutable `PatternViolation` record correctly
 * stores and provides access to the values passed to its constructor.
 * </p>
 */
public class PatternViolationTest {

    /**
     * Tests that the `PatternViolation` record correctly stores the provided
     * rule name, message, and point.
     * <p>
     * This test ensures that the accessor methods (`ruleName()`, `message()`,
     * and `at()`) return the same values that were used to create the record.
     * </p>
     */
    @Test
    @DisplayName("Record stores provided values and provides accessors")
    void testRecordValues() {
        // Create a point and an instance of PatternViolation.
        Point p = new Point(2, 3);
        PatternViolation violation = new PatternViolation("Rule", "Message", p);

        // Use assertions to verify that the accessor methods return the expected values.
        assertEquals("Rule", violation.ruleName(), "ruleName() should match the constructor's value");
        assertEquals("Message", violation.message(), "message() should match the constructor's value");
        assertEquals(p, violation.at(), "at() should match the constructor's Point object");
    }
}