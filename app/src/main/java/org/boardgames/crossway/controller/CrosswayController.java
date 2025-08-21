package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.ui.BoardView;
import org.boardgames.crossway.ui.HistoryView;
import org.boardgames.crossway.utils.GameSerializer;
import org.boardgames.crossway.utils.Messages;
import org.boardgames.crossway.utils.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
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

    /** The title displayed in the main application window. */
    private static final String WINDOW_TITLE = Settings.get("app.name");

    /** Predefined board size options presented to the user. */
    private static final String[] BOARD_SIZE_OPTIONS = Messages.getPrefixedArray("menu.game.boardSize");


    /** Options presented in the win dialog after a game ends. */
    private static final String[] WIN_DIALOG_OPTIONS = {
            Messages.get("menu.game.new"),
            Messages.get("menu.game.restart"),
            Messages.get("menu.file.exit")
    };

    /** The default index for board size selection, corresponding to "Regular". */
    private static final int DEFAULT_SIZE_INDEX = Integer.parseInt(Settings.get("board.defaultSizeIndex"));

    /** The file extension used for saving and loading game files. */
//    private static final String JSON_EXT = "json";
    private static final String JSON_EXT = Settings.get("files.defaultExtension");

    // ==================== State ====================

    /** The core game model that contains the board, history, and rules. */
    private Game game;

    /** The graphical view component that renders the game board. */
    private BoardView view;

    /** The view component that displays the move history. */
    private HistoryView historyView;

    /** The split pane that separates the board view from the history view. */
    private JSplitPane splitPane;

    /** The main application window. */
    private JFrame frame;

    /** The current dimension of the square board. */
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

        initializeComponents();
        setupUserInterface();
        attachEventHandlers();
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

    /**
     * Initializes all the core model and view components.
     * This method does not yet configure their layout or relationships.
     */
    private void initializeComponents() {
        game = new Game(new BoardSize(boardSize));
        view = new BoardView(game.getBoard());
        historyView = new HistoryView();
    }

    /**
     * Builds and lays out the entire application's user interface.
     */
    private void setupUserInterface() {
        createMainWindow();
        configureWindowProperties();
        installCenterContent();
        displayWindow();
    }

    /**
     * Creates the main application window.
     */
    private void createMainWindow() {
        frame = new JFrame(WINDOW_TITLE);
    }

    /**
     * Applies standard frame properties such as default close operation and menu bar.
     */
    private void configureWindowProperties() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setJMenuBar(createApplicationMenuBar());
    }

    /**
     * Creates and installs the central split pane containing the board and history.
     */
    private void installCenterContent() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, view, historyView);
        splitPane.setResizeWeight(0.0);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(false);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.pack();

        // Start with the history view hidden by default.
        splitPane.setDividerLocation(splitPane.getWidth());
    }

    /**
     * Packs the frame and makes it visible on the screen.
     */
    private void displayWindow() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ==================== Menu Bar ====================

    /**
     * Creates the application's menu bar with all its menus and buttons.
     *
     * @return A configured {@link JMenuBar} instance.
     */
    private JMenuBar createApplicationMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        addLeftAlignedMenus(menuBar);
        menuBar.add(Box.createHorizontalGlue());
        addRightAlignedButtons(menuBar);
        return menuBar;
    }

    /**
     * Adds the "File", "View", and "Game" menus to the left side of the menu bar.
     *
     * @param menuBar The menu bar to add menus to.
     */
    private void addLeftAlignedMenus(JMenuBar menuBar) {
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createGameMenu());
    }

    /**
     * Creates the "File" menu with options for import, export, and exit.
     *
     * @return The configured "File" menu.
     */
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu(Messages.get("menu.file"));
        // Language submenu
        fileMenu.add(createLanguageSubmenu());
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem(Messages.get("menu.file.import"), this::handleImportRequest));
        fileMenu.add(createMenuItem(Messages.get("menu.file.export"), this::handleExportRequest));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem(Messages.get("menu.file.exit"), this::handleExitRequest));
        return fileMenu;
    }

    /**
     * Creates the language selection submenu.
     *
     * @return The configured language submenu.
     */
    private JMenu createLanguageSubmenu() {
        JMenu languageMenu = new JMenu(Messages.get("menu.file.language"));

        languageMenu.add(createMenuItem(Messages.get("menu.lang.en"), () -> handleLanguageChange(Locale.forLanguageTag("en-US"))));
        languageMenu.add(createMenuItem(Messages.get("menu.lang.it"), () -> handleLanguageChange(Locale.forLanguageTag("it-IT"))));
        languageMenu.add(createMenuItem(Messages.get("menu.lang.de"), () -> handleLanguageChange(Locale.forLanguageTag("de-DE"))));

        return languageMenu;
    }

    /**
     * Handles the request to switch the application's language.
     * This method updates the locale and refreshes the UI.
     *
     * @param newLocale The new locale to switch to
     */
    private void handleLanguageChange(Locale newLocale) {
        // Update the Messages locale
        Messages.setLocale(newLocale);

        // Recreate the menu bar with updated text
        frame.setJMenuBar(createApplicationMenuBar());

        // Update history view language
        historyView.updateLanguage();

        // Refresh the entire window to reflect language changes
        frame.revalidate();
        frame.repaint();
    }


    /**
     * Creates the "View" menu with options to toggle history visibility.
     *
     * @return The configured "View" menu.
     */
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu(Messages.get("menu.view"));
        viewMenu.add(createMenuItem(Messages.get("menu.view.showHistory"), this::handleShowHistoryRequest));
        return viewMenu;
    }

    /**
     * Creates the "Game" menu with options for new game, restart, undo, and redo.
     *
     * @return The configured "Game" menu.
     */
    private JMenu createGameMenu() {
        JMenu gameMenu = new JMenu(Messages.get("menu.game"));
        gameMenu.add(createMenuItem(Messages.get("menu.game.new"), this::handleNewGameRequest));
        gameMenu.add(createMenuItem(Messages.get("menu.game.restart"), this::handleRestartRequest));
        return gameMenu;
    }

    /**
     * A utility method to create a menu item with a specified text and action.
     *
     * @param text   The label for the menu item.
     * @param action The action to be performed when the item is clicked.
     * @return The created {@link JMenuItem}.
     */
    private JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    /**
     * Adds right-aligned buttons to the menu bar for quick access to actions.
     *
     * @param menuBar The menu bar to add buttons to.
     */
    private void addRightAlignedButtons(JMenuBar menuBar) {
        menuBar.add(createToolbarButton(Messages.get("menu.toolbar.undo"), this::handleUndoRequest));
        menuBar.add(createToolbarButton(Messages.get("menu.toolbar.redo"), this::handleRedoRequest));
    }

    /**
     * Creates a button styled for the menu bar's right side.
     *
     * @param label  The text to display on the button.
     * @param action The action to execute on a click.
     * @return A configured {@link JButton}.
     */
    private JButton createToolbarButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ==================== Event Handling ====================

    /**
     * Attaches all necessary event listeners to the components.
     */
    private void attachEventHandlers() {
        attachBoardMouseHandler();
    }

    /**
     * Removes all existing mouse listeners from the board view to prevent duplicates.
     */
    private void detachBoardMouseHandlers() {
        for (var listener : view.getMouseListeners()) {
            view.removeMouseListener(listener);
        }
    }

    /**
     * Installs a mouse listener on the board view to handle user clicks.
     */
    private void attachBoardMouseHandler() {
        view.addMouseListener(new BoardMouseHandler());
    }

    /**
     * A private inner class that handles mouse events on the board view.
     */
    private class BoardMouseHandler extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            processMouseClick(e);
        }
    }

    // ==================== Input Processing ====================

    /**
     * Processes a mouse click event by attempting to make a move.
     *
     * @param mouseEvent The mouse event containing the click location.
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
     * Converts a mouse click's screen coordinates to a logical board coordinate.
     *
     * @param mouseEvent The event containing the pixel location.
     * @return The corresponding {@link Point} on the board.
     */
    private Point convertMouseToBoardCoordinate(MouseEvent mouseEvent) {
        int cellSize = view.getCellSize();
        int boardX = mouseEvent.getX() / cellSize;
        int boardY = mouseEvent.getY() / cellSize;
        return new Point(boardX, boardY);
    }

    /**
     * Attempts to execute a move by calling the game model.
     *
     * @param position The board coordinate of the move.
     * @param player   The player making the move.
     * @return {@code true} if the move was valid and executed, {@code false} otherwise.
     */
    private boolean attemptMoveExecution(Point position, Stone player) {
        try {
            game.makeMove(new Move(position, player));
            updateHistoryDisplay();
            return true;
        } catch (IllegalArgumentException ex) {
            showWarning(Messages.get("error.invalidMove"), ex.getMessage());
            return false;
        }
    }

    /**
     * Checks if the current player has won the game.
     *
     * @param currentPlayer The player to check for a win condition.
     */
    private void checkForGameCompletion(Stone currentPlayer) {
        if (game.hasWon(currentPlayer)) {
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    currentPlayer + Messages.get("game.wins"),
                    Messages.get("game.over"),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    WIN_DIALOG_OPTIONS,
                    WIN_DIALOG_OPTIONS[1]
            );
            processWinDialogChoice(choice);
        }
    }

    /**
     * Handles the user's choice from the win dialog.
     *
     * @param choice The index of the selected option.
     */
    private void processWinDialogChoice(int choice) {
        switch (choice) {
            case 0 -> handleNewGameRequest();
            case 1 -> handleRestartRequest();
            case 2 -> handleExitRequest();
            default -> { /* Dialog closed */ }
        }
    }

    // ==================== Menu Actions ====================

    /**
     * Toggles the visibility of the move history sidebar.
     */
    private void handleShowHistoryRequest() {
        boolean willShow = !historyView.isHistoryVisible();
        historyView.toggleVisibility();

        int preferredBoardWidth = view.getPreferredSize().width;
        int minHistoryWidth = HistoryView.MIN_WIDTH;
        int currentWindowWidth = frame.getWidth();

        if (willShow) {
            int neededWidth = preferredBoardWidth + minHistoryWidth + splitPane.getDividerSize();
            if (currentWindowWidth < neededWidth) {
                frame.setSize(neededWidth, frame.getHeight());
            }
            splitPane.setDividerLocation(preferredBoardWidth);
        } else {
            int historyWidth = splitPane.getWidth() - splitPane.getDividerLocation() - splitPane.getDividerSize();
            splitPane.setDividerLocation(splitPane.getWidth());
            frame.setSize(currentWindowWidth - historyWidth, frame.getHeight());
        }

        // Update the menu item text to reflect the new state.
        JMenu viewMenu = frame.getJMenuBar().getMenu(1);
        JMenuItem historyItem = viewMenu.getItem(0);
        historyItem.setText(historyView.isHistoryVisible() ? Messages.get("menu.view.hideHistory") : Messages.get("menu.view.showHistory"));
    }

    /**
     * Handles the request to start a new game by prompting the user for a board size.
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
     * @return The index of the selected option, or -1 if the dialog was cancelled.
     */
    private int promptForBoardSize() {
        return JOptionPane.showOptionDialog(
                frame,
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
    private void handleRestartRequest() {
        executeGameRestart();
    }

    /**
     * Resets the game to its initial state using the current board size.
     */
    private void executeGameRestart() {
        initializeComponents();
        rebuildAfterGameChange();
    }

    // ==================== History & Display ====================

    /**
     * Updates the history view with the current move history from the model.
     */
    private void updateHistoryDisplay() {
        historyView.updateHistory(game.getMoveHistory());
    }

    /**
     * Repaints the board view to reflect the latest game state.
     */
    private void refreshBoardDisplay() {
        view.repaint();
    }

    /**
     * Revalidates and repaints the entire window.
     */
    private void refreshWindow() {
        frame.revalidate();
        frame.repaint();
    }

    // ==================== Undo/Redo Actions ====================

    /**
     * Attempts to undo the last move in the game.
     */
    private void handleUndoRequest() {
        try {
            game.undoLastMove();
            refreshBoardDisplay();
            updateHistoryDisplay();
        } catch (IllegalStateException ex) {
            showInfo(
                    Messages.get("menu.toolbar.undo"),
                    Messages.get("warning.toolbar.undo")
            );
        }
    }

    /**
     * Attempts to redo the last undone move.
     */
    private void handleRedoRequest() {
        try {
            game.redoLastMove();
            refreshBoardDisplay();
            updateHistoryDisplay();
        } catch (IllegalStateException ex) {
            showInfo(
                    Messages.get("menu.toolbar.redo"),
                    Messages.get("warning.toolbar.redo")
            );
        }
    }

    // ==================== Import/Export ====================

    /**
     * Prompts the user to save the current game state to a JSON file.
     */
    private void handleExportRequest() {
        JFileChooser chooser = createJsonFileChooser(Messages.get("menu.file.export"));
        int choice = chooser.showSaveDialog(frame);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            executeGameExport(ensureJsonExtension(selectedFile));
        }
    }

    /**
     * Prompts the user to load a game state from a JSON file.
     */
    private void handleImportRequest() {
        JFileChooser chooser = createJsonFileChooser(Messages.get("menu.file.import"));
        int choice = chooser.showOpenDialog(frame);
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
                Messages.get("file.filter.json"), JSON_EXT
        );
        chooser.setFileFilter(filter);
        return chooser;
    }

    /**
     * Ensures that a file has the correct JSON extension.
     *
     * @param file The file to check.
     * @return A {@link File} object with the proper extension.
     */
    private File ensureJsonExtension(File file) {
        String name = file.getName().toLowerCase();
        if (!name.endsWith("." + JSON_EXT)) {
            return new File(file.getParentFile(), file.getName() + "." + JSON_EXT);
        }
        return file;
    }

    /**
     * Loads a game from a specified JSON file and updates the game state.
     *
     * @param targetFile The file to import.
     */
    private void executeGameImport(File targetFile) {
        try {
            this.game = GameSerializer.load(targetFile);
            rebuildAfterGameChange();
        } catch (Exception ex) {
            showError(
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
            showInfo(
                    Messages.get("export.success.title"),
                    Messages.format("export.success.message", targetFile.getName())
            );
        } catch (Exception ex) {
            showError(
                    Messages.get("error.export.title"),
                    Messages.format("error.export.message", ex.getMessage())
            );
        }

    }

    /**
     * Rebuilds the user interface and reattaches event handlers after a
     * significant game state change (e.g., import or restart).
     */
    private void rebuildAfterGameChange() {
        // Create a new BoardView bound to the new game model.
        BoardView newView = new BoardView(this.game.getBoard());

        // Detach old listeners to prevent them from firing on the new view.
        detachBoardMouseHandlers();

        // Swap the views within the split pane.
        if (splitPane != null) {
            splitPane.setLeftComponent(newView);
        } else {
            frame.getContentPane().remove(view);
            frame.add(newView, BorderLayout.CENTER);
        }

        // Update the reference to the new view and re-attach handlers.
        this.view = newView;
        attachBoardMouseHandler();

        // Sync history and UI display.
        updateHistoryDisplay();

        // Pack the frame and maintain the divider's position.
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

    // ==================== Dialog Helpers ====================

    /**
     * Displays a warning dialog to the user.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    private void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays an informational dialog to the user.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    private void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error dialog to the user.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // ==================== Exit ====================

    /**
     * Handles the request to exit the application.
     */
    private void handleExitRequest() {
        System.exit(0);
    }
}

