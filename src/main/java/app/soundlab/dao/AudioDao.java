package app.soundlab.dao;

import app.soundlab.db.DatabaseContext;

import java.util.List;

public class AudioDao {
    public void create(String title, String format, String path) {
        DatabaseContext.update(
                "INSERT INTO Audio (title, format, path) VALUES (?, ?, ?)",
                statement -> {
                    statement.setString(1, title);
                    statement.setString(2, format);
                    statement.setString(3, path);
                }
        );
    }

    public boolean exists(String path) {
        return DatabaseContext.queryForObject(
                        "SELECT COUNT(*) AS count FROM Audio WHERE path = ?",
                        statement -> statement.setString(1, path),
                        rs -> rs.getInt("count"))
                .map(count -> count > 0)
                .orElse(false);
    }

    public List<String> getAll() {
        return DatabaseContext.query(
                "SELECT title, path FROM Audio ORDER BY id DESC",
                statement -> {},
                rs -> rs.getString("title") + " - " + rs.getString("path")
        );
    }

    public List<String> getByWorkspaceID(int workspaceId) {
        String sql = """
            SELECT Audio.title FROM Audio
            JOIN Workspace_Audio ON Audio.id = Workspace_Audio.audio_id
            WHERE Workspace_Audio.workspace_id = ?
            """;
        return DatabaseContext.query(
                sql,
                statement -> statement.setInt(1, workspaceId),
                rs -> rs.getString("title")
        );
    }
}
