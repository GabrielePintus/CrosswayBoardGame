package org.boardgames.crossway.model;

/**
 * Manages the two players of the game, their colors and scores.
 * <p>
 * This class handles player-specific logic, such as updating names,
 * swapping colors, recording wins, and resetting scores. It provides a
 * centralized place to manage player data.
 * </p>
 */
public class PlayerManager {
    /** The first player instance, initially associated with the black stones. */
    private final Player playerOne;
    /** The second player instance, initially associated with the white stones. */
    private final Player playerTwo;

    /**
     * Constructs a new PlayerManager with the initial names for the two players.
     *
     * @param blackName The name for the player who will start with black stones.
     * @param whiteName The name for the player who will start with white stones.
     */
    public PlayerManager(String blackName, String whiteName) {
        this.playerOne = new Player(blackName, Stone.BLACK);
        this.playerTwo = new Player(whiteName, Stone.WHITE);
    }

    /**
     * Updates the names of the current black and white players and resets their scores.
     *
     * @param blackName The new name for the player with black stones.
     * @param whiteName The new name for the player with white stones.
     */
    public void setPlayers(String blackName, String whiteName) {
        getPlayer(Stone.BLACK).setName(blackName);
        getPlayer(Stone.WHITE).setName(whiteName);
        resetScores();
    }

    /**
     * Returns the player associated with the specified stone color.
     *
     * @param color The {@link Stone} color of the player to retrieve.
     * @return The {@link Player} instance for the given color.
     */
    public Player getPlayer(Stone color) {
        return playerOne.getColor() == color ? playerOne : playerTwo;
    }

    /**
     * Swaps the stone colors between the two players.
     */
    public void swapColors() {
        Stone temp = playerOne.getColor();
        playerOne.setColor(playerTwo.getColor());
        playerTwo.setColor(temp);
    }

    /**
     * Records a win for the player with the specified stone color by incrementing their win count.
     *
     * @param color The {@link Stone} color of the player who won the game.
     */
    public void recordWin(Stone color) {
        getPlayer(color).incrementWins();
    }

    /**
     * Resets the win count for both players to zero.
     */
    public void resetScores() {
        playerOne.resetWins();
        playerTwo.resetWins();
    }
}
