package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Checks for winning connections on the game board using on-demand BFS.
 *
 * <p>This class replaces the incremental Union-Find approach with a simpler BFS-based check.
 * It verifies if a player has formed a connected path (8-directional) between their
 * designated borders. No state is maintained beyond the board reference; checks are
 * performed directly on the current board state.</p>
 *
 * @author Gabriele Pintus
 */
public final class ConnectionChecker {

    /**
     * The game board instance to check.
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
     * Constructs a new {@code ConnectionChecker} for the given board.
     *
     * @param board The board to check for connections.
     */
    public ConnectionChecker(Board board) {
        this.board = board;
        this.n = board.getSize().size();
    }

    /**
     * Checks if the specified player has achieved a winning connection.
     *
     * @param s The stone color (player) to check.
     * @return {@code true} if the player has connected their respective sides,
     * {@code false} otherwise.
     */
    public boolean hasWon(Stone s) {
        Set<Point> starts = new HashSet<>();
        Set<Point> targets = new HashSet<>();

        if (s == Stone.WHITE) {
            // White: West (left, x=0) to East (right, x=n-1)
            for (int y = 0; y < n; y++) {
                Point left = new Point(0, y);
                Point right = new Point(n - 1, y);
                if (board.stoneAt(left).orElse(null) == s) {
                    starts.add(left);
                }
                if (board.stoneAt(right).orElse(null) == s) {
                    targets.add(right);
                }
            }
        } else { // s == Stone.BLACK
            // Black: North (top, y=0) to South (bottom, y=n-1)
            for (int x = 0; x < n; x++) {
                Point top = new Point(x, 0);
                Point bottom = new Point(x, n - 1);
                if (board.stoneAt(top).orElse(null) == s) {
                    starts.add(top);
                }
                if (board.stoneAt(bottom).orElse(null) == s) {
                    targets.add(bottom);
                }
            }
        }

        // If no starting stones on the border, can't win
        if (starts.isEmpty()) {
            return false;
        }

        return bfs(starts, targets, s);
    }

    /**
     * Performs BFS from starting border points to check if any target border point is reachable.
     *
     * @param starts Starting points on one border.
     * @param targets Target points on the opposite border.
     * @param s The stone color to traverse.
     * @return {@code true} if a path exists, {@code false} otherwise.
     */
    private boolean bfs(Set<Point> starts, Set<Point> targets, Stone s) {
        Queue<Point> queue = new LinkedList<>(starts);
        Set<Point> visited = new HashSet<>(starts);

        while (!queue.isEmpty()) {
            Point p = queue.poll();

            // If we reach a target, win
            if (targets.contains(p)) {
                return true;
            }

            // Explore 8 neighbors
            for (int[] d : DIR8) {
                Point q = new Point(p.x() + d[0], p.y() + d[1]);
                if (board.isOnBoard(q) && !visited.contains(q) && board.stoneAt(q).orElse(null) == s) {
                    visited.add(q);
                    queue.add(q);
                }
            }
        }

        return false;
    }
}