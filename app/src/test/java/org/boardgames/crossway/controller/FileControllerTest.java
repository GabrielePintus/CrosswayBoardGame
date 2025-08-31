package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class for the {@link FileController} to ensure that games can be
 * saved to and loaded from files correctly.
 */
class FileControllerTest {

    static {
        // Sets the default locale to US for consistent formatting during tests.
        java.util.Locale.setDefault(java.util.Locale.US);
    }

    /**
     * Tests the complete round trip of saving a game to a temporary file
     * and then loading it back.
     *
     * <p>This test creates a new game, makes a move, saves the game,
     * then loads it and asserts that the loaded game's state (board and
     * move history) matches the original game's state.</p>
     *
     * @throws Exception if an I/O error or a reflection error occurs during the test.
     */
    @Test
    @DisplayName("Test saving and loading a game for a complete round trip")
    void saveAndLoadRoundTrip() throws Exception {
        // Required for tests that might involve AWT components in a headless environment.
        System.setProperty("java.awt.headless", "true");
        Game original = new Game(new BoardSize(3));
        original.makeMove(new Move(new Point(0, 0), Stone.BLACK));

        // Use an AtomicReference to hold the loaded game instance.
        AtomicReference<Game> loaded = new AtomicReference<>();

        // Custom DialogHandler for testing, which prevents dialogs from being shown.
        DialogHandler handler = new DialogHandler(null) {
            @Override
            public void showInfo(String title, String message) {
            }

            @Override
            public void showError(String title, String message) {
            }
        };

        // Create the FileController instance with mock handlers.
        FileController controller = new FileController(() -> original, loaded::set, null, handler);

        // Create a temporary file that will be deleted after the test.
        File temp = File.createTempFile("crossway", ".json");
        temp.deleteOnExit();

        // Use reflection to access and invoke the private executeGameExport method.
        Method exportMethod = FileController.class.getDeclaredMethod("executeGameExport", File.class);
        exportMethod.setAccessible(true);
        exportMethod.invoke(controller, temp);

        // Use reflection to access and invoke the private executeGameImport method.
        Method importMethod = FileController.class.getDeclaredMethod("executeGameImport", File.class);
        importMethod.setAccessible(true);
        importMethod.invoke(controller, temp);

        // Assert that a game was successfully loaded and is not null.
        assertNotNull(loaded.get());

        // Assert that the loaded game's board state is identical to the original's.
        assertEquals(original.getBoard().toJson(), loaded.get().getBoard().toJson());

        // Assert that the loaded game's move history is identical to the original's.
        assertEquals(original.getMoveHistory(), loaded.get().getMoveHistory());
    }
}