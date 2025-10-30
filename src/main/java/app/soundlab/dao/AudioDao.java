package app.soundlab.dao;

import app.soundlab.db.DatabaseConnector;

import java.util.ArrayList;
import java.util.List;

public class AudioDao extends DatabaseConnector {
    public AudioDao() {
        super();
    }

    public void create(String name, String format, String path) {
        System.out.println("AudioDao.create: name='" + name + "', format='" + format + "', path='" + path + "'");
        System.out.println("[SIMULATION] Audio record inserted");
    }

    public List<String> getAll() {
        System.out.println("AudioDao.getAllAudio: fetch all audios (simulated)");
        List<String> audioList = new ArrayList<>();
        audioList.add("sample1.mp3 - /path/sample1.mp3");
        audioList.add("sample2.ogg - /path/sample2.ogg");
        return audioList;
    }

    public List<String> getByWorkspace(int workspaceId) {
        System.out.println("AudioDao.getAudioByWorkspace: workspaceId=" + workspaceId + " (simulated)");
        List<String> audioList = new ArrayList<>();
        audioList.add("workspace-audio-1.mp3");
        audioList.add("workspace-audio-2.flac");
        return audioList;
    }

    public boolean exists(String path) {
        System.out.println("AudioDao.audioExists: path='" + path + "' (simulated)");
        return false;
    }
}

