package org.boardgames.crossway.controller;

import org.boardgames.crossway.utils.Messages;

import javax.swing.*;
import java.util.Locale;

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
     * @return A {@link JMenuBar} instance ready to be set on a {@link JFrame}.
     */
    public static JMenuBar createMenuBar(CrosswayController controller) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu(controller));
        menuBar.add(createViewMenu(controller));
        menuBar.add(createGameMenu(controller));

        // Add a "glue" component to push the buttons to the far right.
        menuBar.add(Box.createHorizontalGlue());
        addRightAlignedButtons(menuBar, controller);

        return menuBar;
    }

    /**
     * Creates the "File" menu, including sub-menus for language selection
     * and menu items for import, export, and exit actions.
     *
     * @param controller The controller to which menu actions are linked.
     * @return The configured "File" menu.
     */
    private static JMenu createFileMenu(CrosswayController controller) {
        JMenu fileMenu = new JMenu(Messages.get("menu.file"));
        fileMenu.add(createLanguageSubmenu(controller));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem(Messages.get("menu.file.import"), controller::handleImportRequest));
        fileMenu.add(createMenuItem(Messages.get("menu.file.export"), controller::handleExportRequest));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem(Messages.get("menu.file.exit"), controller::handleExitRequest));
        return fileMenu;
    }

    /**
     * Creates a sub-menu for language selection.
     *
     * @param controller The controller that handles language change requests.
     * @return The configured "Language" sub-menu.
     */
    private static JMenu createLanguageSubmenu(CrosswayController controller) {
        JMenu languageMenu = new JMenu(Messages.get("menu.file.language"));
        languageMenu.add(createMenuItem(Messages.get("menu.lang.en"), () -> controller.handleLanguageChange(Locale.forLanguageTag("en-US"))));
        languageMenu.add(createMenuItem(Messages.get("menu.lang.it"), () -> controller.handleLanguageChange(Locale.forLanguageTag("it-IT"))));
        languageMenu.add(createMenuItem(Messages.get("menu.lang.de"), () -> controller.handleLanguageChange(Locale.forLanguageTag("de-DE"))));
        return languageMenu;
    }

    /**
     * Creates a standard {@link JMenuItem} with a text label and an associated action.
     *
     * @param text   The text to display on the menu item.
     * @param action The action (a {@link Runnable}) to perform when the item is clicked.
     * @return A configured {@link JMenuItem}.
     */
    private static JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    /**
     * Creates the "View" menu with options to toggle history visibility.
     *
     * @param controller The controller that handles the view action.
     * @return The configured "View" menu.
     */
    private static JMenu createViewMenu(CrosswayController controller) {
        JMenu viewMenu = new JMenu(Messages.get("menu.view"));
        viewMenu.add(createMenuItem(Messages.get("menu.view.showHistory"), controller::handleShowHistoryRequest));
        return viewMenu;
    }

    /**
     * Creates the "Game" menu with options for new game and restart.
     *
     * @param controller The controller that handles the game actions.
     * @return The configured "Game" menu.
     */
    private static JMenu createGameMenu(CrosswayController controller) {
        JMenu gameMenu = new JMenu(Messages.get("menu.game"));
        gameMenu.add(createMenuItem(Messages.get("menu.game.new"), controller::handleNewGameRequest));
        gameMenu.add(createMenuItem(Messages.get("menu.game.restart"), controller::handleRestartRequest));
        return gameMenu;
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