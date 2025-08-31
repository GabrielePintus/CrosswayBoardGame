package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.*;
import org.boardgames.crossway.model.rules.*;
import org.boardgames.crossway.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core game logic implementation for the Crossway board game.
 * Core game logic implementation for the Crossway board game.
 *
 * <p>Key features:</p>
 * <ul>
 * <li>Turn-based gameplay with automatic player switching.</li>
 * <li>Move validation using configurable pattern rules.</li>
 * <li>Efficient connectivity tracking for win detection.</li>
 * <li>Complete move history with undo/redo capabilities.</li>
 * <li>Game state serialization and export functionality.</li>
 * </ul>
 *
 * <p>The game follows standard Crossway rules where players alternate
 * placing stones on the board, with the goal of creating a connected
 * path of their stones between their designated opposite sides of the board.</p>
 *
 * @author Gabriele Pintus
 * @version 1.0
 * @see Board
 * @see PatternChecker
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Game {

    // ========== Constants ==========
    private static final Stone DEFAULT_STARTING_PLAYER = Stone.BLACK;

    // ========== Core Components ==========
    private Board board;
    @JsonIgnore // rebuilt from board when loading
    private final PatternChecker patternChecker;
    @JsonIgnore // rebuilt from board when loading
    private ConnectionChecker connectionChecker;
    private final History history;

    // ========== Game State ==========
    private Stone currentPlayer;
    private boolean pieAvailable;

    // ========== Listeners ==========
    @JsonIgnore
    private final List<BoardChangeListener> listeners = new ArrayList<>();

    // ========== Constructors ==========

    /**
     * Constructs a new game instance with the specified board.
     *
     * <p>Initializes all game components including connectivity tracking,
     * pattern checking rules, and move history. The game starts with
     * the black player by default.</p>
     *
     * @param board The game board on which to play. Must not be {@code null}.
     * @throws IllegalArgumentException if the board is {@code null}.
     */
    public Game(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }

        this.board = board;
        this.patternChecker = createDefaultPatternChecker();
        this.connectionChecker = new ConnectionChecker(board);
        this.history = new History();
        this.currentPlayer = DEFAULT_STARTING_PLAYER;
        this.pieAvailable = false;
    }

    /**
     * Constructs a new game instance with the specified board size.
     *
     * <p>This is a convenience constructor that creates a new board
     * with the given dimensions and then initializes the game.</p>
     *
     * @param boardSize The dimensions for the new board. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code boardSize} is {@code null}.
     */
    public Game(BoardSize boardSize) {
        this(new Board(boardSize));
    }

    /**
     * Constructs a game instance from a serialized JSON string.
     *
     * <p>This constructor is used by the Jackson library for deserializing
     * a game state from its JSON representation. It initializes the board,
     * history, and current player based on the provided JSON data. It also
     * rebuilds the transient components like the connectivity tracker and
     * pattern checker.</p>
     *
     * @param board The game board from the JSON.
     * @param history The move history from the JSON.
     * @param currentPlayer The current player from the JSON.
     * @throws IllegalArgumentException if the {@code board} is {@code null}.
     */
    @JsonCreator
    public Game(
            @JsonProperty("board") Board board,
            @JsonProperty("history") History history,
            @JsonProperty("currentPlayer") Stone currentPlayer,
            @JsonProperty("pieAvailable") Boolean pieAvailable
    ) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        this.board = board;
        this.history = (history != null) ? history : new History();
        this.currentPlayer = (currentPlayer != null) ? currentPlayer : DEFAULT_STARTING_PLAYER;
        this.pieAvailable = (pieAvailable != null) ? pieAvailable : false;

        // Rebuild transient components that are not part of the JSON.
        this.patternChecker = createDefaultPatternChecker();
        this.connectionChecker = new ConnectionChecker(board);
    }

    // ========== Listener Management ==========

    /**
     * Registers a listener to be notified when the board state changes.
     *
     * @param listener the listener to register
     */
    public void addBoardChangeListener(BoardChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Notifies all registered listeners that the board has changed.
     */
    private void notifyListeners() {
        for (BoardChangeListener listener : listeners) {
            listener.onBoardChange(board);
        }
    }

    // ========== Initialization Methods ==========

    /**
     * Creates and returns a {@link PatternChecker} instance with the default
     * set of game rules.
     *
     * <p>The default rules include:</p>
     * <ul>
     * <li>{@link BoundsRule}: Checks if the move is within the board boundaries.</li>
     * <li>{@link EmptyRule}: Checks if the target cell is empty.</li>
     * <li>{@link DiagonalXRule}: Checks for the "diagonal X" pattern violation.</li>
     * </ul>
     *
     * @return A new, configured {@link PatternChecker}.
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
     * <p>This method delegates comprehensive validation to the {@link PatternChecker},
     * which checks for bounds, cell occupancy, and compliance with all configured
     * pattern rules.</p>
     *
     * @param point The board position to validate.
     * @param stone The stone color to place.
     * @return {@code true} if the placement is legal, {@code false} otherwise.
     */
    public boolean canPlace(Point point, Stone stone) {
        return patternChecker.isAllowed(board, new Move(point, stone));
    }

    /**
     * Determines whether the given player has at least one legal move
     * available on the current board.
     *
     * <p>The method scans every intersection of the board and delegates the
     * validation to {@link #canPlace(Point, Stone)}. The search stops as soon
     * as a legal placement is found.</p>
     *
     * @param player the player to check for available moves
     * @return {@code true} if the player can place a stone somewhere on the
     *         board, {@code false} otherwise
     */
    public boolean hasLegalMove(Stone player) {
        int size = board.getSize().toInt();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (canPlace(new Point(x, y), player)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ========== Placement ==========

    /**
     * Attempts to make a move in the game.
     *
     * <p>This is the primary method for game progression. It first validates
     * the move. If the move is legal, it is executed, recorded in the history,
     * and the current player is switched. If the move is invalid, an
     * {@link InvalidMoveException} is thrown.</p>
     *
     * @param move The move to execute.
     * @throws InvalidMoveException if the move violates any game rule.
     */
    public void makeMove(Move move) {
        Optional<PatternViolation> violation = patternChecker.firstViolation(board, move);
        if (violation.isPresent()) {
            throw new InvalidMoveException(violation.get());
        }

        // Execute the move by placing the stone and updating connectivity.
        board.placeStone(move.getPoint(), move.getStone());

        // Record the move in the history.
        history.commit(move);
        updatePieAvailability();

        // Switch to the next player.
        currentPlayer = currentPlayer.opposite();

        notifyListeners();
    }

    /**
     * Switches the current player without modifying the board state.
     * This is used when a player must forfeit their turn due to having
     * no legal moves available.
     */
    public void skipTurn() {
        currentPlayer = currentPlayer.opposite();
        notifyListeners();
    }

    /**
     * Updates the availability of the pie rule swap option.
     *
     * <p>The pie rule allows the second player to swap colors after
     * the first move. This method checks the move history to determine
     * if the pie rule is still available (i.e., only one move has been made).</p>
     */
    private void updatePieAvailability() {
        pieAvailable = history.getPastMoves().size() == 1;
    }

    // ========== Undo/Redo Operations ==========

    /**
     * Undoes the most recent move in the game.
     *
     * <p>This method reverses the last move by:</p>
     * <ul>
     * <li>Retrieving the move from history.</li>
     * <li>Clearing the affected board position.</li>
     * <li>Rolling back the connectivity state to the previous checkpoint.</li>
     * <li>Restoring the previous player (the one who made the undone move).</li>
     * </ul>
     *
     * @throws IllegalStateException if no moves are available to undo.
     */
    public void undoLastMove() {
        Move lastMove = history.undo();

        if (lastMove == null) {
            throw new IllegalStateException("No moves available to undo");
        }

        // Reverse the changes on the board.
        board.clearCell(lastMove.getPoint());

        // The current player becomes the one who just had their move undone.
        currentPlayer = lastMove.getStone();

        updatePieAvailability();

        notifyListeners();
    }

    /**
     * Redoes the most recently undone move.
     *
     * <p>This method re-applies the last undone move by:</p>
     * <ul>
     * <li>Retrieving the move from history (which was already moved from future to past).</li>
     * <li>Placing the stone on the board.</li>
     * <li>Updating the connectivity state.</li>
     * <li>Switching to the next player.</li>
     * </ul>
     *
     * @throws IllegalStateException if no moves are available to redo.
     */
    public void redoLastMove() {
        Move move = history.redo();
        if (move == null) {
            throw new IllegalStateException("No moves available to redo");
        }

        // Check to ensure the target cell is empty before re-placing.
        if (board.stoneAt(move.getPoint()).isPresent()) {
            throw new IllegalStateException("Redo invariant violated: target cell is not empty");
        }

        // Re-apply the move. The history update is already handled by history.redo().
        board.placeStone(move.getPoint(), move.getStone());
        currentPlayer = currentPlayer.opposite();

        updatePieAvailability();

        notifyListeners();
    }

    /**
     * Swaps the colors of all stones on the board and in the move history,
     * and disables the pie rule without altering the current turn.
     */
    public void swapColors() {
        pieAvailable = false;
        notifyListeners();
    }


    // ========== Game State Queries ==========

    /**
     * Returns a copy of the complete past move history.
     *
     * @return A {@link List} of all moves in chronological order.
     */
    @JsonIgnore
    public List<Move> getMoveHistory() {
        return history.getPastMoves();
    }

    /**
     * Returns the current game board.
     *
     * @return The {@link Board} instance.
     */
    @JsonProperty("board")
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the move history.
     *
     * @return The {@link History} instance.
     */
    @JsonProperty("history")
    public History getHistory() {
        return history;
    }

    /**
     * Returns the stone of the current player.
     *
     * @return The {@link Stone} of the player whose turn it is.
     */
    @JsonProperty("currentPlayer")
    public Stone getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Indicates whether the pie rule swap is currently available.
     *
     * @return {@code true} if the swap option is available, {@code false} otherwise.
     */
    @JsonProperty("pieAvailable")
    public boolean isPieAvailable() {
        return pieAvailable;
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
     * @param stone The stone color to check for victory.
     * @return {@code true} if the specified color has won, {@code false} otherwise.
     */
    public boolean hasWon(Stone stone) {
        return connectionChecker.hasWon(stone);
    }

    // ========== Game Export ==========

    /**
     * Serializes the current game state into a JSON string.
     *
     * <p>This method uses the {@link JsonUtils} class to convert the game's
     * essential state (board, history, current player) into a JSON representation.
     * This format is suitable for saving games or transmitting state.</p>
     *
     * @return A JSON string representing the current game state.
     */
    public String toJson() {
        return JsonUtils.toJson(this);
    }

    /**
     * Deserializes a JSON string back into a {@code Game} instance.
     *
     * <p>This method uses the {@link JsonUtils} class to reconstruct a
     * {@code Game} object from its JSON representation. It is the
     * counterpart to {@link #toJson()}.</p>
     *
     * @param json The JSON string to deserialize.
     * @return A new {@code Game} instance with the state from the JSON string.
     */
    public static Game fromJson(String json) {
        return JsonUtils.fromJson(json, Game.class);
    }
}


