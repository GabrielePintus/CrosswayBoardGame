package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.ui.BoardView;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.awt.Dimension;

import static org.junit.jupiter.api.Assertions.*;

class CrosswayControllerTest {

    @Test
    void testControllerInitialization() throws Exception {
        short size = 8;
        CrosswayController controller = new CrosswayController(size);

        // Reflectively access private fields
        Field boardField = CrosswayController.class.getDeclaredField("board");
        boardField.setAccessible(true);
        Board board = (Board) boardField.get(controller);
        assertNotNull(board);
        assertEquals(size, board.getSize());

        Field viewField = CrosswayController.class.getDeclaredField("view");
        viewField.setAccessible(true);
        BoardView view = (BoardView) viewField.get(controller);
        assertNotNull(view);
        // test preferred size
        Dimension dim = view.getPreferredSize();
        assertEquals(size * 40, dim.width);
        assertEquals(size * 40, dim.height);
    }
}