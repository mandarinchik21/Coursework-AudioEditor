package app.soundlab.service;

import app.soundlab.dao.AudioDao;

import java.util.List;

public class AudioService {
    private final AudioDao audioDao;

    public AudioService() {
        this.audioDao = new AudioDao();
    }

    public void createAudio(String title, String format, String path) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Audio title cannot be empty");
        }
        if (format == null || format.isBlank()) {
            throw new IllegalArgumentException("Audio format cannot be empty");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Audio path cannot be empty");
        }
        
        audioDao.create(title, format, path);
    }

    public boolean exists(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        return audioDao.exists(path);
    }

    public List<String> getAll() {
        return audioDao.getAll();
    }

    public List<String> getByWorkspace(int workspaceId) {
        if (workspaceId < 0) {
            throw new IllegalArgumentException("Workspace ID must be non-negative");
        }
        return audioDao.getByWorkspaceID(workspaceId);
    }

    public List<String> getLastAudios(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must be non-negative");
        }
        return getAll().stream().limit(limit).toList();
    }
}

