package app.soundlab.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnector {
    private static final String URL = "jdbc:sqlite:soundlab.db";

    private DatabaseConnector() {
    }

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("db connection failed", e);
        }
    }
}

