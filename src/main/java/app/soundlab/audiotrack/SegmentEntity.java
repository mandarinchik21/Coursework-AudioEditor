package app.soundlab.audiotrack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class SegmentEntity {
    private static final int SAMPLE_RATE = 44100;
    private int id;
    protected String fileName;
    protected List<Float> audioData = new ArrayList<>();
    protected List<Float> clipboard = new ArrayList<>();
    protected int segmentStart;
    protected int segmentEnd;

    public SegmentEntity(String fileName) {
        this.fileName = fileName;

        for (int i = 0; i < 1000; i++) {
            audioData.add((float) Math.sin(i * 0.1) * 0.5f);
        }
        System.out.println("Audio file loaded: " + fileName + " (simulated " + audioData.size() + " samples)");
    }

    public abstract String getFormat();
    public abstract File getFileLink();

    public void select(int start, int end) {
        if (audioData == null || audioData.isEmpty()) {
            System.out.println("Error: Audio data is empty: invalid audio data");
            return;
        }
        if (start < 0 || end > audioData.size() || start >= end) {
            System.out.println("Error: Invalid segment bounds selected. Start: " + start + ", End: " + end + ", Max: " + audioData.size());
            return;
        }
        this.segmentStart = start;
        this.segmentEnd = end;
        System.out.println("Segment selected: " + start + " to " + end + " samples");
    }

    public void copy() {
        if (segmentStart < 0 || segmentEnd > audioData.size() || segmentStart >= segmentEnd) {
            System.out.println("Error: Invalid segment bounds for copy operation.");
            return;
        }
        clipboard.clear();
        clipboard.addAll(audioData.subList(segmentStart, segmentEnd));
        System.out.println("Audio segment copied (" + (segmentEnd - segmentStart) + " samples)");
    }

    public void cut() {
        if (segmentStart < 0 || segmentEnd > audioData.size() || segmentStart >= segmentEnd) {
            System.out.println("Error: Invalid segment bounds for cut operation.");
            return;
        }
        clipboard.clear();
        clipboard.addAll(audioData.subList(segmentStart, segmentEnd));
        audioData.subList(segmentStart, segmentEnd).clear();
        System.out.println("Audio segment cut (" + (segmentEnd - segmentStart) + " samples)");
    }

    public void paste(int positionInSeconds, int sampleRate) {
        int position = positionInSeconds * sampleRate;

        if (position < 0 || position > audioData.size()) {
            System.out.println("Error: Invalid paste position in seconds.");
            return;
        }

        audioData.addAll(position, clipboard);
        System.out.println("Audio segment pasted at position " + positionInSeconds + " seconds");
    }

    public void morph(double factor) {
        if (segmentStart < 0 || segmentEnd > audioData.size() || segmentStart >= segmentEnd) {
            System.out.println("Error: Invalid bounds for morphing.");
            return;
        }
        System.out.println("Applying morphing with factor " + factor + " to segment " + segmentStart + "-" + segmentEnd);
        for (int i = segmentStart; i < segmentEnd; i++) {
            float newValue = audioData.get(i) * (float) factor;
            newValue = Math.max(-1.0f, Math.min(1.0f, newValue));
            audioData.set(i, newValue);
        }
        System.out.println("Deformation applied successfully");
    }

    public int getSize() {
        return audioData != null ? audioData.size() : 0;
    }

    public int getDurationSeconds() {
        return audioData.size() / SAMPLE_RATE;
    }

    public int getSamplesBySeconds(int seconds) {
        return seconds * SAMPLE_RATE;
    }

    public int getSecondsBySamples(int samples) {
        return samples / SAMPLE_RATE;
    }

    public void saveAs(String outputFilePath) {
        System.out.println("Saving audio file to: " + outputFilePath);
        System.out.println("File format: " + getFormat());
        System.out.println("Duration: " + getDurationSeconds() + " seconds");
        System.out.println("Samples: " + audioData.size());
        System.out.println("Audio file saved successfully!");
    }
}

