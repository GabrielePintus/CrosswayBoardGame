package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Stone} enum.
 * This class verifies the correctness of the enum's behavior, including
 * the `opposite` and `getStoneName` methods.
 */
class StoneTest {

    /**
     * Tests that the opposite of a BLACK stone is correctly returned as WHITE.
     */
    @Test
    @DisplayName("opposite of BLACK is WHITE")
    void testOppositeBlack() {
        assertEquals(Stone.WHITE, Stone.BLACK.opposite(), "opposite of BLACK should be WHITE");
    }

    /**
     * Tests that the opposite of a WHITE stone is correctly returned as BLACK.
     */
    @Test
    @DisplayName("opposite of WHITE is BLACK")
    void testOppositeWhite() {
        assertEquals(Stone.BLACK, Stone.WHITE.opposite(), "opposite of WHITE should be BLACK");
    }

    /**
     * Tests that the {@code getStoneName} method returns the correct string
     * representation for both BLACK and WHITE stones.
     */
    @Test
    @DisplayName("getStoneName returns the name")
    void testGetStoneName() {
        assertEquals("BLACK", Stone.BLACK.getStoneName(), "getStoneName for BLACK should return 'BLACK'");
        assertEquals("WHITE", Stone.WHITE.getStoneName(), "getStoneName for WHITE should return 'WHITE'");
    }
}
