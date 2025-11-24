package app.soundlab.audiotrack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class SegmentEntity {
    private static final int SAMPLE_RATE = 44100;
    private static final int WAV_HEADER_SIZE = 36;
    private static final int MIN_SAMPLE_VALUE = -32768;
    private static final int MAX_SAMPLE_VALUE = 32767;
    private static final int AUDIO_CHANNELS = 2;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int WAV_FMT_CHUNK_SIZE = 16;
    private static final int PCM_FORMAT = 1;

    protected final String fileName;
    protected final List<Float> audioData = new ArrayList<>();
    protected final List<Float> clipboard = new ArrayList<>();
    protected int segmentStart = -1;
    protected int segmentEnd = -1;

    protected SegmentEntity(String fileName) {
        this.fileName = fileName;
    }

    public abstract String getFormat();

    public abstract File getFileLink();

    public void select(int start, int end) {
        ensureAudioLoaded();
        validateSegmentBounds(start, end);
        this.segmentStart = start;
        this.segmentEnd = end;
    }

    public void copy() {
        ensureAudioLoaded();
        validateActiveSegment();
        clipboard.clear();
        clipboard.addAll(audioData.subList(segmentStart, segmentEnd));
    }

    public void cut() {
        ensureAudioLoaded();
        validateActiveSegment();
        clipboard.clear();
        clipboard.addAll(audioData.subList(segmentStart, segmentEnd));
        audioData.subList(segmentStart, segmentEnd).clear();
    }

    public void paste(int positionInSeconds) {
        if (clipboard.isEmpty()) {
            throw new IllegalStateException("Clipboard is empty. Copy or cut a segment first.");
        }
        int position = positionInSeconds * SAMPLE_RATE * AUDIO_CHANNELS;
        if (position < 0 || position > audioData.size()) {
            throw new IllegalArgumentException("Invalid paste position.");
        }
        audioData.addAll(position, clipboard);
    }

    public void morph(double factor) {
        ensureAudioLoaded();
        validateActiveSegment();
        for (int i = segmentStart; i < segmentEnd; i++) {
            float newValue = audioData.get(i) * (float) factor;
            newValue = clampSampleValue(newValue);
            audioData.set(i, newValue);
        }
    }

    public int getSize() {
        return audioData.size();
    }

    public List<Float> getAudioData() {
        return new ArrayList<>(audioData);
    }

    public int getDurationSeconds() {
        return (audioData.size() / SAMPLE_RATE) / AUDIO_CHANNELS;
    }

    public int getSamplesBySeconds(int seconds) {
        return seconds * SAMPLE_RATE * AUDIO_CHANNELS;
    }

    public int getSecondsBySamples(int samples) {
        return (samples / SAMPLE_RATE) / AUDIO_CHANNELS;
    }

    public void saveAs(String outputFilePath) {
        try {
            File outputFile = new File(outputFilePath);
            writeWavFile(outputFile, audioData, SAMPLE_RATE);
        } catch (IOException e) {
            throw new RuntimeException("Error saving audio file: " + e.getMessage(), e);
        }
    }

    public byte[] generateWavData() throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        writeWavToStream(baos, audioData, SAMPLE_RATE);
        return baos.toByteArray();
    }

    private void writeWavFile(File outputFile, List<Float> samples, int sampleRate) throws IOException {
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile)) {
            writeWavToStream(fos, samples, sampleRate);
            fos.flush();
            fos.getFD().sync();
        }
    }

    private void writeWavToStream(java.io.OutputStream os, List<Float> samples, int sampleRate) throws IOException {
        int byteRate = sampleRate * AUDIO_CHANNELS * BITS_PER_SAMPLE / 8;
        int blockAlign = AUDIO_CHANNELS * BITS_PER_SAMPLE / 8;
        int dataSize = samples.size() * BYTES_PER_SAMPLE;
        int fileSize = WAV_HEADER_SIZE + dataSize;

        writeText(os, "RIFF");
        writeFourBytes(os, fileSize);
        writeText(os, "WAVE");
        writeText(os, "fmt ");
        writeFourBytes(os, WAV_FMT_CHUNK_SIZE);
        writeTwoBytes(os, PCM_FORMAT);
        writeTwoBytes(os, AUDIO_CHANNELS);
        writeFourBytes(os, sampleRate);
        writeFourBytes(os, byteRate);
        writeTwoBytes(os, blockAlign);
        writeTwoBytes(os, BITS_PER_SAMPLE);
        writeText(os, "data");
        writeFourBytes(os, dataSize);

        for (float sample : samples) {
            int sampleInt = Math.round(sample);
            sampleInt = clampSampleValue(sampleInt);
            writeTwoBytes(os, sampleInt);
        }
    }

    private void writeText(java.io.OutputStream os, String text) throws IOException {
        os.write(text.getBytes("ASCII"));
    }

    private void writeTwoBytes(java.io.OutputStream os, int value) throws IOException {
        os.write(intToTwoBytes(value));
    }

    private void writeFourBytes(java.io.OutputStream os, int value) throws IOException {
        os.write(intToFourBytes(value));
    }

    private byte[] intToTwoBytes(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        return bytes;
    }

    private byte[] intToFourBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        bytes[2] = (byte) ((value >> 16) & 0xFF);
        bytes[3] = (byte) ((value >> 24) & 0xFF);
        return bytes;
    }

    private int clampSampleValue(float value) {
        return clampSampleValue((int) value);
    }

    private int clampSampleValue(int value) {
        return Math.max(MIN_SAMPLE_VALUE, Math.min(MAX_SAMPLE_VALUE, value));
    }

    private void ensureAudioLoaded() {
        if (audioData.isEmpty()) {
            throw new IllegalStateException("Audio data is empty. Load a valid file first.");
        }
    }

    private void validateSegmentBounds(int start, int end) {
        if (start < 0 || end > audioData.size() || start >= end) {
            throw new IllegalArgumentException(
                    String.format("Invalid segment bounds. Start: %d, End: %d, Max: %d.", start, end, audioData.size())
            );
        }
    }

    private void validateActiveSegment() {
        if (segmentStart < 0 || segmentEnd > audioData.size() || segmentStart >= segmentEnd) {
            throw new IllegalArgumentException("Invalid segment bounds.");
        }
    }
}
