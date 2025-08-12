package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.boardgames.crossway.model.BoardSize;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;

import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Represents the Go board with a fixed size.
 * Manages the placement and state of stones on the board.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Board {

    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final BoardSize size;
    private final Map<Point, Stone> grid;

    /**
     * Constructs a new empty board of a given size.
     *
     * @param size the size of the board
     */
    public Board(BoardSize size) {
        this.size = size;
        this.grid = new HashMap<>();
    }

    /**
     *
     * @param size
     * @param stones
     */
    @JsonCreator
    public Board(
            @JsonProperty("size") BoardSize size,
            @JsonProperty("stones") List<Move> stones
    ) {
        this(size);
        if (stones != null) {
            for (Move m : stones) {
                Point p = m.getPoint();
                if (!isOnBoard(p)) {
                    throw new IllegalArgumentException("Stone out of bounds at " + p);
                }
                grid.put(p, m.getStone());
            }
        }
    }

    /**
     * Clear the board by removing all stones.
     */
    public void clear() {
        grid.clear();
    }

    /**
     * Places a stone at a specified point on the board.
     *
     * @param point the point to place the stone
     * @param stone the stone to place
     */
    public void placeStone(Point point, Stone stone) {
        if (!isOnBoard(point)) {
            throw new IllegalArgumentException("Point is out of bounds: " + point);
        }else{
            grid.put(point, stone);
        }
    }

    /**
     * Retrieves the stone at a specified point on the board.
     *
     * @param point the point to check
     * @return an Optional containing the stone if present, or empty if no stone is found
     */
    public Optional<Stone> stoneAt(Point point) {
        return Optional.ofNullable(grid.get(point));
    }

    /**
     * Check whether a point is not occupied by a stone.
     *
     * @param point the point to check
     * @return true if empty, false otherwise
     */
    public boolean isEmpty(Point point) {
        return !grid.containsKey(point) && isOnBoard(point);
    }

    /**
     * Checks whether the given point is within the bounds of the board.
     *
     * @param point the point to check
     * @return true if within bounds, false otherwise
     */
    public boolean isOnBoard(Point point) {
        return size.isInBounds(point);
    }
    /**
     * Gets the board size.
     *
     * @return the size of the board
     */
    @JsonProperty("size")
    public BoardSize getSize() {
        return size;
    }

    /**
     *
     * @return
     */
    @JsonProperty("stones")
    public List<Move> getStones() {
        return grid.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<Point, Stone> e) -> e.getKey().x())
                        .thenComparingInt(e -> e.getKey().y()))
                .map(e -> new Move(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize Board", e);
        }
    }

    public static Board fromJson(String json) {
        try {
            return MAPPER.readValue(json, Board.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for Board", e);
        }
    }

    /**
     * Clears the stone at the specified point on the board.
     *
     * @param last the point whose stone should be removed
     */
    public void clearCell(Point last) {
        if (grid.containsKey(last)) {
            grid.remove(last);
        } else {
            throw new IllegalArgumentException("No stone at point: " + last);
        }
    }
}