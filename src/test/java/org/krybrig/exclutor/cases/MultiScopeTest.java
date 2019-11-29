package org.krybrig.exclutor.cases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
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
public class MultiScopeTest {
    private Executor executor;
    
    @Before
    public void setUp() {
        executor = ExclusiveExecutorFactory.create(Runtime.getRuntime().availableProcessors());
    }
    
    @Test
    public void exclusiveRunnableShouldBlockNextNonExclusiveRunnableWhenSameScope() throws InterruptedException {
        int count = 1000;
        int segment = 100;
        int grouping = 4;
        
        final List<Item> resultList = Collections.synchronizedList(new ArrayList<>());
        for (int i = 1; i <= count; i++) {
            executor.execute(new ExclusiveRunnableImpl(i, segment, resultList, grouping));
        }
        
        Thread.sleep(5000);
        
        assertEquals(count, resultList.size());
        
        for (int g = 0; g < grouping; g++) {
            String scope = "scope." + g;
            Item prev = null;
            for (int i = 1; i <= count; i++) {
                Item item = resultList.get(i-1);
                if (!scope.equals(item.group)) {
                    continue;
                }
                
                if (prev == null) {
                    prev = item;
                } else if (prev.id < item.id) {
                    prev = item;
                } else if (item.exclusive) {
                    assertFalse("previous item (" + prev.id + ") should be executed after exclusive item (" + item.id + ")", true);
                    break;
                }
            }
        }
    }
    
    private static class ExclusiveRunnableImpl implements ExclusiveRunnable {
        private final String scope;
        private final int id;
        private final int segment;
        private final List<Item> resultList;

        public ExclusiveRunnableImpl(int id, int segment, List<Item> resultList, int grouping) {
            this.id = id;
            this.segment = segment;
            this.resultList = resultList;
            this.scope = "scope." + (id % grouping);
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
            try {
                if (isExclusive()) {
                    Thread.sleep((id % 5) * 10);
                }
            } catch (InterruptedException ex) { 
            } finally {
                resultList.add(new Item(id, scope, isExclusive()));
            }
        }
    }
    
    private static class Item {
        int id;
        String group;
        boolean exclusive;

        public Item(int id, String group, boolean exclusive) {
            this.id = id;
            this.group = group;
            this.exclusive = exclusive;
        }
    }
}
