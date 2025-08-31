package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Player;

import java.util.List;

/**
 * Abstraction over UI interactions required by the {@link GameController}.
 * <p>
 * Implementations handle history updates and user dialogs triggered by
 * game events such as the pie rule or a win.</p>
 */
public interface GameEvents {
    /**
     * Refreshes the move history display.
     *
     * @param history list of past moves
     */
    void refreshHistory(List<Move> history);

    /**
     * Revalidates and repaints the main window.
     */
    void refreshWindow();

    /**
     * Displays the pie rule dialog asking for a color swap.
     *
     * @return {@code true} if the players choose to swap colours
     */
    boolean showPieDialog();

    /**
     * Displays the win dialog for the given player.
     *
     * @param winner the winning player
     * @return the chosen option from the dialog
     */
    int showWinDialog(Player winner);

    /**
     * Shows an informational dialog.
     *
     * @param title   the dialog title
     * @param message the dialog message
     */
    void showInfo(String title, String message);

    /**
     * Shows a warning dialog.
     *
     * @param title   the dialog title
     * @param message the dialog message
     */
    void showWarning(String title, String message);
}

