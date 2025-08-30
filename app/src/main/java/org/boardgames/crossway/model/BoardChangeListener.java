package org.boardgames.crossway.model;

/**
 * Listener interface for receiving notifications when the game board changes.
 */
@FunctionalInterface
public interface BoardChangeListener {
    /**
     * Invoked after the board state has changed.
     *
     * @param board the updated {@link Board}
     */
    void onBoardChange(Board board);
}

