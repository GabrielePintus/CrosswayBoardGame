package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Game;
import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.utils.Messages;


/**
 * Controller responsible for mediating interactions with the {@link Game} model.
 * <p>
 * It contains the move-processing logic that was previously embedded in
 * {@link CrosswayController}. User interactions coming from the UI are
 * translated here into model operations. Any UI updates are delegated back via
 * the {@link GameEvents} interface.
 * </p>
 *
 * @see Game
 * @see GameEvents
 */
public class GameController {

    /** The current game model instance. */
    private Game game;
    /** Callback target for all UI updates and dialogs. */
    private final GameEvents gameEvents;
    /** A callback to be executed when a new game is requested. */
    private final Runnable newGameCallback;
    /** A callback to be executed when a game restart is requested. */
    private final Runnable restartCallback;
    /** A callback to be executed when the application exit is requested. */
    private final Runnable exitCallback;

    /** Handles scoreboard operations and player management. */
    private final ScoreboardController scoreboardController;

    /**
     * Constructs a new {@code GameController} with the specified dependencies and callbacks.
     *
     * @param game The initial {@link Game} model instance.
     * @param gameEvents   The {@link GameEvents} implementation for UI updates and dialogs.
     * @param newGameCallback A callback for starting a new game.
     * @param restartCallback A callback for restarting the current game.
     * @param exitCallback A callback for exiting the application.
     */
    public GameController(Game game,
                          GameEvents gameEvents,
                          Runnable newGameCallback,
                          Runnable restartCallback,
                          Runnable exitCallback,
                          ScoreboardController scoreboardController) {
        this.game = game;
        this.gameEvents = gameEvents;
        this.newGameCallback = newGameCallback;
        this.restartCallback = restartCallback;
        this.exitCallback = exitCallback;
        this.scoreboardController = scoreboardController;
    }

    /**
     * Updates the underlying game reference. This is used when a new game is
     * created or imported to ensure the controller is working with the correct model.
     *
     * @param game The new {@link Game} instance to set.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Processes a board coordinate produced from a mouse click by orchestrating the
     * move execution, optional pie rule swap, and win handling.
     *
     * @param boardCoordinate The logical board position that was clicked.
     * @see Point
     */
    public void processBoardClick(Point boardCoordinate) {
        Stone currentPlayer = game.getCurrentPlayer();

        if (!game.hasLegalMove(currentPlayer)) {
            gameEvents.showInfo(
                    Messages.get("game.forfeit.title"),
                    Messages.format("game.forfeit.message", currentPlayer)
            );
            game.skipTurn();
            return;
        }

        if (!attemptMoveExecution(boardCoordinate, currentPlayer)) {
            return;
        }

        if (game.isPieAvailable() && gameEvents.showPieDialog()) {
            game.swapColors();
            scoreboardController.swapPlayerColors();
            gameEvents.refreshHistory(game.getMoveHistory());
            gameEvents.refreshWindow();
        }

        if (game.hasWon(currentPlayer)) {
            scoreboardController.recordWin(currentPlayer);
            processWinDialogChoice(gameEvents.showWinDialog(scoreboardController.getPlayer(currentPlayer)));
        }
    }

    /**
     * Attempts to execute a move on the game board. If the move is successful,
     * the move history display is updated. If the move is illegal, a warning is shown.
     *
     * @param position The board position for the move.
     * @param player The player attempting the move.
     * @return {@code true} if the move was successful, {@code false} otherwise.
     */
    private boolean attemptMoveExecution(Point position, Stone player) {
        try {
            game.makeMove(new Move(position, player));
            gameEvents.refreshHistory(game.getMoveHistory());
            return true;
        } catch (IllegalArgumentException ex) {
            gameEvents.showWarning(Messages.get("error.invalidMove"), ex.getMessage());
            return false;
        }
    }

    /**
     * Processes the user's choice from the win dialog, executing the appropriate
     * callback function to either start a new game, restart the current one, or exit.
     *
     * @param choice The integer choice made by the user in the dialog.
     */
    private void processWinDialogChoice(int choice) {
        switch (choice) {
            case 0 -> newGameCallback.run();
            case 1 -> restartCallback.run();
            case 2 -> exitCallback.run();
            default -> {
                // dialog closed
            }
        }
    }

    /**
     * Handles a request to undo the last move made in the game. If no moves
     * can be undone, an informative message is displayed.
     */
    public void handleUndoRequest() {
        try {
            game.undoLastMove();
            gameEvents.refreshHistory(game.getMoveHistory());
        } catch (IllegalStateException ex) {
            gameEvents.showInfo(
                    Messages.get("menu.toolbar.undo"),
                    Messages.get("warning.toolbar.undo")
            );
        }
    }

    /**
     * Handles a request to redo the last undone move in the game. If no moves
     * can be redone, an informative message is displayed.
     */
    public void handleRedoRequest() {
        try {
            game.redoLastMove();
            gameEvents.refreshHistory(game.getMoveHistory());
        } catch (IllegalStateException ex) {
            gameEvents.showInfo(
                    Messages.get("menu.toolbar.redo"),
                    Messages.get("warning.toolbar.redo")
            );
        }
    }
}
