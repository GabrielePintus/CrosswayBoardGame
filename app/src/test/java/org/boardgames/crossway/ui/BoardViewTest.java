package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.ui.BoardView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class BoardViewTest {

    @BeforeAll
    static void headless() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void testPreferredSize() {
        short size = 5;
        Board board = new Board(size);
        BoardView view = new BoardView(board);
        Dimension pref = view.getPreferredSize();
        assertEquals(size * 40, pref.width);
        assertEquals(size * 40, pref.height);
    }

    @Test
    void testPaintComponentNoException() {
        short size = 3;
        Board board = new Board(size);
        BoardView view = new BoardView(board);
        Dimension dim = view.getPreferredSize();
        BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> view.paintComponent(g2));
        g2.dispose();
    }
}