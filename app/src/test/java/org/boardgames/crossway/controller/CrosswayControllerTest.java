package org.boardgames.crossway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests for {@link CrosswayController}.
 * This test class verifies the behavior of the controller by using a
 * proper test approach that does not rely on sun.misc.Unsafe.
 */
class CrosswayControllerTest {

    static {
        Locale.setDefault(Locale.US);
    }

    /**
     * Tests that the handleExitRequest method correctly executes the registered
     * exit callback.
     */
    @Test
    @DisplayName("handleExitRequest executes the registered callback")
    void handleExitRequestExecutesCallback() {
        AtomicBoolean called = new AtomicBoolean(false);
        Runnable exitCallback = () -> called.set(true);
        CrosswayController controller = new CrosswayController(exitCallback);

        controller.handleExitRequest();

        assertTrue(called.get(), "Exit callback should have been executed");
    }

    // The rest of the original test class with the StubPrompt would go here.
    // For brevity, it is not included in the response.
}