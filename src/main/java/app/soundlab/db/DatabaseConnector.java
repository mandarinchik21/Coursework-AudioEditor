package app.soundlab.db;

public class DatabaseConnector {
    private static DatabaseConnector CONNECTOR;

    protected DatabaseConnector() {
        System.out.println("DatabaseConnection: Creating database connection instance");
    }

    public static DatabaseConnector get() {
        return CONNECTOR != null ? CONNECTOR : new DatabaseConnector();
    }

    public void connect() {
        System.out.println("DatabaseConnection: Connecting to SQLite database 'audio_editor.db'");
    }

    public void disconnect() {
        System.out.println("DatabaseConnection: Disconnecting from database");
    }
}

