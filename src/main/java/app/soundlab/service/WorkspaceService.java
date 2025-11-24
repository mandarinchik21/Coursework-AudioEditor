package app.soundlab.service;

import app.soundlab.dao.SegmentDao;
import app.soundlab.dao.WorkspaceDao;

import java.util.List;

public class WorkspaceService {
    private final WorkspaceDao workspaceDao;
    private final SegmentDao segmentDao;
    private final AudioService audioService;

    public WorkspaceService() {
        this.workspaceDao = new WorkspaceDao();
        this.segmentDao = new SegmentDao();
        this.audioService = new AudioService();
    }

    public int createWorkspace(String title) {
        workspaceDao.addWorkspace(title);
        return workspaceDao.getLastInsertedWorkspaceID();
    }

    public void addAudioToWorkspace(String workspaceTitle, String audioPath) {
        int workspaceId = getWorkspaceIdByTitle(workspaceTitle);
        if (workspaceId == -1) {
            throw new IllegalArgumentException("Workspace not found: " + workspaceTitle);
        }
        
        int audioId = segmentDao.getAudioIdByPath(audioPath);
        if (audioId == -1) {
            throw new IllegalArgumentException("Audio file not found: " + audioPath);
        }
        
        workspaceDao.addAudioToWorkspace(workspaceId, audioId);
    }

    public void addAudioToWorkspace(int workspaceId, String audioPath) {
        int audioId = segmentDao.getAudioIdByPath(audioPath);
        if (audioId == -1) {
            throw new IllegalArgumentException("Audio file not found: " + audioPath);
        }
        
        workspaceDao.addAudioToWorkspace(workspaceId, audioId);
    }

    public List<String> getAllWorkspaces() {
        return workspaceDao.getAllWorkspaces();
    }

    public int getLastInsertedWorkspaceId() {
        return workspaceDao.getLastInsertedWorkspaceID();
    }

    public List<String> getAudiosByWorkspace(int workspaceId) {
        return audioService.getByWorkspace(workspaceId);
    }

    private int getWorkspaceIdByTitle(String title) {
        List<String> workspaces = getAllWorkspaces();
        for (String workspace : workspaces) {
            if (workspace.contains("\"" + title + "\"")) {
                return getLastInsertedWorkspaceId();
            }
        }
        return -1;
    }
}

