package org.boardgames.crossway.utils;


import org.boardgames.crossway.model.Game;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Handles the serialization and deserialization of the Game state to and from JSON files.
 *
 * <p>This class encapsulates all file I/O logic, separating the concerns of game
 * persistence from the main application controller.</p>
 *
 * @author Gabriele Pintus
 */
public class GameSerializer {

    /**
     * Saves the current game state to a specified file in JSON format.
     *
     * @param game The game object to serialize.
     * @param file The target file to write to.
     * @throws IOException if an I/O error occurs writing to the file.
     */
    public static void save(Game game, File file) throws IOException {
        String gameData = game.toJson();
        Files.writeString(file.toPath(), gameData);
    }

    /**
     * Loads a game state from a specified JSON file.
     *
     * @param file The file to read from.
     * @return A new {@link Game} instance deserialized from the file.
     * @throws IOException if an I/O error occurs reading from the file, or if the
     * file content is invalid.
     */
    public static Game load(File file) throws IOException {
        if (!file.exists() || !file.canRead()) {
            throw new IOException("Cannot read from file: " + file.getAbsolutePath());
        }
        String gameData = Files.readString(file.toPath());
        try {
            return Game.fromJson(gameData);
        } catch (Exception e) {
            // Wrap JSON parsing exceptions into an IOException for consistent error handling
            throw new IOException("Failed to parse game data from file: " + file.getName(), e);
        }
    }

}
