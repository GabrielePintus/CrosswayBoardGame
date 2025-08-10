package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.ui.BoardView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Controller for the Crossway board game.
 *
 * <p>This class manages the game flow, user interactions, and UI updates.
 * It coordinates between the Game model and BoardView, handling mouse input
 * for stone placement, menu actions for game management, and automatic
 * window resizing when board dimensions change.</p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Managing game state and player turns</li>
 *   <li>Processing user input (mouse clicks, menu selections)</li>
 *   <li>Updating the visual representation of the board</li>
 *   <li>Handling win conditions and game flow</li>
 *   <li>Providing game export functionality</li>
 * </ul>
 *
 * @author Your Name
 * @version 1.0
 */
public class CrosswayController {

    // ========== Constants ==========
    private static final String WINDOW_TITLE = "Crossway";
    private static final String[] BOARD_SIZE_OPTIONS = {
            "Tiny (5x5)",
            "Small (9x9)",
            "Regular (19x19)",
            "Large (25x25)"
    };
    private static final String[] WIN_DIALOG_OPTIONS = {
            "New Game",
            "Restart",
            "Exit"
    };

    // ========== Fields ==========
    private Game game;
    private BoardView view;
    private JFrame frame;
    private int boardSize;

    // ========== Constructors ==========

    /**
     * Constructs a controller with a custom board size.
     *
     * @param boardSize the board dimension (NxN grid)
     */
    public CrosswayController(int boardSize) {
        this.boardSize = boardSize;
        initializeGame();
        initView();
        bindEvents();
    }

    /**
     * Constructs a controller using a predefined BoardSize enumeration.
     *
     * @param size the predefined board size
     */
    public CrosswayController(BoardSize size) {
        this(size.size());
    }

    /**
     * Constructs a controller with the standard 19x19 board size.
     */
    public CrosswayController() {
        this(BoardSize.REGULAR);
    }

    // ========== Initialization Methods ==========

    /**
     * Initializes the Game model and BoardView based on the current board size.
     * This method creates a fresh game instance and corresponding view.
     */
    private void initializeGame() {
        game = new Game(new BoardSize(boardSize));
        view = new BoardView(game.getBoard());
    }

    /**
     * Creates and configures the main application window.
     * Sets up the JFrame with menu bar, adds the board view, and makes it visible.
     */
    private void initView() {
        frame = new JFrame(WINDOW_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setJMenuBar(createMenuBar());
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ========== UI Creation Methods ==========

    /**
     * Creates and configures the application menu bar.
     * Includes File menu (export), Game menu (new game, restart, exit),
     * and a right-aligned Undo button.
     *
     * @return the configured menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Left-aligned menus
        menuBar.add(createFileMenu());
        menuBar.add(createGameMenu());

        // Push remaining components to the right
        menuBar.add(Box.createHorizontalGlue());

        // Right-aligned undo button
        menuBar.add(createUndoButton());
        menuBar.add(createRedoButton());

        return menuBar;
    }

    /**
     * Creates the File menu with export functionality.
     *
     * @return the configured File menu
     */
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem exportGame = new JMenuItem("Export game");
        exportGame.addActionListener(e -> promptExportGame());

        fileMenu.add(exportGame);
        return fileMenu;
    }

    /**
     * Creates the Game menu with game management options.
     *
     * @return the configured Game menu
     */
    private JMenu createGameMenu() {
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(e -> promptNewGame());

        JMenuItem restart = new JMenuItem("Restart");
        restart.addActionListener(e -> restartGame());

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.add(restart);
        gameMenu.add(exit);

        return gameMenu;
    }

    /**
     * Creates the Undo button for the menu bar.
     *
     * @return the configured Undo button
     */
    private JButton createUndoButton() {
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMove());
        return undoButton;
    }

    /**
     * Creates the Redo button for the menu bar.
     * Currently, it does not perform any action as redo functionality is not implemented.
     *
     * @return the configured Redo button
     */
    private JButton createRedoButton() {
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> redoMove());
        return redoButton;
    }

    // ========== Event Handling ==========

    /**
     * Attaches mouse event listeners to the board view.
     * Handles stone placement when the user clicks on the board.
     */
    private void bindEvents() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    /**
     * Processes mouse clicks on the board for stone placement.
     * Validates moves, updates the game state, and checks for win conditions.
     *
     * @param e the mouse event containing click coordinates
     */
    private void handleMouseClick(MouseEvent e) {
        Point boardPoint = toBoardPoint(e);
        Stone currentPlayer = game.getCurrentPlayer();

        // Attempt to make the move
        try {
            game.makeMove(new Move(boardPoint, currentPlayer));
            view.repaint();
        } catch (IllegalArgumentException ex) {
            showInvalidMoveDialog(ex.getMessage());
            return;
        }

        // Check for win condition
        if (game.hasWon(currentPlayer)) {
            handleGameWin(currentPlayer);
        }
    }

    /**
     * Handles the game win scenario by showing a dialog and processing user choice.
     *
     * @param winner the stone color of the winning player
     */
    private void handleGameWin(Stone winner) {
        int choice = JOptionPane.showOptionDialog(
                frame,
                winner + " wins!",
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                WIN_DIALOG_OPTIONS,
                "Restart"
        );

        switch (choice) {
            case 0 -> promptNewGame();    // New Game
            case 1 -> restartGame();      // Restart
            case 2 -> System.exit(0);     // Exit
            // Case -1 (dialog closed) does nothing
        }
    }

    // ========== Game Actions ==========

    /**
     * Attempts to undo the last move made in the game.
     * Shows an information dialog if no moves are available to undo.
     */
    private void undoMove() {
        try {
            game.undoLastMove();
            view.repaint();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "No moves to undo.",
                    "Undo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Attempts to redo the last undone move in the game.
     * Currently, this method does nothing as redo functionality is not implemented.
     */
    private void redoMove() {
        try {
            game.redoLastMove();
            view.repaint();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "No moves to redo.",
                    "Redo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Prompts the user to select a new board size and starts a fresh game.
     * Presents a dialog with predefined size options.
     */
    private void promptNewGame() {
        int selection = JOptionPane.showOptionDialog(
                frame,
                "Select board size:",
                "New Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                BOARD_SIZE_OPTIONS,
                BOARD_SIZE_OPTIONS[2] // Default to Regular
        );

        if (selection < 0 || selection >= BOARD_SIZE_OPTIONS.length) {
            return; // User cancelled or invalid selection
        }

        updateBoardSize(selection);
        restartGame();
    }

    /**
     * Updates the board size based on user selection.
     *
     * @param selectionIndex the index of the selected board size option
     */
    private void updateBoardSize(int selectionIndex) {
        switch (selectionIndex) {
            case 0 -> boardSize = 5;                        // Tiny
            case 1 -> boardSize = BoardSize.SMALL.size();   // Small
            case 2 -> boardSize = BoardSize.REGULAR.size(); // Regular
            case 3 -> boardSize = BoardSize.LARGE.size();   // Large
        }
    }

    /**
     * Prompts the user to export the current game state to a file.
     * Opens a file chooser dialog and handles the export process.
     */
    private void promptExportGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Game");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            handleGameExport(fileChooser.getSelectedFile());
        } else {
            showExportCancelledDialog();
        }
    }

    /**
     * Performs the actual game export to the specified file.
     *
     * @param fileToSave the target file for export
     */
    private void handleGameExport(java.io.File fileToSave) {
        try {
            String exportedGame = game.encode();
            java.nio.file.Files.writeString(fileToSave.toPath(), exportedGame);
            showExportSuccessDialog(fileToSave.getAbsolutePath());
        } catch (Exception ex) {
            showExportErrorDialog(ex.getMessage());
        }
    }

    /**
     * Restarts the current game while preserving the board size.
     * Reinitializes the game model, updates the UI, and adjusts window size.
     */
    private void restartGame() {
        initializeGame();
        frame.getContentPane().removeAll();
        frame.add(view);
        frame.pack(); // Adjust window size to new board dimensions
        bindEvents();
        frame.revalidate();
        frame.repaint();
    }

    // ========== Utility Methods ==========

    /**
     * Converts mouse event coordinates to board grid coordinates.
     *
     * @param e the mouse event
     * @return the corresponding board point
     */
    private Point toBoardPoint(MouseEvent e) {
        int cellSize = view.getCellSize();
        return new Point(e.getX() / cellSize, e.getY() / cellSize);
    }

    // ========== Dialog Helper Methods ==========

    /**
     * Shows an error dialog for invalid moves.
     *
     * @param message the error message to display
     */
    private void showInvalidMoveDialog(String message) {
        JOptionPane.showMessageDialog(
                frame,
                message,
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Shows a success dialog for game export.
     *
     * @param filePath the path where the game was exported
     */
    private void showExportSuccessDialog(String filePath) {
        JOptionPane.showMessageDialog(
                frame,
                "Game exported successfully to " + filePath,
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Shows an error dialog for export failures.
     *
     * @param errorMessage the error message to display
     */
    private void showExportErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(
                frame,
                "Failed to export game: " + errorMessage,
                "Export Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Shows an information dialog when export is cancelled.
     */
    private void showExportCancelledDialog() {
        JOptionPane.showMessageDialog(
                frame,
                "Export cancelled.",
                "Export Cancelled",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}