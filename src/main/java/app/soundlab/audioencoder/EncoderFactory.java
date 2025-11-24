package app.soundlab.audioencoder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EncoderFactory {
    private static final Map<String, AudioEncoder<File>> encoders = new HashMap<>();
    
    static {
        encoders.put("mp3", Mp3Encoder.get());
        encoders.put("ogg", OggEncoder.get());
        encoders.put("flac", FlacEncoder.get());
    }
    
    private EncoderFactory() {
    }
    
    public static AudioEncoder<File> getEncoder(String format) {
        AudioEncoder<File> encoder = encoders.get(format.toLowerCase());
        if (encoder == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        return encoder;
    }
}

