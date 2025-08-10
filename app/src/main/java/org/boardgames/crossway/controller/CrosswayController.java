package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.ui.BoardView;
import org.boardgames.crossway.ui.HistoryView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Crossway board game application.
 *
 * <p>This class serves as the main controller in the MVC architecture, coordinating
 * interactions between the game model and the user interface. It handles all user
 * input, manages game state transitions, and updates the visual representation
 * of the game board.</p>
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 *   <li>Managing game state and player turn transitions</li>
 *   <li>Processing user input (mouse clicks, menu selections)</li>
 *   <li>Updating the visual representation of the board</li>
 *   <li>Handling win conditions and game flow control</li>
 *   <li>Providing game export and import functionality</li>
 *   <li>Managing window lifecycle and UI updates</li>
 * </ul>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * // Create controller with default board size
 * CrosswayController controller = new CrosswayController();
 *
 * // Create controller with custom size
 * CrosswayController controller = new CrosswayController(BoardSize.LARGE);
 * }</pre>
 *
 * @author Crossway Development Team
 * @version 1.0
 * @since 1.0
 */
public class CrosswayController {

    // ==================== Constants ====================

    /** The title displayed in the application window */
    private static final String WINDOW_TITLE = "Crossway";

    /** Available board size options for new game dialog */
    private static final String[] BOARD_SIZE_OPTIONS = {
            "Tiny (5x5)",
            "Small (9x9)",
            "Regular (19x19)",
            "Large (25x25)"
    };

    /** Options presented in the game win dialog */
    private static final String[] WIN_DIALOG_OPTIONS = {
            "New Game",
            "Restart",
            "Exit"
    };

    /** Default board size selection index */
    private static final int DEFAULT_SIZE_INDEX = 2; // Regular

    /** Board size for tiny games */
    private static final int TINY_BOARD_SIZE = 5;

    // ==================== Instance Fields ====================

    /** The game model containing game state and logic */
    private Game game;

    /** The view component responsible for rendering the board */
    private BoardView view;
    /** The history view for displaying game history */
    private HistoryView historyView;


    /** The main application window */
    private JFrame frame;

    /** Current board size (N for NxN grid) */
    private int boardSize;

    // ==================== Constructors ====================

    /**
     * Creates a new controller with the default board size.
     * Initializes the game with a standard 19x19 board.
     */
    public CrosswayController() {
        this(BoardSize.REGULAR);
    }

    /**
     * Creates a new controller with a predefined board size.
     *
     * @param size the predefined board size enumeration value
     * @throws IllegalArgumentException if size is null
     */
    public CrosswayController(BoardSize size) {
        this(size.size());
    }

    /**
     * Creates a new controller with a custom board size.
     *
     * @param boardSize the board dimension (creates NxN grid)
     * @throws IllegalArgumentException if boardSize is less than 3
     */
    public CrosswayController(int boardSize) {
        validateBoardSize(boardSize);
        this.boardSize = boardSize;

        initializeComponents();
        setupUserInterface();
        attachEventHandlers();
    }

    // ==================== Initialization Methods ====================

    /**
     * Validates that the board size is acceptable for gameplay.
     *
     * @param size the board size to validate
     * @throws IllegalArgumentException if size is too small
     */
    private void validateBoardSize(int size) {
        if (size < 3) {
            throw new IllegalArgumentException("Board size must be at least 3x3");
        }
    }

    /**
     * Initializes the core game components.
     * Creates a fresh game instance and corresponding board view.
     */
    private void initializeComponents() {
        game = new Game(new BoardSize(boardSize));
        view = new BoardView(game.getBoard());
        historyView = new HistoryView(); // Add this line
    }

    /**
     * Sets up and displays the main application window.
     * Configures the JFrame with proper settings, menu bar, and layout.
     */
    private void setupUserInterface() {
        createMainWindow();
        configureWindowProperties();
        addComponentsToWindow();
        displayWindow();
    }

    /**
     * Creates the main application window instance.
     */
    private void createMainWindow() {
        frame = new JFrame(WINDOW_TITLE);
    }

    /**
     * Configures the basic properties of the main window.
     */
    private void configureWindowProperties() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setJMenuBar(createApplicationMenuBar());
    }

    /**
     * Adds the board view and other components to the window.
     */
    private void addComponentsToWindow() {
        frame.setLayout(new BorderLayout()); // Add this line
        frame.add(view, BorderLayout.CENTER); // Modify this line
        frame.add(historyView, BorderLayout.EAST); // Add this line
    }

    /**
     * Makes the window visible and centers it on screen.
     */
    private void displayWindow() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ==================== Menu Creation Methods ====================

    /**
     * Creates and configures the complete application menu bar.
     * Includes File and Game menus on the left, and action buttons on the right.
     *
     * @return the fully configured menu bar
     */
    private JMenuBar createApplicationMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        addLeftAlignedMenus(menuBar);
        addMenuSpacer(menuBar);
        addRightAlignedButtons(menuBar);

        return menuBar;
    }

    /**
     * Adds the main menus to the left side of the menu bar.
     *
     * @param menuBar the menu bar to add menus to
     */
    private void addLeftAlignedMenus(JMenuBar menuBar) {
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createGameMenu());
    }

    /**
     * Adds a horizontal spacer to push subsequent components to the right.
     *
     * @param menuBar the menu bar to add the spacer to
     */
    private void addMenuSpacer(JMenuBar menuBar) {
        menuBar.add(Box.createHorizontalGlue());
    }

    /**
     * Adds action buttons to the right side of the menu bar.
     *
     * @param menuBar the menu bar to add buttons to
     */
    private void addRightAlignedButtons(JMenuBar menuBar) {
        menuBar.add(createUndoButton());
        menuBar.add(createRedoButton());
    }

    /**
     * Creates the File menu with game management operations.
     * Currently includes export functionality.
     *
     * @return the configured File menu
     */
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem exportItem = createMenuItem("Export Game", this::handleExportRequest);
        fileMenu.add(exportItem);

        return fileMenu;
    }

    /**
     * Creates the View menu with display options.
     *
     * @return the configured View menu
     */
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");

        JMenuItem showHistoryItem = createMenuItem("Show History", this::handleShowHistoryRequest);
        viewMenu.add(showHistoryItem);

        return viewMenu;
    }

    /**
     * Creates the Game menu with game control operations.
     * Includes new game, restart, and exit functionality.
     *
     * @return the configured Game menu
     */
    private JMenu createGameMenu() {
        JMenu gameMenu = new JMenu("Game");

        gameMenu.add(createMenuItem("New Game", this::handleNewGameRequest));
        gameMenu.add(createMenuItem("Restart", this::handleRestartRequest));
        gameMenu.add(createMenuItem("Exit", this::handleExitRequest));

        return gameMenu;
    }

    /**
     * Creates a menu item with the specified text and action.
     *
     * @param text the display text for the menu item
     * @param action the action to perform when clicked
     * @return the configured menu item
     */
    private JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    /**
     * Creates the Undo button for the menu bar.
     * Allows players to reverse their last move.
     *
     * @return the configured Undo button
     */
    private JButton createUndoButton() {
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> handleUndoRequest());
        return undoButton;
    }

    /**
     * Creates the Redo button for the menu bar.
     * Allows players to restore previously undone moves.
     *
     * @return the configured Redo button
     */
    private JButton createRedoButton() {
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> handleRedoRequest());
        return redoButton;
    }

    // ==================== Event Handling Setup ====================

    /**
     * Attaches all necessary event listeners to UI components.
     * Currently handles mouse interactions with the board view.
     */
    private void attachEventHandlers() {
        attachBoardMouseHandler();
    }

    /**
     * Attaches mouse event handling to the board view.
     * Processes mouse clicks for stone placement.
     */
    private void attachBoardMouseHandler() {
        view.addMouseListener(new BoardMouseHandler());
    }

    /**
     * Custom mouse adapter for handling board interactions.
     * Processes mouse releases as move attempts.
     */
    private class BoardMouseHandler extends MouseAdapter {
        /**

         * Handles mouse release events on the board component.

         *

         * @param e the mouse event

         */

        @Override
        public void mouseReleased(MouseEvent e) {
            processMouseClick(e);
        }
    }

    // ==================== Game Input Processing ====================

    /**
     * Processes mouse clicks on the board for stone placement.
     * Validates the move, updates game state, refreshes display, and checks for wins.
     *
     * @param mouseEvent the mouse event containing click coordinates
     */
    private void processMouseClick(MouseEvent mouseEvent) {
        Point boardCoordinate = convertMouseToBoardCoordinate(mouseEvent);
        Stone currentPlayer = game.getCurrentPlayer();

        if (attemptMoveExecution(boardCoordinate, currentPlayer)) {
            refreshBoardDisplay();
            checkForGameCompletion(currentPlayer);
        }
    }

    /**
     * Converts mouse event coordinates to board grid coordinates.
     *
     * @param mouseEvent the mouse event containing screen coordinates
     * @return the corresponding point on the game board
     */
    private Point convertMouseToBoardCoordinate(MouseEvent mouseEvent) {
        int cellSize = view.getCellSize();
        int boardX = mouseEvent.getX() / cellSize;
        int boardY = mouseEvent.getY() / cellSize;
        return new Point(boardX, boardY);
    }

    /**
     * Attempts to execute a move at the specified board position.
     *
     * @param position the board position for the move
     * @param player the player making the move
     * @return true if the move was successful, false if it was invalid
     */
    private boolean attemptMoveExecution(Point position, Stone player) {
        try {
            Move move = new Move(position, player);
            game.makeMove(move);
            updateHistoryDisplay(); // Add this line
            return true;
        } catch (IllegalArgumentException ex) {
            displayInvalidMoveWarning(ex.getMessage());
            return false;
        }
    }

    /**
     * Refreshes the visual display of the game board.
     */
    private void refreshBoardDisplay() {
        view.repaint();
    }

    /**
     * Checks if the current player has won and handles game completion.
     *
     * @param currentPlayer the player who just made a move
     */
    private void checkForGameCompletion(Stone currentPlayer) {
        if (game.hasWon(currentPlayer)) {
            handleGameWin(currentPlayer);
        }
    }

    // ==================== Game State Management ====================

    /**
     * Handles the scenario when a player wins the game.
     * Displays a win dialog and processes the player's choice for next action.
     *
     * @param winner the stone color of the winning player
     */
    private void handleGameWin(Stone winner) {
        int playerChoice = displayWinDialog(winner);
        processWinDialogChoice(playerChoice);
    }

    /**
     * Displays the game win dialog and returns the player's choice.
     *
     * @param winner the winning player
     * @return the index of the selected option
     */
    private int displayWinDialog(Stone winner) {
        return JOptionPane.showOptionDialog(
                frame,
                winner + " wins!",
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                WIN_DIALOG_OPTIONS,
                WIN_DIALOG_OPTIONS[1] // Default to "Restart"
        );
    }

    /**
     * Processes the player's choice from the win dialog.
     *
     * @param choice the selected option index
     */
    private void processWinDialogChoice(int choice) {
        switch (choice) {
            case 0 -> handleNewGameRequest();    // New Game
            case 1 -> handleRestartRequest();    // Restart
            case 2 -> handleExitRequest();       // Exit
            default -> { /* Dialog was closed - do nothing */ }
        }
    }

    // ==================== Menu Action Handlers ====================

    /**
     * Handles requests to show/hide the game history.
     */
    private void handleShowHistoryRequest() {
        // Placeholder for future implementation
        historyView.toggleVisibility();

        // Expand window size
        int widthDiff = historyView.getExpandedWidth();
        int newSize = historyView.isHistoryVisible() ? frame.getWidth() + widthDiff : frame.getWidth() - widthDiff;
        frame.setSize(newSize, frame.getHeight());

        // Update menu text based on visibility state
        JMenu viewMenu = (JMenu) frame.getJMenuBar().getMenu(1); // View menu is 3rd (index 2)
        JMenuItem historyItem = viewMenu.getItem(0);
        historyItem.setText(historyView.isHistoryVisible() ? "Hide History" : "Show History");
    }

    /**
     * Handles requests to start a new game with potentially different board size.
     */
    private void handleNewGameRequest() {
        int sizeSelection = promptForBoardSize();

        if (isValidSizeSelection(sizeSelection)) {
            updateBoardSizeFromSelection(sizeSelection);
            executeGameRestart();
        }
    }

    /**
     * Prompts the user to select a board size for a new game.
     *
     * @return the index of the selected size option, or -1 if cancelled
     */
    private int promptForBoardSize() {
        return JOptionPane.showOptionDialog(
                frame,
                "Select board size:",
                "New Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                BOARD_SIZE_OPTIONS,
                BOARD_SIZE_OPTIONS[DEFAULT_SIZE_INDEX]
        );
    }

    /**
     * Checks if the size selection is valid (not cancelled).
     *
     * @param selection the selection index
     * @return true if the selection is valid
     */
    private boolean isValidSizeSelection(int selection) {
        return selection >= 0 && selection < BOARD_SIZE_OPTIONS.length;
    }

    /**
     * Updates the board size based on the user's selection.
     *
     * @param selectionIndex the index of the selected size option
     */
    private void updateBoardSizeFromSelection(int selectionIndex) {
        boardSize = switch (selectionIndex) {
            case 0 -> TINY_BOARD_SIZE;              // Tiny (5x5)
            case 1 -> BoardSize.SMALL.size();      // Small (9x9)
            case 2 -> BoardSize.REGULAR.size();    // Regular (19x19)
            case 3 -> BoardSize.LARGE.size();      // Large (25x25)
            default -> BoardSize.REGULAR.size();   // Fallback to regular
        };
    }

    /**
     * Handles requests to restart the current game with the same board size.
     */
    private void handleRestartRequest() {
        executeGameRestart();
    }

    /**
     * Executes a complete game restart, preserving the current board size.
     * Reinitializes all components and updates the UI accordingly.
     */
    private void executeGameRestart() {
        initializeComponents();
        updateWindowContents();
        attachEventHandlers();
        refreshWindow();
    }

    /**
     * Updates the window contents with the new game components.
     */
    private void updateWindowContents() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout()); // Add this line
        frame.add(view, BorderLayout.CENTER); // Modify this line
        frame.add(historyView, BorderLayout.EAST); // Add this line
        frame.pack();
    }

    /**
     * Updates the history display with the current game move history.
     * This method should be called whenever a move is made or undone.
     */
    private void updateHistoryDisplay() {
        historyView.updateHistory(game.getMoveHistory());
    }

    /**
     * Refreshes the window display after content changes.
     */
    private void refreshWindow() {
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Handles requests to exit the application.
     */
    private void handleExitRequest() {
        System.exit(0);
    }

    // ==================== Move Management ====================

    /**
     * Handles requests to undo the last move.
     * Attempts to undo and provides feedback if no moves are available.
     */
    private void handleUndoRequest() {
        try {
            game.undoLastMove();
            refreshBoardDisplay();
            updateHistoryDisplay(); // Add this line
        } catch (IllegalStateException ex) {
            displayNoUndoAvailableDialog();
        }
    }

    /**
     * Handles requests to redo the last undone move.
     * Attempts to redo and provides feedback if no moves are available.
     */
    private void handleRedoRequest() {
        try {
            game.redoLastMove();
            refreshBoardDisplay();
            updateHistoryDisplay(); // Add this line
        } catch (IllegalStateException ex) {
            displayNoRedoAvailableDialog();
        }
    }

    // ==================== Export Functionality ====================

    /**
     * Handles requests to export the current game state.
     * Opens a file chooser and manages the export process.
     */
    private void handleExportRequest() {
        JFileChooser fileChooser = createExportFileChooser();
        int userChoice = fileChooser.showSaveDialog(frame);

        processExportChoice(userChoice, fileChooser);
    }

    /**
     * Creates and configures a file chooser for game export.
     *
     * @return the configured file chooser
     */
    private JFileChooser createExportFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Game");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return chooser;
    }

    /**
     * Processes the user's choice from the export file dialog.
     *
     * @param choice the user's choice (APPROVE_OPTION or CANCEL_OPTION)
     * @param fileChooser the file chooser containing the selected file
     */
    private void processExportChoice(int choice, JFileChooser fileChooser) {
        if (choice == JFileChooser.APPROVE_OPTION) {
            executeGameExport(fileChooser.getSelectedFile());
        } else {
            displayExportCancelledDialog();
        }
    }

    /**
     * Executes the actual game export to the specified file.
     *
     * @param targetFile the file to export the game data to
     */
    private void executeGameExport(File targetFile) {
        try {
            String gameData = game.encode();
            Files.writeString(targetFile.toPath(), gameData);
            displayExportSuccessDialog(targetFile.getAbsolutePath());
        } catch (Exception ex) {
            displayExportErrorDialog(ex.getMessage());
        }
    }

    // ==================== User Dialog Methods ====================

    /**
     * Displays a warning dialog for invalid moves.
     *
     * @param errorMessage the specific error message to show
     */
    private void displayInvalidMoveWarning(String errorMessage) {
        JOptionPane.showMessageDialog(
                frame,
                errorMessage,
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Displays an information dialog when no moves are available to undo.
     */
    private void displayNoUndoAvailableDialog() {
        JOptionPane.showMessageDialog(
                frame,
                "No moves available to undo.",
                "Undo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays an information dialog when no moves are available to redo.
     */
    private void displayNoRedoAvailableDialog() {
        JOptionPane.showMessageDialog(
                frame,
                "No moves available to redo.",
                "Redo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a success dialog after successful game export.
     *
     * @param filePath the path where the game was successfully exported
     */
    private void displayExportSuccessDialog(String filePath) {
        JOptionPane.showMessageDialog(
                frame,
                "Game exported successfully to " + filePath,
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays an error dialog when game export fails.
     *
     * @param errorMessage the specific error message describing the failure
     */
    private void displayExportErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(
                frame,
                "Failed to export game: " + errorMessage,
                "Export Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays an information dialog when export is cancelled by the user.
     */
    private void displayExportCancelledDialog() {
        JOptionPane.showMessageDialog(
                frame,
                "Export cancelled.",
                "Export Cancelled",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}