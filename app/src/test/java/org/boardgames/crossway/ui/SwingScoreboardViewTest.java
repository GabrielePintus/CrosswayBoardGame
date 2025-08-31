package org.boardgames.crossway.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwingScoreboardViewTest {

    @Test
    void updateSetsLabelTextAndComponent() {
        SwingScoreboardView view = new SwingScoreboardView();
        assertEquals("", view.getLabel().getText());

        String score = "Black 1 - White 0";
        view.update(score);
        assertEquals(score, view.getLabel().getText());
        assertSame(view.getLabel(), view.getComponent());
    }
}