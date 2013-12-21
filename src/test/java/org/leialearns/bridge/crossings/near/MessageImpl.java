package org.leialearns.bridge.crossings.near;

import org.leialearns.bridge.crossings.api.Message;
import org.leialearns.bridge.crossings.api.MessageType;

public class MessageImpl implements Message {
    private final int row;
    private final int column;
    private final MessageType type;
    private final String message;

    public MessageImpl(int distance, int arc, MessageType type, String message) {
        if (arc <= distance) {
            row = distance;
            column = arc;
        } else {
            row = (2*distance) - arc;
            column = distance;
        }
        this.type = type;
        this.message = message;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
