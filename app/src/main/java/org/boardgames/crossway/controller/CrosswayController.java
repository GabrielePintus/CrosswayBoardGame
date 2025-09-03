package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.ui.SwingScoreboardView;
import org.boardgames.crossway.utils.Messages;
import org.boardgames.crossway.utils.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * The main controller for the Crossway board game application, coordinating
 * the Model, View, and user interactions.
 *
 * <p>This class acts as the central hub of the MVC architecture. It processes
 * all user input from the GUI, manages the game's state through the model,
 * and updates the view to reflect the current state. It also handles
 * game-specific logic such as move validation, turn transitions, win conditions,
 * and history management (undo/redo).</p>
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 * <li>Manages game state, including player turns and win conditions.</li>
 * <li>Processes user input from mouse clicks and menu selections.</li>
 * <li>Updates the graphical user interface based on model changes.</li>
 * <li>Provides functionality for saving (exporting) and loading (importing) game states.</li>
 * <li>Manages the application's main window and its components.</li>
 * </ul>
 *
 * @author Gabriele Pintus
 */
public class CrosswayController {

    // ==================== Constants ====================

    /**
     * Predefined board size options presented to the user.
     */
    private static final String[] BOARD_SIZE_OPTIONS = Messages.getPrefixedArray("menu.game.boardSize");

    /**
     * The default index for board size selection, corresponding to "Regular".
     */
    private static final int DEFAULT_SIZE_INDEX = Integer.parseInt(Settings.get("board.defaultSizeIndex"));

    // ==================== State ====================

    /**
     * The core game model that contains the board, history, and rules.
     */
    private Game game;

    /**
     * Handles all layout and frame operations.
     */
    private final UiController uiController;

    /**
     * Handles interactions with the game model.
     */
    private final GameController gameController;

    /**
     * Handler for dialog interactions, such as warnings and errors.
     */
    private final DialogHandler dialogHandler;

    /** Prompts the user for board size selections. */
    private final BoardSizePrompt boardSizePrompt;

    /** Handles file import and export operations. */
    private final FileController fileController;

    /** Manages the scoreboard and player information. */
    private final ScoreboardController scoreboardController;

    /** Callback executed when the application exit is requested. */
    private final Runnable exitCallback;

    /**
     * The current dimension of the square board.
     */
    private int boardSize;

    // ==================== Constructors ====================

    /**
     * Constructs a new controller with the default board size (Regular).
     */
    public CrosswayController(Runnable exitCallback) {
        this(BoardSize.REGULAR.toInt(), null, exitCallback);
    }

    /**
     * Constructs a new controller with a predefined board size.
     *
     * @param size The preset {@link BoardSize} to use.
     * @param exitCallback Callback executed when the application exit is requested.
     */
    public CrosswayController(BoardSize size, Runnable exitCallback) {
        this(size.toInt(), null, exitCallback);
    }

    /**
     * Constructs a new controller with a specific board size.
     *
     * @param boardSize The dimension of the board (cells per side).
     * @throws IllegalArgumentException if the size is too small (less than 3).
     */
    public CrosswayController(int boardSize, BoardSizePrompt prompt, Runnable exitCallback) {
        validateBoardSize(boardSize);
        this.boardSize = boardSize;
        this.exitCallback = exitCallback;
        this.game = new Game(new BoardSize(boardSize));

        if (!GraphicsEnvironment.isHeadless()) {
            this.uiController = new UiController(this, game);
            this.dialogHandler = new DialogHandler(uiController.getFrame());
            uiController.setDialogHandler(dialogHandler);
            String[] names = dialogHandler.askPlayerNames();
            SwingScoreboardView scoreboardView = new SwingScoreboardView();
            this.scoreboardController = new ScoreboardController(names[0], names[1], scoreboardView, dialogHandler);
            this.fileController = new FileController(() -> game, this::updateGame, uiController, dialogHandler);
            this.gameController = new GameController(
                    game,
                    uiController,
                    this::handleNewGameRequest,
                    this::handleRestartRequest,
                    this::handleExitRequest,
                    scoreboardController);
            uiController.getFrame().setJMenuBar(MenuBarFactory.createMenuBar(this, fileController, scoreboardController));
            uiController.refreshWindow();
            attachEventHandlers();
            this.boardSizePrompt = prompt != null ? prompt : dialogHandler;
        } else {
            this.uiController = null;
            this.dialogHandler = null;
            this.fileController = null;
            this.gameController = null;
            this.scoreboardController = null;
            this.boardSizePrompt = prompt;
        }
    }

    // ==================== Initialization ====================

    /**
     * Validates that the provided board size is acceptable.
     *
     * @param size The proposed board dimension.
     * @throws IllegalArgumentException if {@code size} is less than 3.
     */
    private void validateBoardSize(int size) {
        if (size < 3) {
            throw new IllegalArgumentException(Messages.get("error.invalidBoardSize"));
        }
    }

    // ==================== Menu Bar ====================

    /**
     * Handles the request to switch the application's language.
     * This method updates the locale and refreshes the UI.
     *
     * @param newLocale The new locale to switch to.
     */
    public void handleLanguageChange(Locale newLocale) {
        // Update the Messages locale
        Messages.setLocale(newLocale);

        // Recreate the menu bar with updated text
        uiController.getFrame().setJMenuBar(MenuBarFactory.createMenuBar(this, fileController, scoreboardController));
        scoreboardController.refreshScoreboard();

        // Update history view language
        uiController.getHistoryView().updateLanguage();

        // Refresh the entire window to reflect language changes
        uiController.refreshWindow();
    }

    // ==================== Event Handling ====================

    /**
     * Attaches all necessary event listeners to the components.
     */
    private void attachEventHandlers() {
        uiController.getBoardView().setBoardClickCallback(gameController::processBoardClick);
    }

    /**
     * Injects the menu item used to toggle the history view into the UI controller.
     *
     * @param historyMenuItem the menu item created by the menu factory
     */
    public void setHistoryMenuItem(JMenuItem historyMenuItem) {
        uiController.setHistoryMenuItem(historyMenuItem);
    }

    // ==================== Menu Actions ====================

    /**
     * Handles the request to start a new game by prompting the user for a board size.
     */
    public void handleNewGameRequest() {
        int sizeSelection = promptForBoardSize();
        if (isValidSizeSelection(sizeSelection)) {
            updateBoardSizeFromSelection(sizeSelection);
            executeGameRestart();
        }
    }


    /**
     * Prompts the user to select a board size for a new game.
     *
     * @return The index of the selected option, or -1 if the dialog was cancelled.
     */
    private int promptForBoardSize() {
        return boardSizePrompt.promptForBoardSize(
                BOARD_SIZE_OPTIONS,
                BOARD_SIZE_OPTIONS[DEFAULT_SIZE_INDEX]
        );
    }

    /**
     * Checks if the user's board size selection is valid.
     *
     * @param selection The index of the selected option.
     * @return {@code true} if the selection is a valid index, {@code false} otherwise.
     */
    private boolean isValidSizeSelection(int selection) {
        return selection >= 0 && selection < BOARD_SIZE_OPTIONS.length;
    }

    /**
     * Updates the internal board size based on the user's selection.
     *
     * @param selectionIndex The index corresponding to the chosen board size.
     */
    private void updateBoardSizeFromSelection(int selectionIndex) {
        boardSize = switch (selectionIndex) {
            case 0 -> BoardSize.SMALL.size();
            case 1 -> BoardSize.REGULAR.size();
            case 2 -> BoardSize.LARGE.size();
            default -> BoardSize.REGULAR.size();
        };
    }

    /**
     * Handles the request to restart the current game with the same board size.
     */
    public void handleRestartRequest() {
        executeGameRestart();
    }

    /**
     * Resets the game to its initial state using the current board size.
     */
    protected void executeGameRestart() {
        this.game = new Game(new BoardSize(boardSize));
        if (gameController != null && uiController != null) {
            gameController.setGame(game);
            uiController.rebuildAfterGameChange(game, gameController::processBoardClick);
        }
    }

    // ==================== History & Display ====================

    /**
     * Handles the request to show or hide the game history panel.
     */
    public void handleShowHistoryRequest() {
        uiController.handleShowHistoryRequest();
    }

    // ==================== Undo/Redo Actions ====================

    /**
     * Attempts to undo the last move in the game.
     */
    public void handleUndoRequest() {
        gameController.handleUndoRequest();
    }

    /**
     * Attempts to redo the last undone move.
     */
    public void handleRedoRequest() {
        gameController.handleRedoRequest();
    }

    // ==================== Exit ====================

    /**
     * Handles the request to exit the application.
     */
    public void handleExitRequest() {
        exitCallback.run();
    }

    /** Updates internal game reference and UI after an import or restart. */
    private void updateGame(Game newGame) {
        this.game = newGame;
        gameController.setGame(newGame);
        uiController.rebuildAfterGameChange(newGame, gameController::processBoardClick);
    }
}
