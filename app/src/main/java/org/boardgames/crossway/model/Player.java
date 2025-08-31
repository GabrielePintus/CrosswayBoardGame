package org.boardgames.crossway.model;

/**
 * Represents a player in the game with a name, stone color and win counter.
 */
public class Player {
    private String name;
    private Stone color;
    private int wins;

    public Player(String name, Stone color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stone getColor() {
        return color;
    }

    public void setColor(Stone color) {
        this.color = color;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        wins++;
    }

    public void resetWins() {
        wins = 0;
    }
}