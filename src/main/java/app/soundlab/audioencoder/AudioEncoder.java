package app.soundlab.audioencoder;

import app.soundlab.audiotrack.SegmentEncodingAdapter;

import java.io.File;

public interface AudioEncoder<T> {
    T encode(SegmentEncodingAdapter adapter);
    
    File encode(File inputFile, File outputFile);
    
    String getFormat();
}