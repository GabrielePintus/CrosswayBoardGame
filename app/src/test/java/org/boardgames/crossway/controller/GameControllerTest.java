package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.util.List;
import java.util.Optional;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

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

    @Test
    void processBoardClickTriggersHistoryRefreshAndPieDialog() {
        Game game = new Game(new BoardSize(3));
        ScoreboardController sb = new ScoreboardController("A", "B", new JLabel(), null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(0, 0));

        assertEquals(1, events.historyCalls);
        assertEquals(1, events.pieDialogCalls);
    }

    @Test
    void processBoardClickTriggersWinDialog() {
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
        ScoreboardController sb = new ScoreboardController("A", "B", new JLabel(), null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(0, 0));

        assertEquals(1, events.winDialogCalls);
    }

    @Test
    void processBoardClickPlacesStoneOnBoard() {
        Game game = new Game(new BoardSize(3));
        ScoreboardController sb = new ScoreboardController("A", "B", new JLabel(), null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        controller.processBoardClick(new Point(0, 0));

        assertEquals(Optional.of(Stone.BLACK), game.getBoard().stoneAt(new Point(0,0)));
        assertEquals(Stone.WHITE, game.getCurrentPlayer());
    }

    @Test
    void processBoardClickSkipsTurnWhenNoLegalMove() {
        Locale.setDefault(Locale.US);
        Board board = new Board(new BoardSize(2));
        board.placeStone(new Point(0,0), Stone.BLACK);
        board.placeStone(new Point(0,1), Stone.WHITE);
        board.placeStone(new Point(1,0), Stone.WHITE);
        board.placeStone(new Point(1,1), Stone.BLACK);
        Game game = new Game(board);
        ScoreboardController sb = new ScoreboardController("A", "B", new JLabel(), null) {
            @Override public void refreshScoreboard() { }
        };
        MockGameEvents events = new MockGameEvents();
        GameController controller = new GameController(game, events, ()->{}, ()->{}, ()->{}, sb);

        Stone before = game.getCurrentPlayer();
        controller.processBoardClick(new Point(0,0));

        assertEquals(1, events.infoCalls);
        assertEquals(before.opposite(), game.getCurrentPlayer());
    }
}

