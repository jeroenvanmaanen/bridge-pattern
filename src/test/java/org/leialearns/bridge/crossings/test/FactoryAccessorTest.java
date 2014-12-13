package org.leialearns.bridge.crossings.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.leialearns.bridge.BridgeFactory;
import org.leialearns.bridge.FactoryAccessor;
import org.leialearns.bridge.crossings.api.Puzzle;
import org.leialearns.bridge.crossings.far.PuzzleDTO;
import org.leialearns.spring.test.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/leialearns/bridge/crossings/AppTest-context.xml"})
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class,ExecutionListener.class})
public class FactoryAccessorTest {

    @Autowired
    BridgeFactory puzzleFactory;

    @Test
    public void testFactoryAccessor() {
        FactoryAccessor<Puzzle> factoryAccessor = new FactoryAccessor<>(Puzzle.class);
        factoryAccessor.set(puzzleFactory);
        assertEquals(Puzzle.class, factoryAccessor.getNearType());
        PuzzleDTO farObject = new PuzzleDTO();
        Puzzle nearObject = factoryAccessor.getNearObject(farObject);
        assertNotNull(nearObject);
    }
}
