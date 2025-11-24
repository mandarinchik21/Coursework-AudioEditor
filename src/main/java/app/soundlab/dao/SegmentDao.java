package app.soundlab.dao;

import app.soundlab.db.DatabaseContext;

import java.util.List;

public class SegmentDao {
    public void create(int audioId, int beginningMs, int endMs, String label) {
        DatabaseContext.update(
                "INSERT INTO Segment (audio_id, beginning_ms, end_ms, label) VALUES (?, ?, ?, ?)",
                statement -> {
                    statement.setInt(1, audioId);
                    statement.setInt(2, beginningMs);
                    statement.setInt(3, endMs);
                    statement.setString(4, label);
                }
        );
    }

    public List<String> getAll() {
        String sql = """
            SELECT Segment.beginning_ms, Segment.end_ms, Segment.label, Audio.title
            FROM Segment JOIN Audio ON Segment.audio_id = Audio.id
            ORDER BY Segment.id DESC
            """;
        return DatabaseContext.query(
                sql,
                statement -> {},
                rs -> {
                    String label = rs.getString("label");
                    String labelPrefix = (label == null || label.isBlank()) ? "" : "[" + label + "] ";
                    int beginningMs = rs.getInt("beginning_ms");
                    int endMs = rs.getInt("end_ms");
                    double beginningSeconds = beginningMs / 1000.0;
                    double endSeconds = endMs / 1000.0;
                    return labelPrefix
                            + formatSeconds(beginningSeconds) + "s to "
                            + formatSeconds(endSeconds) + "s in "
                            + rs.getString("title");
                }
        );
    }

    public void update(int segmentId, int beginningMs, int endMs, String label) {
        DatabaseContext.update(
                "UPDATE Segment SET beginning_ms = ?, end_ms = ?, label = ? WHERE id = ?",
                statement -> {
                    statement.setInt(1, beginningMs);
                    statement.setInt(2, endMs);
                    statement.setString(3, label);
                    statement.setInt(4, segmentId);
                }
        );
    }

    public int getAudioIdByPath(String path) {
        return DatabaseContext.queryForObject(
                        "SELECT id FROM Audio WHERE path = ?",
                        statement -> statement.setString(1, path),
                        rs -> rs.getInt("id"))
                .orElse(-1);
    }

    private String formatSeconds(double seconds) {
        if (seconds == (long) seconds) {
            return String.valueOf((long) seconds);
        }
        return String.format("%.3f", seconds);
    }
}
