package app.soundlab.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseBootstrap {
    private static final String SCHEMA_FILE = "/db/schema.sql";

    public static void init() {
        try (Connection connection = DatabaseConnector.connect();
             Statement statement = connection.createStatement()) {
            
            String schema = loadSchema();
            executeSchema(statement, schema);
            
            System.out.println("Tables initialized!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    private static String loadSchema() {
        try (InputStream inputStream = DatabaseBootstrap.class.getResourceAsStream(SCHEMA_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Schema file not found: " + SCHEMA_FILE);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load schema file: " + SCHEMA_FILE, e);
        }
    }

    private static void executeSchema(Statement statement, String schema) throws SQLException {
        String[] statements = schema.split(";");
        for (String sql : statements) {
            String trimmed = sql.trim();
            if (!trimmed.isEmpty()) {
                statement.execute(trimmed);
            }
        }
    }

}

