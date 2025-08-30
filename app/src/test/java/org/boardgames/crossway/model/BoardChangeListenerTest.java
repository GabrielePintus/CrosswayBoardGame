package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BoardChangeListener interface.
 */
public class BoardChangeListenerTest {

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