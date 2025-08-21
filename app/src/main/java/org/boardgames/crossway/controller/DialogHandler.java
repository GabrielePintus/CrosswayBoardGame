package org.boardgames.crossway.controller;

import javax.swing.*;

public class DialogHandler {

    private final JFrame frame;

    public DialogHandler(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Displays a warning dialog to the user.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays an informational dialog to the user.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error dialog to the user.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     */
    void showError(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
