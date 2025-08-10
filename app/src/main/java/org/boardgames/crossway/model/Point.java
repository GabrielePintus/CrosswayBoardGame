package org.boardgames.crossway.model;


/**
 * Immutable record representing a point on the board.
 * Coordinates follow the standard (x, y) format, where:
 * - x is the column (horizontal)
 * - y is the row (vertical)
 */
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


    public String encode() {
        return "(x=" + x + ",y=" + y + ")".strip();
    }

    public static Point fromString(String pointString) {
        // Remove the surrounding parentheses
        pointString = pointString.replace("(", "").replace(")", "");
        // Split on equal sign
        String[] parts = pointString.split(",");
        String x_string = parts[0].substring(2);
        String y_string = parts[1].substring(2);
        // Parse the coordinates
        int x = Integer.parseInt(x_string);
        int y = Integer.parseInt(y_string);
        // Return a new Point instance
        return new Point(x, y);
    }
}
