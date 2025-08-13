package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.ui.BoardView;
import org.boardgames.crossway.ui.HistoryView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
 * @version 1.1
 * @since 1.0
 */
public class CrosswayController {

    // ==================== Constants ====================

    /** Title displayed in the main application window. */
    private static final String WINDOW_TITLE = "Crossway";

    /** Options for board sizes available in the game.
     * These are presented to the user when starting a new game.
     */
    private static final String[] BOARD_SIZE_OPTIONS = {
            "Tiny (5x5)", "Small (9x9)", "Regular (19x19)", "Large (25x25)"
    };

    /** Options shown in the win dialog allowing the user to choose the next action. */
    private static final String[] WIN_DIALOG_OPTIONS = { "New Game", "Restart", "Exit" };

    /** Default index into {@link #BOARD_SIZE_OPTIONS} (Regular board). */
    private static final int DEFAULT_SIZE_INDEX = 2;     // Regular

    /** Exact size used for the tiniest board preset. */
    private static final int TINY_BOARD_SIZE = 5;        // Exact size for tiny

    /** File extension used for JSON import/export. */
    private static final String JSON_EXT = "json";

    // ==================== State ====================

    /** Core game model holding rules, state, and move logic. */
    private Game game;

    /** Swing component responsible for rendering the board. */
    private BoardView view;

    /** Panel that renders and toggles the move history sidebar. */
    private HistoryView historyView;

    /** Split pane that hosts the board and the optional history sidebar. */
    private JSplitPane splitPane;

    /** Top-level Swing frame for the application. */
    private JFrame frame;

    /** Current board size in cells (width == height). */
    private int boardSize;

    // ==================== Constructors ====================

    /** Creates a controller with the default board size. */
    public CrosswayController() {
        this(BoardSize.REGULAR);
    }

    /** Creates a controller for a predefined {@link BoardSize}.
     * @param size the preset board size.
     */
    public CrosswayController(BoardSize size) {
        this(size.size());
    }

    /** Creates a controller with an explicit board size.
     * @param boardSize the board dimension (cells per side).
     * @throws IllegalArgumentException if the size is too small.
     */
    public CrosswayController(int boardSize) {
        validateBoardSize(boardSize);
        this.boardSize = boardSize;

        initializeComponents();
        setupUserInterface();
        attachEventHandlers();
    }

    // ==================== Initialization ====================

    /** Validates the provided board size.
     * @param size the proposed board dimension.
     * @throws IllegalArgumentException if {@code size} is below the minimum supported.
     */
    private void validateBoardSize(int size) {
        if (size < 3) {
            throw new IllegalArgumentException("Board size must be at least 3x3");
        }
    }

    /** Instantiates model and view components but does not lay them out. */
    private void initializeComponents() {
        game = new Game(new BoardSize(boardSize));
        view = new BoardView(game.getBoard());
        historyView = new HistoryView();
    }

    /** Builds and lays out the application UI and menu bar. */
    private void setupUserInterface() {
        createMainWindow();
        configureWindowProperties();
        installCenterContent();
        displayWindow();
    }

    /** Creates the main application window and sets the content pane. */
    private void createMainWindow() {
        frame = new JFrame(WINDOW_TITLE);
    }

    /** Applies standard frame properties (close operation, sizing, icons). */
    private void configureWindowProperties() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setJMenuBar(createApplicationMenuBar());
    }

    /** Creates/installs the center split pane (board + history). */
    private void installCenterContent() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, view, historyView);
        splitPane.setResizeWeight(0.0);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(false);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.pack();

        // Start hidden by default: divider at full width
        splitPane.setDividerLocation(splitPane.getWidth());
    }

    /** Packs and shows the main frame on screen. */
    private void displayWindow() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ==================== Menu ====================

    /** Creates the application menu bar.
     * @return a configured {@link JMenuBar} instance.
     */
    private JMenuBar createApplicationMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        addLeftAlignedMenus(menuBar);
        menuBar.add(Box.createHorizontalGlue());
        addRightAlignedButtons(menuBar);

        return menuBar;
    }

    /** Adds the File/View/Game menus to the left side of the bar.
     * @param menuBar the target menu bar.
     */
    private void addLeftAlignedMenus(JMenuBar menuBar) {
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createGameMenu());
    }

    /** Builds the File menu with import/export and exit.
     * @return the File menu.
     */
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("Import Game", this::handleImportRequest));
        fileMenu.add(createMenuItem("Export Game", this::handleExportRequest));
        fileMenu.add(createMenuItem("Exit", this::handleExitRequest));
        return fileMenu;
    }

    /** Builds the View menu with history toggle and board size options.
     * @return the View menu.
     */
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(createMenuItem("Show History", this::handleShowHistoryRequest));
        return viewMenu;
    }

    /** Builds the Game menu with Undo/Redo and New Game/Restart.
     * @return the Game menu.
     */
    private JMenu createGameMenu() {
        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(createMenuItem("New Game", this::handleNewGameRequest));
        gameMenu.add(createMenuItem("Restart", this::handleRestartRequest));
        return gameMenu;
    }

    /** Utility to create a menu item bound to a {@link Runnable} action.
     * @param text the menu item label.
     * @param action the action to run when selected.
     * @return the created menu item.
     */
    private JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    /** Adds right-aligned toolbar-style buttons (Undo/Redo, Import/Export).
     * @param menuBar the target menu bar.
     */
    private void addRightAlignedButtons(JMenuBar menuBar) {
        menuBar.add(createToolbarButton("Undo", this::handleUndoRequest));
        menuBar.add(createToolbarButton("Redo", this::handleRedoRequest));
    }

    /** Creates a button for the menu bar's right side.
     * @param label text to display.
     * @param action action executed on click.
     * @return a configured {@link JButton}.
     */
    private JButton createToolbarButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ==================== Event wiring ====================

    /** Attaches listeners for resize, mouse interaction, and window events. */
    private void attachEventHandlers() {
        attachBoardMouseHandler();
    }

    /** Removes existing mouse listeners from the board view to avoid duplicates. */
    private void detachBoardMouseHandlers() {
        for (var ml : view.getMouseListeners()) {
            view.removeMouseListener(ml);
        }
    }

    /** Installs a mouse listener on the board view to handle move input. */
    private void attachBoardMouseHandler() {
        view.addMouseListener(new BoardMouseHandler());
    }

    /** Attaches a component listener to handle window resize events. */
    private class BoardMouseHandler extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            processMouseClick(e);
        }
    }

    // ==================== Input → Model ====================

    /** Processes a mouse click and attempts to place a stone.
     * @param mouseEvent the originating mouse event.
     */
    private void processMouseClick(MouseEvent mouseEvent) {
        Point boardCoordinate = convertMouseToBoardCoordinate(mouseEvent);
        Stone currentPlayer = game.getCurrentPlayer();

        if (attemptMoveExecution(boardCoordinate, currentPlayer)) {
            refreshBoardDisplay();
            checkForGameCompletion(currentPlayer);
        }
    }

    /** Converts screen coordinates to a board coordinate.
     * @param mouseEvent the mouse event containing the click location.
     * @return the board coordinate, or {@code null} if outside the board.
     */
    private Point convertMouseToBoardCoordinate(MouseEvent mouseEvent) {
        int cellSize = view.getCellSize();
        int boardX = mouseEvent.getX() / cellSize;
        int boardY = mouseEvent.getY() / cellSize;
        return new Point(boardX, boardY);
    }

    /** Attempts to place a stone for {@code player} at {@code position}.
     * @param position board coordinate.
     * @param player the player making the move.
     * @return {@code true} if the move was executed.
     * @throws IllegalArgumentException if the move is illegal.
     */
    private boolean attemptMoveExecution(Point position, Stone player) {
        try {
            game.makeMove(new Move(position, player));
            updateHistoryDisplay();
            return true;
        } catch (IllegalArgumentException ex) {
            showWarning("Invalid Move", ex.getMessage());
            return false;
        }
    }

    /** Repaints the board and updates any dependent UI state. */
    private void refreshBoardDisplay() {
        view.repaint();
    }

    /** Checks if the specified player has achieved a winning connection.
     * @param currentPlayer the player whose victory is evaluated.
     */
    private void checkForGameCompletion(Stone currentPlayer) {
        if (game.hasWon(currentPlayer)) {
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    currentPlayer + " wins!",
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    WIN_DIALOG_OPTIONS,
                    WIN_DIALOG_OPTIONS[1]
            );
            processWinDialogChoice(choice);
        }
    }

    /** Handles the result of the win dialog.
     * @param choice index of the selected option.
     */
    private void processWinDialogChoice(int choice) {
        switch (choice) {
            case 0 -> handleNewGameRequest();
            case 1 -> handleRestartRequest();
            case 2 -> handleExitRequest();
            default -> { /* closed */ }
        }
    }

    // ==================== View menu actions ====================

    /** Toggles the visibility of the move history sidebar. */
    private void handleShowHistoryRequest() {
        boolean willShow = !historyView.isHistoryVisible();
        historyView.toggleVisibility();

        int preferredBoard = view.getPreferredSize().width;
        int minHistory = HistoryView.MIN_WIDTH;
        int currentW = frame.getWidth();

        if (willShow) {
            int needed = preferredBoard + minHistory;
            if (currentW < needed) frame.setSize(needed, frame.getHeight());
            splitPane.setDividerLocation(preferredBoard);
        } else {
            int historyWidth = splitPane.getWidth() - splitPane.getDividerLocation() - splitPane.getDividerSize();
            splitPane.setDividerLocation(splitPane.getWidth());
            frame.setSize(currentW - historyWidth, frame.getHeight());
        }

        // Update menu text
        JMenu viewMenu = frame.getJMenuBar().getMenu(1);
        JMenuItem historyItem = viewMenu.getItem(0);
        historyItem.setText(historyView.isHistoryVisible() ? "Hide History" : "Show History");
    }

    // ==================== Game menu actions ====================

    /** Handles the request to start a new game with a selected board size. */
    private void handleNewGameRequest() {
        int sizeSelection = promptForBoardSize();
        if (isValidSizeSelection(sizeSelection)) {
            updateBoardSizeFromSelection(sizeSelection);
            executeGameRestart();
        }
    }

    /** Prompts the user to select a board size for a new game.
     * @return index of the selected board size option, or -1 if cancelled.
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

    /** Validates the board size selection.
     * @param selection index of the selected board size option.
     * @return {@code true} if the selection is valid.
     */
    private boolean isValidSizeSelection(int selection) {
        return selection >= 0 && selection < BOARD_SIZE_OPTIONS.length;
    }

    /** Updates the board size based on the user's selection.
     * @param selectionIndex index of the selected board size option.
     */
    private void updateBoardSizeFromSelection(int selectionIndex) {
        boardSize = switch (selectionIndex) {
            case 0 -> TINY_BOARD_SIZE;
            case 1 -> BoardSize.SMALL.size();
            case 2 -> BoardSize.REGULAR.size();
            case 3 -> BoardSize.LARGE.size();
            default -> BoardSize.REGULAR.size();
        };
    }

    /** Handles the request to restart the game.
     *  Reinitializes components and updates the UI.
     */
    private void handleRestartRequest() {
        executeGameRestart();
    }

    /** Restarts the game with the current board size.
     *  Reinitializes components and updates the UI.
     */
    private void executeGameRestart() {
        initializeComponents();
        rebuildAfterGameChange();
    }

    // ==================== History & repaint ====================

    /** Updates the history view with the current move history. */
    private void updateHistoryDisplay() {
        historyView.updateHistory(game.getMoveHistory());
    }

    /** Revalidates and repaints the main frame after structural UI changes. */
    private void refreshWindow() {
        frame.revalidate();
        frame.repaint();
    }

    // ==================== Undo/Redo ====================

    /** Performs an Undo operation on the game, if possible. */
    private void handleUndoRequest() {
        try {
            game.undoLastMove();
            refreshBoardDisplay();
            updateHistoryDisplay();
        } catch (IllegalStateException ex) {
            showInfo("Undo", "No moves available to undo.");
        }
    }

    /** Performs a Redo operation on the game, if possible. */
    private void handleRedoRequest() {
        try {
            game.redoLastMove();
            refreshBoardDisplay();
            updateHistoryDisplay();
        } catch (IllegalStateException ex) {
            showInfo("Redo", "No moves available to redo.");
        }
    }

    // ==================== Import/Export ====================

    /** Prompts the user to export the current game state to a JSON file. */
    private void handleExportRequest() {
        JFileChooser chooser = createJsonFileChooser("Export Game");
        int choice = chooser.showSaveDialog(frame);
        if (choice == JFileChooser.APPROVE_OPTION) {
            executeGameExport(ensureJsonExtension(chooser.getSelectedFile()));
        } else {
            showInfo("Export Cancelled", "Export cancelled.");
        }
    }

    /** Prompts the user to import a game state from a JSON file. */
    private void handleImportRequest() {
        JFileChooser chooser = createJsonFileChooser("Import Game");
        int choice = chooser.showOpenDialog(frame);
        if (choice == JFileChooser.APPROVE_OPTION) {
            executeGameImport(chooser.getSelectedFile());
        } else {
            showInfo("Import Cancelled", "Import cancelled.");
        }
    }

    /** Creates a file chooser configured for JSON files.
     * @param title dialog title.
     * @return the configured chooser.
     */
    private JFileChooser createJsonFileChooser(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", JSON_EXT));
        return chooser;
    }

    /** Ensures the selected file has a .json extension.
     * @param file base file.
     * @return a file with the proper extension.
     */
    private File ensureJsonExtension(File file) {
        String name = file.getName().toLowerCase();
        if (!name.endsWith("." + JSON_EXT)) {
            return new File(file.getParentFile(), file.getName() + "." + JSON_EXT);
        }
        return file;
    }

    /** Loads a game from a JSON file and updates the UI.
     * @param targetFile the source file.
     */
    private void executeGameImport(File targetFile) {
        try {
            if (!targetFile.exists() || !targetFile.canRead()) {
                throw new IllegalArgumentException("Cannot read file: " + targetFile.getAbsolutePath());
            }
            String gameData = Files.readString(targetFile.toPath());
            this.game = Game.fromJson(gameData);
            rebuildAfterGameChange();
        } catch (Exception ex) {
            showError("Import Error", "Failed to import game: " + ex.getMessage());
        }
    }

    /** Saves the current game state to a JSON file.
     * @param targetFile destination file.
     */
    private void executeGameExport(File targetFile) {
        try {
            String gameData = game.toJson();
            Files.writeString(targetFile.toPath(), gameData);
            showInfo("Export Successful", "Game exported to: " + targetFile.getAbsolutePath());
        } catch (Exception ex) {
            showError("Export Error", "Failed to export game: " + ex.getMessage());
        }
    }

    /** Rebuilds view/listeners/history after the model (`game`) changes. */
    private void rebuildAfterGameChange() {
        // Replace BoardView with one bound to the imported/restarted board
        BoardView newView = new BoardView(this.game.getBoard());

        // Prevent stacked listeners on the old view
        detachBoardMouseHandlers();

        // Swap in split pane (or center area if splitPane absent)
        if (splitPane != null) {
            splitPane.setLeftComponent(newView);
        } else {
            frame.getContentPane().remove(view);
            frame.add(newView, BorderLayout.CENTER);
        }

        // Update ref and reattach handlers
        this.view = newView;
        attachBoardMouseHandler();

        // Sync history + UI
        updateHistoryDisplay();

        // Pack & keep divider behavior consistent
        frame.pack();
        if (splitPane != null) {
            if (historyView.isHistoryVisible()) {
                splitPane.setDividerLocation(view.getPreferredSize().width);
            } else {
                splitPane.setDividerLocation(splitPane.getWidth());
            }
        }
        refreshWindow();
    }

    // ==================== Dialog helpers ====================

    /** Shows a warning dialog.
     * @param title dialog title.
     * @param message dialog message.
     */
    private void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /** Shows an informational dialog.
     * @param title dialog title.
     * @param message dialog message.
     */
    private void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Shows an error dialog.
     * @param title dialog title.
     * @param message dialog message.
     */
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // ==================== Exit ====================

    /** Exits the application. */
    private void handleExitRequest() {
        System.exit(0);
    }
}

