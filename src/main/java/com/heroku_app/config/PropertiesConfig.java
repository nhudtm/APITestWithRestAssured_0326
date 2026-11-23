package com.heroku_app.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.heroku_app.commons.GlobalConstants;

public class PropertiesConfig {
    private static final Properties PROPS = new Properties();

    static {
        String filePath = GlobalConstants.PROPERTIES_FILE_PATH;
        if (filePath == null || filePath.isEmpty()) {
            throw new ExceptionInInitializerError("Config file path is not specified.");
        };
        Path configPath = Path.of(filePath);
        try (Reader reader = Files.newBufferedReader(configPath , StandardCharsets.UTF_8 )) {
            PROPS.load(reader);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load config properties from " + filePath + ": " + e.getMessage());
        }
    }

    private PropertiesConfig() {
        // Private constructor to prevent instantiation
    }

    public static String getProperty(String key) {

        validateKey(key);

        // 1. System Properties
        String systemPropertyValue = normalizeValue(System.getProperty(key));
        if (systemPropertyValue != null) {
            return systemPropertyValue;
        }
        
        // 2. Environment Variables with original key
        String envPropertyValue = normalizeValue(System.getenv(key));
        if (envPropertyValue != null) {
            return envPropertyValue;
        }

        // 3. Environment Variables with normalized key
        String normalizedEnvKey = normalizeKey(key);
        String normalizedEnvPropValue = normalizeValue(System.getenv(normalizedEnvKey));
        if (normalizedEnvPropValue != null) {
            return normalizedEnvPropValue;
        }

        // 4. Properties file
        return normalizeValue(PROPS.getProperty(key));
    }

    public static String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' is not set.");
        }
        return value;
    }

    /**
     * Validates a property key.
     *
     * @param key the property key to validate
     * @return the validated property key
     * @throws IllegalArgumentException if the property key is null or empty
     */
    // Normalize the key by converting it to uppercase and replacing non-alphanumeric characters with underscores
    private static void validateKey(String key) {
        if(key == null || key.isBlank()) {
            throw new IllegalArgumentException("Property key cannot be null or empty.");
        }
        
    }

   /**
     * Converts a property key to the conventional environment variable format.
     *
     * Examples:
     * baseUrl     -> BASE_URL
     * devUrl      -> DEV_URL
     * db-password -> DB_PASSWORD
     * db.password -> DB_PASSWORD
     */
    private static String normalizeKey(String key) {

        return key
                .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .replaceAll("[^a-zA-Z0-9]+", "_")
                .toUpperCase();
    }

    /**
     * Normalizes a configuration value.
     * - Removes leading and trailing whitespace.
     * - Removes surrounding double quotes.
     * - Returns null for null or empty values.
     */
    private static String normalizeValue(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();
        if (normalizedValue.length() >= 2 && normalizedValue.startsWith("\"") && normalizedValue.endsWith("\"")) {
            normalizedValue = normalizedValue.substring(1, normalizedValue.length() - 1).trim();
        }
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }

}
