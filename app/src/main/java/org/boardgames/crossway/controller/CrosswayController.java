package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Board;
import org.boardgames.crossway.model.BoardSize;
import org.boardgames.crossway.ui.BoardView;
import javax.swing.*;

/**
 * Controller component: initializes model and view, and shows the window.
 */
public class CrosswayController {
    private final Board board;
    private final BoardView view;

    public CrosswayController(short size) {
        this.board = new Board(size);
        this.view = new BoardView(board);
    }

    public void init() {
        JFrame frame = new JFrame("Crossway");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}