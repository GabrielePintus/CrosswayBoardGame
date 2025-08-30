package org.boardgames.crossway.controller;

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
}