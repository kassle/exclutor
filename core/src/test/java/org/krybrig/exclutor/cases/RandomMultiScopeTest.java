package org.krybrig.exclutor.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.krybrig.exclutor.ExclusiveExecutorFactory;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class RandomMultiScopeTest {
    private Executor executor;
    
    @Before
    public void setUp() {
        executor = ExclusiveExecutorFactory.create(8);
        Executors.newSingleThreadScheduledExecutor();
    }
    
    @Test
    public void randomMultiScopeTest() throws InterruptedException {
        int taskCount = 1000;
        List<String> scopeList = Arrays.asList("one", "two", "three");
        int scopeNum = scopeList.size();
        Random random = new Random();
        
        List<Item> itemList = Collections.synchronizedList(new ArrayList<>(taskCount));
        
        for (int i = 0; i < taskCount; i++) {
            Item item = new Item(scopeList.get(random.nextInt(scopeNum)), i, random.nextBoolean());
            executor.execute(new Task(item, itemList));
        }
        
        Thread.sleep(20000);
        
        assertEquals(taskCount, itemList.size());
        
        for (int s = 0; s < scopeNum; s++) {
            String scope = scopeList.get(s);
            Item prev = null;
            
            for (int t = 0; t < taskCount; t++) {
                Item item = itemList.get(t);
                
                if (!scope.equals(item.scope)) {
                    continue;
                }
                
                if (prev == null) {
                    prev = item;
                } else if (prev.value < item.value) {
                    prev = item;
                } else if (item.exclusive) {
                    fail("previous item (" + prev.value + ") should be executed after exclusive item (" + item.value + ")");
                    break;
                }
            }
        }
        System.out.println("finish checking");
    }
    
    private static class Task implements ExclusiveRunnable {
        private Item item;
        private List<Item> itemList;

        public Task(Item item, List<Item> itemList) {
            this.item = item;
            this.itemList = itemList;
        }

        @Override
        public String getScope() {
            return item.scope;
        }

        @Override
        public boolean isExclusive() {
            return item.exclusive;
        }

        @Override
        public void run() {
            Random random = new Random();
            int delay = random.nextInt(10) * 10;
            
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {                
            } finally {
                itemList.add(item);
            }
        }
    }
    
    private static class Item {
        private String scope;
        private int value;
        private boolean exclusive;

        public Item(String scope, int value, boolean exclusive) {
            this.scope = scope;
            this.value = value;
            this.exclusive = exclusive;
        }
    }
}
