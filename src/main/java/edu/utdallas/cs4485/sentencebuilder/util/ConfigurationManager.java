package edu.utdallas.cs4485.sentencebuilder.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * Singleton configuration manager for loading and accessing application and
 * database settings from properties files.
 *
 * Provides centralized access to configuration data stored in application.properties
 * (general settings) and database.properties (connection settings). Implements the
 * Singleton pattern to ensure consistent configuration across all application components
 * with lazy initialization and synchronized access for thread safety.
 *
 * Offers type-safe getter methods for different data types (String, int, boolean)
 * with default value support to prevent null pointer exceptions. This centralized
 * approach enables easy configuration modification without code recompilation, useful
 * for database credentials, file size limits, UI preferences, and feature flags.
 *
 * Properties files are loaded from the classpath at initialization, with lazy loading
 * deferring object creation until first use to optimize startup performance.
 *
 * @author Caedon Ewing
 */
public class ConfigurationManager {

    private static ConfigurationManager instance;
    private Properties appProperties;
    private Properties dbProperties;

    /**
     * Private constructor for singleton pattern.
     */
    private ConfigurationManager() {
        // TODO: Initialize configuration
        loadProperties();
    }

    /**
     * Gets the singleton instance.
     *
     * @return the configuration manager instance
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Loads properties from files.
     */
    private void loadProperties() {
        appProperties = new Properties();
        dbProperties = new Properties();

        try (InputStream appInput = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (appInput != null) {
                appProperties.load(appInput);
            }
        } catch (IOException e) {
            System.err.println("Failed to load application.properties: " + e.getMessage());
        }

        try (InputStream dbInput = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (dbInput != null) {
                dbProperties.load(dbInput);
            }
        } catch (IOException e) {
            System.err.println("Failed to load database.properties: " + e.getMessage());
        }
    }

    /**
     * Gets an application property value.
     *
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String getAppProperty(String key) {
        // TODO: Implement property retrieval
        return appProperties.getProperty(key);
    }

    /**
     * Gets an application property with a default value.
     *
     * @param key the property key
     * @param defaultValue the default value
     * @return the property value, or default if not found
     */
    public String getAppProperty(String key, String defaultValue) {
        return appProperties.getProperty(key, defaultValue);
    }

    /**
     * Gets a database property value.
     *
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String getDbProperty(String key) {
        // TODO: Implement database property retrieval
        return dbProperties.getProperty(key);
    }

    /**
     * Gets an integer application property.
     *
     * @param key the property key
     * @param defaultValue the default value
     * @return the property value as integer
     */
    public int getIntProperty(String key, int defaultValue) {
        // TODO: Implement integer property retrieval
        String value = getAppProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer property: " + key);
            }
        }
        return defaultValue;
    }

    /**
     * Gets a boolean application property.
     *
     * @param key the property key
     * @param defaultValue the default value
     * @return the property value as boolean
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        // TODO: Implement boolean property retrieval
        String value = getAppProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
}