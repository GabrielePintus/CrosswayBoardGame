package org.boardgames.crossway;

import org.boardgames.crossway.controller.CrosswayController;
import org.boardgames.crossway.model.BoardSize;

import javax.swing.SwingUtilities;

/**
 * Entry point: launches the MVC components on the EDT.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CrosswayController app = new CrosswayController(BoardSize.REGULAR); // default size
        });
    }
}