package org.boardgames.crossway.model;


import java.util.*;

public final class Connectivity {
    // One DSU for everything; we only union same-color neighbors
    private final DisjointSet<Object> uf = new DisjointSet<>();

    // Use objects (not sentinel Points) for virtual borders
    private enum Border { WHITE_WEST, WHITE_EAST, BLACK_NORTH, BLACK_SOUTH }
    private final Map<Border, Object> border = Map.of(
            Border.WHITE_WEST,  new Object(),
            Border.WHITE_EAST,  new Object(),
            Border.BLACK_NORTH, new Object(),
            Border.BLACK_SOUTH, new Object()
    );

    private final Board board;

    /** Adjacency in Crossway is 8-connected (orthogonal + diagonal). */
    private static final int[][] DIR8 = {
            {-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}
    };

    public Connectivity(Board board) {
        this.board = board;
        rebuild(); // safe default: build from current board state
    }

    /** Rebuild UF from the entire board (simple & robust). */
    public void rebuild() {
        uf.clear();
        // Make border nodes
        border.values().forEach(uf::makeSet);

        // Register all occupied cells
        // NOTE: replace board.size() with your accessor (e.g., board.getBoardSize().size())
        int n = board.getSize().toInt();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                Point p = new Point(x, y);
                var st = board.stoneAt(p);
                if (st.isPresent()) {
                    uf.makeSet(p);
                }
            }
        }
        // Wire neighbors and borders
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                Point p = new Point(x, y);
                var st = board.stoneAt(p);
                if (st.isEmpty()) continue;
                Stone s = st.get();

                // union with same-color neighbors
                unionSameColorNeighbors(p, s);

                // union with virtual borders
                wireBorders(p, s, n);
            }
        }
    }

    /** Incremental update for a newly placed stone (faster than full rebuild). */
    public void onPlace(Point p, Stone s) {
        uf.makeSet(p);
        unionSameColorNeighbors(p, s);
        wireBorders(p, s, board.getSize().toInt());
    }

    /** For undo, you can either call rebuild() or add a DSU-with-rollback (see notes). */
    public void onUndoRebuild() {
        rebuild();
    }

    public boolean hasWon(Stone s) {
        if (s == Stone.WHITE) {
            return uf.connected(border.get(Border.WHITE_WEST), border.get(Border.WHITE_EAST));
        } else { // BLACK
            return uf.connected(border.get(Border.BLACK_NORTH), border.get(Border.BLACK_SOUTH));
        }
    }

    private void unionSameColorNeighbors(Point p, Stone s) {
        for (int[] d : DIR8) {
            Point q = new Point(p.x() + d[0], p.y() + d[1]);
            if (!board.isOnBoard(q)) continue;
            if (board.stoneAt(q).filter(s::equals).isPresent()) {
                uf.makeSet(q);
                uf.union(p, q);
            }
        }
    }

    private void wireBorders(Point p, Stone s, int n) {
        if (s == Stone.WHITE) {
            if (p.x() == 0)      uf.union(p, border.get(Border.WHITE_WEST));
            if (p.x() == n - 1)  uf.union(p, border.get(Border.WHITE_EAST));
        } else { // BLACK
            if (p.y() == 0)      uf.union(p, border.get(Border.BLACK_NORTH));
            if (p.y() == n - 1)  uf.union(p, border.get(Border.BLACK_SOUTH));
        }
    }
}
