package app.soundlab.audiotrack;

import java.io.File;

public class Mp3Asset extends SegmentEntity {
    private File fileLink;

    public Mp3Asset(String filePath) {
        super(filePath);
        checkFileFormat(filePath);
        fileLink = new File(filePath);
        System.out.println("MP3 file initialized with codec: libmp3lame, bitrate: 128kbps");
    }

    private void checkFileFormat(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Warning: File does not exist or is not a valid file: " + path);
            return;
        }
        String extension = getFileExtension(file);
        if (!"mp3".equalsIgnoreCase(extension)) {
            System.out.println("Warning: Expected MP3 format, but got: " + extension);
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
        System.out.println("Converting to MP3 format...");
        super.saveAs(outputFilePath);
        System.out.println("MP3 encoding completed with libmp3lame codec");
    }
}