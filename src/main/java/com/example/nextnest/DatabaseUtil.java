package com.example.nextnest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    // Database settings are read from db.properties in the project root
    // (copy db.properties.example -> db.properties). That file is in .gitignore,
    // so the real password is never uploaded to GitHub.
    private static final Properties CONFIG = loadConfig();

    private static final String URL = CONFIG.getProperty("db.url", "jdbc:mysql://localhost:3306/nextnest");
    private static final String USER = CONFIG.getProperty("db.user", "root");
    private static final String PASSWORD = CONFIG.getProperty("db.password", "");

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("db.properties not found - using default database settings.");
        }
        return props;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
