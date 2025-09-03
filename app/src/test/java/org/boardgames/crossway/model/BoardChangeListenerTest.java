package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BoardChangeListener} functional interface.
 * This class verifies that the single abstract method {@code onBoardChange}
 * is correctly implemented and invoked with the expected board object.
 */
class BoardChangeListenerTest {

    /**
     * Tests the invocation of the {@code onBoardChange} method on a listener.
     * It verifies that the listener's method is called and receives the correct
     * board object as an argument.
     */
    @Test
    @DisplayName("onBoardChange is invoked with correct board")
    void testOnBoardChangeInvocation() {
        AtomicBoolean called = new AtomicBoolean(false);
        Board expectedBoard = new Board(BoardSize.SMALL);
        BoardChangeListener listener = board -> {
            assertEquals(expectedBoard, board, "Passed board should match the expected");
            called.set(true);
        };
        listener.onBoardChange(expectedBoard);
        assertTrue(called.get(), "Listener should be invoked");
    }
}