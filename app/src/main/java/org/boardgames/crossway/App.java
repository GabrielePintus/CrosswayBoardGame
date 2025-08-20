package org.boardgames.crossway;

import org.boardgames.crossway.controller.CrosswayController;
import org.boardgames.crossway.model.BoardSize;

import javax.swing.SwingUtilities;

/**
 * The main application class and entry point for the Crossway game.
 * <p>
 * This class is responsible for initializing and launching the application's
 * components, following the Model-View-Controller (MVC) design pattern. It
 * ensures that the application's graphical user interface (GUI) is created
 * and updated on the Event Dispatch Thread (EDT) to maintain thread safety.
 * </p>
 *
 * @author Gabriele Pintus
 */
public class App {

    /**
     * The main method, which serves as the application's entry point.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure that the GUI creation
        // and all subsequent UI-related tasks are executed on the EDT.
        SwingUtilities.invokeLater(() -> {
            // Create an instance of the main controller with a default board size.
            // This initializes the model, view, and ties them together.
            new CrosswayController(BoardSize.REGULAR);
        });
    }
}