package org.boardgames.crossway.controller;

import org.boardgames.crossway.utils.Settings;

import javax.swing.*;
import java.awt.*;

/**
 * A factory class responsible for creating and configuring the main application window (JFrame).
 * <p>
 * This class encapsulates the logic for setting up the main frame's properties,
 * such as its title, default close operation, resizability, and menu bar. It
 * decouples the window creation process from the main controller.
 * </p>
 *
 * @author Gabriele Pintus
 */
abstract class FrameFactory {

    /** The title of the main application window, retrieved from settings. */
    private static final String WINDOW_TITLE = Settings.get("app.name");

    /**
     * Creates and returns a fully configured {@link JFrame} for the application.
     * <p>
     * This method sets the window's title, default close operation, and
     * attaches the main menu bar created by the {@link MenuBarFactory}. It also
     * prepares the content pane for the application's layout.
     * </p>
     *
     * @param controller The main application controller that handles window events and menu actions.
     * @return A ready-to-use {@link JFrame} instance.
     */
    static JFrame createFrame(CrosswayController controller) {
        // Create the main application window.
        JFrame frame = new JFrame(WINDOW_TITLE);

        // Configure the main window properties.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setJMenuBar(MenuBarFactory.createMenuBar(controller));

        // Initialize the split pane with the board and history views.
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        return frame;
    }
}