package org.leialearns.utilities;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.common.Setting;
import org.leialearns.common.logging.LogConfigurator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/leialearns/bridge/crossings/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class,ExecutionListener.class})
public class TestUtilities {

    @BeforeClass
    public static void beforeClass() throws IOException {
        beforeClass(null);
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

    @Test
    public void noTest() {
        // Ignore
    }
}
