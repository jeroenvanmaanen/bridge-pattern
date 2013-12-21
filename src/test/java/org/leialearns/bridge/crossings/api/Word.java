package org.leialearns.bridge.crossings.api;

import org.leialearns.bridge.NearIterable;

public interface Word {
    String getDescription();
    int getLength();
    int getStartRow();
    int getStartColumn();
    String get();
    void set(String word);

    interface Iterable extends NearIterable<Word> {
        Word declareNearType();
    }
}
