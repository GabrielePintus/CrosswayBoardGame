package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FileControllerTest {

    static {
        java.util.Locale.setDefault(java.util.Locale.US);
    }

    @Test
    void saveAndLoadRoundTrip() throws Exception {
        System.setProperty("java.awt.headless", "true");
        Game original = new Game(new BoardSize(3));
        original.makeMove(new Move(new Point(0, 0), Stone.BLACK));

        AtomicReference<Game> loaded = new AtomicReference<>();
        DialogHandler handler = new DialogHandler(null) {
            @Override public void showInfo(String title, String message) { }
            @Override public void showError(String title, String message) { }
        };
        FileController controller = new FileController(() -> original, loaded::set, null, handler);

        File temp = File.createTempFile("crossway", ".json");
        temp.deleteOnExit();

        Method exportMethod = FileController.class.getDeclaredMethod("executeGameExport", File.class);
        exportMethod.setAccessible(true);
        exportMethod.invoke(controller, temp);

        Method importMethod = FileController.class.getDeclaredMethod("executeGameImport", File.class);
        importMethod.setAccessible(true);
        importMethod.invoke(controller, temp);

        assertNotNull(loaded.get());
        assertEquals(original.getBoard().toJson(), loaded.get().getBoard().toJson());
        assertEquals(original.getMoveHistory(), loaded.get().getMoveHistory());
    }
}

