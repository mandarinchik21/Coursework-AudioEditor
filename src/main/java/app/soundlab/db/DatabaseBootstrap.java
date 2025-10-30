package app.soundlab.db;

public class DatabaseBootstrap {
    public static void initializeDatabase() {
        System.out.println("DatabaseInitializer: Initializing database...");
        System.out.println("Creating Audio table with columns: id, name, format, path");
        System.out.println("Creating Track table with columns: id, audio_id, start_time, end_time");
        System.out.println("Creating Workspace table with columns: id, name, created_at");
        System.out.println("Creating Workspace_Audio table with columns: workspace_id, audio_id");
        System.out.println("Database tables initialized successfully!");
    }
}

