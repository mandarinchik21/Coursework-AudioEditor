package app.soundlab.dao;

import app.soundlab.db.DatabaseContext;

import java.util.List;

public class WorkspaceDao {
    public void addWorkspace(String title) {
        DatabaseContext.update(
                "INSERT INTO Workspace (title) VALUES (?)",
                statement -> statement.setString(1, title)
        );
    }

    public List<String> getAllWorkspaces() {
        return DatabaseContext.query(
                "SELECT title, created_at FROM Workspace ORDER BY id DESC",
                statement -> {},
                rs -> "\"" + rs.getString("title") + "\" - last save: " + rs.getString("created_at")
        );
    }

    public void addAudioToWorkspace(int workspaceId, int audioId) {
        DatabaseContext.update(
                "INSERT INTO Workspace_Audio (workspace_id, audio_id) VALUES (?, ?)",
                statement -> {
                    statement.setInt(1, workspaceId);
                    statement.setInt(2, audioId);
                }
        );
    }

    public int getLastInsertedWorkspaceID() {
        return DatabaseContext.queryForObject(
                        "SELECT id FROM Workspace ORDER BY id DESC LIMIT 1",
                        statement -> {},
                        rs -> rs.getInt("id"))
                .orElse(-1);
    }
}
