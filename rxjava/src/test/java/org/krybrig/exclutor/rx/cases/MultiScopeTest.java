package org.krybrig.exclutor.rx.cases;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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
public class MultiScopeTest {

    private ExclusiveSchedulerFactory factory;
    private final String threadName = "executors";

    @Before
    public void setUp() {
        factory = new ExclusiveSchedulerFactory(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String name = r.getClass().getSimpleName();
                if (name.startsWith("ExclusiveWorker")) {
                    name = threadName;
                }
                thread.setName(name + "-" + thread.hashCode());
                return thread;
            }
        });
    }

    @Test
    public void testAsyncWithMultiScope() throws InterruptedException {
        String scopePrefix = "scope-";
        int start = 0;
        int count = 1000;
        int segmentNum = 5;

        List<Item> result = Flowable.range(start, count)
                .flatMap(new Function<Integer, Publisher<Item>>() {
                    @Override
                    public Publisher<Item> apply(Integer value) throws Exception {
                        int segment = (value % segmentNum);
                        boolean exclusive = (segment == 0);
                        int delay = 0;
                        if (exclusive) {
                            delay = 10;
                        }
                        final String scope = scopePrefix + segment;
                        Scheduler scheduler = factory.createScheduler(scope, exclusive);
                        return Flowable.just(delay)
                                .observeOn(scheduler)
                                .map(new Function<Integer, Item>() {
                                    @Override
                                    public Item apply(Integer delay) throws Exception {
                                        Item item = new Item();
                                        item.value = value;
                                        item.threadName = Thread.currentThread().getName();
                                        item.exclusive = exclusive;
                                        item.scope = scope;

                                        Thread.sleep(delay);
                                        return item;
                                    }
                                });
                    }
                })
                .observeOn(Schedulers.computation())
                .toList()
                .blockingGet();

        assertEquals(count, result.size());
        Item prev;
        for (int s = 0; s < segmentNum; s++) {
            String scope = scopePrefix + s;
            prev = null;
            
            for (int i = start; i < count; i++) {
                Item item = result.get(i);
                if (!scope.equals(item.scope)) {
                    continue;
                }

                if (prev == null || prev.value < item.value) {
                    prev = item;
                } else if (!item.exclusive) {
                    prev = item;
                } else {
                    fail("[" + scope + "] found regular task (" + prev.value + ") executed before exclusive task (" + item.value + ")");
                }

                assertEquals(true, item.threadName.startsWith(threadName));
            }
        }
    }

    private static class Item {

        private int value;
        private String scope;
        private String threadName;
        private boolean exclusive;
    }
}
