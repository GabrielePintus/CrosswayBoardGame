package org.boardgames.crossway.ui;

import java.awt.Component;

/**
 * View abstraction for the scoreboard display.
 * Implementations provide a UI component that can be
 * placed in the application window and updated with
 * formatted scoreboard text.
 */
public interface ScoreboardView {

    /**
     * Updates the scoreboard text.
     *
     * @param text formatted scoreboard information to display
     */
    void update(String text);

    /**
     * Returns the underlying UI component representing the
     * scoreboard. This component can be added to containers
     * such as a menu bar.
     *
     * @return the component displaying the scoreboard
     */
    Component getComponent();
}
