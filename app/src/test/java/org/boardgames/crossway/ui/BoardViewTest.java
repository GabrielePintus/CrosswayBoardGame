package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BoardView} class.
 * These tests verify the correct behavior of the view component,
 * ensuring it handles rendering, mouse events, and size calculations properly.
 */
class BoardViewTest {
    /**
     * Tests that the preferred size is calculated correctly and that the
     * paint method does not throw an exception during headless rendering.
     */
    @Test
    @DisplayName("Preferred size is positive and paint method doesn't throw exceptions")
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

    /**
     * Verifies that the {@link BoardView#paint} method does not alter
     * the underlying {@link Board} state.
     */
    @Test
    @DisplayName("Paint method does not mutate the board's state")
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

    /**
     * Tests the mapping of pixel coordinates from a mouse event
     * to the correct board {@link Point}.
     */
    @Test
    @DisplayName("Mouse event coordinates are correctly mapped to a board point")
    void mouseEventToPointMapsPixelsToBoard() {
        Board board = new Board(new BoardSize(3));
        BoardView view = new BoardView(board);
        view.setBounds(0, 0, 90, 90);   // 30px cells

        MouseEvent e = new MouseEvent(view, MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(), 0, 45, 60, 1, false);
        Point p = view.mouseEventToPoint(e);
        assertEquals(new Point(1, 2), p);
    }

    /**
     * Verifies that the cell size is calculated correctly based on the
     * component's bounds, choosing the smaller dimension to maintain a square aspect ratio.
     */
    @Test
    @DisplayName("Cell size is calculated correctly based on component bounds")
    void getCellSizeRespectsBounds() {
        Board board = new Board(new BoardSize(3));
        BoardView view = new BoardView(board);
        view.setBounds(0, 0, 90, 30);   // square side = 30 ⇒ cell = 10
        assertEquals(10, view.getCellSize());
    }

    /**
     * Tests that the registered click callback is invoked and receives the
     * correct board {@link Point} when a mouse event occurs.
     */
    @Test
    @DisplayName("Board click callback is invoked with the correct board point")
    void setBoardClickCallbackInvokesConsumer() {
        System.setProperty("java.awt.headless", "true");
        Board board = new Board(new BoardSize(3));
        BoardView view = new BoardView(board);
        view.setBounds(0, 0, 90, 90);

        AtomicReference<Point> ref = new AtomicReference<>();
        view.setBoardClickCallback(ref::set);

        MouseEvent e = new MouseEvent(
                view, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
                0, 45, 30, 1, false);
        view.dispatchEvent(e);

        assertEquals(new Point(1, 1), ref.get());
    }
}