package org.boardgames.crossway.ui;

import javax.swing.JLabel;
import java.awt.Component;

/**
 * Swing implementation of {@link ScoreboardView} using a {@link JLabel}.
 *
 * @author Gabriele Pintus
 */
public class SwingScoreboardView implements ScoreboardView {

    /** The underlying Swing label component that displays the scoreboard text. */
    private final JLabel label;

    /**
     * Creates a new view backed by an empty {@link JLabel}.
     */
    public SwingScoreboardView() {
        this.label = new JLabel();
    }

    /**
     * Updates the scoreboard text by setting the text of the underlying {@link JLabel}.
     *
     * @param text formatted scoreboard information to display.
     */
    @Override
    public void update(String text) {
        label.setText(text);
    }

    /**
     * Returns the underlying {@link JLabel} component representing the scoreboard.
     *
     * @return the component displaying the scoreboard.
     */
    @Override
    public Component getComponent() {
        return label;
    }

    /**
     * Exposes the underlying {@link JLabel} for cases where
     * direct access is needed.
     *
     * @return the wrapped {@link JLabel}
     */
    public JLabel getLabel() {
        return label;
    }
}
