package app.soundlab.audioencoder;

public class Mp3Encoder extends BaseAudioEncoder {
    private static volatile Mp3Encoder sharedReference;
    private static final String FORMAT = "mp3";
    private static final String CODEC = "libmp3lame";

    private Mp3Encoder() {
        super(FORMAT);
    }

    public static Mp3Encoder get() {
        Mp3Encoder current = sharedReference;
        if (current == null) {
            synchronized (Mp3Encoder.class) {
                current = sharedReference;
                if (current == null) {
                    current = new Mp3Encoder();
                    sharedReference = current;
                }
            }
        }
        return current;
    }

    @Override
    public String getFormat() {
        return FORMAT;
    }

    @Override
    protected String getCodec() {
        return CODEC;
    }
}

