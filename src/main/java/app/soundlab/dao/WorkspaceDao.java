package app.soundlab.dao;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceDao {
    public void addWorkspace(String name) {
        System.out.println("WorkspaceDao.addWorkspace: name='" + name + "'");
        System.out.println("[SIMULATION] Workspace inserted");
    }

    public List<String> getAllWorkspaces() {
        System.out.println("WorkspaceDao.getAllWorkspaces: fetch all workspaces (simulated)");
        List<String> workspaceList = new ArrayList<>();
        workspaceList.add("\"Demo Workspace\" - last save: 2025-10-29 12:00:00");
        workspaceList.add("\"Another Workspace\" - last save: 2025-10-28 18:30:00");
        return workspaceList;
    }

    public void addAudioToWorkspace(int workspaceId, int audioId) {
        System.out.println("WorkspaceDao.addAudioToWorkspace: workspaceId=" + workspaceId + ", audioId=" + audioId);
        System.out.println("[SIMULATION] Workspace_Audio relation inserted");
    }

    public int getLastInsertedWorkspaceId() {
        System.out.println("WorkspaceDao.getLastInsertedWorkspaceId: return last id (simulated)");
        return 42;
    }
}

