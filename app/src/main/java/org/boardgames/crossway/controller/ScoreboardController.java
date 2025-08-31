package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Player;
import org.boardgames.crossway.model.PlayerManager;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.ui.ScoreboardView;
import org.boardgames.crossway.utils.Messages;

import java.awt.Component;

/**
 * Controller responsible for managing player information and the scoreboard display.
 *
 * @author Gabriele Pintus
 * @see PlayerManager
 * @see DialogHandler
 */
public class ScoreboardController {

    /** Manages player names, colors and scores. */
    private final PlayerManager playerManager;
    /** View displayed in the menu bar showing the current scoreboard. */
    private final ScoreboardView scoreboardView;
    /** Handler for dialog interactions, such as warnings and errors. */
    private final DialogHandler dialogHandler;

    /**
     * Constructs a new controller with initial player names.
     *
     * @param blackName Name of the black player.
     * @param whiteName Name of the white player.
     * @param view      The view used to display the scoreboard.
     * @param dialogHandler Handler used for prompting the user when player names change.
     */
    public ScoreboardController(String blackName,
                                String whiteName,
                                ScoreboardView view,
                                DialogHandler dialogHandler) {
        this.playerManager = new PlayerManager(blackName, whiteName);
        this.scoreboardView = view;
        this.dialogHandler = dialogHandler;
        refreshScoreboard();
    }

    /**
     * Returns the component displaying the scoreboard
     *
     * @return the scoreboard component
     */
    public Component getScoreboardComponent() {
        return scoreboardView.getComponent();
    }


    /**
     * Handles a request to change player names and reset their scores.
     */
    public void handleChangePlayersRequest() {
        String[] names = dialogHandler.askPlayerNames();
        playerManager.setPlayers(names[0], names[1]);
        refreshScoreboard();
    }

    /**
     * Records a win for the player currently using the given stone.
     *
     * @param stone The stone color of the winning player.
     */
    public void recordWin(Stone stone) {
        playerManager.recordWin(stone);
        refreshScoreboard();
    }

    /**
     * Swaps player colors.
     */
    public void swapPlayerColors() {
        playerManager.swapColors();
        refreshScoreboard();
    }

    /**
     * Resets both players' scores.
     */
    public void resetScores() {
        playerManager.resetScores();
        refreshScoreboard();
    }

    /**
     * Returns the player associated with the specified stone.
     *
     * @param stone The stone color of the player to retrieve.
     * @return The {@link Player} instance for the given stone color.
     */
    public Player getPlayer(Stone stone) {
        return playerManager.getPlayer(stone);
    }

    /**
     * Updates the scoreboard label with current names and scores.
     */
    public void refreshScoreboard() {
        Player black = playerManager.getPlayer(Stone.BLACK);
        Player white = playerManager.getPlayer(Stone.WHITE);
        scoreboardView.update(Messages.format(
                "scoreboard.format",
                black.getName(), black.getWins(),
                white.getName(), white.getWins()));
    }
}
