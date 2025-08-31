package org.boardgames.crossway.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Messages} utility class.
 * This class verifies the correct behavior of the message retrieval and
 * localization functionalities.
 */
class MessagesTest {

    /**
     * Tests that the {@code setLocale} method correctly changes the language
     * of subsequent message lookups. It verifies that a message for the
     * German locale can be successfully retrieved after setting the locale.
     */
    @Test
    @DisplayName("setLocale should change the language for message retrieval")
    void setLocaleChangesLanguage() {
        Messages.setLocale(Locale.GERMAN);
        String value = Messages.get("menu.file.exit");
        assertNotNull(value);
    }

    /**
     * Tests that retrieving a non-existent key returns a default string
     * wrapped in exclamation marks to indicate a missing resource.
     * This is a fail-safe mechanism to help with debugging.
     */
    @Test
    @DisplayName("Retrieving a missing key should return a default exclamation-wrapped string")
    void missingKeyFallsBackToExclamationWrapped() {
        assertEquals("!unknown.key!", Messages.get("unknown.key"));
    }

    /**
     * Tests that the {@code getPrefixedMap} method correctly returns a map where
     * the prefixes have been removed from the keys. This is useful for
     * grouping related messages without their full hierarchical key.
     */
    @Test
    @DisplayName("getPrefixedMap should remove the specified prefix from keys")
    void prefixedMapRemovesPrefix() {
        Messages.setLocale(Locale.US);
        Map<String, String> map = Messages.getPrefixedMap("menu.file");
        assertTrue(map.containsKey("exit"));
    }

    /**
     * Tests that the {@code getPrefixedList} and {@code getPrefixedArray} methods
     * return collections with the same number of elements for the same prefix,
     * ensuring consistency between the two retrieval methods.
     */
    @Test
    @DisplayName("Prefixed list and array methods should return the same number of elements")
    void prefixedListMatchesArraySize() {
        List<String> list = Messages.getPrefixedList("menu.file");
        String[] arr = Messages.getPrefixedArray("menu.file");
        assertEquals(list.size(), arr.length);
    }
}