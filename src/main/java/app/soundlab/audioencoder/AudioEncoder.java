package app.soundlab.audioencoder;

import app.soundlab.audiotrack.SegmentEntity;

public interface AudioEncoder<T> {
    T convert(SegmentEntity audio);
}