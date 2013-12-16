package org.leialearns.bridge.crossings.logic;

public interface Puzzle {
    int getMaxOrdinal(Orientation orientation);
    String getWord(int ordinal, Orientation orientation);
}
