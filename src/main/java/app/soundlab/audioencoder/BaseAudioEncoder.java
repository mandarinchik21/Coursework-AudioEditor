package app.soundlab.audioencoder;

import app.soundlab.audiotrack.SegmentEncodingAdapter;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.io.IOException;

abstract class BaseAudioEncoder implements AudioEncoder<File> {
    protected static final int DEFAULT_BIT_RATE = 128000;
    protected static final int DEFAULT_CHANNELS = 2;
    protected static final int DEFAULT_SAMPLING_RATE = 44100;
    
    protected final EncodingAttributes encodingAttributes;
    protected final Encoder encoder;
    
    protected BaseAudioEncoder(String outputFormat) {
        encodingAttributes = new EncodingAttributes();
        encodingAttributes.setOutputFormat(outputFormat);
        encoder = new Encoder();
    }
    
    protected AudioAttributes createAudioAttributes(String codec) {
        AudioAttributes audioAttrs = new AudioAttributes();
        audioAttrs.setCodec(codec);
        audioAttrs.setBitRate(DEFAULT_BIT_RATE);
        audioAttrs.setChannels(DEFAULT_CHANNELS);
        audioAttrs.setSamplingRate(DEFAULT_SAMPLING_RATE);
        return audioAttrs;
    }
    
    protected File createOutputFile(File inputFile, String format) {
        return new File(inputFile.getParent(), inputFile.getName() + " (converted to " + format + ")." + format);
    }
    
    @Override
    public File encode(SegmentEncodingAdapter adapter) {
        File inputFile = adapter.adaptFile();
        File outputFile = createOutputFile(inputFile, getFormat());
        return encode(inputFile, outputFile);
    }
    
    @Override
    public File encode(File inputFile, File outputFile) {
        try {
            if (!outputFile.createNewFile()) {
                throw new IOException("Such a file already exists: " + outputFile.getAbsolutePath());
            }
            AudioAttributes audioAttrs = createAudioAttributes(getCodec());
            encodingAttributes.setAudioAttributes(audioAttrs);
            encoder.encode(new MultimediaObject(inputFile), outputFile, encodingAttributes);
            return outputFile;
        } catch (IOException | EncoderException e) {
            throw new RuntimeException("Converter error: " + e.getMessage(), e);
        }
    }
    
    @Override
    public abstract String getFormat();
    
    protected abstract String getCodec();
}

