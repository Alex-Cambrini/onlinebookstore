package com.bittercode.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static final Properties prop = new Properties();

    public static final String DRIVER_NAME;
    public static final String DB_HOST;
    public static final String DB_PORT;
    public static final String DB_NAME;
    public static final String DB_USER_NAME;
    public static final String DB_PASSWORD;
    public static final String CONNECTION_STRING;

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("application.properties");

        try {
            prop.load(input);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Failed to load properties", e);
        }

        DRIVER_NAME = prop.getProperty("db.driver");
        DB_HOST = prop.getProperty("db.host");
        DB_PORT = prop.getProperty("db.port");
        DB_NAME = prop.getProperty("db.name");
        DB_USER_NAME = prop.getProperty("db.username");
        DB_PASSWORD = prop.getProperty("db.password");
        CONNECTION_STRING = DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    }

    private DatabaseConfig() {
        // prevent instantiation
    }
}
