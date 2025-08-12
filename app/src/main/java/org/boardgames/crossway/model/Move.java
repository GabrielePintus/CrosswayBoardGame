package org.boardgames.crossway.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Move implements Exportable{

    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final Point point;
    private final Stone stone;

//    /**
//     * Constructs a new Move with the specified point and stone.
//     *
//     * @param point the point where the stone is placed
//     * @param stone the stone being placed
//     */
//    public Move(Point point, Stone stone) {
//        this.point = point;
//        this.stone = stone;
//    }

    /**
     * Constructs a new Move with the specified point and stone.
     *
     * @param point the point where the stone is placed
     * @param stone the stone being placed
     */
    @JsonCreator
    public Move(
            @JsonProperty(value = "point", required = true) Point point,
            @JsonProperty(value = "stone", required = true) Stone stone
    ) {
        if (point == null) throw new IllegalArgumentException("point cannot be null");
        if (stone == null) throw new IllegalArgumentException("stone cannot be null");
        this.point = point;
        this.stone = stone;
    }

    /**
     * Gets the point of this move.
     *
     * @return the point where the stone is placed
     */
    public Point getPoint() {
        return point;
    }
    /**
     * Gets the stone of this move.
     *
     * @return the stone being placed
     */
    public Stone getStone() {
        return stone;
    }

    /**
     * Encodes this move into a string representation.
     *
     * @return a string representation of the move, including point and stone
     */
    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize Move", e);
        }
    }

    public static Move fromJson(String json) {
        try {
            return MAPPER.readValue(json, Move.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for Move", e);
        }
    }
    /**
     * Checks if this move is equal to another move.
     * Two moves are considered equal if their points and stones are the same.
     *
     * @param o the object to compare with
     * @return true if the moves are equal, false otherwise
     **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move move)) return false;
        return point.equals(move.point) && stone == move.stone;
    }
}