package org.boardgames.crossway.controller;

import org.boardgames.crossway.utils.Messages;

import javax.swing.*;

/**
 * A factory class for creating the "View" menu.
 * <p>
 * This class extends {@link MenuFactory} to reuse the menu item creation logic.
 * </p>
 *
 * @author Gabriele Pintus
 */
abstract class ViewMenuFactory extends MenuFactory {

    /**
     * Creates the "View" menu with an option to toggle history visibility.
     *
     * @param controller The controller that handles the view action.
     * @return The configured "View" menu.
     */
    static JMenu createViewMenu(CrosswayController controller) {
        JMenu viewMenu = new JMenu(Messages.get("menu.view"));
        viewMenu.add(createMenuItem(Messages.get("menu.view.showHistory"), controller::handleShowHistoryRequest));
        return viewMenu;
    }
}