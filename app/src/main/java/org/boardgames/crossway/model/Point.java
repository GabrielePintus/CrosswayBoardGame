package org.boardgames.crossway.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Immutable record representing a point on the board.
 * Coordinates follow the standard (x, y) format, where:
 * - x is the column (horizontal)
 * - y is the row (vertical)
 */
@JsonIgnoreProperties(ignoreUnknown = true)          // forward compatibility
@JsonInclude(JsonInclude.Include.NON_NULL)           // cleaner output
public record Point(int x, int y) implements Exportable {

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

    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);


    /**
     * JSON creator for Jackson.
     */
    @JsonCreator
    public Point(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Serializes this Point to a JSON string.
     *
     * @return JSON representation of the Point
     */
    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this); // e.g., {"x":3,"y":5}
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize Point", e);
        }
    }

    /**
     * Deserializes a Point from a JSON string.
     *
     * @param json the JSON string
     * @return a Point object
     */
    public static Point fromJson(String json) {
        try {
            return MAPPER.readValue(json, Point.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for Point", e);
        }
    }
}