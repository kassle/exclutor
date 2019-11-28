package org.krybrig.exclutor.cases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.krybrig.exclutor.ExclusiveExecutorFactory;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class SingleScopeTest {
    private ExecutorService executor;
    
    @Before
    public void setUp() {
        executor = ExclusiveExecutorFactory.create(
                Runtime.getRuntime().availableProcessors(),
                Executors.defaultThreadFactory());
    }
    
    @Test
    public void exclusiveRunnableShouldBlockNextNonExclusiveRunnable() throws InterruptedException {
        int count = 100000;
        int segment = 100;
        
        final List<Integer> resultList = Collections.synchronizedList(new ArrayList<>());
        for (int i = 1; i <= count; i++) {
            executor.submit(new ExclusiveRunnableImpl(i, segment, resultList));
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(count, resultList.size());
        
        int block = segment;
        for (int i = 1; i <= count; i++) {
            int id = resultList.get(i-1);
            
            if (id == block) {
                block += segment ;
            } else if (id > block) {
                assertFalse("current = " + id + "; block = " + block, true);
            }
        }
    }
    
    private static class ExclusiveRunnableImpl implements ExclusiveRunnable {
        private final String scope = "global";
        private final int id;
        private final int segment;
        private final List<Integer> resultList;

        public ExclusiveRunnableImpl(int id, int segment, List<Integer> resultList) {
            this.id = id;
            this.segment = segment;
            this.resultList = resultList;
        }

        @Override
        public String getScope() {
            return scope;
        }

        @Override
        public boolean isExclusive() {
            return id % segment == 0;
        }

        @Override
        public void run() {
            if (isExclusive()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) { }
            }
            resultList.add(id);
        }
    }
}
