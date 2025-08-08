package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Stone enum.
 */
public class StoneTest {

    @Test
    @DisplayName("Opposite of BLACK is WHITE and vice versa")
    void testOpposite() {
        assertEquals(Stone.WHITE, Stone.BLACK.opposite(), "Opposite of BLACK should be WHITE");
        assertEquals(Stone.BLACK, Stone.WHITE.opposite(), "Opposite of WHITE should be BLACK");
    }

    @Test
    @DisplayName("Opposite of opposite returns original stone")
    void testDoubleOpposite() {
        assertEquals(Stone.BLACK, Stone.BLACK.opposite().opposite(), "opposite(opposite(WHITE)) should be WHITE");
        assertEquals(Stone.WHITE, Stone.WHITE.opposite().opposite(), "opposite(opposite(BLACK)) should be BLACK");
    }
}