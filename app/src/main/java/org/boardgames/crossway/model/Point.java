package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An immutable record representing a point on the game board.
 * Coordinates follow the standard (x, y) format, where 'x' is the column (horizontal)
 * and 'y' is the row (vertical).
 *
 * <p>This record is designed to be used with the Jackson library for JSON serialization
 * and deserialization, with annotations for forward compatibility and clean output.
 *
 * @param x The x-coordinate (column).
 * @param y The y-coordinate (row).
 * @author Gabriele Pintus
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Point(int x, int y) {

    /**
     * Checks if another point is a neighbor of this point.
     *
     * <p>A neighbor is defined as a point that is directly adjacent to this point,
     * either horizontally, vertically, or diagonally. This includes the point itself,
     * which is considered a neighbor. The method calculates the absolute difference
     * between the x-coordinates and y-coordinates to determine adjacency.
     *
     * @param other The point to check against.
     * @return {@code true} if the other point is a neighbor (within one unit horizontally
     * and vertically), {@code false} otherwise.
     */
    public boolean isNeighbourOf(Point other) {
        return (Math.abs(this.x - other.x) <= 1 && Math.abs(this.y - other.y) <= 1);
    }

    /**
     * JSON creator for Jackson deserialization.
     *
     * <p>This constructor is used by the Jackson library to create a new {@code Point}
     * object from a JSON representation. The {@code @JsonProperty} annotations
     * map the JSON fields "x" and "y" to the record's components.
     *
     * @param x The x-coordinate from the JSON input.
     * @param y The y-coordinate from the JSON input.
     */
    @JsonCreator
    public Point(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }
}