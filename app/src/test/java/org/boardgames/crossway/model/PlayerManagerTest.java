package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PlayerManager} class.
 * This class verifies the correct behavior of player management,
 * including score tracking and player state changes.
 */
class PlayerManagerTest {

    /**
     * Tests that the {@link PlayerManager#recordWin(Stone)} method
     * correctly increments the win count for the specified player,
     * while leaving the other player's score unchanged.
     */
    @Test
    @DisplayName("recordWin increments the correct player's win count")
    void recordWinIncrementsCorrectPlayer() {
        PlayerManager pm = new PlayerManager("A", "B");
        pm.recordWin(Stone.BLACK);
        assertEquals(1, pm.getPlayer(Stone.BLACK).getWins());
        assertEquals(0, pm.getPlayer(Stone.WHITE).getWins());
    }

    /**
     * Tests that the {@link PlayerManager#resetScores()} method
     * correctly sets the win count for both players back to zero,
     * regardless of their previous scores.
     */
    @Test
    @DisplayName("resetScores clears win counts for both players")
    void resetScoresClearsBothPlayers() {
        PlayerManager pm = new PlayerManager("A", "B");
        pm.recordWin(Stone.BLACK);
        pm.recordWin(Stone.WHITE);
        pm.resetScores();
        assertEquals(0, pm.getPlayer(Stone.BLACK).getWins());
        assertEquals(0, pm.getPlayer(Stone.WHITE).getWins());
    }
}