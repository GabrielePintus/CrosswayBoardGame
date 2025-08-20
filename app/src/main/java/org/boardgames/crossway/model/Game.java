package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.boardgames.crossway.model.rules.*;

import java.util.*;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Game implements Exportable {

    // ========== Constants ==========
    private static final Stone DEFAULT_STARTING_PLAYER = Stone.BLACK;
    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    // ========== Core Components ==========
    private Board board;
    @JsonIgnore // rebuilt from board when loading
    private final PatternChecker patternChecker;
    @JsonIgnore // rebuilt from board when loading
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
        this.patternChecker = createDefaultPatternChecker();
        this.connectivity = new Connectivity(board);
        this.connectivity.initFromBoard(board);
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

    /**
     * Constructs a game instance from a serialized JSON string.
     *
     * <p>This constructor is used for deserializing game state from
     * a JSON representation. It initializes the board, history, and
     * current player based on the provided JSON data.</p>
     * @throws IllegalArgumentException if json is null or empty
     */
    @JsonCreator
    public Game(
            @JsonProperty("board") Board board,
            @JsonProperty("history") History history,
            @JsonProperty("currentPlayer") Stone currentPlayer
    ) {
        if (board == null) throw new IllegalArgumentException("Board cannot be null");
        this.board = board;
        this.history = (history != null) ? history : new History();
        this.currentPlayer = (currentPlayer != null) ? currentPlayer : DEFAULT_STARTING_PLAYER;

        // rebuild transient components
        this.patternChecker = createDefaultPatternChecker();
        this.connectivity = new Connectivity(board);
        this.connectivity.initFromBoard(board);
    }

    // ========== Initialization Methods ==========

    /**
     * Creates the default pattern checker with standard game rules.
     *
     * @return a configured PatternChecker with default rules
     */
    private PatternChecker createDefaultPatternChecker() {
        List<PatternRule> defaultRules = List.of(
                new BoundsRule(),
                new EmptyRule(),
                new DiagonalXRule()
        );
        return new PatternChecker(defaultRules);
    }

    // ========== Move Validation ==========

    /**
     * Validates whether a stone can be legally placed at the specified position.
     *
     * <p>This method delegates comprehensive validation to the PatternChecker,
     * including bounds checking, cell occupancy verification, and pattern rule compliance.</p>
     *
     * @param point the board position to validate
     * @param stone the stone color to place
     * @return true if the placement is legal, false otherwise
     */
    public boolean canPlace(Point point, Stone stone) {
        return patternChecker.isAllowed(board, new Move(point, stone));
    }

    // ========== Placement ==========

    /**
     * Places a stone at the specified point and updates connectivity.
     *
     * @param point the position to place the stone
     * @param stone the stone to place
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
     * @throws InvalidMoveException if the move is invalid
     */
    public void makeMove(Move move) {
        Optional<PatternViolation> violation = patternChecker.firstViolation(board, move);
        if (violation.isPresent()) {
            throw new InvalidMoveException(violation.get());
        }

        // Execute the move
        place(move.getPoint(), move.getStone());

        // Record the move in history
        history.commit(move);

        // Switch to the next player
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
        currentPlayer = currentPlayer.opposite();
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

    @JsonProperty("board")
    public Board getBoard() { return board; }

    @JsonProperty("history")
    public History getHistory() { return history; }

    @JsonProperty("currentPlayer")
    public Stone getCurrentPlayer() { return currentPlayer; }


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
    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize Game", e);
        }
    }

    public static Game fromJson(String json) {
        try {
            Game game = MAPPER.readValue(json, Game.class);
            return game;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for Game", e);
        }
    }




}