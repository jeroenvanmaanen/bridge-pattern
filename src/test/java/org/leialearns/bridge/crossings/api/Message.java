package org.leialearns.bridge.crossings.api;

public interface Message {
    int getRow();
    int getColumn();
    MessageType getType();
    String getMessage();
}
