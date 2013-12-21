package org.leialearns.bridge.crossings.far;

import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.FarObject;
import org.leialearns.bridge.crossings.api.Orientation;
import org.leialearns.bridge.crossings.api.Puzzle;
import org.leialearns.utilities.TypedIterable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleDTO implements FarObject<Puzzle> {
    private Map<Orientation,List<WordDTO>> words = new HashMap<>();

    public PuzzleDTO() {
        for (Orientation orientation : Orientation.values()) {
            words.put(orientation, new ArrayList<WordDTO>());
        }
    }

    @BridgeOverride
    public int getMaxOrdinal(Orientation orientation) {
        return words.get(orientation).size();
    }

    @BridgeOverride
    public WordDTO getWord(int ordinal, Orientation orientation) {
        return words.get(orientation).get(ordinal);
    }

    @BridgeOverride
    public TypedIterable<WordDTO> getWords(Orientation orientation) {
        return new TypedIterable<>(words.get(orientation), WordDTO.class);
    }

    @SuppressWarnings("unused") // Called using reflection
    public void addWord(WordDTO word) {
        words.get(word.getOrientation()).add(word);
    }

    @Override
    public Puzzle declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

    @Override
    public String toString() {
        return "[Puzzle#" + System.identityHashCode(this) + "]";
    }

}
