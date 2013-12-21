package org.leialearns.bridge.crossings.api;

import java.net.URL;

public interface Root {
    Puzzle getPuzzle(URL location);
    Puzzle getPuzzle(String descriptionXml);
}
