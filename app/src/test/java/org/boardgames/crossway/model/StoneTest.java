package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Stone enum.
 */
public class StoneTest {

    @Test
    @DisplayName("opposite of BLACK is WHITE")
    void testOppositeBlack() {
        assertEquals(Stone.WHITE, Stone.BLACK.opposite(), "opposite of BLACK should be WHITE");
    }

    @Test
    @DisplayName("opposite of WHITE is BLACK")
    void testOppositeWhite() {
        assertEquals(Stone.BLACK, Stone.WHITE.opposite(), "opposite of WHITE should be BLACK");
    }

    @Test
    @DisplayName("getStoneName returns the name")
    void testGetStoneName() {
        assertEquals("BLACK", Stone.BLACK.getStoneName(), "getStoneName for BLACK should return 'BLACK'");
        assertEquals("WHITE", Stone.WHITE.getStoneName(), "getStoneName for WHITE should return 'WHITE'");
    }
}