package app.soundlab.audioencoder;

public class OggEncoder extends BaseAudioEncoder {
    private static volatile OggEncoder sharedReference;
    private static final String FORMAT = "ogg";
    private static final String CODEC = "libvorbis";

    private OggEncoder() {
        super(FORMAT);
    }

    public static OggEncoder get() {
        OggEncoder current = sharedReference;
        if (current == null) {
            synchronized (OggEncoder.class) {
                current = sharedReference;
                if (current == null) {
                    current = new OggEncoder();
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

