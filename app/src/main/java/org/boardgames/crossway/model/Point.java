package org.boardgames.crossway.model;


/**
 * Immutable record representing a point on the board.
 * Coordinates follow the standard (x, y) format, where:
 * - x is the column (horizontal)
 * - y is the row (vertical)
 */
public record Point(int x, int y) {

    /**
     * Checks if another point is a neighbour of this point.
     * A neighbour is defined as a point that is directly adjacent
     * either horizontally, vertically, or diagonally.
     * Every point is considered a neighbour of itself.
     *
     * @param other the point to check against
     * @return true if the other point is a neighbour, false otherwise
     */
    public boolean isNeighbourOf(Point other) {
        return (Math.abs(this.x - other.x) <= 1 && Math.abs(this.y - other.y) <= 1);
    }

}
