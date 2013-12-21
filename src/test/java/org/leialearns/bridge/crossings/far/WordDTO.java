package org.leialearns.bridge.crossings.far;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.FarObject;
import org.leialearns.bridge.crossings.api.Orientation;
import org.leialearns.bridge.crossings.api.Word;

public class WordDTO implements FarObject<Word> {
    private int startRow;
    private int startColumn;
    private int length;
    private Orientation orientation;
    private String description;
    private String word;

    @SuppressWarnings("unused") // Called using reflection
    public void setStartRow(int startRow) {
        if (startRow < 1) {
            throw new IllegalArgumentException("The start row should be at least 1: " + startRow);
        }
        this.startRow = startRow;
    }

    @BridgeOverride
    public int getStartRow() {
        return startRow;
    }

    @SuppressWarnings("unused") // Called using reflection
    public void setStartColumn(int startColumn) {
        if (startColumn < 1) {
            throw new IllegalArgumentException("The start column should be at least 1: " + startColumn);
        }
        this.startColumn = startColumn;
    }

    @BridgeOverride
    public int getStartColumn() {
        return startColumn;
    }

    @SuppressWarnings("unused") // Called using reflection
    public void setLength(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("The length should be at least 1: " + length);
        }
        this.length = length;
    }

    @BridgeOverride
    public int getLength() {
        return length;
    }

    @SuppressWarnings("unused") // Called using reflection
    public void setOrientation(String orientation) {
        this.orientation = Orientation.valueOf(orientation.toUpperCase());
    }

    @BridgeOverride
    public Orientation getOrientation() {
        return orientation;
    }

    @BridgeOverride
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unused") // Called using reflection
    public void setDescription(String description) {
        this.description = description;
    }

    @BridgeOverride
    public String get() {
        return word;
    }

    @SuppressWarnings("unused") // Called using reflection
    public void setValue(String value) {
        set(value);
    }

    @BridgeOverride
    public void set(String word) {
        if (word.length() != length) {
            throw new IllegalArgumentException("The length of word should be: " + length + ": '" + word + "(" + word.length() + ")");
        }
        this.word = word;
    }

    @Override
    public Word declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

    @Override
    public String toString() {
        return "[Word#" + System.identityHashCode(this) + "]";
    }

}
