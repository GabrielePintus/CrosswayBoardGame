package org.boardgames.crossway.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Settings} utility class.
 * This class verifies that settings values are retrieved correctly from the
 * properties file, including both string and integer types.
 */
class SettingsTest {

    /**
     * Tests that the {@code get} method correctly returns a stored string value
     * from the properties file.
     */
    @Test
    @DisplayName("get returns a stored string value")
    void getReturnsStoredValue() {
        assertEquals("Crossway", Settings.get("app.name"));
    }

    /**
     * Tests that the {@code getInt} method correctly parses an integer value
     * when it exists and uses the provided default value when the key is not found.
     */
    @Test
    @DisplayName("getInt parses existing values and uses default for missing ones")
    void getIntParsesOrUsesDefault() {
        assertEquals(9, Settings.getInt("board.smallSize", 0));
        assertEquals(7, Settings.getInt("non.existent", 7));
    }
}