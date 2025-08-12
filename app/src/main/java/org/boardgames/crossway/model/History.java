package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Linear history implementation with undo/redo functionality.
 *
 * <p>This class maintains a complete move history using a two-stack approach
 * that supports both undo and redo operations. When moves are undone, they
 * are moved to a "future moves" stack, allowing them to be redone. When new
 * moves are committed after undoing, the future moves are discarded to
 * maintain a consistent linear history.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Linear move history with bidirectional navigation</li>
 *   <li>Automatic future move clearing when branching from past states</li>
 *   <li>State validation methods for UI integration</li>
 *   <li>Complete history serialization including both stacks</li>
 * </ul>
 *
 * <p>History state transitions:</p>
 * <pre>
 * Initial: pastMoves=[], futureMoves=[]
 * After moves: pastMoves=[M1,M2,M3], futureMoves=[]
 * After undo: pastMoves=[M1,M2], futureMoves=[M3]
 * After redo: pastMoves=[M1,M2,M3], futureMoves=[]
 * After new move: pastMoves=[M1,M2,M4], futureMoves=[] (M3 discarded)
 * </pre>
 *
 * @author Your Name
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class History implements Exportable {

    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    // ========== Fields ==========
    private final Stack<Move> pastMoves;
    private final Stack<Move> futureMoves;

    // ========== Constructor ==========

    /**
     * Creates a new empty history instance.
     * Both past and future move stacks are initialized as empty.
     */
    public History() {
        this.pastMoves = new Stack<>();
        this.futureMoves = new Stack<>();
    }

    /**
     * Creates a new history instance with specified past and future moves.
     *
     * <p>Initializes the history with provided lists of past and future moves.
     * If either list is null, it defaults to an empty stack.</p>
     *
     * @param pastMoves initial list of past moves
     * @param futureMoves initial list of future moves
     */
    @JsonCreator
    public History(
            @JsonProperty("pastMoves") List<Move> pastMoves,
            @JsonProperty("futureMoves") List<Move> futureMoves
    ) {
        this();
        if (pastMoves != null) this.pastMoves.addAll(pastMoves);
        if (futureMoves != null) this.futureMoves.addAll(futureMoves);
    }

    // ========== Move Management ==========

    /**
     * Commits a new move to the history.
     *
     * <p>When a new move is committed, it is added to the past moves stack.
     * If there were any future moves (from previous undo operations), they
     * are discarded to maintain linear history consistency. This prevents
     * creating branching timelines.</p>
     *
     * @param move the move to add to history
     * @throws IllegalArgumentException if move is null
     */
    public void commit(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null");
        }

        // Clear future moves when committing new move after undo
        // This prevents branching history and maintains linearity
        futureMoves.clear();

        pastMoves.push(move);
    }

    /**
     * Undoes the most recent move in the history.
     *
     * <p>Moves the last move from the past stack to the future stack,
     * making it available for redo. If no moves are available to undo,
     * returns null.</p>
     *
     * @return the undone move, or null if no moves are available to undo
     */
    public Move undo() {
        if (pastMoves.isEmpty()) {
            return null;
        }

        Move lastMove = pastMoves.pop();
        futureMoves.push(lastMove);
        return lastMove;
    }

    /**
     * Redoes the most recently undone move.
     *
     * <p>Moves a move from the future stack back to the past stack,
     * effectively replaying it. If no moves are available to redo,
     * returns null.</p>
     *
     * @return the redone move, or null if no moves are available to redo
     */
    public Move redo() {
        if (futureMoves.isEmpty()) {
            return null;
        }

        Move nextMove = futureMoves.pop();
        pastMoves.push(nextMove);
        return nextMove;
    }


    /**
     * Gets a copy of the past moves for display purposes.
     *
     * @return list of past moves in chronological order
     */
    @JsonProperty("pastMoves")
    public List<Move> getPastMoves() {
        return new ArrayList<>(pastMoves);
    }

    /**
     * Gets a copy of the future moves for display purposes.
     *
     * <p>Future moves represent moves that can be redone after an undo operation.</p>
     *
     * @return list of future moves in reverse chronological order
     */
    @JsonProperty("futureMoves")
    public List<Move> getFutureMoves() {
        return new ArrayList<>(futureMoves);
    }


    /**
     * Serializes the history to a JSON string.
     *
     * <p>This method converts the entire history, including both past and future moves,
     * into a JSON string representation. It uses Jackson's ObjectMapper for serialization.</p>
     * * @return a JSON string representing the history
     * @throws IllegalStateException if serialization fails
     */
    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize History", e);
        }
    }

    /**
     * Parses a JSON string to create a History object.
     *
     * <p>This method deserializes the JSON representation of the history,
     * including both past and future moves, into a History instance.</p>
     *
     * @param json the JSON string to parse
     * @return a History object created from the JSON
     * @throws IllegalArgumentException if the JSON is invalid
     */
    public static History fromJson(String json) {
        try {
            return MAPPER.readValue(json, History.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for History", e);
        }
    }
}