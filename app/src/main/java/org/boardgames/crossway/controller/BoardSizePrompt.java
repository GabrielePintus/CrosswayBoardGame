package org.boardgames.crossway.controller;

/**
 * Abstraction for prompting the user to choose a board size.
 */
public interface BoardSizePrompt {
    /**
     * Prompts the user to select a board size.
     *
     * @param options the available options to present
     * @param defaultOption the default selected option
     * @return the index of the chosen option or {@code -1} if cancelled
     */
    int promptForBoardSize(Object[] options, Object defaultOption);
}