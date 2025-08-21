package org.boardgames.crossway.controller;

import org.boardgames.crossway.utils.Messages;

import javax.swing.*;
import java.util.Locale;

public abstract class MenuBarFactory {

    public static JMenuBar createMenuBar(CrosswayController controller) {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu(controller));
        menuBar.add(createViewMenu(controller));
        menuBar.add(createGameMenu(controller));

        // Add right-aligned buttons for undo and redo
        menuBar.add(Box.createHorizontalGlue());
        addRightAlignedButtons(menuBar, controller);

        return menuBar;
    }

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

    private static JMenu createLanguageSubmenu(CrosswayController controller) {
        JMenu languageMenu = new JMenu(Messages.get("menu.file.language"));

        languageMenu.add(createMenuItem(Messages.get("menu.lang.en"), () -> controller.handleLanguageChange(Locale.forLanguageTag("en-US"))));
        languageMenu.add(createMenuItem(Messages.get("menu.lang.it"), () -> controller.handleLanguageChange(Locale.forLanguageTag("it-IT"))));
        languageMenu.add(createMenuItem(Messages.get("menu.lang.de"), () -> controller.handleLanguageChange(Locale.forLanguageTag("de-DE"))));

        return languageMenu;
    }

    private static JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }


    /**
     * Creates the "View" menu with options to toggle history visibility.
     *
     * @return The configured "View" menu.
     */
    private static JMenu createViewMenu(CrosswayController controller) {
        JMenu viewMenu = new JMenu(Messages.get("menu.view"));
        viewMenu.add(createMenuItem(Messages.get("menu.view.showHistory"), controller::handleShowHistoryRequest));
        return viewMenu;
    }

    /**
     * Creates the "Game" menu with options for new game, restart, undo, and redo.
     *
     * @return The configured "Game" menu.
     */
    private static JMenu createGameMenu(CrosswayController controller) {
        JMenu gameMenu = new JMenu(Messages.get("menu.game"));
        gameMenu.add(createMenuItem(Messages.get("menu.game.new"), controller::handleNewGameRequest));
        gameMenu.add(createMenuItem(Messages.get("menu.game.restart"), controller::handleRestartRequest));
        return gameMenu;
    }


    private static void addRightAlignedButtons(JMenuBar menuBar, CrosswayController controller) {
        // Note: createToolbarButton method would need to be moved or made public static
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
