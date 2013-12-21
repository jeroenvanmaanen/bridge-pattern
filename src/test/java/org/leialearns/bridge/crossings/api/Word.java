package org.leialearns.bridge.crossings.api;

public interface Word {
    String getDescription();
    int getLength();
    int getStartRow();
    int getStartColumn();
    String get();
    void set(String word);
}
