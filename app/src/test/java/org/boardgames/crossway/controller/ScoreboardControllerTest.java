package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Player;
import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.ui.ScoreboardView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ScoreboardController} class.
 * This class verifies the controller's logic for managing player scores,
 * names, and communicating updates to the view.
 */
class ScoreboardControllerTest {

    static {
        // Ensure a consistent locale and run in headless mode for AWT components.
        Locale.setDefault(Locale.US);
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * A stub implementation of the {@link ScoreboardView} interface for testing.
     * It captures the last text sent to the view instead of rendering it,
     * allowing for easy verification of updates.
     */
    private static class StubScoreboardView implements ScoreboardView {
        String lastText;
        final JLabel label = new JLabel();
        @Override
        public void update(String text) {
            lastText = text;
        }
        @Override
        public Component getComponent() {
            return label;
        }
    }

    /**
     * A stub implementation of the {@link DialogHandler} for testing.
     * It allows for pre-setting the player names that would be
     * "returned" by a dialog box.
     */
    private static class StubDialogHandler extends DialogHandler {
        String[] nextNames = {"Black", "White"};
        StubDialogHandler() { super(null); }
        @Override String[] askPlayerNames() { return nextNames; }
    }

    /**
     * Tests that a request to change players correctly resets the scores
     * and updates the scoreboard view with the new player names and zero scores.
     */
    @Test
    @DisplayName("Change players request resets scores and updates view")
    void changePlayersResetsScoresAndUpdatesView() {
        StubScoreboardView view = new StubScoreboardView();
        StubDialogHandler handler = new StubDialogHandler();
        ScoreboardController controller = new ScoreboardController("A", "B", view, handler);

        // Set up the next names that the stub handler will return
        handler.nextNames = new String[]{"C", "D"};
        controller.handleChangePlayersRequest();

        assertEquals("C 0 - D 0", view.lastText);
    }

    /**
     * Tests a sequence of operations including recording a win, swapping player colors,
     * resetting scores, and retrieving the player objects.
     * It verifies that each operation correctly modifies the internal state and
     * updates the view accordingly.
     */
    @Test
    @DisplayName("Record win, swap, reset, and get player methods work correctly")
    void recordWinSwapResetAndGetPlayerWork() {
        StubScoreboardView view = new StubScoreboardView();
        StubDialogHandler handler = new StubDialogHandler();
        ScoreboardController controller = new ScoreboardController("A", "B", view, handler);

        controller.recordWin(Stone.BLACK);
        assertEquals("A 1 - B 0", view.lastText);

        controller.swapPlayerColors();
        assertEquals("B 0 - A 1", view.lastText);

        controller.resetScores();
        assertEquals("B 0 - A 0", view.lastText);

        Player black = controller.getPlayer(Stone.BLACK);
        Player white = controller.getPlayer(Stone.WHITE);
        assertEquals("B", black.getName());
        assertEquals("A", white.getName());
    }
}