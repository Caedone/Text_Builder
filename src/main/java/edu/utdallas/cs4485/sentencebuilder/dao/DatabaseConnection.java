package edu.utdallas.cs4485.sentencebuilder.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * Manages database connection pooling using HikariCP for optimal performance.
 * This class implements the singleton pattern to provide a centralized point
 * for database connectivity across the application.
 *
 * HikariCP connection pooling drastically improves performance by reusing database
 * connections rather than creating new connections for each operation. The pool
 * configuration is loaded from database.properties file and manages connection
 * lifecycle automatically.
 *
 * This implementation works alongside java.sql standard interfaces while providing
 * enterprise-grade connection management and performance optimizations.
 *
 * @author Manraj Singh
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    /**
     * Private constructor for singleton pattern.
     */
    private DatabaseConnection() {
        // TODO: Initialize connection pool
        initializeDataSource();
    }

    /**
     * Gets the singleton instance.
     *
     * @return the database connection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Initializes the HikariCP data source. Note for future self: TODO:
     * Implment manual connection management i.e. Open/Close connection per
     * method call
     */
    private void initializeDataSource() {
        try {
            Properties props = loadDatabaseProperties();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

            // Connection pool settings
            /*  Not really necessary since DB isn't complex enough 
            config.setMaximumPoolSize(
                    Integer.parseInt(props.getProperty("db.pool.maximum.size", "10")));
            config.setMinimumIdle(
                    Integer.parseInt(props.getProperty("db.pool.minimum.idle", "5")));
            config.setConnectionTimeout(
                    Long.parseLong(props.getProperty("db.pool.connection.timeout", "30000")));
            config.setIdleTimeout(
                    Long.parseLong(props.getProperty("db.pool.idle.timeout", "600000")));
            config.setMaxLifetime(
                    Long.parseLong(props.getProperty("db.pool.max.lifetime", "1800000")));

            // Performance settings
            config.addDataSourceProperty("cachePrepStmts",
                    props.getProperty("db.cache.prep.stmts", "true"));
            config.addDataSourceProperty("prepStmtCacheSize",
                    props.getProperty("db.prep.stmt.cache.size", "250"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit",
                    props.getProperty("db.prep.stmt.cache.sql.limit", "2048"));
             */
            this.dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            System.err.println("Failed to load database properties: " + e.getMessage());
            throw new RuntimeException("Database configuration error", e);
        }
    }

    /**
     * Loads database properties from file.
     *
     * @return properties object
     * @throws IOException if properties file cannot be read
     */
    private Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Unable to find database.properties");
            }
            props.load(input);
        }
        return props;
    }

    /**
     * Gets a connection from the pool.
     *
     * @return database connection
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        // TODO: Implement connection retrieval
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool.
     */
    public void close() {
        // TODO: Implement cleanup
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Tests the database connection.
     *
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
