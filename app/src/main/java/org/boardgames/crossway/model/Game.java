package org.boardgames.crossway.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core game logic implementation for the Crossway board game.
 *
 * <p>This class manages the complete game state and enforces game rules.
 * It utilizes a Union-Find data structure through the Connectivity class
 * to efficiently track stone connectivity patterns and determine win conditions.
 * The game supports move validation, history tracking with undo functionality,
 * and pattern-based rule checking.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Turn-based gameplay with automatic player switching</li>
 *   <li>Move validation using configurable pattern rules</li>
 *   <li>Efficient connectivity tracking for win detection</li>
 *   <li>Complete move history with undo/redo capabilities</li>
 *   <li>Game state serialization and export functionality</li>
 * </ul>
 *
 * <p>The game follows standard Crossway rules where players alternate
 * placing stones on the board, with the goal of creating a connected
 * path between opposite sides of the board.</p>
 *
 * @author Your Name
 * @version 1.0
 * @see Board
 * @see Connectivity
 * @see PatternChecker
 */
public class Game implements Exportable {

    // ========== Constants ==========
    private static final Stone DEFAULT_STARTING_PLAYER = Stone.BLACK;
    private static final String INVALID_PLACEMENT_MESSAGE = "Invalid point for placing stone: ";

    // ========== Core Components ==========
    private Board board;
    private final PatternChecker patternChecker;
    private final Connectivity connectivity;
    private final History history;

    // ========== Game State ==========
    private Stone currentPlayer;

    // ========== Constructors ==========

    /**
     * Constructs a new game instance with the specified board.
     *
     * <p>Initializes all game components including connectivity tracking,
     * pattern checking rules, and move history. The game starts with
     * the black player by default.</p>
     *
     * @param board the game board on which to play
     * @throws IllegalArgumentException if the board is null
     */
    public Game(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }

        this.board = board;
        this.connectivity = new Connectivity(board);
        this.patternChecker = createDefaultPatternChecker();
        this.history = new History();
        this.currentPlayer = DEFAULT_STARTING_PLAYER;
    }

    /**
     * Constructs a new game instance with the specified board size.
     *
     * <p>This is a convenience constructor that creates a new board
     * with the given dimensions and initializes the game.</p>
     *
     * @param boardSize the dimensions for the new board
     * @throws IllegalArgumentException if boardSize is null
     */
    public Game(BoardSize boardSize) {
        this(new Board(boardSize));
    }

    // ========== Initialization Methods ==========

    /**
     * Creates the default pattern checker with standard game rules.
     *
     * @return a configured PatternChecker with default rules
     */
    private PatternChecker createDefaultPatternChecker() {
        List<PatternRule> defaultRules = List.of(new DiagonalXRule());
        return new PatternChecker(defaultRules);
    }

    // ========== Move Validation ==========

    /**
     * Validates whether a stone can be legally placed at the specified position.
     *
     * <p>This method performs comprehensive validation including:</p>
     * <ul>
     *   <li>Position bounds checking</li>
     *   <li>Cell occupancy verification</li>
     *   <li>Pattern rule compliance</li>
     * </ul>
     *
     * @param point the board position to validate
     * @param stone the stone color to place
     * @return true if the placement is legal, false otherwise
     */
    public boolean canPlace(Point point, Stone stone) {
        // Validate position is within board bounds
        if (!board.isOnBoard(point)) {
            return false;
        }

        // Verify the cell is empty
        if (board.stoneAt(point).isPresent()) {
            return false;
        }

        // Check pattern rules compliance
        Move proposedMove = new Move(point, stone);
        return patternChecker.isAllowed(board, proposedMove);
    }

    // ========== Move Execution ==========

    /**
     * Executes a validated move by placing a stone on the board.
     *
     * <p>This method handles the low-level placement mechanics including
     * creating a connectivity checkpoint for potential rollback, updating
     * the board state, and maintaining connectivity information.</p>
     *
     * @param point the position where to place the stone
     * @param stone the stone color to place
     */
    private void place(Point point, Stone stone) {
        // Create checkpoint for potential undo operation
        connectivity.checkpoint();

        // Update board state
        board.placeStone(point, stone);

        // Update connectivity information
        connectivity.onPlace(point, stone);
    }

    /**
     * Attempts to make a move in the game.
     *
     * <p>This is the primary method for game progression. It validates
     * the move, executes it if legal, records it in history, and switches
     * to the next player. If the move is invalid, an exception is thrown.</p>
     *
     * @param move the move to execute
     * @throws IllegalArgumentException if the move is invalid
     */
    public void makeMove(Move move) {
        Point movePoint = move.getPoint();
        Stone moveStone = move.getStone();

        // Validate the proposed move
        if (!canPlace(movePoint, moveStone)) {
            throw new IllegalArgumentException(INVALID_PLACEMENT_MESSAGE + movePoint);
        }

        // Execute the move
        place(movePoint, moveStone);

        // Record the move in history
        history.commit(move);

        // Switch to the next player
        switchToNextPlayer();
    }

    /**
     * Switches the current player to the opposite color.
     */
    private void switchToNextPlayer() {
        currentPlayer = currentPlayer.opposite();
    }

    // ========== Undo Operations ==========

    /**
     * Undoes the most recent move in the game.
     *
     * <p>This method reverses the last move by:</p>
     * <ul>
     *   <li>Retrieving the move from history</li>
     *   <li>Clearing the affected board position</li>
     *   <li>Rolling back connectivity state</li>
     *   <li>Restoring the previous player</li>
     * </ul>
     *
     * @throws IllegalStateException if no moves are available to undo
     */
    public void undoLastMove() {
        Move lastMove = history.undo();

        if (lastMove == null) {
            throw new IllegalStateException("No moves available to undo");
        }

        // Reverse the board changes
        board.clearCell(lastMove.getPoint());

        // Restore connectivity state
        connectivity.rollback();

        // Restore previous player (the one who made the undone move)
        currentPlayer = lastMove.getStone();
    }

    /**
     * Redoes the most recently undone move.
     *
     * <p>This method re-applies the last undone move by:</p>
     * <ul>
     *   <li>Retrieving the move from history</li>
     *   <li>Placing the stone on the board</li>
     *   <li>Updating connectivity state</li>
     *   <li>Switching to the next player</li>
     * </ul>
     *
     * @throws IllegalStateException if no moves are available to redo
     */
    public void redoLastMove() {
        Move m = history.redo();
        if (m == null) {
            throw new IllegalStateException("No moves available to redo");
        }

        // (Optional) sanity check: the cell must be empty after an undo
        if (board.stoneAt(m.getPoint()).isPresent()) {
            throw new IllegalStateException("Redo invariant violated: target cell is not empty");
        }

        // Re-apply the move without committing to history (history.redo() already did)
        place(m.getPoint(), m.getStone());  // creates checkpoint, updates board & connectivity
        switchToNextPlayer();
    }

    // ========== Game State Queries ==========

    /**
     * Gets the complete move history as a list.
     *
     * @return list of moves in chronological order
     */
    public List<Move> getMoveHistory() {
        return history.getPastMoves();
    }

    /**
     * Gets the stone color of the player whose turn it is.
     *
     * @return the current player's stone color
     */
    public Stone getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the game board instance.
     *
     * @return the underlying board
     */
    public Board getBoard() {
        return board;
    }

    // ========== Win Condition Checking ==========

    /**
     * Determines if the specified stone color has achieved victory.
     *
     * <p>A player wins by forming a connected path of their stones
     * that spans between the designated opposite sides of the board.
     * This method delegates to the connectivity system for efficient
     * path detection.</p>
     *
     * @param stone the stone color to check for victory
     * @return true if the specified color has won, false otherwise
     */
    public boolean hasWon(Stone stone) {
        return connectivity.hasWon(stone);
    }

    // ========== Game Export ==========

    /**
     * Serializes the current game state into a string representation.
     *
     * <p>The encoded string contains essential game information including
     * the current player and complete move history. This format can be
     * used for saving games, network transmission, or debugging purposes.</p>
     *
     * <p>The encoding format follows the pattern:</p>
     * <pre>Game{currentPlayer=COLOR, history=ENCODED_HISTORY}</pre>
     *
     * @return a string representation of the current game state
     */
    @Override
    public String encode() {
        StringBuilder gameStateBuilder = new StringBuilder();

        gameStateBuilder.append("Game{")
                .append("currentPlayer=").append(currentPlayer)
                .append(", history=").append(history.encode())
                .append("}");

        return gameStateBuilder.toString();
    }
}