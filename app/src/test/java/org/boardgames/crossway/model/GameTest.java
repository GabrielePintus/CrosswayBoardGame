package org.boardgames.crossway.model;


import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    @Test
    void canPlaceRespectsPatternsAndOccupancy() {
        Game g = new Game(new BoardSize(5)); // assumes Game constructs Board/Connectivity/PatternChecker

        // Occupy (2,2)
        g.place(new Point(2,2), Stone.BLACK);
        assertFalse(g.canPlace(new Point(2,2), Stone.WHITE), "occupied cell");

        // Set up near-X and check pattern denial
        g.place(new Point(3,2), Stone.WHITE);
        g.place(new Point(2,3), Stone.WHITE);
        assertFalse(g.canPlace(new Point(3,3), Stone.BLACK),
                "DiagonalXRule should block completing the X");
    }

    @Test
    void placeAndWin() {
        Game g = new Game(new BoardSize(3));
        g.place(new Point(0,1), Stone.WHITE);
        g.place(new Point(1,1), Stone.WHITE);
        g.place(new Point(2,1), Stone.WHITE);
        assertTrue(g.hasWon(Stone.WHITE));
    }

    @Test
    void undoRevertsConnectivity() {
        Game g = new Game(new BoardSize(3));
        g.place(new Point(0,1), Stone.WHITE);
        g.place(new Point(1,1), Stone.WHITE);
        g.place(new Point(2,1), Stone.WHITE);
        assertTrue(g.hasWon(Stone.WHITE));
        g.undoLastMove();
        assertFalse(g.hasWon(Stone.WHITE));
    }
}
