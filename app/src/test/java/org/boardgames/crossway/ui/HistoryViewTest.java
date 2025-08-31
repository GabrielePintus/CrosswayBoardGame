package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.utils.Messages;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class HistoryViewTest {

    @Test
    void toggleVisibilityChangesStateAndComponentVisibility() {
        HistoryView view = new HistoryView();
        assertFalse(view.isHistoryVisible());
        assertFalse(view.isVisible());

        view.toggleVisibility();
        assertTrue(view.isHistoryVisible());
        assertTrue(view.isVisible());

        view.toggleVisibility();
        assertFalse(view.isHistoryVisible());
        assertFalse(view.isVisible());
    }

    @Test
    void updateHistoryPopulatesMoveList() {
        HistoryView view = new HistoryView();
        List<Move> moves = List.of(
                new Move(new Point(1, 2), Stone.BLACK),
                new Move(new Point(3, 4), Stone.WHITE)
        );

        view.updateHistory(moves);

        JScrollPane scrollPane = (JScrollPane) view.getComponent(1);
        @SuppressWarnings("unchecked")
        JList<String> list = (JList<String>) scrollPane.getViewport().getView();
        ListModel<String> model = list.getModel();

        assertEquals(2, model.getSize());
        assertEquals("1. ● (1,2)", model.getElementAt(0));
        assertEquals("2. ○ (3,4)", model.getElementAt(1));
    }

    @Test
    void updateLanguageRefreshesTitle() {
        Locale original = Locale.getDefault();
        Messages.setLocale(Locale.US);
        HistoryView view = new HistoryView();

        Messages.setLocale(Locale.GERMANY);
        view.updateLanguage();

        JLabel label = (JLabel) ((JPanel) view.getComponent(0)).getComponent(0);
        assertEquals(Messages.get("history.title"), label.getText());

        Messages.setLocale(original);
    }
}