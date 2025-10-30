package app.soundlab.audioencoder;

import app.soundlab.audiotrack.SegmentEntity;
import java.io.File;

public class Mp3Encoder implements AudioEncoder<File> {
    private static Mp3Encoder ENCODER;

    private Mp3Encoder() {
        System.out.println("Mp3Encoder singleton instance created");
    }

    public static Mp3Encoder get() {
        return ENCODER != null ? ENCODER : new Mp3Encoder();
    }

    @Override
    public File convert(SegmentEntity audio) {
        System.out.println("Mp3Encoder: Converting to MP3 format");
        System.out.println("Input file: " + audio.getFileLink().getName());
        System.out.println("Output format: MP3");
        System.out.println("Codec: libmp3lame");
        System.out.println("Bitrate: 128kbps");
        System.out.println("Channels: 2");
        System.out.println("Sample rate: 44100Hz");

        String inputName = audio.getFileLink().getName();
        String outputName = inputName.replaceFirst("\\.[^.]+$", "") + "_converted_to_mp3.mp3";
        File outputFile = new File(outputName);
        System.out.println("Output file: " + outputFile.getName());
        System.out.println("MP3 conversion completed successfully!");
        return outputFile;
    }
}

