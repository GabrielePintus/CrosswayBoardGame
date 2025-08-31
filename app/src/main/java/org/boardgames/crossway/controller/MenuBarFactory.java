package org.boardgames.crossway.controller;

import org.boardgames.crossway.utils.Messages;

import javax.swing.*;

/**
 * A factory class for creating the application's menu bar and its sub-menus.
 * <p>
 * This class uses static methods to construct the {@link JMenuBar} and all
 * its components, separating menu creation logic from the main controller.
 * </p>
 *
 * @author Gabriele Pintus
 */
public abstract class MenuBarFactory {

    /**
     * Creates and returns a fully configured {@link JMenuBar} for the application.
     * <p>
     * This menu bar includes a "File" menu, a "View" menu, a "Game" menu,
     * and right-aligned buttons for undo and redo functionality.
     * </p>
     *
     * @param controller The main application controller that handles menu item actions.
     * @param fileController Controller handling import/export logic.
     * @param scoreboardController Controller managing the scoreboard display.
     * @return A {@link JMenuBar} instance ready to be set on a {@link JFrame}.
     */
    public static JMenuBar createMenuBar(CrosswayController controller,
                                         FileController fileController,
                                         ScoreboardController scoreboardController){
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(FileMenuFactory.createFileMenu(controller, fileController));
        menuBar.add(ViewMenuFactory.createViewMenu(controller));
        menuBar.add(GameMenuFactory.createGameMenu(controller, scoreboardController));

        // Add a "glue" component to push the buttons to the far right.
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(scoreboardController.getScoreboardLabel());
        menuBar.add(Box.createHorizontalGlue());
        addRightAlignedButtons(menuBar, controller);

        return menuBar;
    }

    /**
     * Adds buttons for common actions (like undo and redo) to the right side of the menu bar.
     *
     * @param menuBar    The menu bar to which the buttons will be added.
     * @param controller The controller that handles the actions for the buttons.
     */
    private static void addRightAlignedButtons(JMenuBar menuBar, CrosswayController controller) {
        menuBar.add(createToolbarButton(Messages.get("menu.toolbar.undo"), controller::handleUndoRequest));
        menuBar.add(createToolbarButton(Messages.get("menu.toolbar.redo"), controller::handleRedoRequest));
    }

    /**
     * Creates a button styled for the menu bar's right side.
     *
     * @param label  The text to display on the button.
     * @param action The action to execute on a click.
     * @return A configured {@link JButton}.
     */
    private static JButton createToolbarButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> action.run());
        return btn;
    }
}