package org.boardgames.crossway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * A utility class for handling JSON serialization and deserialization.
 * <p>
 * This class provides static methods to easily convert Java objects to JSON strings
 * and vice versa, using the Jackson library. It is configured to be robust by
 * ignoring unknown properties during deserialization, which helps with
 * forward and backward compatibility.
 * </p>
 * @author Gabriele Pintus
 */
public final class JsonUtils {

    public final static String JSON_EXT = Settings.get("files.defaultExtension");

    /**
     * The Jackson ObjectMapper instance used for all serialization and deserialization operations.
     * It is configured to not fail when it encounters unknown JSON properties, making it more
     * resilient to schema changes.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JsonUtils() {
    }

    /**
     * Serializes a Java object to a JSON string.
     *
     * @param obj The object to serialize.
     * @return The JSON string representation of the object.
     * @throws RuntimeException if the serialization process fails.
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Deserializes a JSON string into a Java object of the specified class.
     *
     * @param json  The JSON string to deserialize.
     * @param clazz The class type to deserialize the JSON into.
     * @param <T>   The generic type of the class.
     * @return An instance of the specified class populated with data from the JSON string.
     * @throws RuntimeException if the deserialization process fails.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON to object of class " + clazz.getName(), e);
        }
    }

    /**
     * Ensures that a file has the correct JSON extension.
     *
     * @param file The file to check.
     * @return A {@link File} object with the proper extension.
     */
    public static File ensureJsonExtension(File file) {
            String name = file.getName().toLowerCase();
            if (!name.endsWith("." + JSON_EXT)) {
                return new File(file.getParentFile(), file.getName() + "." + JSON_EXT);
            }
            return file;
        }
}