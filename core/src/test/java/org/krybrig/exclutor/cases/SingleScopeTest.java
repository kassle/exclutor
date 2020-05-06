package org.krybrig.exclutor.cases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.krybrig.exclutor.ExclusiveExecutorFactory;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class SingleScopeTest {

    private Executor executor;

    @Before
    public void setUp() {
        executor = ExclusiveExecutorFactory.create(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void exclusiveRunnableShouldBlockNextNonExclusiveRunnable() throws InterruptedException {
        int count = 10000;
        int segment = 100;

        final List<Item> resultList = Collections.synchronizedList(new ArrayList<>());
        long start = System.currentTimeMillis();
        for (int i = 1; i <= count; i++) {
            executor.execute(new ExclusiveRunnableImpl(i, segment, resultList));
        }
        long finish = System.currentTimeMillis();
        System.out.println("submit for " + count + " job finish in " + (finish - start));

        Thread.sleep(1000 * 5);

        assertEquals(count, resultList.size());

        Item prev = null;
        for (int i = 0; i < count; i++) {
            Item item = resultList.get(i);

            if (prev == null || prev.value < item.value) {
                prev = item;
            } else if (!item.exclusive) {
                prev = item;
            } else {
                Assert.fail("found regular task (" + prev.value + ") executed before exclusive task (" + item.value + ")");
            }
        }
    }

    private static class ExclusiveRunnableImpl implements ExclusiveRunnable {

        private final String scope = "global";
        private final int id;
        private final int segment;
        private final List<Item> resultList;

        public ExclusiveRunnableImpl(int id, int segment, List<Item> resultList) {
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
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                }
            }

            Item item = new Item();
            item.value = id;
            item.exclusive = isExclusive();

            resultList.add(item);
        }
    }

    private static class Item {

        private int value;
        private boolean exclusive;
    }
}
