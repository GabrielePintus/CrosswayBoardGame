package org.boardgames.crossway.model;

import org.boardgames.crossway.model.rules.Connectivity;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ConnectivityBordersTest {
    @Test
    void whiteWinWestToEast() {
        Board b = new Board(new BoardSize(5));
        Connectivity conn = new Connectivity(b);

        for (int x = 0; x < 5; x++) {
            Point p = new Point(x, 2);
            b.placeStone(p, Stone.WHITE);
            conn.onPlace(p, Stone.WHITE);
        }
        assertTrue(conn.hasWon(Stone.WHITE));
        assertFalse(conn.hasWon(Stone.BLACK));
    }

    @Test
    void blackWinNorthToSouthDiagonalAllowed() {
        Board b = new Board(new BoardSize(4));
        Connectivity conn = new Connectivity(b);

        Point[] pts = { new Point(1,0), new Point(1,1), new Point(2,2), new Point(2,3) };
        for (Point p : pts) { b.placeStone(p, Stone.BLACK); conn.onPlace(p, Stone.BLACK); }
        assertTrue(conn.hasWon(Stone.BLACK));
    }

    @Test
    void cornerTouchesBothAdjoiningBorders() {
        Board b = new Board(new BoardSize(3));
        Connectivity conn = new Connectivity(b);

        Point corner = new Point(0,0);
        b.placeStone(corner, Stone.WHITE);
        conn.onPlace(corner, Stone.WHITE);

        // Not a full path yet, but the stone should be connected to the WEST border.
        // (If you expose a method to query direct border connectivity, assert it here.)
        assertFalse(conn.hasWon(Stone.WHITE));
    }
}
