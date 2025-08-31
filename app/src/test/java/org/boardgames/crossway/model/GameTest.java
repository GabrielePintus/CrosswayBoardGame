package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.boardgames.crossway.model.rules.InvalidMoveException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Game class.
 */
public class GameTest {

    @Test
    @DisplayName("constructor with board sets defaults")
    void testConstructorWithBoard() {
        Board board = new Board(BoardSize.SMALL);
        Game game = new Game(board);
        assertEquals(board, game.getBoard(), "Board should match the provided");
        assertEquals(Stone.BLACK, game.getCurrentPlayer(), "Starting player should be BLACK");
        assertFalse(game.isPieAvailable(), "Pie rule should not be available initially");
        assertTrue(game.getMoveHistory().isEmpty(), "Move history should be empty initially");
    }

    @Test
    @DisplayName("constructor throws exception for null board")
    void testConstructorNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new Game((Board) null),
                "Constructor should throw IllegalArgumentException for null board");
    }

    @Test
    @DisplayName("constructor with board size creates correct board")
    void testConstructorWithBoardSize() {
        Game game = new Game(BoardSize.SMALL);
        assertEquals(9, game.getBoard().getSize().size(), "Board size should match the provided");
    }

    @Test
    @DisplayName("addBoardChangeListener notifies on change")
    void testAddBoardChangeListener() {
        Game game = new Game(BoardSize.SMALL);
        AtomicBoolean called = new AtomicBoolean(false);
        game.addBoardChangeListener(board -> called.set(true));
        game.makeMove(new Move(new Point(0, 0), Stone.BLACK));
        assertTrue(called.get(), "Listener should be notified after board change");
    }

    @Test
    @DisplayName("makeMove places stone and switches player")
    void testMakeMoveValid() {
        Game game = new Game(BoardSize.SMALL);
        Point point = new Point(0, 0);
        Move move = new Move(point, Stone.BLACK);
        game.makeMove(move);
        assertEquals(Optional.of(Stone.BLACK), game.getBoard().stoneAt(point), "Stone should be placed");
        assertEquals(Stone.WHITE, game.getCurrentPlayer(), "Player should switch after move");
        assertTrue(game.isPieAvailable(), "Pie rule should be available after first move");
        assertEquals(1, game.getMoveHistory().size(), "History should have one move");
    }

    @Test
    @DisplayName("makeMove throws exception for occupied cell")
    void testMakeMoveOccupied() {
        Game game = new Game(BoardSize.SMALL);
        Point point = new Point(0, 0);
        Move move = new Move(point, Stone.BLACK);
        game.makeMove(move);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove(move),
                "makeMove should throw IllegalArgumentException for occupied cell");
    }

    @Test
    @DisplayName("makeMove throws exception for out of bounds")
    void testMakeMoveOutOfBounds() {
        Game game = new Game(BoardSize.SMALL);
        Point point = new Point(9, 0);
        Move move = new Move(point, Stone.BLACK);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove(move),
                "makeMove should throw IllegalArgumentException for out of bounds");
    }

    @Test
    @DisplayName("skipTurn switches player")
    void testSkipTurn() {
        Game game = new Game(BoardSize.SMALL);
        game.skipTurn();
        assertEquals(Stone.WHITE, game.getCurrentPlayer(), "Player should switch after skip");
    }

    @Test
    @DisplayName("undoLastMove reverts state")
    void testUndoLastMove() {
        Game game = new Game(BoardSize.SMALL);
        Point point = new Point(0, 0);
        Move move = new Move(point, Stone.BLACK);
        game.makeMove(move);
        game.undoLastMove();
        assertEquals(Optional.empty(), game.getBoard().stoneAt(point), "Board should revert after undo");
        assertEquals(Stone.BLACK, game.getCurrentPlayer(), "Player should revert after undo");
        assertFalse(game.isPieAvailable(), "Pie rule should update after undo");
        assertTrue(game.getMoveHistory().isEmpty(), "History should be empty after undo");
    }

    @Test
    @DisplayName("undoLastMove throws exception when no moves")
    void testUndoNoMoves() {
        Game game = new Game(BoardSize.SMALL);
        assertThrows(IllegalStateException.class, game::undoLastMove,
                "undoLastMove should throw IllegalStateException when no moves");
    }

    @Test
    @DisplayName("redoLastMove reapplies state")
    void testRedoLastMove() {
        Game game = new Game(BoardSize.SMALL);
        Point point = new Point(0, 0);
        Move move = new Move(point, Stone.BLACK);
        game.makeMove(move);
        game.undoLastMove();
        game.redoLastMove();
        assertEquals(Optional.of(Stone.BLACK), game.getBoard().stoneAt(point), "Board should reapply after redo");
        assertEquals(Stone.WHITE, game.getCurrentPlayer(), "Player should switch after redo");
        assertTrue(game.isPieAvailable(), "Pie rule should update after redo");
    }

    @Test
    @DisplayName("redoLastMove throws exception when no redo available")
    void testRedoNoMoves() {
        Game game = new Game(BoardSize.SMALL);
        assertThrows(IllegalStateException.class, game::redoLastMove,
                "redoLastMove should throw IllegalStateException when no redo");
    }

    @Test
    @DisplayName("swapColors keeps stones and switches current player")
    void testSwapColors() {
        Game game = new Game(BoardSize.SMALL);
        Move move = new Move(new Point(0, 0), Stone.BLACK);
        game.makeMove(move);
        game.swapColors();
        assertEquals(Optional.of(Stone.BLACK), game.getBoard().stoneAt(new Point(0, 0)), "Stones should remain unchanged");
        assertEquals(Stone.WHITE, game.getCurrentPlayer(), "Turn should switch to white after swap");
        assertFalse(game.isPieAvailable(), "Pie rule should be disabled after swap");
    }

    @Test
    @DisplayName("swapColors leaves current player unchanged")
    void testSwapColorsKeepsCurrentPlayer() {
        Game game = new Game(BoardSize.SMALL);
        game.makeMove(new Move(new Point(0, 0), Stone.BLACK));
        Stone playerBeforeSwap = game.getCurrentPlayer();
        game.swapColors();
        assertEquals(playerBeforeSwap, game.getCurrentPlayer(), "Current player should remain the same after swap");
    }

    @Test
    @DisplayName("hasWon returns false initially")
    void testHasWonFalse() {
        Game game = new Game(BoardSize.SMALL);
        assertFalse(game.hasWon(Stone.BLACK), "No win for BLACK initially");
        assertFalse(game.hasWon(Stone.WHITE), "No win for WHITE initially");
    }

    @Test
    @DisplayName("hasWon returns true for BLACK connected north to south")
    void testHasWonBlack() {
        Game game = new Game(new BoardSize(3));
        Board board = game.getBoard();
        board.placeStone(new Point(1, 0), Stone.BLACK);
        board.placeStone(new Point(1, 1), Stone.BLACK);
        board.placeStone(new Point(1, 2), Stone.BLACK);
        assertTrue(game.hasWon(Stone.BLACK), "BLACK should win with vertical connection");
    }

    @Test
    @DisplayName("hasWon returns true for WHITE connected west to east")
    void testHasWonWhite() {
        Game game = new Game(new BoardSize(3));
        Board board = game.getBoard();
        board.placeStone(new Point(0, 1), Stone.WHITE);
        board.placeStone(new Point(1, 1), Stone.WHITE);
        board.placeStone(new Point(2, 1), Stone.WHITE);
        assertTrue(game.hasWon(Stone.WHITE), "WHITE should win with horizontal connection");
    }

    @Test
    @DisplayName("toJson serializes game state")
    void testToJson() {
        Game game = new Game(BoardSize.SMALL);
        Move move = new Move(new Point(0, 0), Stone.WHITE);
        game.makeMove(move);
        String json = game.toJson();
        assertTrue(json.contains("\"currentPlayer\":\"WHITE\""), "JSON should contain current player");
        assertTrue(json.contains("\"pieAvailable\":true"), "JSON should contain pie available");
    }

    @Test
    @DisplayName("fromJson deserializes game state")
    void testFromJson() {
        String json = "{\"board\":{\"size\":9,\"stones\":[{\"point\":{\"x\":0,\"y\":0},\"stone\":\"BLACK\"}]},\"history\":{\"pastMoves\":[{\"point\":{\"x\":0,\"y\":0},\"stone\":\"BLACK\"}]},\"currentPlayer\":\"WHITE\",\"pieAvailable\":true}";
        Game game = Game.fromJson(json);
        assertEquals(Stone.WHITE, game.getCurrentPlayer(), "Deserialized current player should match");
        assertTrue(game.isPieAvailable(), "Deserialized pie available should match");
        assertEquals(Optional.of(Stone.BLACK), game.getBoard().stoneAt(new Point(0, 0)), "Deserialized board should match");
    }

    // ===== Additional tests for rules, edge cases, and endgame =====

    @Test
    @DisplayName("canPlace returns false for occupied cell")
    void testCanPlaceOccupied() {
        Game game = new Game(BoardSize.SMALL);
        Point p = new Point(0, 0);
        game.makeMove(new Move(p, Stone.BLACK));
        assertFalse(game.canPlace(p, Stone.WHITE), "Cannot place on occupied cell");
    }

    @Test
    @DisplayName("canPlace returns false for out of bounds")
    void testCanPlaceOutOfBounds() {
        Game game = new Game(BoardSize.SMALL);
        assertFalse(game.canPlace(new Point(-1, 0), Stone.BLACK), "Out of bounds placement is illegal");
    }

    @Test
    @DisplayName("canPlace allows corner placement")
    void testCanPlaceCorner() {
        Game game = new Game(BoardSize.SMALL);
        assertTrue(game.canPlace(new Point(0, 0), Stone.BLACK), "Corner placement should be legal");
    }

    @Test
    @DisplayName("makeMove forbids creating diagonal X pattern")
    void testMakeMoveDiagonalX() {
        Board b = new Board(new BoardSize(4));
        b.placeStone(new Point(1, 1), Stone.BLACK);
        b.placeStone(new Point(2, 1), Stone.WHITE);
        b.placeStone(new Point(1, 2), Stone.WHITE);
        Game game = new Game(b);
        Move invalid = new Move(new Point(2, 2), Stone.BLACK);
        assertThrows(InvalidMoveException.class, () -> game.makeMove(invalid), "Diagonal X pattern must be rejected");
        assertTrue(game.getBoard().isEmpty(new Point(2, 2)), "Invalid move must not alter the board");
    }

    @Test
    @DisplayName("pie rule becomes unavailable after second move")
    void testPieRuleUnavailableAfterSecondMove() {
        Game game = new Game(BoardSize.SMALL);
        game.makeMove(new Move(new Point(0, 0), Stone.BLACK));
        assertTrue(game.isPieAvailable(), "Pie rule should be available after first move");
        game.makeMove(new Move(new Point(1, 0), Stone.WHITE));
        assertFalse(game.isPieAvailable(), "Pie rule should be disabled after second move");
    }

    @Test
    @DisplayName("hasLegalMove is false on a full board for both players")
    void testHasLegalMoveFullBoard() {
        BoardSize size = new BoardSize(2);
        Board board = new Board(size);
        board.placeStone(new Point(0, 0), Stone.BLACK);
        board.placeStone(new Point(0, 1), Stone.WHITE);
        board.placeStone(new Point(1, 0), Stone.WHITE);
        board.placeStone(new Point(1, 1), Stone.BLACK);
        Game game = new Game(board);
        assertFalse(game.hasLegalMove(Stone.BLACK), "Black should have no legal moves");
        assertFalse(game.hasLegalMove(Stone.WHITE), "White should have no legal moves");
    }

    @Test
    @DisplayName("hasLegalMove true with single empty cell then false after filling")
    void testHasLegalMoveOneEmptyCell() {
        Board board = new Board(new BoardSize(3));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (!(x == 0 && y == 0)) {
                    board.placeStone(new Point(x, y), Stone.WHITE);
                }
            }
        }
        Game game = new Game(board);
        assertTrue(game.hasLegalMove(Stone.BLACK), "Black should have one legal move");
        game.makeMove(new Move(new Point(0, 0), Stone.BLACK));
        assertFalse(game.hasLegalMove(Stone.WHITE), "White should have no moves on full board");
    }

    @Test
    @DisplayName("sequence of moves results in win detection")
    void testSequenceLeadsToWin() {
        Game game = new Game(new BoardSize(3));
        game.makeMove(new Move(new Point(1, 0), Stone.BLACK));
        game.makeMove(new Move(new Point(0, 0), Stone.WHITE));
        game.makeMove(new Move(new Point(1, 1), Stone.BLACK));
        game.makeMove(new Move(new Point(0, 1), Stone.WHITE));
        game.makeMove(new Move(new Point(1, 2), Stone.BLACK));
        assertTrue(game.hasWon(Stone.BLACK), "Black should win with vertical path");
    }
}