package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.utils.GameSerializer;
import org.boardgames.crossway.utils.JsonUtils;
import org.boardgames.crossway.utils.Messages;
import org.boardgames.crossway.utils.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
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
     * Options presented in the win dialog after a game ends.
     */
    private static final String[] WIN_DIALOG_OPTIONS = {
            Messages.get("menu.game.new"),
            Messages.get("menu.game.restart"),
            Messages.get("menu.file.exit")
    };

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

    /** Manages player names, colors and scores. */
    private final PlayerManager playerManager;

    /** Label displayed in the menu bar showing the current scoreboard. */
    private final JLabel scoreboardLabel = new JLabel();

    /**
     * The current dimension of the square board.
     */
    private int boardSize;

    // ==================== Constructors ====================

    /**
     * Constructs a new controller with the default board size (Regular).
     */
    public CrosswayController() {
        this(BoardSize.REGULAR);
    }

    /**
     * Constructs a new controller with a predefined board size.
     *
     * @param size The preset {@link BoardSize} to use.
     */
    public CrosswayController(BoardSize size) {
        this(size.toInt());
    }

    /**
     * Constructs a new controller with a specific board size.
     *
     * @param boardSize The dimension of the board (cells per side).
     * @throws IllegalArgumentException if the size is too small (less than 3).
     */
    public CrosswayController(int boardSize) {
        validateBoardSize(boardSize);
        this.boardSize = boardSize;
        this.game = new Game(new BoardSize(boardSize));
        this.uiController = new UiController(this, game, scoreboardLabel);
        this.dialogHandler = new DialogHandler(uiController.getFrame());
        String[] names = dialogHandler.askPlayerNames();
        this.playerManager = new PlayerManager(names[0], names[1]);
        this.gameController = new GameController(
                game,
                uiController,
                dialogHandler,
                this::handleNewGameRequest,
                this::handleRestartRequest,
                this::handleExitRequest,
                playerManager,
                this::refreshScoreboard);
        attachEventHandlers();
        refreshScoreboard();
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
        uiController.getFrame().setJMenuBar(MenuBarFactory.createMenuBar(this, scoreboardLabel));
        refreshScoreboard();

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
        return JOptionPane.showOptionDialog(
                uiController.getFrame(),
                Messages.get("menu.game.selectBoardSize"),
                Messages.get("menu.game.new"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
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
    private void executeGameRestart() {
        this.game = new Game(new BoardSize(boardSize));
        gameController.setGame(game);
        uiController.rebuildAfterGameChange(game, gameController::processBoardClick);
    }

    /**
     * Handles a request to change player names and reset their scores.
     */
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

    /** Updates the scoreboard label with current names and scores. */
    public void refreshScoreboard() {
        Player black = playerManager.getPlayer(Stone.BLACK);
        Player white = playerManager.getPlayer(Stone.WHITE);
        scoreboardLabel.setText(Messages.format(
                "scoreboard.format",
                black.getName(), black.getWins(),
                white.getName(), white.getWins()));
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

    // ==================== Import/Export ====================

    /**
     * Prompts the user to save the current game state to a JSON file.
     */
    public void handleExportRequest() {
        JFileChooser chooser = createJsonFileChooser(Messages.get("menu.file.export"));
        int choice = chooser.showSaveDialog(uiController.getFrame());
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            executeGameExport(JsonUtils.ensureJsonExtension(selectedFile));
        }
    }

    /**
     * Prompts the user to load a game state from a JSON file.
     */
    public void handleImportRequest() {
        JFileChooser chooser = createJsonFileChooser(Messages.get("menu.file.import"));
        int choice = chooser.showOpenDialog(uiController.getFrame());
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            executeGameImport(selectedFile);
        }
    }

    /**
     * Creates a file chooser configured specifically for JSON files.
     *
     * @param title The title of the dialog.
     * @return The configured {@link JFileChooser}.
     */
    private JFileChooser createJsonFileChooser(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                Messages.get("file.filter.json"), JsonUtils.JSON_EXT
        );
        chooser.setFileFilter(filter);
        return chooser;
    }

    /**
     * Loads a game from a specified JSON file and updates the game state.
     *
     * @param targetFile The file to import.
     */
    private void executeGameImport(File targetFile) {
        try {
            this.game = GameSerializer.load(targetFile);
            gameController.setGame(game);
            uiController.rebuildAfterGameChange(game, gameController::processBoardClick);
        } catch (Exception ex) {
            dialogHandler.showError(
                    Messages.get("error.import.title"),
                    Messages.format("error.import.message", ex.getMessage())
            );
        }
    }

    /**
     * Saves the current game state to a JSON file.
     *
     * @param targetFile The destination file.
     */
    private void executeGameExport(File targetFile) {
        try {
            GameSerializer.save(game, targetFile);
            dialogHandler.showInfo(
                    Messages.get("export.success.title"),
                    Messages.format("export.success.message", targetFile.getName())
            );
        } catch (Exception ex) {
            dialogHandler.showError(
                    Messages.get("error.export.title"),
                    Messages.format("error.export.message", ex.getMessage())
            );
        }
    }

    // ==================== Exit ====================

    /**
     * Handles the request to exit the application.
     */
    public void handleExitRequest() {
        System.exit(0);
    }
}
