package app.soundlab.audiotrack;

import java.io.File;

public class OggAsset extends SegmentEntity {
    private File fileLink;

    public OggAsset(String filePath) {
        super(filePath);
        checkFileFormat(filePath);
        fileLink = new File(filePath);

        System.out.println("OGG file initialized with codec: libvorbis, bitrate: 128kbps");
    }

    private void checkFileFormat(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Warning: File does not exist or is not a valid file: " + path);
            return;
        }

        String extension = getFileExtension(file);
        if (!"ogg".equalsIgnoreCase(extension)) {
            System.out.println("Error: Expected OGG format, but got: " + extension);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return (lastIndex > 0) ? name.substring(lastIndex + 1) : "";
    }

    @Override
    public String getFormat() {
        return "OGG";
    }

    @Override
    public File getFileLink() {
        return fileLink;
    }

    @Override
    public void saveAs(String outputFilePath) {
        System.out.println("Converting to OGG format...");
        super.saveAs(outputFilePath);
        System.out.println("OGG encoding completed with libvorbis codec");
    }
}
