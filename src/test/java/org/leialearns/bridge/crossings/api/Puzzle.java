package org.leialearns.bridge.crossings.api;

public interface Puzzle {
    int getMaxOrdinal(Orientation orientation);
    Word getWord(int ordinal, Orientation orientation);
    Word.Iterable getWords(Orientation orientation);
    Iterable<Message> check();
    String getSlice(int row, int column, Orientation orientation, int length);
    void clear();
}
