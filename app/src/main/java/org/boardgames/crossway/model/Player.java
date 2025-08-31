package org.boardgames.crossway.model;

/**
 * Represents a player in the game with a name, stone color, and win counter.
 */
public class Player {
    /** The player's name. */
    private String name;
    /** The color of the stones the player is using. */
    private Stone color;
    /** The number of games the player has won. */
    private int wins;

    /**
     * Constructs a new Player with the specified name and stone color.
     *
     * @param name The player's name.
     * @param color The color of the player's stones.
     */
    public Player(String name, Stone color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Returns the player's name.
     *
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the player's name.
     *
     * @param name The new name for the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the player's stone color.
     *
     * @return The {@link Stone} color of the player.
     */
    public Stone getColor() {
        return color;
    }

    /**
     * Sets the player's stone color.
     *
     * @param color The new {@link Stone} color for the player.
     */
    public void setColor(Stone color) {
        this.color = color;
    }

    /**
     * Returns the number of wins for the player.
     *
     * @return The number of wins.
     */
    public int getWins() {
        return wins;
    }

    /**
     * Increments the player's win count by one.
     */
    public void incrementWins() {
        wins++;
    }

    /**
     * Resets the player's win count to zero.
     */
    public void resetWins() {
        wins = 0;
    }
}
