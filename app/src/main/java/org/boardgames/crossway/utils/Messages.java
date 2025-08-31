package org.boardgames.crossway.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A utility class for retrieving and formatting localized messages from a
 * resource bundle.
 * <p>
 * This class provides static methods to handle internationalization (i18n)
 * and message formatting throughout the application. It ensures that all
 * user-facing text is sourced from a central location, allowing for easy
 * localization.
 * </p>
 */
public final class Messages {
    private static volatile ResourceBundle BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());

    private Messages() {}

    /**
     * Sets the locale for the message resource bundle.
     * <p>
     * This method reloads the resource bundle based on the specified locale,
     * ensuring that subsequent calls to {@code get} and {@code format}
     * return messages in the correct language.
     * </p>
     * @param locale The new {@link Locale} to use.
     */
    public static void setLocale(Locale locale) {
        BUNDLE = ResourceBundle.getBundle("messages", locale);
    }

    /**
     * Retrieves a localized string for the given key.
     * <p>
     * If the key is not found in the resource bundle, this method returns
     * the key surrounded by exclamation marks (e.g., {@code !key!}) to
     * indicate a missing translation.
     * </p>
     * @param key The key of the message to retrieve.
     * @return The localized string, or a fallback string if the key is missing.
     */
    public static String get(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    /**
     * Retrieves and formats a localized message string.
     * <p>
     * This method works like {@link #get(String)} but also applies
     * {@link String#format(String, Object...)} to the retrieved message,
     * allowing for dynamic content insertion.
     * </p>
     * @param key The key of the message to retrieve.
     * @param args The arguments to be formatted into the message string.
     * @return The formatted localized string.
     */
    public static String format(String key, Object... args) {
        return String.format(get(key), args);
    }

    /**
     * Gets all keys that start with the given prefix and returns them as a Map.
     * The keys in the returned map are the suffixes of the original keys after the prefix.
     * For example, if the resource bundle contains "menu.game.boardSize.tiny=Tiny (5x5)",
     * calling getPrefixedMap("menu.game.boardSize") will return a map with the entry {"tiny": "Tiny (5x5)"}.
     *
     * @param prefix The prefix to search for.
     * @return A Map<String, String> containing the found key-value pairs, preserving their order from the properties file.
     */
    public static Map<String, String> getPrefixedMap(String prefix) {
        final String prefixWithDot = prefix + ".";
        return BUNDLE.keySet().stream()
                .filter(key -> key.startsWith(prefixWithDot))
                .collect(Collectors.toMap(
                        key -> key.substring(prefixWithDot.length()),
                        BUNDLE::getString,
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new
                ));
    }

    /**
     * Gets all values for keys that start with the given prefix and returns them as a List.
     * The values are returned in an order determined by the reverse alphabetical sorting of their keys.
     *
     * @param prefix The prefix to search for.
     * @return A List<String> containing the found values.
     */
    public static List<String> getPrefixedList(String prefix) {
        final String prefixWithDot = prefix + ".";
        return BUNDLE.keySet().stream()
                .filter(key -> key.startsWith(prefixWithDot))
                .sorted(Comparator.reverseOrder()) // Sort keys to ensure a predictable order of values
                .map(BUNDLE::getString)
                .collect(Collectors.toList());
    }

    /**
     * Gets all values for keys that start with the given prefix and returns them as an array.
     * The values are returned in an order determined by the reverse alphabetical sorting of their keys.
     *
     * @param prefix The prefix to search for.
     * @return An array of String containing the found values.
     */
    public static String[] getPrefixedArray(String prefix) {
        final String prefixWithDot = prefix + ".";
        return BUNDLE.keySet().stream()
                .filter(key -> key.startsWith(prefixWithDot))
                .sorted(Comparator.reverseOrder()) // Sort keys to ensure a predictable order of values
                .map(BUNDLE::getString)
                .toArray(String[]::new);
    }
}
