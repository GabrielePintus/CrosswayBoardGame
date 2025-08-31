package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.Stone;
import org.boardgames.crossway.utils.Messages;

import javax.swing.*;

/**
 * A utility class for displaying various types of dialogs to the user.
 * <p>
 * This class centralizes the logic for showing warning, informational, and error dialogs,
 * ensuring a consistent user experience. It requires a reference to the parent {@link JFrame}
 * to correctly position the dialogs.
 * </p>
 *
 * @author Gabriele Pintus
 */
public class DialogHandler {

    /**
     * The parent frame for the dialogs, used for correct positioning.
     */
    private final JFrame frame;

    /**
     * Constructs a new DialogHandler.
     *
     * @param frame The parent {@link JFrame} to which dialogs will be attached.
     */
    public DialogHandler(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Displays a warning dialog to the user.
     * <p>
     * This method shows a standard Swing warning message dialog with a yellow
     * triangle icon.
     * </p>
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays an informational dialog to the user.
     * <p>
     * This method shows a standard Swing informational dialog, typically with
     * an 'i' icon.
     * </p>
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error dialog to the user.
     * <p>
     * This method shows a standard Swing error message dialog, typically with
     * a red 'X' icon.
     * </p>
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    void showError(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Prompts the user to decide whether to swap colors using the pie rule.
     *
     * @return {@code true} if the user chooses to swap, {@code false} otherwise.
     */
    boolean askPieSwap() {
        int choice = JOptionPane.showOptionDialog(
                frame,
                Messages.get("game.swapPrompt"),
                Messages.get("game.swap"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{Messages.get("game.swap"), Messages.get("game.continue")},
                Messages.get("game.continue")
        );
        return choice == JOptionPane.YES_OPTION;
    }

    /**
     * Shows a dialog informing the user that a player has won and returns the user's choice.
     *
     * @param winner the player who won the game.
     * @return an integer representing the chosen action.
     */
    int showWinDialog(Stone winner) {
        return JOptionPane.showOptionDialog(
                frame,
                "%s %s".formatted(winner, Messages.get("game.wins")),
                Messages.get("game.over"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{
                        Messages.get("menu.game.new"),
                        Messages.get("menu.game.restart"),
                        Messages.get("menu.file.exit")
                },
                Messages.get("menu.game.restart")
        );
    }
}