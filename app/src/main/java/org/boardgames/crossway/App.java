package org.boardgames.crossway;

import org.boardgames.crossway.controller.CrosswayController;
import javax.swing.SwingUtilities;

/**
 * Entry point: launches the MVC components on the EDT.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CrosswayController app = new CrosswayController((short) 12); // default size
            app.init();
        });
    }
}