package org.boardgames.crossway.model;

import java.util.*;


final class Squares {
    private Squares() {}

    /** Top-left coordinates of 2×2 blocks that could include `p`. */
    static List<Point> topLeftsAround(Board board, Point p) {
        int x = p.x(), y = p.y();
        // candidates: (x-1,y-1), (x-1,y), (x,y-1), (x,y)
        List<Point> cands = List.of(
                new Point(x-1, y-1), new Point(x-1, y),
                new Point(x,   y-1), new Point(x,   y)
        );
        // Filter to those that can form a full 2×2 on board.
        List<Point> out = new ArrayList<>(4);
        for (Point tl : cands) {
            int tx = tl.x(), ty = tl.y();
            boolean fits = board.isOnBoard(tl)
                    && board.isOnBoard(new Point(tx+1, ty))
                    && board.isOnBoard(new Point(tx,   ty+1))
                    && board.isOnBoard(new Point(tx+1, ty+1));
            if (fits) out.add(tl);
        }
        return out;
    }
}