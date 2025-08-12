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
 * @version 1.0
 * @since 1.0
 */
public class CrosswayController {

    // ==================== Constants ====================

    private static final String WINDOW_TITLE = "Crossway";

    private static final String[] BOARD_SIZE_OPTIONS = {
            "Tiny (5x5)", "Small (9x9)", "Regular (19x19)", "Large (25x25)"
    };

    private static final String[] WIN_DIALOG_OPTIONS = { "New Game", "Restart", "Exit" };

    private static final int DEFAULT_SIZE_INDEX = 2;     // Regular
    private static final int TINY_BOARD_SIZE = 5;        // Exact size for tiny
    private static final String JSON_EXT = "json";

    // ==================== State ====================

    private Game game;
    private BoardView view;
    private HistoryView historyView;
    private JSplitPane splitPane;
    private JFrame frame;
    private int boardSize;

    // ==================== Constructors ====================

    public CrosswayController() {
        this(BoardSize.REGULAR);
    }

    public CrosswayController(BoardSize size) {
        this(size.size());
    }

    public CrosswayController(int boardSize) {
        validateBoardSize(boardSize);
        this.boardSize = boardSize;

        initializeComponents();
        setupUserInterface();
        attachEventHandlers();
    }

    // ==================== Initialization ====================

    private void validateBoardSize(int size) {
        if (size < 3) {
            throw new IllegalArgumentException("Board size must be at least 3x3");
        }
    }

    private void initializeComponents() {
        game = new Game(new BoardSize(boardSize));
        view = new BoardView(game.getBoard());
        historyView = new HistoryView();
    }

    private void setupUserInterface() {
        createMainWindow();
        configureWindowProperties();
        installCenterContent();
        displayWindow();
    }

    private void createMainWindow() {
        frame = new JFrame(WINDOW_TITLE);
    }

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

    private void displayWindow() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ==================== Menu ====================

    private JMenuBar createApplicationMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        addLeftAlignedMenus(menuBar);
        menuBar.add(Box.createHorizontalGlue());
        addRightAlignedButtons(menuBar);

        return menuBar;
    }

    private void addLeftAlignedMenus(JMenuBar menuBar) {
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createGameMenu());
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("Import Game", this::handleImportRequest));
        fileMenu.add(createMenuItem("Export Game", this::handleExportRequest));
        return fileMenu;
    }

    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(createMenuItem("Show History", this::handleShowHistoryRequest));
        return viewMenu;
    }

    private JMenu createGameMenu() {
        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(createMenuItem("New Game", this::handleNewGameRequest));
        gameMenu.add(createMenuItem("Restart", this::handleRestartRequest));
        gameMenu.add(createMenuItem("Exit", this::handleExitRequest));
        return gameMenu;
    }

    private JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    private void addRightAlignedButtons(JMenuBar menuBar) {
        menuBar.add(createToolbarButton("Undo", this::handleUndoRequest));
        menuBar.add(createToolbarButton("Redo", this::handleRedoRequest));
    }

    private JButton createToolbarButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ==================== Event wiring ====================

    private void attachEventHandlers() {
        attachBoardMouseHandler();
    }

    private void detachBoardMouseHandlers() {
        for (var ml : view.getMouseListeners()) {
            view.removeMouseListener(ml);
        }
    }

    private void attachBoardMouseHandler() {
        view.addMouseListener(new BoardMouseHandler());
    }

    private class BoardMouseHandler extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            processMouseClick(e);
        }
    }

    // ==================== Input → Model ====================

    private void processMouseClick(MouseEvent mouseEvent) {
        Point boardCoordinate = convertMouseToBoardCoordinate(mouseEvent);
        Stone currentPlayer = game.getCurrentPlayer();

        if (attemptMoveExecution(boardCoordinate, currentPlayer)) {
            refreshBoardDisplay();
            checkForGameCompletion(currentPlayer);
        }
    }

    private Point convertMouseToBoardCoordinate(MouseEvent mouseEvent) {
        int cellSize = view.getCellSize();
        int boardX = mouseEvent.getX() / cellSize;
        int boardY = mouseEvent.getY() / cellSize;
        return new Point(boardX, boardY);
    }

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

    private void refreshBoardDisplay() {
        view.repaint();
    }

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

    private void processWinDialogChoice(int choice) {
        switch (choice) {
            case 0 -> handleNewGameRequest();
            case 1 -> handleRestartRequest();
            case 2 -> handleExitRequest();
            default -> { /* closed */ }
        }
    }

    // ==================== View menu actions ====================

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

    private void handleNewGameRequest() {
        int sizeSelection = promptForBoardSize();
        if (isValidSizeSelection(sizeSelection)) {
            updateBoardSizeFromSelection(sizeSelection);
            executeGameRestart();
        }
    }

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

    private boolean isValidSizeSelection(int selection) {
        return selection >= 0 && selection < BOARD_SIZE_OPTIONS.length;
    }

    private void updateBoardSizeFromSelection(int selectionIndex) {
        boardSize = switch (selectionIndex) {
            case 0 -> TINY_BOARD_SIZE;
            case 1 -> BoardSize.SMALL.size();
            case 2 -> BoardSize.REGULAR.size();
            case 3 -> BoardSize.LARGE.size();
            default -> BoardSize.REGULAR.size();
        };
    }

    private void handleRestartRequest() {
        executeGameRestart();
    }

    private void executeGameRestart() {
        initializeComponents();
        rebuildAfterGameChange();
    }

    // ==================== History & repaint ====================

    private void updateHistoryDisplay() {
        historyView.updateHistory(game.getMoveHistory());
    }

    private void refreshWindow() {
        frame.revalidate();
        frame.repaint();
    }

    // ==================== Undo/Redo ====================

    private void handleUndoRequest() {
        try {
            game.undoLastMove();
            refreshBoardDisplay();
            updateHistoryDisplay();
        } catch (IllegalStateException ex) {
            showInfo("Undo", "No moves available to undo.");
        }
    }

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

    private void handleExportRequest() {
        JFileChooser chooser = createJsonFileChooser("Export Game");
        int choice = chooser.showSaveDialog(frame);
        if (choice == JFileChooser.APPROVE_OPTION) {
            executeGameExport(ensureJsonExtension(chooser.getSelectedFile()));
        } else {
            showInfo("Export Cancelled", "Export cancelled.");
        }
    }

    private void handleImportRequest() {
        JFileChooser chooser = createJsonFileChooser("Import Game");
        int choice = chooser.showOpenDialog(frame);
        if (choice == JFileChooser.APPROVE_OPTION) {
            executeGameImport(chooser.getSelectedFile());
        } else {
            showInfo("Import Cancelled", "Import cancelled.");
        }
    }

    private JFileChooser createJsonFileChooser(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", JSON_EXT));
        return chooser;
    }

    private File ensureJsonExtension(File file) {
        String name = file.getName().toLowerCase();
        if (!name.endsWith("." + JSON_EXT)) {
            return new File(file.getParentFile(), file.getName() + "." + JSON_EXT);
        }
        return file;
    }

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

    private void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // ==================== Exit ====================

    private void handleExitRequest() {
        System.exit(0);
    }
}

