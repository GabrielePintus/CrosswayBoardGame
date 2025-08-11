package org.boardgames.crossway.ui;

import org.boardgames.crossway.model.Move;
import org.boardgames.crossway.model.Stone;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Collapsible view component for displaying move history.
 * Controlled via menu option rather than toggle button.
 */
public class HistoryView extends JPanel {

    public static final int MIN_WIDTH = 200;

    private final JList<String> moveList;
    private final DefaultListModel<String> listModel;
    private boolean isVisible = false;

    public HistoryView() {
        setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        titlePanel.add(new JLabel("Move History"), BorderLayout.CENTER);

        // Move list
        listModel = new DefaultListModel<>();
        moveList = new JList<String>(listModel);
        moveList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(moveList);

        // Layout
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initial state - hidden
        setMinimumSize(new Dimension(0, 400));
        setPreferredSize(new Dimension(0, 400));
        setVisible(false);
    }

    /**
     * Toggles the visibility of the history view panel.
     * Expands to show move history or collapses to hide it.
     */
    public void toggleVisibility() {
        isVisible = !isVisible;
        setVisible(isVisible);

        // Trigger parent container to revalidate
        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }

    /**
     * Checks whether the history view is currently visible.
     *
     * @return true if the history view is visible, false otherwise
     */
    public boolean isHistoryVisible() {
        return isVisible;
    }

    /**
     * Updates the displayed move history.
     *
     * @param moves the list of moves to display in the history view
     */
    public void updateHistory(List<Move> moves) {
        listModel.clear();
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            String moveText = String.format("%d. %s (%d,%d)",
                    i + 1,
                    move.getStone() == Stone.BLACK ? "●" : "○",
                    move.getPoint().x(),
                    move.getPoint().y()
            );
            listModel.addElement(moveText);
        }

        // Auto-scroll to bottom
        if (!listModel.isEmpty()) {
            moveList.ensureIndexIsVisible(listModel.getSize() - 1);
        }
    }
}