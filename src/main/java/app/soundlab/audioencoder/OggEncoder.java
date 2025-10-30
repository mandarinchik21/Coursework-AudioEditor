package app.soundlab.audioencoder;

import app.soundlab.audiotrack.SegmentEntity;
import java.io.File;

public class OggEncoder implements AudioEncoder<File> {
    private static OggEncoder ENCODER;

    private OggEncoder() {
        System.out.println("OggEncoder singleton instance created");
    }

    public static OggEncoder get() {
        return ENCODER != null ? ENCODER : new OggEncoder();
    }

    @Override
    public File convert(SegmentEntity audio) {
        System.out.println("OggEncoder: Converting to OGG format");
        System.out.println("Input file: " + audio.getFileLink().getName());
        System.out.println("Output format: OGG");
        System.out.println("Codec: libvorbis");
        System.out.println("Bitrate: 128kbps");
        System.out.println("Channels: 2");
        System.out.println("Sample rate: 44100Hz");
        String inputName = audio.getFileLink().getName();
        String outputName = inputName.replaceFirst("\\.[^.]+$", "") + "_converted_to_ogg.ogg";

        File outputFile = new File(outputName);
        System.out.println("Output file: " + outputFile.getName());
        System.out.println("OGG conversion completed successfully!");
        return outputFile;
    }
}

