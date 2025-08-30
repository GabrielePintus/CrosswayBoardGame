package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * An immutable class representing a single move in the game, consisting of a point
 * on the board and the stone placed at that point.
 *
 * <p>This class is designed to be used with the Jackson library for JSON serialization
 * and deserialization, with annotations for forward compatibility and clean output.
 *
 * @author Gabriele Pintus
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Move {

    private final Point point;
    private final Stone stone;

    /**
     * Constructs a new {@code Move} with the specified point and stone.
     * This constructor is used by the Jackson library to create a new {@code Move}
     * object from a JSON representation. It ensures that both the point and the
     * stone are not null.
     *
     * @param point The point where the stone is placed. Must not be {@code null}.
     * @param stone The stone being placed. Must not be {@code null}.
     * @throws IllegalArgumentException if either the point or the stone is {@code null}.
     */
    @JsonCreator
    public Move(@JsonProperty(value = "point", required = true) Point point,
                @JsonProperty(value = "stone", required = true) Stone stone) {
        if (point == null) {
            throw new IllegalArgumentException("point cannot be null");
        }
        if (stone == null) {
            throw new IllegalArgumentException("stone cannot be null");
        }
        this.point = point;
        this.stone = stone;
    }

    /**
     * Gets the point of this move.
     *
     * @return The {@link Point} where the stone is placed.
     */
    public Point getPoint() {
        return point;
    }

    /**
     * Gets the stone of this move.
     *
     * @return The {@link Stone} being placed.
     */
    public Stone getStone() {
        return stone;
    }

    /**
     * Checks if this move is equal to another object.
     * <p>
     * Two {@code Move} objects are considered equal if their points and stones are the same.
     *
     * @param o The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Move move)) {
            return false;
        }
        return point.equals(move.point) && stone == move.stone;
    }

    /**
     * Computes the hash code for this move.
     * The hash code is based on the point and the stone, ensuring consistency
     * with the {@link #equals(Object)} method.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(point, stone);
    }
}

