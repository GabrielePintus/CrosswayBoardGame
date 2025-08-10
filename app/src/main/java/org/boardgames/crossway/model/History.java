package org.boardgames.crossway.model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
public final class History implements Exportable {

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
    public List<Move> getPastMoves() {
        return new ArrayList<>(pastMoves);
    }

    public String encode() {
        String movesString = pastMoves.stream()
                .map(Move::encode)
                .collect(Collectors.joining(", "));
        return String.format("History{ pastMoves=[%s]}", movesString);
    }
}