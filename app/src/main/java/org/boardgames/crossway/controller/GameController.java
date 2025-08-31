package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Game;
import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.utils.Messages;

import javax.swing.*;

/**
 * Controller responsible for mediating interactions with the {@link Game} model.
 * <p>
 * It contains the move-processing logic that was previously embedded in
 * {@link CrosswayController}. User interactions coming from the UI are
 * translated here into model operations. Any UI updates are delegated back to
 * {@link UiController}.
 * </p>
 *
 * @see Game
 * @see UiController
 */
public class GameController {

    /** The current game model instance. */
    private Game game;
    /** The controller responsible for managing UI updates. */
    private final UiController uiController;
    /** The handler for displaying dialogs and alerts to the user. */
    private final DialogHandler dialogHandler;
    /** A callback to be executed when a new game is requested. */
    private final Runnable newGameCallback;
    /** A callback to be executed when a game restart is requested. */
    private final Runnable restartCallback;
    /** A callback to be executed when the application exit is requested. */
    private final Runnable exitCallback;

    /**
     * Constructs a new {@code GameController} with the specified dependencies and callbacks.
     *
     * @param game The initial {@link Game} model instance.
     * @param uiController The {@link UiController} for managing UI state.
     * @param dialogHandler The {@link DialogHandler} for displaying user dialogs.
     * @param newGameCallback A callback for starting a new game.
     * @param restartCallback A callback for restarting the current game.
     * @param exitCallback A callback for exiting the application.
     */
    public GameController(Game game,
                          UiController uiController,
                          DialogHandler dialogHandler,
                          Runnable newGameCallback,
                          Runnable restartCallback,
                          Runnable exitCallback) {
        this.game = game;
        this.uiController = uiController;
        this.dialogHandler = dialogHandler;
        this.newGameCallback = newGameCallback;
        this.restartCallback = restartCallback;
        this.exitCallback = exitCallback;
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
     * Processes a board coordinate produced from a mouse click by attempting to make a move.
     * It first checks if a legal move is available, then attempts the move,
     * and finally checks for game completion or the opportunity to perform a "pie rule" swap.
     *
     * @param boardCoordinate The logical board position that was clicked.
     * @see Point
     */
    public void processBoardClick(Point boardCoordinate) {
        Stone currentPlayer = game.getCurrentPlayer();

        if (!game.hasLegalMove(currentPlayer)) {
            dialogHandler.showInfo(
                    Messages.get("game.forfeit.title"),
                    Messages.format("game.forfeit.message", currentPlayer)
            );
            game.skipTurn();
            return;
        }

        if (attemptMoveExecution(boardCoordinate, currentPlayer)) {
            if (game.isPieAvailable()) {
                promptSwapDecision();
            }
            checkForGameCompletion(currentPlayer);
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
            uiController.updateHistoryDisplay(game.getMoveHistory());
            return true;
        } catch (IllegalArgumentException ex) {
            dialogHandler.showWarning(Messages.get("error.invalidMove"), ex.getMessage());
            return false;
        }
    }

    /**
     * Displays a dialog to the current player, prompting them to decide whether to
     * swap colors based on the "pie rule." If the player chooses to swap, the
     * game's colors are swapped, and the history display is updated.
     */
    private void promptSwapDecision() {
        int choice = JOptionPane.showOptionDialog(
                uiController.getFrame(),
                Messages.get("game.swapPrompt"),
                Messages.get("game.swap"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{Messages.get("game.swap"), Messages.get("game.continue")},
                Messages.get("game.continue")
        );
        if (choice == JOptionPane.YES_OPTION) {
            game.swapColors();
            uiController.updateHistoryDisplay(game.getMoveHistory());
        }
    }

    /**
     * Checks if the current player has won the game. If so, it displays a game-over
     * dialog with options to start a new game, restart, or exit.
     *
     * @param currentPlayer The {@link Stone} of the player to check for a win.
     */
    private void checkForGameCompletion(Stone currentPlayer) {
        if (game.hasWon(currentPlayer)) {
            int choice = JOptionPane.showOptionDialog(
                    uiController.getFrame(),
                    "%s %s".formatted(currentPlayer, Messages.get("game.wins")),
                    Messages.get("game.over"),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{
                            Messages.get("menu.game.new"),
                            Messages.get("menu.game.restart"),
                            Messages.get("menu.file.exit")
                    },
                    Messages.get("menu.game.restart")
            );
            processWinDialogChoice(choice);
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
            uiController.updateHistoryDisplay(game.getMoveHistory());
        } catch (IllegalStateException ex) {
            dialogHandler.showInfo(
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
            uiController.updateHistoryDisplay(game.getMoveHistory());
        } catch (IllegalStateException ex) {
            dialogHandler.showInfo(
                    Messages.get("menu.toolbar.redo"),
                    Messages.get("warning.toolbar.redo")
            );
        }
    }
}
