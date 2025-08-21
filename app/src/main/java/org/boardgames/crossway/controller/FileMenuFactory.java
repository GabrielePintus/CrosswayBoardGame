package org.boardgames.crossway.controller;

import org.boardgames.crossway.utils.Messages;

import javax.swing.*;
import java.util.Locale;

/**
 * A factory class for creating the "File" menu.
 * <p>
 * This class extends {@link MenuFactory} to reuse the menu item creation logic.
 * </p>
 *
 * @author Gabriele Pintus
 */
abstract class FileMenuFactory extends MenuFactory {

    /**
     * Creates the "File" menu, including sub-menus for language selection
     * and menu items for import, export, and exit actions.
     *
     * @param controller The controller to which menu actions are linked.
     * @return The configured "File" menu.
     */
    static JMenu createFileMenu(CrosswayController controller) {
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
     * Creates a sub-menu for language selection with options for English, Italian, and German.
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
}