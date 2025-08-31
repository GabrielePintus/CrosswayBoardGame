package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Player;
import org.boardgames.crossway.model.PlayerManager;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.utils.Messages;

import javax.swing.*;

/**
 * Controller responsible for managing player information and the scoreboard display.
 */
public class ScoreboardController {

    /** Manages player names, colors and scores. */
    private final PlayerManager playerManager;
    /** Label displayed in the menu bar showing the current scoreboard. */
    private final JLabel scoreboardLabel;
    /** Handler for dialog interactions, such as warnings and errors. */
    private final DialogHandler dialogHandler;

    /**
     * Constructs a new controller with initial player names.
     *
     * @param blackName Name of the black player.
     * @param whiteName Name of the white player.
     * @param label     The label used to display the scoreboard.
     * @param dialogHandler Handler used for prompting the user when player names change.
     */
    public ScoreboardController(String blackName,
                                String whiteName,
                                JLabel label,
                                DialogHandler dialogHandler) {
        this.playerManager = new PlayerManager(blackName, whiteName);
        this.scoreboardLabel = label;
        this.dialogHandler = dialogHandler;
        refreshScoreboard();
    }

    /** Returns the label displaying the scoreboard. */
    public JLabel getScoreboardLabel() {
        return scoreboardLabel;
    }

    /** Handles a request to change player names and reset their scores. */
    public void handleChangePlayersRequest() {
        String[] names = dialogHandler.askPlayerNames();
        playerManager.setPlayers(names[0], names[1]);
        refreshScoreboard();
    }

    /** Records a win for the player currently using the given stone. */
    public void recordWin(Stone stone) {
        playerManager.recordWin(stone);
        refreshScoreboard();
    }

    /** Swaps player colors. */
    public void swapPlayerColors() {
        playerManager.swapColors();
        refreshScoreboard();
    }

    /** Resets both players' scores. */
    public void resetScores() {
        playerManager.resetScores();
        refreshScoreboard();
    }

    /** Returns the player associated with the specified stone. */
    public Player getPlayer(Stone stone) {
        return playerManager.getPlayer(stone);
    }

    /** Updates the scoreboard label with current names and scores. */
    public void refreshScoreboard() {
        Player black = playerManager.getPlayer(Stone.BLACK);
        Player white = playerManager.getPlayer(Stone.WHITE);
        scoreboardLabel.setText(Messages.format(
                "scoreboard.format",
                black.getName(), black.getWins(),
                white.getName(), white.getWins()));
    }
}


