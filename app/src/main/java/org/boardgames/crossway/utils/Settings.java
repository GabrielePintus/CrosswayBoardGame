package org.boardgames.crossway.utils;

import java.io.InputStream;
import java.util.Properties;

public final class Settings {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Settings.class.getClassLoader().getResourceAsStream("settings.properties")) {
            if (in != null) PROPS.load(in);
            else throw new IllegalStateException("settings.properties not found on classpath");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load settings.properties", e);
        }
    }

    private Settings() {}

    public static String get(String key) {
        return PROPS.getProperty(key);
    }

    public static String get(String key, String def) {
        return PROPS.getProperty(key, def);
    }

    public static int getInt(String key, int def) {
        try { return Integer.parseInt(PROPS.getProperty(key)); } catch (Exception e) { return def; }
    }

    public static boolean getBool(String key, boolean def) {
        String v = PROPS.getProperty(key);
        return (v == null) ? def : Boolean.parseBoolean(v);
    }
}
