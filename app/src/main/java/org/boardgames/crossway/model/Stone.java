package org.boardgames.crossway.model;




public record Stone(int row, int col, Board.Color color) {
    public Stone {
        if (row < 0 || col < 0) throw new IllegalArgumentException("Invalid position");
    }
}


