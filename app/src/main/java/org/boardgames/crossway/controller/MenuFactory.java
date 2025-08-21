package org.boardgames.crossway.controller;

import javax.swing.*;

/**
 * A base factory class providing a utility method for creating standard menu items.
 * Other menu factory classes can extend this class to reuse the item creation logic.
 *
 * @author Gabriele Pintus
 */
abstract class MenuFactory {

    /**
     * Creates a standard {@link JMenuItem} with a text label and an associated action.
     *
     * @param text   The text to display on the menu item.
     * @param action The action (a {@link Runnable}) to perform when the item is clicked.
     * @return A configured {@link JMenuItem}.
     */
    protected static JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }
}