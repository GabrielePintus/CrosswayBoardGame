package org.boardgames.crossway.model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal branching history:
 * - append via commit(move) under the current head
 * - undo() jumps to parent
 * - checkout(node) jumps anywhere
 * - pathFromRoot(node) returns [root .. node] for replay
 */
public final class History implements Exportable {

    private final Stack<Move> pastMoves;
    private final Stack<Move> futureMoves;

    public History() {
        this.pastMoves = new Stack<>();
        this.futureMoves = new Stack<>();
    }

    /**
     * Adds a move to the history.
     *
     * @param move the move to add
     */
    public void commit(Move move) {
        this.pastMoves.push(move);
    }

    /**
     * Undoes the last move in the history.
     *
     * @return the last move, or null if no moves are available
     */
    public Move undo() {
        Move lastMove = null;
        if (!pastMoves.isEmpty()) {
            lastMove = pastMoves.pop();
            futureMoves.push(lastMove);
        }
        return lastMove;
    }
    /**
     * Redoes the last undone move in the history.
     *
     * @return the last undone move, or null if no moves are available
     */
    public Move redo() {
        Move nextMove = null;
        if (!futureMoves.isEmpty()) {
            nextMove = futureMoves.pop();
            pastMoves.push(nextMove);
        }
        return nextMove;
    }

    public String encode() {
        String movesString = pastMoves.stream()
                .map(Move::encode)
                .collect(Collectors.joining(", "));
        return String.format("History{ pastMoves=[%s]}", movesString);
    }
}