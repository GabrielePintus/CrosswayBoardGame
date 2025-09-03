package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.boardgames.crossway.model.rules.InvalidMoveException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Game} class, covering its core functionalities from initialization
 * and move management to game rules, state persistence, and win condition checks.
 */
class GameTest {

    /**
     * Tests the {@link Game#Game(Board)} constructor.
     * Verifies that the game is initialized with the provided board, the correct starting player (BLACK),
     * and a clean state (empty history, pie rule unavailable).
     */
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

    /**
     * Tests the {@link Game#Game(Board)} constructor with a null board.
     * Verifies that it throws an {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("constructor throws exception for null board")
    void testConstructorNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new Game((Board) null),
                "Constructor should throw IllegalArgumentException for null board");
    }

    /**
     * Tests the {@link Game#Game(BoardSize)} constructor.
     * Verifies that it correctly creates a board of the specified size.
     */
    @Test
    @DisplayName("constructor with board size creates correct board")
    void testConstructorWithBoardSize() {
        Game game = new Game(BoardSize.SMALL);
        assertEquals(9, game.getBoard().getSize().size(), "Board size should match the provided");
    }

    /**
     * Tests the {@link Game#addBoardChangeListener(BoardChangeListener)} method.
     * Verifies that a registered listener is notified when the board state changes, such as after a move.
     */
    @Test
    @DisplayName("addBoardChangeListener notifies on change")
    void testAddBoardChangeListener() {
        Game game = new Game(BoardSize.SMALL);
        AtomicBoolean called = new AtomicBoolean(false);
        game.addBoardChangeListener(board -> called.set(true));
        game.makeMove(new Move(new Point(0, 0), Stone.BLACK));
        assertTrue(called.get(), "Listener should be notified after board change");
    }

    /**
     * Tests the {@link Game#makeMove(Move)} method with a valid move.
     * Verifies that the stone is placed on the board, the current player switches, the pie rule becomes available,
     * and the move is recorded in the history.
     */
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

    /**
     * Tests the {@link Game#makeMove(Move)} method's handling of an attempt to place a stone
     * on an already occupied cell.
     * Verifies that it throws an {@link IllegalArgumentException}.
     */
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

    /**
     * Tests the {@link Game#makeMove(Move)} method's handling of an attempt to place a stone
     * out of the board's bounds.
     * Verifies that it throws an {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("makeMove throws exception for out of bounds")
    void testMakeMoveOutOfBounds() {
        Game game = new Game(BoardSize.SMALL);
        Point point = new Point(9, 0);
        Move move = new Move(point, Stone.BLACK);
        assertThrows(IllegalArgumentException.class, () -> game.makeMove(move),
                "makeMove should throw IllegalArgumentException for out of bounds");
    }

    /**
     * Tests the {@link Game#skipTurn()} method.
     * Verifies that the current player is switched without any stone placement.
     */
    @Test
    @DisplayName("skipTurn switches player")
    void testSkipTurn() {
        Game game = new Game(BoardSize.SMALL);
        game.skipTurn();
        assertEquals(Stone.WHITE, game.getCurrentPlayer(), "Player should switch after skip");
    }

    /**
     * Tests the {@link Game#undoLastMove()} method.
     * Verifies that the board state and current player are correctly reverted to the state before the last move.
     */
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

    /**
     * Tests the {@link Game#undoLastMove()} method when the history is empty.
     * Verifies that it throws an {@link IllegalStateException}.
     */
    @Test
    @DisplayName("undoLastMove throws exception when no moves")
    void testUndoNoMoves() {
        Game game = new Game(BoardSize.SMALL);
        assertThrows(IllegalStateException.class, game::undoLastMove,
                "undoLastMove should throw IllegalStateException when no moves");
    }

    /**
     * Tests the {@link Game#redoLastMove()} method.
     * Verifies that a previously undone move is correctly reapplied, and the game state is restored.
     */
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

    /**
     * Tests the {@link Game#redoLastMove()} method when there are no moves to redo.
     * Verifies that it throws an {@link IllegalStateException}.
     */
    @Test
    @DisplayName("redoLastMove throws exception when no redo available")
    void testRedoNoMoves() {
        Game game = new Game(BoardSize.SMALL);
        assertThrows(IllegalStateException.class, game::redoLastMove,
                "redoLastMove should throw IllegalStateException when no redo");
    }

    /**
     * Tests the {@link Game#hasWon(Stone)} method at the start of the game.
     * Verifies that no player has won on an empty board.
     */
    @Test
    @DisplayName("hasWon returns false initially")
    void testHasWonFalse() {
        Game game = new Game(BoardSize.SMALL);
        assertFalse(game.hasWon(Stone.BLACK), "No win for BLACK initially");
        assertFalse(game.hasWon(Stone.WHITE), "No win for WHITE initially");
    }

    /**
     * Tests the {@link Game#hasWon(Stone)} method for a winning condition for the BLACK stone.
     * Verifies that a continuous path of BLACK stones from the top to the bottom of the board is correctly detected as a win.
     */
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

    /**
     * Tests the {@link Game#hasWon(Stone)} method for a winning condition for the WHITE stone.
     * Verifies that a continuous path of WHITE stones from the left to the right of the board is correctly detected as a win.
     */
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

    /**
     * Tests the serialization functionality of the {@link Game#toJson()} method.
     * Verifies that the resulting JSON string contains key game state information.
     */
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

    /**
     * Tests the deserialization functionality of the {@link Game#fromJson(String)} method.
     * Verifies that a game object created from a JSON string correctly restores the game state.
     */
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

    /**
     * Tests the {@link Game#canPlace(Point, Stone)} method for an occupied cell.
     * Verifies that placing a stone on an occupied cell is correctly identified as an illegal move.
     */
    @Test
    @DisplayName("canPlace returns false for occupied cell")
    void testCanPlaceOccupied() {
        Game game = new Game(BoardSize.SMALL);
        Point p = new Point(0, 0);
        game.makeMove(new Move(p, Stone.BLACK));
        assertFalse(game.canPlace(p, Stone.WHITE), "Cannot place on occupied cell");
    }

    /**
     * Tests the {@link Game#canPlace(Point, Stone)} method for a point outside the board boundaries.
     * Verifies that such a placement is correctly identified as illegal.
     */
    @Test
    @DisplayName("canPlace returns false for out of bounds")
    void testCanPlaceOutOfBounds() {
        Game game = new Game(BoardSize.SMALL);
        assertFalse(game.canPlace(new Point(-1, 0), Stone.BLACK), "Out of bounds placement is illegal");
    }

    /**
     * Tests the {@link Game#canPlace(Point, Stone)} method for a valid corner placement.
     * Verifies that a move to a corner is initially legal.
     */
    @Test
    @DisplayName("canPlace allows corner placement")
    void testCanPlaceCorner() {
        Game game = new Game(BoardSize.SMALL);
        assertTrue(game.canPlace(new Point(0, 0), Stone.BLACK), "Corner placement should be legal");
    }

    /**
     * Tests the rule that forbids creating a diagonal X pattern of stones.
     * Verifies that an attempt to make a move that would form this pattern results in an {@link InvalidMoveException}.
     */
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

    /**
     * Tests the availability of the pie rule.
     * Verifies that the pie rule is only available after the first move and is disabled thereafter.
     */
    @Test
    @DisplayName("pie rule becomes unavailable after second move")
    void testPieRuleUnavailableAfterSecondMove() {
        Game game = new Game(BoardSize.SMALL);
        game.makeMove(new Move(new Point(0, 0), Stone.BLACK));
        assertTrue(game.isPieAvailable(), "Pie rule should be available after first move");
        game.makeMove(new Move(new Point(1, 0), Stone.WHITE));
        assertFalse(game.isPieAvailable(), "Pie rule should be disabled after second move");
    }

    /**
     * Tests the {@link Game#hasLegalMove(Stone)} method on a full board.
     * Verifies that no legal moves exist for either player when the board is completely filled.
     */
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

    /**
     * Tests the {@link Game#hasLegalMove(Stone)} method with a single empty cell.
     * Verifies that a legal move is detected and that after that move, no further legal moves exist.
     */
    @Test
    @DisplayName("hasLegalMove true with single empty cell then false after filling")
    void testHasLegalMoveOneEmptyCell() {
        // Arrange
        final BoardSize smallBoard = new BoardSize(3);
        final Point lastEmptyPosition = new Point(0, 0);
        final Stone fillStone = Stone.WHITE;
        final Stone testStone = Stone.BLACK;

        Board board = createAlmostFullBoard(smallBoard, lastEmptyPosition, fillStone);
        Game game = new Game(board);

        // Act & Assert - Before filling last position
        assertTrue(game.hasLegalMove(testStone),
                "Player should have exactly one legal move when only one cell is empty");

        // Act - Fill the last empty position
        Move finalMove = new Move(lastEmptyPosition, testStone);
        game.makeMove(finalMove);

        // Assert - After filling last position (test both players)
        Stream.of(Stone.WHITE, Stone.BLACK)
                .forEach(stone ->
                        assertFalse(game.hasLegalMove(stone),
                                stone + " should have no legal moves on completely filled board"));
    }

    /**
     * Creates a board that is completely filled except for one position.
     *
     * @param boardSize The size of the board to create
     * @param emptyPosition The position to leave empty
     * @param fillStone The stone color to use for filling all other positions
     * @return A board with all positions filled except the specified empty position
     */
    private Board createAlmostFullBoard(BoardSize boardSize, Point emptyPosition, Stone fillStone) {
        Board board = new Board(boardSize);

        getAllBoardPositions(boardSize)
                .stream()
                .filter(position -> !position.equals(emptyPosition))
                .forEach(position -> board.placeStone(position, fillStone));

        return board;
    }

    /**
     * Generates all valid positions on a board of the given size.
     *
     * @param boardSize The size of the board
     * @return A list of all positions on the board
     */
    private List<Point> getAllBoardPositions(BoardSize boardSize) {
        int size = boardSize.size();
        List<Point> positions = new ArrayList<>();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                positions.add(new Point(x, y));
            }
        }

        return positions;
    }

    /**
     * Creates a board that is completely filled except for one position.
     * Alternative implementation using streams for a more functional approach.
     */
    private Board createAlmostFullBoardStreams(BoardSize boardSize, Point emptyPosition, Stone fillStone) {
        Board board = new Board(boardSize);
        int size = boardSize.size();

        IntStream.range(0, size)
                .boxed()
                .flatMap(x -> IntStream.range(0, size)
                        .mapToObj(y -> new Point(x, y)))
                .filter(position -> !position.equals(emptyPosition))
                .forEach(position -> board.placeStone(position, fillStone));

        return board;
    }

    /**
     * Creates a board that is completely filled except for one position.
     * Uses enhanced for-each loops for better readability.
     */
    private Board createAlmostFullBoardEnhanced(BoardSize boardSize, Point emptyPosition, Stone fillStone) {
        Board board = new Board(boardSize);
        List<Point> allPositions = getAllBoardPositions(boardSize);

        for (Point position : allPositions) {
            if (!position.equals(emptyPosition)) {
                board.placeStone(position, fillStone);
            }
        }

        return board;
    }


    /**
     * Tests a sequence of moves that leads to a win.
     * Verifies that the {@link Game#hasWon(Stone)} method correctly identifies the winning condition after a series of valid moves.
     */
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