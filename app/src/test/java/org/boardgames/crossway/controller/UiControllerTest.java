package org.boardgames.crossway.controller;

import org.boardgames.crossway.model.*;
import org.boardgames.crossway.ui.*;
import org.boardgames.crossway.utils.Messages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class for the {@link UiController}, focusing on the interaction between
 * the controller and its UI components.
 * <p>
 * This class uses reflection and a custom `Unsafe` factory method to create
 * an instance of `UiController` without invoking its constructor, allowing for
 * focused testing of its public methods and their side effects on injected mocks.
 * </p>
 */
class UiControllerTest {

    static {
        Locale.setDefault(Locale.US);
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Retrieves an instance of `sun.misc.Unsafe` using reflection.
     * This is a low-level operation used to bypass standard object
     * creation mechanisms for testing purposes.
     *
     * @return an instance of `Unsafe`.
     * @throws Exception if an error occurs during reflection.
     */
    private static Unsafe unsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    /**
     * Sets the value of a private field on an object using reflection.
     *
     * @param obj   The object on which to set the field.
     * @param name  The name of the field.
     * @param value The value to set the field to.
     * @throws Exception if the field is not found or cannot be set.
     */
    private static void setField(Object obj, String name, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    /**
     * A dummy implementation of {@link JFrame} for testing purposes.
     * <p>
     * This class overrides key methods to prevent a real UI from being
     * created and to allow for controlled testing of size and visibility
     * changes.
     * </p>
     */
    static class DummyFrame extends JFrame {
        int w;
        int h;

        @Override
        public Dimension getSize() {
            return new Dimension(w, h);
        }

        @Override
        public int getWidth() {
            return w;
        }

        @Override
        public int getHeight() {
            return h;
        }

        @Override
        public void setSize(int w, int h) {
            this.w = w;
            this.h = h;
        }

        @Override
        public void revalidate() {
        }

        @Override
        public void repaint() {
        }

        @Override
        public void pack() {
        }

        @Override
        public void setVisible(boolean b) {
        }

        @Override
        public void setLocationRelativeTo(Component c) {
        }
    }

    /**
     * Creates a mocked `UiController` instance for testing using reflection.
     *
     * @param handler A mock `DialogHandler`.
     * @param hv      A mock `HistoryView`.
     * @param bv      A mock `BoardView`.
     * @param sp      A mock `BoardHistorySplitPane`.
     * @param item    A mock `JMenuItem`.
     * @return a fully constructed `UiController` instance.
     * @throws Exception if an error occurs during object creation or field setting.
     */
    private static UiController createController(DialogHandler handler, HistoryView hv, BoardView bv, BoardHistorySplitPane sp, JMenuItem item) throws Exception {
        UiController uc = (UiController) unsafe().allocateInstance(UiController.class);
        setField(uc, "dialogHandler", handler);
        setField(uc, "historyView", hv);
        setField(uc, "boardView", bv);
        setField(uc, "splitPane", sp);
        setField(uc, "historyMenuItem", item);
        DummyFrame frame = (DummyFrame) unsafe().allocateInstance(DummyFrame.class);
        setField(uc, "frame", frame);
        return uc;
    }

    /**
     * A stub implementation of {@link DialogHandler} to capture method calls
     * and control return values for testing.
     */
    private static class StubDialogHandler extends DialogHandler {
        boolean pieCalled;
        boolean infoCalled;
        boolean warnCalled;
        int winCalls;
        boolean pieResult;
        int winResult;

        StubDialogHandler() {
            super(null);
        }

        @Override
        boolean askPieSwap() {
            pieCalled = true;
            return pieResult;
        }

        @Override
        int showWinDialog(Player winner) {
            winCalls++;
            return winResult;
        }

        @Override
        void showInfo(String title, String message) {
            infoCalled = true;
        }

        @Override
        void showWarning(String title, String message) {
            warnCalled = true;
        }
    }

    /**
     * Tests that the `handleShowHistoryRequest` method correctly toggles the
     * visibility of the history view and updates the text of the menu item.
     *
     * @throws Exception if a reflection error occurs.
     */
    @Test
    @DisplayName("handleShowHistoryRequest toggles history view visibility and menu item text")
    void handleShowHistoryRequestTogglesVisibilityAndMenu() throws Exception {
        HistoryView hv = new HistoryView();
        BoardView bv = new BoardView(new Board(new BoardSize(3)));
        BoardHistorySplitPane sp = new BoardHistorySplitPane(bv, hv);
        JMenuItem item = new JMenuItem("Show History");
        UiController ui = createController(new StubDialogHandler(), hv, bv, sp, item);

        ui.handleShowHistoryRequest();
        assertTrue(hv.isHistoryVisible());
        assertEquals(Messages.get("menu.view.hideHistory"), item.getText());

        ui.handleShowHistoryRequest();
        assertFalse(hv.isHistoryVisible());
        assertEquals(Messages.get("menu.view.showHistory"), item.getText());
    }

    /**
     * Tests that the public dialog methods on `UiController` correctly delegate
     * their calls to the injected `DialogHandler`.
     *
     * @throws Exception if a reflection error occurs.
     */
    @Test
    @DisplayName("UI controller's dialog methods delegate calls to the dialog handler")
    void dialogMethodsDelegateToHandler() throws Exception {
        HistoryView hv = new HistoryView();
        BoardView bv = new BoardView(new Board(new BoardSize(3)));
        BoardHistorySplitPane sp = new BoardHistorySplitPane(bv, hv);
        JMenuItem item = new JMenuItem("Show History");
        StubDialogHandler handler = new StubDialogHandler();
        handler.pieResult = true;
        handler.winResult = 7;
        UiController ui = createController(handler, hv, bv, sp, item);

        assertTrue(ui.showPieDialog());
        assertTrue(handler.pieCalled);

        Player winner = new Player("X", Stone.BLACK);
        assertEquals(7, ui.showWinDialog(winner));
        assertEquals(1, handler.winCalls);

        ui.showInfo("t", "m");
        ui.showWarning("t", "m");
        assertTrue(handler.infoCalled);
        assertTrue(handler.warnCalled);
    }
}