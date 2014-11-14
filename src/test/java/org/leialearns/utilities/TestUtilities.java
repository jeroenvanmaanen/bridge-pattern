package org.leialearns.utilities;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.executable.LogConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.leialearns.utilities.Display.display;
import static org.leialearns.utilities.Static.asList;
import static org.leialearns.utilities.Static.compare;
import static org.leialearns.utilities.Static.equal;
import static org.leialearns.utilities.Static.gcd;
import static org.leialearns.utilities.Static.getLoggingClass;
import static org.leialearns.utilities.Static.notNull;
import static org.leialearns.utilities.Static.toList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/leialearns/bridge/crossings/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class,ExecutionListener.class})
public class TestUtilities {
    private final Logger logger = LoggerFactory.getLogger(getLoggingClass(this));

    @BeforeClass
    public static void beforeClass() throws IOException {
        beforeClass(null);
    }

    @Test
    public void testDisplay() {
        logger.info(display(new String[]{"Hello", " ", "World", null}));
    }

    @Test
    public void testNotNull() {
        Iterable<Integer> iterable = notNull(null);
        assertNotNull(iterable);
        assertFalse(iterable.iterator().hasNext());

        Iterable<Integer> nonEmpty = Arrays.asList(1, 2);
        Iterable<Integer> notNull = notNull(nonEmpty);
        assertTrue(notNull == nonEmpty);
    }

    @Test
    public void testAsList() {
        List<Integer> fib = Arrays.asList(1, 1, 2, 3, 5, 8, 13);
        List<Integer> copy = asList(fib);
        Iterator iterator = copy.iterator();
        for (Integer f : fib) {
            assertTrue(iterator.hasNext());
            assertEquals(f, iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testToList() {
        List<Integer> fib = Arrays.asList(1, 1, 2, 3, 5, 8, 13);
        TypedIterable<Integer> fibonacci = new TypedIterable<>(fib, Integer.class);
        List<Integer> copy = toList(fibonacci);
        Iterator iterator = copy.iterator();
        for (Integer f : fib) {
            assertTrue(iterator.hasNext());
            assertEquals(f, iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testCompare() {
        assertEquals(0, compare(null, null));
        assertEquals(0, compare(1, 1));
        assertEquals(0, compare("abc", "a" + "bc"));
        Comparable trivial = new Trivial();
        assertEquals(-1,compare(null,trivial));
        assertEquals(1, compare(trivial, null));
    }

    @Test
    public void testEqual() {
        assertTrue(equal(null, null));
        assertTrue(equal(1, 1));
        assertTrue(equal("abc", "a" + "bc"));
        Object trivial = new Object();
        assertFalse(equal(null, trivial));
        assertFalse(equal(trivial, null));
    }

    @Test
    public void testGCD() {
        assertEquals(1, gcd(1, 1));
        assertEquals(1, gcd(7, 13));
        assertEquals(12, gcd(7*12, 13*12));
    }

    public static void beforeClass(Setting<String> projectDirSetting) throws IOException {
        // Expected to be run from Maven, therefore the user.dir is assumed to be identical to the project directory.
        String projectDir = System.getProperty("user.dir");
        if (projectDirSetting != null) {
            projectDirSetting.set(projectDir);
        }
        System.err.print("Project directory: ");
        System.err.println(projectDir);
        String logDir = getPath(projectDir, "target", "log");
        String configFile = getPath(projectDir, "src", "test", "resources", "logging.properties");
        InputStream loggingProperties = new FileInputStream(configFile);
        new LogConfigurator(logDir).configure(loggingProperties);
    }

    public static String getPath(String... components) {
        File result = null;
        for (String component : components) {
            if (result == null) {
                result = new File(component);
            } else {
                result = new File(result, component);
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("No path components given");
        }
        return result.getAbsolutePath();
    }

    private static class Trivial implements Comparable<Trivial> {
        @Override
        public int compareTo(@NotNull Trivial other) {
            throw new UnsupportedOperationException("Unexpected call to compareTo(Trivial)");
        }
    }

}
