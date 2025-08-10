package org.boardgames.crossway.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main game logic for Crossway. Uses a Union-Find structure to
 * track connectivity for efficient win detection.
 */
public class Game implements Exportable {
    private Board board;
    private final PatternChecker patternChecker;
    private final Connectivity connectivity;
//    private Deque<Move> history = new ArrayDeque<Move>();
    private History history;
    private Stone currentPlayer = Stone.BLACK; // BLACK starts by default


    /**
     * Constructs a new game on the given board.
     * Initializes Union-Find sets for virtual border nodes.
     *
     * @param board the game board
     */
    public Game(Board board) {
        this.board = board;
        this.connectivity = new Connectivity(board);
        this.patternChecker = new PatternChecker(List.of(new DiagonalXRule()));
        this.history = new History();
    }

    public Game(BoardSize boardSize) {
        this(new Board(boardSize));
    }

    public boolean canPlace(Point p, Stone s) {
        if (!board.isOnBoard(p)) return false;
        if (board.stoneAt(p).isPresent()) return false;
        return patternChecker.isAllowed(board, new Move(p, s));
    }

    private void place(Point p, Stone s) {
        connectivity.checkpoint();            // snapshot UF state for this move
        board.placeStone(p, s);
        connectivity.onPlace(p, s);           // do unions
    }


    public void undoLastMove() {
        Move lastMove = history.undo();
        if (lastMove != null) {
            board.clearCell(lastMove.getPoint());
            connectivity.rollback();
            currentPlayer = lastMove.getStone();
        }
    }

    public Stone getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return the underlying board
     */
    public Board getBoard() {
        return board;
    }

    public void makeMove(Move move) {
        if (!canPlace(move.getPoint(), move.getStone())) {
            throw new IllegalArgumentException("Invalid point for placing stone: " + move.getPoint());
        }

        place(move.getPoint(), move.getStone());
        history.commit(move);

        currentPlayer = currentPlayer.opposite();
    }


    /**
     * Checks if the given color has formed a connected path across.
     *
     * @param s the stone color to check
     * @return true if that color has won
     */
    public boolean hasWon(Stone s) {
        return connectivity.hasWon(s);
    }


    public String encode() {
        StringBuilder builder = new StringBuilder();
        builder.append("Game{")
                .append("currentPlayer=").append(currentPlayer)
                .append(", history=").append(history.encode());
        return builder.toString();
    }
}