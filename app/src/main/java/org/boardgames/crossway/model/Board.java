package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Represents the game board with a fixed square size.
 * This class manages the state of stones placed on the board and provides
 * methods for placement, retrieval, and serialization.
 *
 * <p>The board uses a {@link Map} to store the positions of placed stones,
 * which makes it efficient for sparse board states and lookups. The class
 * is designed to be easily serialized to and deserialized from JSON using
 * the Jackson library.</p>
 *
 * @author Gabriele Pintus
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Board {

    /**
     * An ObjectMapper instance configured for JSON serialization and deserialization.
     */
    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * The size of the square board.
     */
    private final BoardSize size;

    /**
     * The map storing the locations of stones on the board.
     * The key is the {@link Point} and the value is the {@link Stone} color.
     */
    private final Map<Point, Stone> grid;

    /**
     * Constructs a new empty board of a given size.
     *
     * @param size The size of the board. Must not be null.
     */
    public Board(BoardSize size) {
        this.size = size;
        this.grid = new HashMap<>();
    }

    /**
     * Constructs a new board instance from a list of moves.
     * This constructor is used by the Jackson library for deserialization.
     * It populates the board grid with stones based on the provided moves.
     *
     * @param size The size of the board.
     * @param stones A list of {@link Move} objects representing the stones to place.
     * @throws IllegalArgumentException if a stone is out of the board's bounds.
     */
    @JsonCreator
    public Board(@JsonProperty("size") BoardSize size,
                 @JsonProperty("stones") List<Move> stones) {
        this(size);
        if (stones != null) {
            for (Move move : stones) {
                Point point = move.getPoint();
                if (!isOnBoard(point)) {
                    throw new IllegalArgumentException("Stone at " + point + " is out of bounds.");
                }
                grid.put(point, move.getStone());
            }
        }
    }

    /**
     * Clears all stones from the board, resetting it to an empty state.
     */
    public void clear() {
        grid.clear();
    }

    /**
     * Places a stone at a specified point on the board.
     *
     * @param point The point where the stone will be placed.
     * @param stone The stone to be placed.
     * @throws IllegalArgumentException if the point is out of the board's bounds.
     */
    public void placeStone(Point point, Stone stone) {
        if (!isOnBoard(point)) {
            throw new IllegalArgumentException("Point is out of bounds: " + point);
        }
        grid.put(point, stone);
    }

    /**
     * Clears the stone at the specified point from the board.
     *
     * @param point The point whose stone should be removed.
     * @throws IllegalArgumentException if there is no stone at the specified point.
     */
    public void clearCell(Point point) {
        if (!grid.containsKey(point)) {
            throw new IllegalArgumentException("No stone at point: " + point);
        }
        grid.remove(point);
    }

    /**
     * Retrieves the stone at a specified point on the board.
     *
     * @param point The point to check.
     * @return An {@link Optional} containing the stone if one is present at the point,
     * or an empty {@link Optional} if the cell is empty.
     */
    public Optional<Stone> stoneAt(Point point) {
        return Optional.ofNullable(grid.get(point));
    }

    /**
     * Checks whether a point on the board is not occupied by a stone.
     * This also checks if the point is within the board's boundaries.
     *
     * @param point The point to check.
     * @return {@code true} if the point is within bounds and is empty, {@code false} otherwise.
     */
    public boolean isEmpty(Point point) {
        return !grid.containsKey(point) && isOnBoard(point);
    }

    /**
     * Checks whether the given point is within the bounds of the board.
     *
     * @param point The point to check.
     * @return {@code true} if the point's coordinates are valid for this board,
     * {@code false} otherwise.
     */
    public boolean isOnBoard(Point point) {
        return size.isInBounds(point);
    }

    /**
     * Gets the size of the board.
     *
     * @return The {@link BoardSize} object representing the dimensions of the board.
     */
    @JsonProperty("size")
    public BoardSize getSize() {
        return size;
    }

    /**
     * Gets a list of all stones on the board as a list of moves.
     * This method is used for JSON serialization and returns a sorted list
     * for consistent output.
     *
     * @return A sorted {@link List} of {@link Move} objects representing the placed stones.
     */
    @JsonProperty("stones")
    public List<Move> getStones() {
        return grid.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<Point, Stone> e) -> e.getKey().x())
                        .thenComparingInt(e -> e.getKey().y()))
                .map(e -> new Move(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Serializes the current board state into a JSON string.
     *
     * @return A JSON string representing the board.
     * @throws IllegalStateException if serialization fails.
     */
    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize Board", e);
        }
    }

    /**
     * Deserializes a JSON string into a new {@code Board} instance.
     *
     * @param json The JSON string to deserialize.
     * @return A new {@code Board} instance with the state from the JSON string.
     * @throws IllegalArgumentException if the JSON is invalid or represents an invalid board state.
     */
    public static Board fromJson(String json) {
        try {
            return MAPPER.readValue(json, Board.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for Board", e);
        }
    }
}

