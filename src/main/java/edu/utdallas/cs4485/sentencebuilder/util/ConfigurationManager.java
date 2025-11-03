package edu.utdallas.cs4485.sentencebuilder.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application configuration from properties files.
 *
 * @author CS4485 Team
 * @version 1.0
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