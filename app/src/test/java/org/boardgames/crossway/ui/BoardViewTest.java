package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.*;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class BoardViewTest {
    @Test
    void preferredSizeAndPaintDoNotThrow() {
        Board b = new Board(new BoardSize(11));
        BoardView v = new BoardView(b);

        Dimension d = v.getPreferredSize();
        assertTrue(d.width > 0 && d.height > 0, "preferred size should be positive");

        BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> v.paint(g2), "paint should not throw in headless rendering");
        g2.dispose();
    }

    @Test
    void paintDoesNotMutateBoard() {
        Board b = new Board(new BoardSize(5));
        b.placeStone(new Point(0,0), Stone.BLACK);
        BoardView v = new BoardView(b);

        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        int before = b.getStones().size();
        v.paint(g2);
        g2.dispose();

        assertEquals(before, b.getStones().size(), "Rendering should not change board state");
        assertEquals(Optional.of(Stone.BLACK), b.stoneAt(new Point(0,0)));
    }
}
