package org.krybrig.exclutor.rx.cases;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.krybrig.exclutor.rx.ExclusiveSchedulerFactory;
import org.reactivestreams.Publisher;

/**
 *
 * @author kassle
 */
public class SequentialProcessTest {

    private ExclusiveSchedulerFactory factory;
    private final String scope = "sequential";

    @Before
    public void setUp() {
        factory = new ExclusiveSchedulerFactory(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String name = r.getClass().getSimpleName();
                if (name.startsWith("ExclusiveWorker")) {
                    name = scope;
                }
                thread.setName(name + "-" + thread.hashCode());
                return thread;
            }
        });
    }

    @Test
    public void checkFlowReactiveX() throws InterruptedException {
        int start = 0;
        int count = 1000;

        List<Item> result = Flowable.range(start, count)
                .flatMap(new Function<Integer, Publisher<Item>>() {
                    @Override
                    public Publisher<Item> apply(Integer value) throws Exception {
                        boolean exclusive = (value % 5 == 0);
                        Scheduler scheduler = factory.createScheduler(scope, exclusive);
                        return Flowable.just(value)
                                .map(new Function<Integer, Item>() {
                                    @Override
                                    public Item apply(Integer value) throws Exception {
                                        Item item = new Item();
                                        item.value = value;
                                        item.threadName = Thread.currentThread().getName();
                                        item.exclusive = exclusive;
                                        return item;
                                    }
                                })
                                .subscribeOn(scheduler);
                    }
                })
                .toList()
                .blockingGet();
        
        assertEquals(count, result.size());
        for (int i = start; i < count; i++) {
            Item item = result.get(i);

            assertEquals(i, item.value);
            assertEquals(true, item.threadName.startsWith(scope));
            assertEquals((i % 5 == 0), item.exclusive);
        }
    }

    private static class Item {
        int value;
        String threadName;
        boolean exclusive;
    }
}
