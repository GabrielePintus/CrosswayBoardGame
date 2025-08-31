package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.BoardSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardHistorySplitPaneTest {

    @Test
    void toggleHistoryFlipsVisibility() {
        BoardHistorySplitPane pane = new BoardHistorySplitPane(new BoardView(new Board(BoardSize.SMALL)));

        assertFalse(pane.isHistoryVisible());
        pane.toggleHistory();
        assertTrue(pane.isHistoryVisible());
        pane.toggleHistory();
        assertFalse(pane.isHistoryVisible());
    }

    @Test
    void showAndHideHistoryControlVisibility() {
        BoardHistorySplitPane pane = new BoardHistorySplitPane(new BoardView(new Board(BoardSize.SMALL)));

        pane.showHistory();
        assertTrue(pane.isHistoryVisible());
        pane.hideHistory();
        assertFalse(pane.isHistoryVisible());
    }

    @Test
    void replaceBoardUpdatesBoardViewAndHistorySize() {
        BoardHistorySplitPane pane = new BoardHistorySplitPane(new BoardView(new Board(BoardSize.SMALL)));
        BoardView original = pane.getBoardView();

        pane.replaceBoard(new Board(BoardSize.LARGE));
        BoardView replaced = pane.getBoardView();

        assertNotSame(original, replaced);
        assertEquals(replaced.getPreferredSize().height, pane.getHistoryView().getPreferredSize().height);
    }
}
