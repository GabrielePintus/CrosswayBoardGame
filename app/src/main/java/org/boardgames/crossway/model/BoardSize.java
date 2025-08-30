package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An immutable record representing the size of a square board.
 * The board dimensions are defined by a single integer value, as the board
 * is always square (e.g., a size of 19 means a 19x19 board).
 *
 * <p>This record provides a set of standard board sizes as static constants
 * and utility methods for boundary checking and serialization.</p>
 *
 * @param size The integer dimension of the square board.
 * @author Gabriele Pintus
 */
public record BoardSize(int size) {

    /**
     * Small board size, representing a 9x9 board.
     */
    public static final BoardSize SMALL = new BoardSize(9);

    /**
     * Regular board size, representing a 19x19 board, which is the standard size for the game of Go.
     */
    public static final BoardSize REGULAR = new BoardSize(19);

    /**
     * Large board size, representing a 25x25 board.
     */
    public static final BoardSize LARGE = new BoardSize(25);

    /**
     * Returns the size of the board as an integer.
     * <p>This method is annotated with {@code @JsonValue} to ensure that
     * when a {@code BoardSize} object is serialized to JSON, it is represented
     * simply as its integer value (e.g., {@code 19}) rather than a complex object
     * (e.g., {@code {"size": 19}}).</p>
     *
     * @return The dimension of the board.
     */
    @JsonValue
    public int toInt() {
        return size;
    }

    /**
     * Checks whether a given point is within the boundaries of the square board.
     *
     * @param point The {@link Point} to check.
     * @return {@code true} if the point's x and y coordinates are both
     * within the range [0, size - 1], {@code false} otherwise.
     */
    public boolean isInBounds(Point point) {
        return point.x() >= 0 && point.x() < size &&
                point.y() >= 0 && point.y() < size;
    }
}