package org.boardgames.crossway.model;

/**
 * Manages the two players of the game, their colors and scores.
 */
public class PlayerManager {
    private final Player playerOne; // initially black
    private final Player playerTwo; // initially white

    public PlayerManager(String blackName, String whiteName) {
        this.playerOne = new Player(blackName, Stone.BLACK);
        this.playerTwo = new Player(whiteName, Stone.WHITE);
    }

    /**
     * Updates player names (for current black and white players) and resets their scores.
     */
    public void setPlayers(String blackName, String whiteName) {
        getPlayer(Stone.BLACK).setName(blackName);
        getPlayer(Stone.WHITE).setName(whiteName);
        resetScores();
    }

    public Player getPlayer(Stone color) {
        return playerOne.getColor() == color ? playerOne : playerTwo;
    }

    public void swapColors() {
        Stone temp = playerOne.getColor();
        playerOne.setColor(playerTwo.getColor());
        playerTwo.setColor(temp);
    }

    public void recordWin(Stone color) {
        getPlayer(color).incrementWins();
    }

    public void resetScores() {
        playerOne.resetWins();
        playerTwo.resetWins();
    }
}