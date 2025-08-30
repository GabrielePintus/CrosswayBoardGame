package org.boardgames.crossway.utils;

import java.util.*;
import java.util.stream.Collectors;

public final class Messages {
    private static volatile ResourceBundle BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());

    private Messages() {}

    public static void setLocale(Locale locale) {
        BUNDLE = ResourceBundle.getBundle("messages", locale);
    }

    public static String get(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

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
     * The values are returned in an order determined by the alphabetical sorting of their keys.
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
     * The values are returned in an order determined by the alphabetical sorting of their keys.
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