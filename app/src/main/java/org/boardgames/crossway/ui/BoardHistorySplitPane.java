package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * A custom {@link JSplitPane} that contains the game board and the move history view.
 * <p>
 * This component manages the layout and visibility of the board and history
 * side-by-side. It also provides methods to toggle the history view's visibility,
 * repaint its sub-components, and replace the board view with a new one.
 * </p>
 *
 * @author Gabriele Pintus
 */
public class BoardHistorySplitPane extends JSplitPane {

    /** The view component that renders the game board. */
    private BoardView boardView;
    /** The view component that displays the move history. */
    private HistoryView historyView;

    /**
     * Constructs a new BoardHistorySplitPane with the specified board and history views.
     *
     * @param boardView   The component that displays the game board.
     * @param historyView The component that displays the move history.
     */
    public BoardHistorySplitPane(BoardView boardView, HistoryView historyView) {
        super(JSplitPane.HORIZONTAL_SPLIT, boardView, historyView);
        this.boardView = Objects.requireNonNull(boardView);
        this.historyView = Objects.requireNonNull(historyView);

        int prefHeight = boardView.getPreferredSize().height;
        historyView.setPreferredSize(new Dimension(0 , prefHeight));
        historyView.setMinimumSize(new Dimension(0, 0));

        setResizeWeight(0);
        setContinuousLayout(true);
        setOneTouchExpandable(true);
    }

    /**
     * Constructs a new BoardHistorySplitPane with a specified board view and a
     * default {@link HistoryView}.
     *
     * @param boardView The component that displays the game board.
     */
    public BoardHistorySplitPane(BoardView boardView) {
        this(boardView, new HistoryView());
    }

    /**
     * Replaces the current board view with a new one based on the provided board.
     *
     * @param newBoard The new board model to render.
     */
    public void replaceBoard(Board newBoard) {
        this.boardView = new BoardView(newBoard);
        setLeftComponent(this.boardView);

        int prefHeight = boardView.getPreferredSize().height;
        historyView.setPreferredSize(new Dimension(historyView.getPreferredSize().width, prefHeight));
    }

    /**
     * Gets the current board view component.
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
     * Checks if the history view is currently visible.
     *
     * @return true if the history view is visible, false otherwise.
     */
    public boolean isHistoryVisible() {
        return historyView.isHistoryVisible();
    }

    /**
     * Toggles the visibility of the history view.
     * If visible, it expands to show move history; if hidden, it collapses.
     * Adjusts the divider location accordingly.
     */
    public void toggleHistory() {
        historyView.toggleVisibility();
        if (historyView.isHistoryVisible()) {
            // Calculate and set divider for shown state
            int preferredBoardWidth = boardView.getPreferredSize().width;
            setDividerLocation(preferredBoardWidth);
        } else {
            // Hide by moving divider to end
            setDividerLocation(getWidth());
        }
        revalidate();
        repaint();
    }

    /**
     * Repaints the history view to reflect any changes.
     */
    public void repaintHistory() {
        historyView.repaint();
    }

    /**
     * Repaints the board view to reflect any changes.
     */
    public void repaintBoard() {
        boardView.repaint();
    }

    /**
     * Repaints both the board and history views.
     */
    public void repaintAll() {
        boardView.repaint();
        historyView.repaint();
    }
}