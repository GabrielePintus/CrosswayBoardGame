package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.DisjointSet;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;

import java.util.*;

/**
 * Manages the connectivity of stones on the game board using a transactional
 * Union-Find data structure.
 *
 * <p>This class is central to determining the game's win condition. It models
 * stone placements and their connections, and efficiently checks for
 * a winning path between a player's designated border segments. It handles
 * both standard game progression and supports undo/redo operations by
 * leveraging the rollback capability of its underlying {@link DisjointSet}
 * implementation.</p>
 *
 * <p>The connectivity is tracked by treating each stone and the four virtual
 * board borders as elements in a disjoint set. A player wins when their
 * two corresponding borders become connected by a continuous path of
 * their stones.</p>
 *
 * @author Gabriele Pintus
 */
public final class Connectivity {

    /**
     * The underlying Union-Find data structure with rollback functionality.
     * It stores {@link Point} objects and {@link Border} enum values.
     */
    private final DisjointSet<Object> uf = new DisjointSet<>();

    /**
     * An enum representing the four virtual borders of the board.
     * These borders are used as special nodes in the Union-Find structure
     * to check for winning connections.
     */
    private enum Border {
        WHITE_WEST,
        WHITE_EAST,
        BLACK_NORTH,
        BLACK_SOUTH
    }

    /**
     * A map storing the singleton objects representing each virtual border.
     */
    private final Map<Border, Object> border = Map.of(
            Border.WHITE_WEST,  new Object(),
            Border.WHITE_EAST,  new Object(),
            Border.BLACK_NORTH, new Object(),
            Border.BLACK_SOUTH, new Object()
    );

    /**
     * The game board instance this class is tracking.
     */
    private final Board board;

    /**
     * The dimension of the square board.
     */
    private final int n;

    /**
     * An array of 8-directional offsets for checking neighboring cells.
     */
    private static final int[][] DIR8 = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    /**
     * Constructs a new {@code Connectivity} instance for a given board.
     * Initializes the Union-Find structure and adds the virtual border nodes.
     *
     * @param board The game board to track.
     */
    public Connectivity(Board board) {
        this.board = board;
        this.n = board.getSize().toInt();
        uf.clear();
        border.values().forEach(uf::makeSet);
    }

    /**
     * Initializes the connectivity state from the current board by iterating
     * through all existing stones and simulating their placement.
     *
     * @param board The board containing the initial stone layout.
     */
    public void initFromBoard(Board board) {
        board.getStones().forEach(m -> onPlace(m.getPoint(), m.getStone()));
    }

    /**
     * Creates a checkpoint in the Union-Find structure.
     * All subsequent changes can be rolled back to this point. This method
     * should be called before a move is committed to the board.
     */
    public void checkpoint() {
        uf.checkpoint();
    }

    /**
     * Rolls back the state of the Union-Find structure to the last checkpoint.
     * This is used to undo a move by reverting the connectivity changes.
     */
    public void rollback() {
        uf.rollback();
    }

    /**
     * Updates the connectivity graph after a stone is placed at a specific point.
     * It creates a new set for the placed stone and performs unions with all
     * adjacent stones of the same color and relevant board borders.
     *
     * @param p The {@link Point} where the stone was placed.
     * @param s The {@link Stone} color of the placed stone.
     */
    public void onPlace(Point p, Stone s) {
        uf.makeSet(p);

        // Union with adjacent stones of the same color.
        for (int[] d : DIR8) {
            Point q = new Point(p.x() + d[0], p.y() + d[1]);
            if (!board.isOnBoard(q)) {
                continue;
            }
            if (board.stoneAt(q).filter(s::equals).isPresent()) {
                uf.makeSet(q);
                uf.union(p, q);
            }
        }

        // Union with relevant virtual borders if the stone is on the edge.
        if (s == Stone.WHITE) {
            if (p.x() == 0) {
                uf.union(p, border.get(Border.WHITE_WEST));
            }
            if (p.x() == n - 1) {
                uf.union(p, border.get(Border.WHITE_EAST));
            }
        } else { // s == Stone.BLACK
            if (p.y() == 0) {
                uf.union(p, border.get(Border.BLACK_NORTH));
            }
            if (p.y() == n - 1) {
                uf.union(p, border.get(Border.BLACK_SOUTH));
            }
        }
    }

    /**
     * Checks if the specified player has achieved a winning connection.
     *
     * <p>A player wins by connecting their two corresponding virtual borders.
     * This check is performed in near-constant time using the Union-Find's
     * {@code connected()} method.</p>
     *
     * @param s The stone color (player) to check.
     * @return {@code true} if the player has connected their respective sides,
     * {@code false} otherwise.
     */
    public boolean hasWon(Stone s) {
        if (s == Stone.WHITE) {
            return uf.connected(border.get(Border.WHITE_WEST), border.get(Border.WHITE_EAST));
        } else { // s == Stone.BLACK
            return uf.connected(border.get(Border.BLACK_NORTH), border.get(Border.BLACK_SOUTH));
        }
    }
}

