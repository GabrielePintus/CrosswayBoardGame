package org.boardgames.crossway.controller;

import org.boardgames.crossway.utils.Messages;

import javax.swing.*;

/**
 * A factory class for creating the "Game" menu.
 * <p>
 * This class extends {@link MenuFactory} to reuse the menu item creation logic.
 * </p>
 *
 * @author Gabriele Pintus
 */
abstract class GameMenuFactory extends MenuFactory {

    /**
     * Creates the "Game" menu with options for a new game and restarting the current one.
     *
     * @param controller The controller that handles the game actions.
     * @return The configured "Game" menu.
     */
    static JMenu createGameMenu(CrosswayController controller) {
        JMenu gameMenu = new JMenu(Messages.get("menu.game"));
        gameMenu.add(createMenuItem(Messages.get("menu.game.new"), controller::handleNewGameRequest));
        gameMenu.add(createMenuItem(Messages.get("menu.game.restart"), controller::handleRestartRequest));
        gameMenu.add(createMenuItem(Messages.get("menu.game.changePlayers"), controller::handleChangePlayersRequest));
        return gameMenu;
    }
}