package org.leialearns.bridge.crossings.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.bridge.crossings.api.Message;
import org.leialearns.bridge.crossings.api.Orientation;
import org.leialearns.bridge.crossings.api.Puzzle;
import org.leialearns.bridge.crossings.api.Root;
import org.leialearns.bridge.crossings.api.Word;
import org.leialearns.utilities.ExecutionListener;
import org.leialearns.utilities.TestUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.leialearns.utilities.Static.getLoggingClass;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/leialearns/bridge/crossings/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class,ExecutionListener.class})
public class RootTest {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @Autowired
    private Root root;

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtilities.beforeClass(null);
    }

    @Test
    public void test() {
        logger.info("TEST!");
        URL url = getClass().getClassLoader().getResource("org/leialearns/bridge/crossings/puzzle.xml");
        Puzzle puzzle = root.getPuzzle(url);
        assertNotNull(puzzle);
        Iterable<Message> messages = puzzle.check();
        assertFalse(messages.iterator().hasNext());
    }

    @Test
    public void testSquare() {
        URL url = getClass().getClassLoader().getResource("org/leialearns/bridge/crossings/test-square.xml");
        Puzzle puzzle = root.getPuzzle(url);
        assertNotNull(puzzle);
        puzzle.check();
        char[][] matrix = new char[5][5];
        char ch = 'A';
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                matrix[c][r] = ch;
                ch++;
            }
        }
        for (int column = 0; column < 5; column++) {
            String reference = String.valueOf(matrix[column]);
            String slice = puzzle.getSlice(1, column + 1, Orientation.DOWN, 5);
            assertEquals(reference, slice);
        }
    }

    @Test
    public void testKidsCrossword() {
        URL url = getClass().getClassLoader().getResource("org/leialearns/bridge/crossings/kids-crossword.xml");
        Puzzle puzzle = root.getPuzzle(url);
        assertNotNull(puzzle);
        puzzle.check();
        String word = puzzle.getSlice(6, 4, Orientation.DOWN, 4);
        assertEquals("TREE", word);
        Word.Iterable words = puzzle.getWords(Orientation.ACROSS);
        int i = 0;
        for (Iterator<Word> it = words.iterator(); it.hasNext(); it.next()) i++;
        assertEquals(10, i);
    }

}
