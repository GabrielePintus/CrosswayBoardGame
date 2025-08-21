package org.boardgames.crossway.ui;


import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.utils.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Objects;
import java.util.prefs.Preferences;

public class BoardHistorySplitPane extends JSplitPane {

    private BoardView boardView;
    private HistoryView historyView;


    public BoardHistorySplitPane(BoardView boardView, HistoryView historyView) {
        super(JSplitPane.HORIZONTAL_SPLIT, boardView, historyView);
        this.boardView = Objects.requireNonNull(boardView);
        this.historyView = Objects.requireNonNull(historyView);

        setLeftComponent(boardView);
        setRightComponent(historyView);

        setResizeWeight(0.5);
        setContinuousLayout(true);
        setOneTouchExpandable(true);
    }

    public BoardHistorySplitPane(BoardView boardView){
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






}