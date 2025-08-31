package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import org.boardgames.crossway.ui.ScoreboardView;
import java.util.List;
import java.util.Optional;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GameController} class.
 * This class verifies the controller's logic for handling game events,
 * board clicks, and interacting with the game model and views.
 */
class GameControllerTest {

    static {
        // Set a default locale to ensure consistent message retrieval in tests
        Locale.setDefault(Locale.US);
    }

    /**
     * A mock implementation of the {@link GameEvents} interface for testing purposes.
     * It tracks method calls and allows for setting return values for dialogs,
     * which avoids the need for a real UI during tests.
     */
    static class MockGameEvents implements GameEvents {
        int historyCalls;
        int pieDialogCalls;
        int winDialogCalls;
        boolean pieDialogResult;
        int winDialogResult;
        int infoCalls;
        int warningCalls;

        @Override
        public void refreshHistory(List<Move> history) {
            historyCalls++;
        }

        @Override
        public void refreshWindow() {
            // no-op for tests
        }

        @Override
        public boolean showPieDialog() {
            pieDialogCalls++;
            return pieDialogResult;
        }

        @Override
        public int showWinDialog(Player winner) {
            winDialogCalls++;
            return winDialogResult;
        }

        @Override
        public void showInfo(String title, String message) {
            infoCalls++;
        }

        @Override
        public void showWarning(String title, String message) {
            warningCalls++;
        }
    }

    /**
     * Tests that a valid board click by the first player (BLACK) triggers a history
     * refresh and presents the pie rule dialog.
     */
    @Test
    @DisplayName("Board click triggers history refresh and pie dialog")
    void processBoardClickTriggersHistoryRefreshAndPieDialog() {
        Game game = new Game(new BoardSize(3));
        ScoreboardView view = new ScoreboardView() {
            @Override public void update(String text) {}
            @Override public java.awt.Component getComponent() { return new JLabel(); }
        };
        ScoreboardController sb = new ScoreboardController("A", "B", view, null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(0, 0));

        assertEquals(1, events.historyCalls);
        assertEquals(1, events.pieDialogCalls);
    }

    /**
     * Tests that a board click resulting in a win condition correctly triggers the
     * win dialog.
     */
    @Test
    @DisplayName("Board click triggers win dialog when win condition is met")
    void processBoardClickTriggersWinDialog() {
        // Mock the Game to immediately return true for hasWon
        Game game = new Game(new BoardSize(3)) {
            @Override
            public boolean hasWon(Stone player) {
                return true;
            }

            @Override
            public boolean isPieAvailable() {
                return false;
            }
        };
        ScoreboardView view = new ScoreboardView() {
            @Override public void update(String text) {}
            @Override public java.awt.Component getComponent() { return new JLabel(); }
        };
        ScoreboardController sb = new ScoreboardController("A", "B", view, null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(0, 0));

        assertEquals(1, events.winDialogCalls);
    }

    /**
     * Tests that a valid board click places a stone on the board and correctly
     * switches the current player's turn.
     */
    @Test
    @DisplayName("Board click places stone and switches player")
    void processBoardClickPlacesStoneOnBoard() {
        Game game = new Game(new BoardSize(3));
        ScoreboardView view = new ScoreboardView() {
            @Override public void update(String text) {}
            @Override public java.awt.Component getComponent() { return new JLabel(); }
        };
        ScoreboardController sb = new ScoreboardController("A", "B", view, null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(0, 0));

        assertEquals(Optional.of(Stone.BLACK), game.getBoard().stoneAt(new Point(0,0)));
        assertEquals(Stone.WHITE, game.getCurrentPlayer());
    }

    /**
     * Tests that the game automatically skips a player's turn if they have no legal moves,
     * and shows an informational message to the user.
     */
    @Test
    @DisplayName("Board click skips turn and shows info dialog when no legal moves exist")
    void processBoardClickSkipsTurnWhenNoLegalMove() {
        Locale.setDefault(Locale.US);
        // Set up a full board where no legal moves exist for the current player
        Board board = new Board(new BoardSize(2));
        board.placeStone(new Point(0, 0), Stone.BLACK);
        board.placeStone(new Point(0, 1), Stone.WHITE);
        board.placeStone(new Point(1, 0), Stone.WHITE);
        board.placeStone(new Point(1, 1), Stone.BLACK);
        Game game = new Game(board);
        ScoreboardView view = new ScoreboardView() {
            @Override
            public void update(String text) {
            }

            @Override
            public java.awt.Component getComponent() {
                return new JLabel();
            }
        };
        ScoreboardController sb = new ScoreboardController("A", "B", view, null) {
            @Override
            public void refreshScoreboard() {
            }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, () -> {
        }, () -> {
        }, () -> {
        }, sb);

        Stone before = game.getCurrentPlayer();
        controller.processBoardClick(new Point(0, 0));

        assertEquals(1, events.infoCalls);
        assertEquals(before.opposite(), game.getCurrentPlayer());
    }

    /**
     * Tests that the game can handle the last remaining move on a board
     * correctly, placing the stone and switching the player.
     */
    @Test
    @DisplayName("Board click handles a single remaining move correctly")
    void processBoardClickHandlesSingleRemainingMove() {
        Board board = new Board(new BoardSize(2));
        board.placeStone(new Point(0,0), Stone.BLACK);
        board.placeStone(new Point(0,1), Stone.WHITE);
        board.placeStone(new Point(1,0), Stone.BLACK);
        Game game = new Game(board);
        game.skipTurn(); // set current player to WHITE
        ScoreboardView view = new ScoreboardView() {
            @Override public void update(String text) {}
            @Override public java.awt.Component getComponent() { return new JLabel(); }
        };
        ScoreboardController sb = new ScoreboardController("A", "B", view, null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(1,1));

        assertEquals(Optional.of(Stone.WHITE), game.getBoard().stoneAt(new Point(1,1)));
        assertEquals(Stone.BLACK, game.getCurrentPlayer());
    }

    /**
     * Tests that a move which completes a winning path at the board's edge
     * correctly triggers the win dialog.
     */
    @Test
    @DisplayName("Win detection at board border triggers win dialog")
    void winDetectionAtBoardBorderTriggersDialog() {
        Board board = new Board(new BoardSize(3));
        board.placeStone(new Point(1,0), Stone.BLACK);
        board.placeStone(new Point(1,1), Stone.BLACK);
        Game game = new Game(board);
        ScoreboardView view = new ScoreboardView() {
            @Override public void update(String text) {}
            @Override public java.awt.Component getComponent() { return new JLabel(); }
        };
        ScoreboardController sb = new ScoreboardController("A", "B", view, null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(1,2));

        assertEquals(1, events.winDialogCalls);
    }
}