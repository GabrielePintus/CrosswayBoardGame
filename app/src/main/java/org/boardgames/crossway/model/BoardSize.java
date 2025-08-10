package org.boardgames.crossway.model;

/**
 * Immutable class representing the size of a square board.
 */
public record BoardSize(int size) {

    /**
     * Tiny board (5x5).
     */
    public static final BoardSize TINY   = new BoardSize(5);

    /**
     * Small board (9x9).
     */
    public static final BoardSize SMALL   = new BoardSize(9);
    /**
     * Regular board (19x19), standard Go board size.
     */
    public static final BoardSize REGULAR = new BoardSize(19);
    /**
     * Large board (25x25).
     */
    public static final BoardSize LARGE   = new BoardSize(25);

    /**
     * Returns the width of the board (same as height).
     */
    public int size() {
        return size;
    }

    /**
     * Checks whether a point is within the square board boundaries.
     *
     * @param point the point to check
     * @return true if the point lies within the board, false otherwise
     */
    public boolean isInBounds(Point point) {
        return point.x() >= 0 && point.x() < size &&
                point.y() >= 0 && point.y() < size;
    }

    /**
     * Returns the board size as an integer value.
     *
     * @return the board dimension
     */
    public int toInt() {
        return size;
    }
}