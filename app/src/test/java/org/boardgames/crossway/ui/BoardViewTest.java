package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
}
