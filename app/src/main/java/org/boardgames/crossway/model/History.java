package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A linear history implementation with undo and redo functionality.
 *
 * <p>This class maintains a complete move history using a two-stack approach:
 * one for "past" moves and one for "future" moves. This design supports both
 * undoing and redoing moves. When moves are undone, they are transferred
 * from the {@code pastMoves} stack to the {@code futureMoves} stack. If a new
 * move is committed after an undo operation, the {@code futureMoves} stack is
 * cleared to ensure the history remains linear and does not branch.</p>
 *
 * <p>Key features:</p>
 * <ul>
 * <li>Linear move history with bidirectional navigation.</li>
 * <li>Automatic clearing of future moves when a new move is committed after an undo.</li>
 * <li>Serialization and deserialization support for JSON.</li>
 * </ul>
 *
 * <p>History state transitions:</p>
 * <pre>
 * Initial state:
 * pastMoves=[], futureMoves=[]
 *
 * After committing moves M1, M2, M3:
 * pastMoves=[M1, M2, M3], futureMoves=[]
 *
 * After one undo operation:
 * pastMoves=[M1, M2], futureMoves=[M3]
 *
 * After one redo operation:
 * pastMoves=[M1, M2, M3], futureMoves=[]
 *
 * After one undo and then committing a new move M4:
 * pastMoves=[M1, M2, M4], futureMoves=[] (M3 is discarded)
 * </pre>
 *
 * @author Gabriele Pintus
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class History {

    /**
     * Stack to store moves that have been played.
     */
    private final Stack<Move> pastMoves;

    /**
     * Stack to store moves that have been undone and can be redone.
     */
    private final Stack<Move> futureMoves;

    /**
     * Creates a new, empty history instance.
     * Both the past and future move stacks are initialized as empty.
     */
    public History() {
        this.pastMoves = new Stack<>();
        this.futureMoves = new Stack<>();
    }

    /**
     * Creates a new history instance with specified past and future moves.
     * This constructor is primarily used by the Jackson library for deserialization.
     *
     * @param pastMoves A list of moves representing the past history. Can be {@code null}.
     * @param futureMoves A list of moves representing the future history. Can be {@code null}.
     */
    @JsonCreator
    public History(@JsonProperty("pastMoves") List<Move> pastMoves,
                   @JsonProperty("futureMoves") List<Move> futureMoves) {
        this();
        if (pastMoves != null) {
            this.pastMoves.addAll(pastMoves);
        }
        if (futureMoves != null) {
            this.futureMoves.addAll(futureMoves);
        }
    }

    /**
     * Commits a new move to the history.
     *
     * <p>The new move is added to the top of the {@code pastMoves} stack.
     * If there are any moves in the {@code futureMoves} stack (due to a previous
     * undo operation), they are cleared to ensure the history remains linear.</p>
     *
     * @param move The move to add to the history. Must not be {@code null}.
     * @throws IllegalArgumentException if the provided {@code move} is {@code null}.
     */
    public void commit(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null");
        }

        // Clear future moves when a new move is committed after an undo.
        // This prevents the history from branching and maintains linearity.
        futureMoves.clear();
        pastMoves.push(move);
    }

    /**
     * Undoes the most recent move in the history.
     *
     * <p>The last move from the {@code pastMoves} stack is popped and pushed onto
     * the {@code futureMoves} stack, making it available for a redo operation.
     * If the {@code pastMoves} stack is empty, no action is taken.</p>
     *
     * @return The undone {@link Move}, or {@code null} if there are no moves to undo.
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
     * <p>The last move from the {@code futureMoves} stack is popped and pushed
     * back onto the {@code pastMoves} stack. This effectively reapplies a move
     * that was previously undone. If the {@code futureMoves} stack is empty,
     * no action is taken.</p>
     *
     * @return The redone {@link Move}, or {@code null} if there are no moves to redo.
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
     * Swaps the color of all moves stored in this history.
     */
    public void swapColors() {
        pastMoves.replaceAll(m -> new Move(m.getPoint(), m.getStone().opposite()));
        futureMoves.replaceAll(m -> new Move(m.getPoint(), m.getStone().opposite()));
    }

    /**
     * Returns a copy of the past moves.
     * <p>This method is used for serialization and to provide a read-only view of
     * the moves that have been committed. The moves are returned in chronological order.</p>
     *
     * @return A new {@link List} containing all past moves.
     */
    @JsonProperty("pastMoves")
    public List<Move> getPastMoves() {
        return new ArrayList<>(pastMoves);
    }

    /**
     * Returns a copy of the future moves.
     * <p>This method is used for serialization and to provide a read-only view of
     * the moves that have been undone and can be redone. The moves are returned in
     * reverse chronological order of their undo.</p>
     *
     * @return A new {@link List} containing all future moves.
     */
    @JsonProperty("futureMoves")
    public List<Move> getFutureMoves() {
        return new ArrayList<>(futureMoves);
    }
}

