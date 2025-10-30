package app.soundlab.dao;

import app.soundlab.db.DatabaseConnector;

import java.util.ArrayList;
import java.util.List;

public class SegmentDao extends DatabaseConnector {
    public SegmentDao() {
        super();
    }

    public void create(int audioId, int startTime, int endTime) {
        System.out.println("SegmentDao.addTrack: audioId=" + audioId + ", start=" + startTime + ", end=" + endTime);
        System.out.println("[SIMULATION] Track record inserted");
    }

    public List<String> getAll() {
        System.out.println("SegmentDao.getAllTracks: fetch all tracks (simulated)");
        List<String> trackList = new ArrayList<>();
        trackList.add("0s to 5s in sample1.mp3");
        trackList.add("10s to 20s in sample2.ogg");
        return trackList;
    }

    public void update(int trackId, int startTime, int endTime) {
        System.out.println("SegmentDao.updateTrack: id=" + trackId + ", start=" + startTime + ", end=" + endTime);
        System.out.println("[SIMULATION] Track updated");
    }

    public int getAudioIdByPath(String path) {
        System.out.println("SegmentDao.getAudioIdByPath: path='" + path + "' (simulated)");
        return 1; 
    }
}

