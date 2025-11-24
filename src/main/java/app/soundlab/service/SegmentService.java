package app.soundlab.service;

import app.soundlab.audiotrack.SegmentEntity;
import app.soundlab.audiotrack.WavePreviewPanel;
import app.soundlab.dao.SegmentDao;

import javax.swing.JPanel;
import java.util.List;

public class SegmentService {
    private final SegmentDao segmentDao;

    public SegmentService() {
        this.segmentDao = new SegmentDao();
    }

    public void createSegment(String filePath, int startSeconds, int endSeconds, String label) {
        int audioId = getAudioIdByPath(filePath);
        if (audioId == -1) {
            throw new IllegalArgumentException("Audio file not found: " + filePath);
        }
        
        String segmentLabel = (label == null || label.isBlank())
                ? String.format("Segment %ds-%ds", startSeconds, endSeconds)
                : label.trim();
        
        int startMs = startSeconds * 1000;
        int endMs = endSeconds * 1000;
        
        segmentDao.create(audioId, startMs, endMs, segmentLabel);
    }

    public List<String> getAllSegments() {
        return segmentDao.getAll();
    }

    public int getAudioIdByPath(String path) {
        return segmentDao.getAudioIdByPath(path);
    }

    public JPanel outputWaveForm(SegmentEntity audio) {
        if (audio == null) {
            throw new IllegalArgumentException("Audio entity cannot be null");
        }
        
        List<Float> audioData = audio.getAudioData();
        if (audioData == null || audioData.isEmpty()) {
            throw new IllegalStateException("Audio data is empty or not loaded");
        }
        
        return new WavePreviewPanel(audioData);
    }
}

