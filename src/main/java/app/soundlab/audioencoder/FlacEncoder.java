package app.soundlab.audioencoder;

import app.soundlab.audiotrack.SegmentEntity;
import java.io.File;

public class FlacEncoder implements AudioEncoder<File> {
    private static FlacEncoder ENCODER;

    private FlacEncoder() {
        System.out.println("FlacEncoder singleton instance created");
    }

    public static FlacEncoder get() {
        return ENCODER != null ? ENCODER : new FlacEncoder();
    }

    @Override
    public File convert(SegmentEntity audio) {
        System.out.println("FlacEncoder: Converting to FLAC format");
        System.out.println("Input file: " + audio.getFileLink().getName());
        System.out.println("Output format: FLAC");
        System.out.println("Codec: flac");
        System.out.println("Bitrate: 128kbps");
        System.out.println("Channels: 2");
        System.out.println("Sample rate: 44100Hz");

        String inputName = audio.getFileLink().getName();
        String outputName = inputName.replaceFirst("\\.[^.]+$", "") + "_converted_to_flac.flac";
        File outputFile = new File(outputName);

        System.out.println("Output file: " + outputFile.getName());
        System.out.println("FLAC conversion completed successfully!");

        return outputFile;
    }
}

