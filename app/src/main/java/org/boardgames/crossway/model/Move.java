package org.boardgames.crossway.model;

public class Move {

    private final Point point;
    private final Stone stone;

    /**
     * Constructs a new Move with the specified point and stone.
     *
     * @param point the point where the stone is placed
     * @param stone the stone being placed
     */
    public Move(Point point, Stone stone) {
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

}
