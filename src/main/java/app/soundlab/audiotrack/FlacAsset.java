package app.soundlab.audiotrack;

import java.io.File;

public class FlacAsset extends SegmentEntity {
    private File fileLink;

    public FlacAsset(String filePath) {
        super(filePath);
        checkFileFormat(filePath);
        fileLink = new File(filePath);

        System.out.println("FLAC file initialized with codec: flac, bitrate: 128kb/ps");
    }

    private void checkFileFormat(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Warning: File does not exist or is not a valid file: " + path);
            return;
        }

        String extension = getFileExtension(file);
        if (!"flac".equalsIgnoreCase(extension)) {
            System.out.println("Error: Expected FLAC format, but got: " + extension);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return (lastIndex > 0) ? name.substring(lastIndex + 1) : "";
    }

    @Override
    public String getFormat() {
        return "FLAC";
    }

    @Override
    public File getFileLink() {
        return fileLink;
    }

    @Override
    public void saveAs(String outputFilePath) {
        System.out.println("Converting to FLAC format...");

        super.saveAs(outputFilePath);

        System.out.println("FLAC encoding completed with flac codec");
    }
}
