package org.boardgames.crossway.model;

public record Point(int row, int col) {
    public Point {
        if (row < 0 || col < 0) throw new IllegalArgumentException("Invalid position");
    }

    public Point translate(int dRow, int dCol) {
        return new Point(row + dRow, col + dCol);
    }
}
