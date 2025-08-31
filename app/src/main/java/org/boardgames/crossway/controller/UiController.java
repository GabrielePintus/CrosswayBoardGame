package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Game;
import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.utils.Messages;
import org.boardgames.crossway.ui.BoardHistorySplitPane;
import org.boardgames.crossway.ui.BoardView;
import org.boardgames.crossway.ui.HistoryView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller that manages the user interface components of the application.
 * <p>
 * Responsibilities include setting up the frame, managing the history panel and
 * updating view components when the underlying game changes.
 * </p>
 *
 * @see CrosswayController
 * @see BoardHistorySplitPane
 * @see BoardView
 * @see HistoryView
 */
public class UiController {

    /** The main application window frame. */
    private JFrame frame;
    /** The split pane that holds the board view and the history view. */
    private BoardHistorySplitPane splitPane;
    /** The view component for displaying the game board. */
    private BoardView boardView;
    /** The view component for displaying the game move history. */
    private HistoryView historyView;

    /**
     * Constructs a new {@code UiController}, initializing the UI components and setting up the user interface.
     *
     * @param controller The main application controller.
     * @param game The initial game model.
     */
    public UiController(CrosswayController controller, Game game) {
        initializeComponents(game);
        setupUserInterface(controller);
    }

    /**
     * Initializes the UI components, creating new ones if they don't exist and
     * setting up listeners.
     *
     * @param game The game model instance.
     */
    private void initializeComponents(Game game) {
        Board board = game.getBoard();
        if (splitPane == null) {
            splitPane = new BoardHistorySplitPane(new BoardView(board));
            boardView = splitPane.getBoardView();
            historyView = splitPane.getHistoryView();
            game.addBoardChangeListener(boardView);
        } else {
            splitPane.replaceBoard(board);
            boardView = splitPane.getBoardView();
            historyView = splitPane.getHistoryView();
            game.addBoardChangeListener(boardView);
        }
    }

    /**
     * Sets up the main user interface, including the main frame and adding the split pane.
     *
     * @param controller The main application controller.
     */
    private void setupUserInterface(CrosswayController controller) {
        frame = FrameFactory.createFrame(controller);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.pack();
        splitPane.setDividerLocation(splitPane.getWidth());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Replaces the board with a new one after a game import or restart. This method
     * is responsible for updating all UI components to reflect the new game state.
     *
     * @param game The new game instance.
     * @param boardClickHandler The callback to handle board clicks.
     */
    public void rebuildAfterGameChange(Game game, Consumer<Point> boardClickHandler) {
        splitPane.replaceBoard(game.getBoard());
        boardView = splitPane.getBoardView();
        historyView = splitPane.getHistoryView();
        game.addBoardChangeListener(boardView);
        boardView.setBoardClickCallback(boardClickHandler);
        updateHistoryDisplay(game.getMoveHistory());
        frame.pack();
        refreshWindow();
    }

    /**
     * Updates the display of the move history.
     *
     * @param history The list of moves to display.
     */
    public void updateHistoryDisplay(List<Move> history) {
        historyView.updateHistory(history);
    }

    /**
     * Handles a request to show or hide the move history panel.
     * It toggles the visibility, updates the UI, and adjusts the menu item text.
     */
    public void handleShowHistoryRequest() {
        historyView.toggleVisibility();
        if (historyView.isHistoryVisible()) {
            showHistory();
        } else {
            hideHistory();
        }
        splitPane.revalidate();
        splitPane.repaint();
        JMenu viewMenu = frame.getJMenuBar().getMenu(1);
        JMenuItem historyItem = viewMenu.getItem(0);
        historyItem.setText(splitPane.isHistoryVisible() ? Messages.get("menu.view.hideHistory") : Messages.get("menu.view.showHistory"));
    }

    /**
     * Shows the history panel by resizing the frame and setting the divider location.
     */
    private void showHistory() {
        int boardPrefWidth = splitPane.getBoardView().getPreferredSize().width;
        int dividerSize = splitPane.getDividerSize();
        int minHistoryWidth = HistoryView.MIN_WIDTH;
        int minTotalWidth = boardPrefWidth + dividerSize + minHistoryWidth;
        int currentContentWidth = splitPane.getWidth();

        if (currentContentWidth < minTotalWidth) {
            int extraNeeded = minTotalWidth - currentContentWidth;
            Dimension currentFrameSize = frame.getSize();
            frame.setSize(currentFrameSize.width + extraNeeded, currentFrameSize.height);
        }

        splitPane.setDividerLocation(boardPrefWidth);
    }

    /**
     * Hides the history panel by collapsing the split pane and resizing the frame.
     */
    private void hideHistory() {
        int fullWidth = splitPane.getWidth();
        splitPane.setDividerLocation(fullWidth);
        int boardWidth = splitPane.getBoardView().getPreferredSize().width;
        frame.setSize(boardWidth, frame.getHeight());
    }

    /**
     * Gets the main application frame.
     *
     * @return The {@link JFrame} instance.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Gets the board view component.
     *
     * @return The {@link BoardView} instance.
     */
    public BoardView getBoardView() {
        return boardView;
    }

    /**
     * Gets the history view component.
     *
     * @return The {@link HistoryView} instance.
     */
    public HistoryView getHistoryView() {
        return historyView;
    }

    /**
     * Refreshes the main window by revalidating and repainting it.
     */
    public void refreshWindow() {
        frame.revalidate();
        frame.repaint();
    }
}
