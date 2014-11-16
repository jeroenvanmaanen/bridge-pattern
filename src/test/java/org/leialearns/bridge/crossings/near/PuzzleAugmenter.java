package org.leialearns.bridge.crossings.near;

import org.leialearns.bridge.BaseBridgeFacet;
import org.leialearns.bridge.BridgeOverride;
import org.leialearns.bridge.crossings.api.Message;
import org.leialearns.bridge.crossings.api.MessageType;
import org.leialearns.bridge.crossings.api.Orientation;
import org.leialearns.bridge.crossings.api.Puzzle;
import org.leialearns.bridge.crossings.api.Word;
import org.leialearns.utilities.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.leialearns.utilities.Static.getLoggingClass;

public class PuzzleAugmenter extends BaseBridgeFacet {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));
    private Setting<Puzzle> puzzle = new Setting<>("Puzzle", new Supplier<Puzzle>() {
        @Override
        public Puzzle get() {
            return (Puzzle) getBridgeFacets().getNearObject();
        }
    });
    private List<char[]> layout = new ArrayList<>();
    private Map<Orientation,Integer> maxOrdinal = new HashMap<>();

    public PuzzleAugmenter() {
        clear();
    }

    @BridgeOverride
    public Iterable<Message> check() {
        Collection<Message> result = new ArrayList<>();
        for (Orientation orientation : Orientation.values()) {
            int newMaxOrdinal = puzzle.get().getMaxOrdinal(orientation);
            for (int i = maxOrdinal.get(orientation); i < newMaxOrdinal; i++) {
                checkNewDescription(orientation, i, result);
            }
            maxOrdinal.put(orientation, newMaxOrdinal);
        }
        for (char[] layer : layout) {
            for (int i = 0; i < layer.length; i++) {
                if (Character.isAlphabetic(layer[i])) {
                    layer[i] = ' ';
                }
            }
        }
        for (Orientation orientation : Orientation.values()) {
            for (int i = 0; i < maxOrdinal.get(orientation); i++) {
                checkWord(orientation, i, result);
            }
        }
        return result;
    }

    @BridgeOverride
    public String getSlice(int row, int column, Orientation orientation, int length) {
        char[] reference = new char[length];
        StringBuilder result = new StringBuilder();
        visitSquares(row, column, orientation, reference, new SquareVisitor<StringBuilder>() {
            @Override
            public char visit(int distance, int arc, char oldChar, char newChar, StringBuilder accumulator) {
                accumulator.append(oldChar);
                return oldChar;
            }
        }, result);
        return result.toString();
    }

    protected void checkNewDescription(Orientation orientation, int ordinal, Collection<Message> messages) {
        Word word = puzzle.get().getWord(ordinal, orientation);
        int length = word.getLength() + 2;
        char[] reference = new char[length];
        Arrays.fill(reference, ' ');
        reference[0] = '#';
        reference[reference.length - 1] = '#';
        int row = word.getStartRow();
        int column = word.getStartColumn();
        if (orientation == Orientation.ACROSS) {
            column--;
        } else {
            row--;
        }
        visitSquares(row, column, orientation, reference, new SquareVisitor<Collection<Message>>() {
            @Override
            public char visit(int distance, int arc, char oldChar, char newChar, Collection<Message> messages) {
                char result = newChar;
                char ref = oldChar;
                if (Character.isAlphabetic(ref)) {
                    ref = ' ';
                }
                if (oldChar != '?' && ref != newChar) {
                    messages.add(new MessageImpl(-1, -1, MessageType.ADJACENT, ""));
                    result = (ref != oldChar ? oldChar : ' ');
                }
                return result;
            }
        }, messages);
    }

    protected void checkWord(Orientation orientation, int ordinal, Collection<Message> messages) {
        Word word = puzzle.get().getWord(ordinal, orientation);
        String candidate = word.get();
        if (candidate != null) {
            char[] chars = candidate.toCharArray();
            visitSquares(word.getStartRow(), word.getStartColumn(), orientation, chars, new SquareVisitor<Collection<Message>>() {
                @Override
                public char visit(int distance, int arc, char oldChar, char newChar, Collection<Message> messages) {
                    if (Character.isAlphabetic(oldChar) && newChar != oldChar) {
                        messages.add(new MessageImpl(-1, -1, MessageType.CONFLICT, ""));
                    }
                    return newChar;
                }
            }, messages);
        }
    }

    protected <A> void visitSquares(int row, int column, Orientation orientation, char[] reference, SquareVisitor<A> visitor, A accumulator) {
        logger.trace("Visit squares {");
        int distance = Math.max(row, column);
        int arc = (row < column ? 2 * distance - row : column);
        int distanceIncrement;
        int arcIncrement;
        if (orientation == Orientation.ACROSS) {
            if (row > column) {
                distanceIncrement = 0;
                arcIncrement = 1;
            } else {
                distanceIncrement = 1;
                arcIncrement = 2;
            }
        } else {
            if (column > row) {
                distanceIncrement = 0;
                arcIncrement = -1;
            } else {
                distanceIncrement = 1;
                arcIncrement = 0;
            }
        }
        for (char newChar : reference) {
            while (distance >= layout.size()) {
                char[] strip = new char[2*layout.size()+1];
                Arrays.fill(strip, '?');
                layout.add(strip);
            }
            char oldChar = layout.get(distance)[arc];
            newChar = visitor.visit(distance, arc, oldChar, newChar, accumulator);
            layout.get(distance)[arc] = newChar;

            distance += distanceIncrement;
            arc += arcIncrement;
            if (distanceIncrement == 0 && arc == distance) {
                distanceIncrement = 1;
                arcIncrement = (orientation == Orientation.DOWN ? 0 : 2);
            }
            if (logger.isTraceEnabled()) {
                int r;
                int c;
                if (arc <= distance) {
                    r = distance;
                    c = arc;
                } else {
                    r = (2 * distance) - arc;
                    c = distance;
                }
                logger.trace("  Square: ({}, {}): [{}, {}]: delta: [{}, {}]", new Object[]{r, c, distance, arc, distanceIncrement, arcIncrement});
            }
        }
        logger.trace("}");
    }

    public void clear() {
        maxOrdinal.clear();
        layout.clear();
        for (Orientation orientation : Orientation.values()) {
            maxOrdinal.put(orientation, 0);
        }
    }

    protected interface SquareVisitor<Accumulator> {
        public char visit(int distance, int arc, char oldChar, char newChar, Accumulator accumulator);
    }

}
