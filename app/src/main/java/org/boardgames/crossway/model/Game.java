package org.boardgames.crossway.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main game logic for Crossway. Uses a Union-Find structure to
 * track connectivity for efficient win detection.
 */
public class Game {
    private Board board;
//    private DisjointSet<Point> uf = new DisjointSet<Point>();
    private final PatternChecker patternChecker;
    private final Connectivity connectivity;
    private Deque<Move> history = new ArrayDeque<Move>();
    private Stone currentPlayer = Stone.BLACK; // BLACK starts by default

    // Virtual border nodes for Union-Find
//    private static final Point WHITE_WEST  = new Point(-1, -1);
//    private static final Point WHITE_EAST  = new Point(-2, -2);
//    private static final Point BLACK_NORTH = new Point(-3, -3);
//    private static final Point BLACK_SOUTH = new Point(-4, -4);

    // All 8 neighbor directions
//    private static final List<int[]> DIRECTIONS = Arrays.asList(
//            new int[]{-1, -1}, new int[]{-1,  0}, new int[]{-1,  1},
//            new int[]{ 0, -1},                   new int[]{ 0,  1},
//            new int[]{ 1, -1}, new int[]{ 1,  0}, new int[]{ 1,  1}
//    );


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
    }

    public Game(BoardSize boardSize) {
        this(new Board(boardSize));
    }

    public boolean canPlace(Point p, Stone s) {
        if (!board.isOnBoard(p)) return false;
        if (board.stoneAt(p).isPresent()) return false;
        return patternChecker.isAllowed(board, new Move(p, s));
    }

    public boolean place(Point p, Stone s) {
        if (!canPlace(p, s)) return false;
        connectivity.checkpoint();            // snapshot UF state for this move
        board.placeStone(p, s);
        connectivity.onPlace(p, s);           // do unions
        history.push(new Move(p, s));
        return true;
    }


    public boolean undoLastMove() {
        if (history.isEmpty()) return false;
        Move last = history.pop();
        board.clearCell(last.getPoint());
        connectivity.rollback();
        currentPlayer = last.getStone();
        return true;
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

        history.add(move); // Track the move for potential undo

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



}