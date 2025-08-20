package org.boardgames.crossway.model;



import java.util.*;

public final class Connectivity {
    private final DisjointSet<Object> uf = new DisjointSet<>();

    private enum Border { WHITE_WEST, WHITE_EAST, BLACK_NORTH, BLACK_SOUTH }
    private final Map<Border, Object> border = Map.of(
            Border.WHITE_WEST,  new Object(),
            Border.WHITE_EAST,  new Object(),
            Border.BLACK_NORTH, new Object(),
            Border.BLACK_SOUTH, new Object()
    );

    private final Board board;
    private final int n;

    private static final int[][] DIR8 = {
            {-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}
    };

    public Connectivity(Board board) {
        this.board = board;
        this.n = board.getSize().toInt();
        // Initial build (optional if board starts empty)
        uf.clear();
        border.values().forEach(uf::makeSet);
    }

    /** Initializes connectivity from the current board state by placing all existing stones. */
    public void initFromBoard(Board board) {
        board.getStones().stream().forEach(m -> onPlace(m.getPoint(), m.getStone()));
    }

    /** Call before placing a stone; pairs with rollback() on undo. */
    public void checkpoint() { uf.checkpoint(); }

    /** Roll back the unions done since last checkpoint (for undo). */
    public void rollback() { uf.rollback(); }

    /** Incremental unions for a newly placed stone. */
    public void onPlace(Point p, Stone s) {
        uf.makeSet(p);

        // neighbors
        for (int[] d : DIR8) {
            Point q = new Point(p.x() + d[0], p.y() + d[1]);
            if (!board.isOnBoard(q)) continue;
            if (board.stoneAt(q).filter(s::equals).isPresent()) {
                uf.makeSet(q);
                uf.union(p, q);
            }
        }
        // borders
        if (s == Stone.WHITE) {
            if (p.x() == 0)     uf.union(p, border.get(Border.WHITE_WEST));
            if (p.x() == n - 1) uf.union(p, border.get(Border.WHITE_EAST));
        } else {
            if (p.y() == 0)     uf.union(p, border.get(Border.BLACK_NORTH));
            if (p.y() == n - 1) uf.union(p, border.get(Border.BLACK_SOUTH));
        }
    }

    /**
     * Checks if the specified player has achieved a winning connection.
     *
     * @param s the stone color (player) to check
     * @return true if the player has connected their respective sides, false otherwise
     */
    public boolean hasWon(Stone s) {
        if (s == Stone.WHITE) {
            return uf.connected(border.get(Border.WHITE_WEST), border.get(Border.WHITE_EAST));
        } else {
            return uf.connected(border.get(Border.BLACK_NORTH), border.get(Border.BLACK_SOUTH));
        }
    }
}