package org.boardgames.crossway.model;

import java.util.HashMap;
import java.util.Map;

public class Board {

    public enum Color {
        NONE, WHITE, BLACK
    }

    private final short boardSize;
    private final HashMap<Point, Stone> stones;

    /*
        CONSTRUCTORS
    */
    public Board(final short boardSize) {
        assert boardSize > 1 : "Board size must be greater than 1";
        this.boardSize = boardSize;

        this.stones = new HashMap<Point, Stone>();
    }


    // Other methods
    public short getSize() {
        return boardSize;
    }

    public void addStone(final Stone stone) {
        if (stone.row() >= boardSize || stone.col() >= boardSize) {
            throw new IllegalArgumentException("Stone position out of bounds");
        }
        stones.put(new Point(stone.row(), stone.col()), stone);
    }
    public Stone getStone(final Point point) {
        return stones.get(point);
    }
    public Stone getStone(int r, int c) {
        return stones.get(new Point(r, c));
    }
}