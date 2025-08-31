package org.boardgames.crossway.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link JsonUtils} utility class.
 * This class verifies the correctness of JSON serialization, deserialization,
 * and file handling utilities.
 */
class JsonUtilsTest {

    /**
     * Tests that the {@code ensureJsonExtension} method correctly adds the ".json"
     * suffix to a file name if it is not already present.
     */
    @Test
    @DisplayName("ensureJsonExtension adds .json suffix")
    void ensureJsonExtensionAddsSuffix() {
        File f = new File("save");
        File result = JsonUtils.ensureJsonExtension(f);
        assertTrue(result.getName().endsWith("." + JsonUtils.JSON_EXT));
    }

    /**
     * Verifies that the combination of {@code toJson} and {@code fromJson}
     * correctly performs a data round-trip, ensuring that an object
     * can be serialized to JSON and then deserialized back without data loss.
     */
    @Test
    @DisplayName("toJson and fromJson perform a correct round trip")
    void toJsonFromJsonRoundTrip() {
        record Dummy(String value) {}
        Dummy d = new Dummy("hello");
        String json = JsonUtils.toJson(d);
        Dummy parsed = JsonUtils.fromJson(json, Dummy.class);
        assertEquals(d.value(), parsed.value());
    }

    /**
     * Tests the error handling of the {@code fromJson} method.
     * It asserts that a {@link RuntimeException} is thrown when the input
     * string is not valid JSON.
     */
    @Test
    @DisplayName("fromJson throws RuntimeException on invalid input")
    void fromJsonThrowsOnInvalidInput() {
        assertThrows(RuntimeException.class, () -> JsonUtils.fromJson("not-json", Object.class));
    }
}