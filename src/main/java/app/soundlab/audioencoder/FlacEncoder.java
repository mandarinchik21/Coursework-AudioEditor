package app.soundlab.audioencoder;

public class FlacEncoder extends BaseAudioEncoder {
    private static volatile FlacEncoder sharedReference;
    private static final String FORMAT = "flac";
    private static final String CODEC = "flac";

    private FlacEncoder() {
        super(FORMAT);
    }

    public static FlacEncoder get() {
        FlacEncoder current = sharedReference;
        if (current == null) {
            synchronized (FlacEncoder.class) {
                current = sharedReference;
                if (current == null) {
                    current = new FlacEncoder();
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
