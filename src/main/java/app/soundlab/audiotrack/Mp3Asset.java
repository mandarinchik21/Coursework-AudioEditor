package app.soundlab.audiotrack;

import app.soundlab.audioencoder.Mp3Encoder;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;

public class Mp3Asset extends SegmentEntity {
    private final AudioAttributes audioAttributes;
    private final File fileLink;

    public Mp3Asset(String filePath) {
        super(filePath);
        checkFileFormat(filePath);
        this.fileLink = new File(filePath);
        this.audioAttributes = new AudioAttributes();
        audioAttributes.setCodec("libmp3lame");
        audioAttributes.setBitRate(128000);
        audioAttributes.setChannels(2);
        audioAttributes.setSamplingRate(44100);
        try {
            loadAudioDataWithJave(fileLink);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load audio data: " + e.getMessage());
        }
    }

    private void loadAudioDataWithJave(File audioFile) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println("[Mp3Asset] Starting JAVE conversion: " + audioFile.getName());
        
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("wav");
        attrs.setAudioAttributes(audio);
        File tempWav = File.createTempFile("temp_audio", ".wav");
        
        long beforeEncode = System.currentTimeMillis();
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(audioFile), tempWav, attrs);
        long afterEncode = System.currentTimeMillis();
        System.out.println("[Mp3Asset] JAVE encode (MP3→WAV) took: " + (afterEncode - beforeEncode) + "ms");
        
        long beforePcm = System.currentTimeMillis();
        loadPcmData(tempWav);
        long afterPcm = System.currentTimeMillis();
        System.out.println("[Mp3Asset] PCM data load took: " + (afterPcm - beforePcm) + "ms");
        
        if (!tempWav.delete()) {
            tempWav.deleteOnExit();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("[Mp3Asset] Total load time: " + totalTime + "ms");
    }
    
    private static final int RIFF_HEADER_SIZE = 12;
    private static final int CHUNK_HEADER_SIZE = 8;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int READ_BUFFER_SIZE = 65536;
    private static final int BATCH_SIZE = 32768;
    private static final int MAX_SIGNED_16BIT = 32767;
    private static final int UNSIGNED_16BIT_RANGE = 65536;
    private static final int MAX_SEARCH_DISTANCE = 1024 * 1024;
    
    private static final byte[] RIFF_SIGNATURE = {'R', 'I', 'F', 'F'};
    private static final byte[] WAVE_SIGNATURE = {'W', 'A', 'V', 'E'};
    private static final byte[] DATA_CHUNK_ID = {'d', 'a', 't', 'a'};
    
    private void loadPcmData(File wavFile) throws Exception {
        validateWavFile(wavFile);
        
        int dataOffset = findDataChunk(wavFile);
        
        try (java.io.FileInputStream fis = new java.io.FileInputStream(wavFile)) {
            fis.skip(dataOffset);
            
            byte[] dataHeader = new byte[CHUNK_HEADER_SIZE];
            if (fis.read(dataHeader) < CHUNK_HEADER_SIZE) {
                throw new Exception("Could not read data chunk header");
            }
            
            int dataSize = readLittleEndianInt(dataHeader, 4);
            int estimatedSamples = dataSize / BYTES_PER_SAMPLE;
            ((java.util.ArrayList<Float>) audioData).ensureCapacity(estimatedSamples);
            
            byte[] buffer = new byte[READ_BUFFER_SIZE];
            long totalBytesRead = 0;
            java.util.ArrayList<Float> tempSamples = new java.util.ArrayList<>(BATCH_SIZE);
            
            while (totalBytesRead < dataSize) {
                int bytesRead = fis.read(buffer);
                if (bytesRead == -1) break;
            
                int bytesToProcess = Math.min(bytesRead, (int)(dataSize - totalBytesRead));
                totalBytesRead += bytesToProcess;
                
                for (int i = 0; i < bytesToProcess - 1; i += BYTES_PER_SAMPLE) {
                    int sample = readLittleEndianShort(buffer, i);
                    if (sample > MAX_SIGNED_16BIT) {
                        sample -= UNSIGNED_16BIT_RANGE;
                    }
                    tempSamples.add((float) sample);
                    
                    if (tempSamples.size() >= BATCH_SIZE) {
                        audioData.addAll(tempSamples);
                        tempSamples.clear();
                    }
                }
            }
            
            if (!tempSamples.isEmpty()) {
                audioData.addAll(tempSamples);
            }
        }
    }
    
    private void validateWavFile(File wavFile) throws Exception {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(wavFile)) {
            byte[] header = new byte[RIFF_HEADER_SIZE];
            if (fis.read(header) < RIFF_HEADER_SIZE) {
                throw new Exception("Invalid WAV file: too small");
            }
            
            if (!matchesSignature(header, 0, RIFF_SIGNATURE)) {
                throw new Exception("Invalid WAV file: missing RIFF header");
            }
            
            if (!matchesSignature(header, 8, WAVE_SIGNATURE)) {
                throw new Exception("Invalid WAV file: missing WAVE format");
            }
        }
    }
    
    private boolean matchesSignature(byte[] data, int offset, byte[] signature) {
        for (int i = 0; i < signature.length; i++) {
            if (data[offset + i] != signature[i]) {
                return false;
        }
        }
        return true;
    }
    
    private int findDataChunk(File wavFile) throws Exception {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(wavFile)) {
            fis.skip(RIFF_HEADER_SIZE);
            
            int offset = RIFF_HEADER_SIZE;
            byte[] chunkHeader = new byte[CHUNK_HEADER_SIZE];
            
            while (offset < MAX_SEARCH_DISTANCE) {
                int bytesRead = fis.read(chunkHeader, 0, CHUNK_HEADER_SIZE);
                if (bytesRead < CHUNK_HEADER_SIZE) {
                    break;
                }
                
                if (matchesSignature(chunkHeader, 0, DATA_CHUNK_ID)) {
                    return offset;
                }
                
                int chunkSize = readLittleEndianInt(chunkHeader, 4);
                long skipped = fis.skip(chunkSize);
                if (skipped != chunkSize) {
                    break;
                }
                
                offset += CHUNK_HEADER_SIZE + chunkSize;
            }
        }
        
        throw new Exception("Could not find data chunk in WAV file");
    }
    
    private int readLittleEndianInt(byte[] data, int offset) {
        return (data[offset] & 0xFF) |
               ((data[offset + 1] & 0xFF) << 8) |
               ((data[offset + 2] & 0xFF) << 16) |
               ((data[offset + 3] & 0xFF) << 24);
    }
    
    private int readLittleEndianShort(byte[] data, int offset) {
        return (data[offset + 1] << 8) | (data[offset] & 0xFF);
    }

    private void checkFileFormat(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a valid file.");
        }
        String extension = getFileExtension(file);
        if (!"mp3".equalsIgnoreCase(extension)) {
            throw new IllegalArgumentException("Wrong audio format. Expected MP3, but got: " + extension);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return (lastIndex > 0) ? name.substring(lastIndex + 1) : "";
    }

    @Override
    public String getFormat() {
        return "MP3";
    }

    @Override
    public File getFileLink() {
        return fileLink;
    }

    @Override
    public void saveAs(String outputFilePath) {
        super.saveAs(outputFilePath);
        try {
            File tempWav = new File(outputFilePath);
            if (!tempWav.exists()) {
                throw new RuntimeException("Temporary WAV file not found.");
            }
            File output = new File(outputFilePath.replace(".wav", ".mp3"));
            Mp3Encoder.get().encode(tempWav, output);
            if (!tempWav.delete()) {
                System.err.println("Failed to delete temporary WAV file.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error encoding MP3 file: " + e.getMessage());
        }
    }
}