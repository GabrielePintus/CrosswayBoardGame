package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Player} class.
 * <p>
 * These tests ensure that the `Player` class correctly manages a player's
 * name, stone color, and win count.
 * </p>
 */
class PlayerTest {

    /**
     * Tests that the getter methods (`getName()`, `getColor()`, and `getWins()`)
     * correctly return the values set during object creation.
     */
    @Test
    @DisplayName("Getters return the values provided during construction")
    void testGetters() {
        Player player = new Player("Alice", Stone.BLACK);
        assertEquals("Alice", player.getName(), "getName should return the initial name");
        assertEquals(Stone.BLACK, player.getColor(), "getColor should return the initial color");
        assertEquals(0, player.getWins(), "The initial win count should be zero");
    }

    /**
     * Tests that the setter methods (`setName()` and `setColor()`) correctly
     * update the player's name and stone color.
     */
    @Test
    @DisplayName("Setters correctly update the player's name and color")
    void testSetters() {
        Player player = new Player("Bob", Stone.WHITE);
        player.setName("Robert");
        player.setColor(Stone.BLACK);
        assertEquals("Robert", player.getName(), "setName should update the player's name");
        assertEquals(Stone.BLACK, player.getColor(), "setColor should update the player's color");
    }

    /**
     * Tests that the `incrementWins()` method correctly increases the win count
     * and that the `resetWins()` method resets it to zero.
     */
    @Test
    @DisplayName("Win count methods increment and reset the win counter")
    void testWinCounting() {
        Player player = new Player("Carol", Stone.WHITE);
        player.incrementWins();
        player.incrementWins();
        assertEquals(2, player.getWins(), "incrementWins should increase the win counter by one for each call");
        player.resetWins();
        assertEquals(0, player.getWins(), "resetWins should reset the win count to zero");
    }
}