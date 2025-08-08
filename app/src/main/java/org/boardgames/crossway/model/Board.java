package org.boardgames.crossway.model;

import org.boardgames.crossway.model.BoardSize;
import org.boardgames.crossway.model.Point;
import org.boardgames.crossway.model.Stone;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the Go board with a fixed size.
 * Manages the placement and state of stones on the board.
 */
public class Board {
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
     * Retrieves the stone at a given point.
     *
     * @param point the point on the board
     * @return the stone at that point, or null if empty
     */
    public Stone getStone(Point point) {
        return grid.get(point);
    }

    /**
     * Check wether a point is occupied by a stone.
     *
     * @param point the point to check
     * @return true if occupied, false otherwise
     */
    public boolean isOccupied(Point point) {
        return grid.containsKey(point);
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
    public BoardSize getSize() {
        return size;
    }
}
