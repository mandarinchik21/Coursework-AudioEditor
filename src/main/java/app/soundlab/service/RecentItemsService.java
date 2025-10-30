package app.soundlab.service;

import java.util.List;

import app.soundlab.dao.AudioDao;
import app.soundlab.dao.SegmentDao;
import app.soundlab.dao.WorkspaceDao;

public class RecentItemsService {
    private final AudioDao audioRepository;
    private final SegmentDao trackRepository;
    private final WorkspaceDao workspaceRepository;

    public RecentItemsService() {
        this.audioRepository = new AudioDao();
        this.trackRepository = new SegmentDao();
        this.workspaceRepository = new WorkspaceDao();
    }

    public List<String> getLastAudios(int limit) {
        return audioRepository.getAll().stream().limit(limit).toList();
    }

    public List<String> getLastTracks(int limit) {
        return trackRepository.getAll().stream().limit(limit).toList();
    }

    public List<String> getLastWorkspaces(int limit) {
        return workspaceRepository.getAllWorkspaces().stream().limit(limit).toList();
    }
}


