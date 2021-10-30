package org.krybrig.exclutor.rx.cases;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.krybrig.exclutor.rx.ExclusiveSchedulerFactory;
import org.reactivestreams.Publisher;

/**
 *
 * @author kassle
 */
public class SingleScopeTest {

    private ExclusiveSchedulerFactory factory;
    private final String scope = "asynchronous";

    @Before
    public void setUp() {
        factory = new ExclusiveSchedulerFactory(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
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
    public void testAsyncWithSingleScope() throws InterruptedException {
        int start = 0;
        int count = 1000;
        
        List<Item> result = Flowable.range(start, count)
                .flatMap(new Function<Integer, Publisher<Item>>() {
                    @Override
                    public Publisher<Item> apply(final Integer index) throws Exception {
                        boolean exclusive = (index % 5 == 0);
                        int delay = 0;
                        if (exclusive) {
                            delay = 10;
                        }
                        Scheduler scheduler = factory.createScheduler(scope, exclusive);
                        return Flowable.just(delay)
                                .subscribeOn(scheduler)
                                .map(new Function<Integer, Item>() {
                                    @Override
                                    public Item apply(Integer delay) throws Exception {
                                        Item item = new Item();
                                        item.value = index;
                                        item.threadName = Thread.currentThread().getName();
                                        item.exclusive = exclusive;

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
        Item prev = null;
        for (int i = start; i < count; i++) {
            Item item = result.get(i);

            if (prev == null || prev.value < item.value) {
                prev = item;
            } else if (!item.exclusive) {
                prev = item;
            } else {
                fail("found regular task (" + prev.value + ") executed before exclusive task (" + item.value + ")");
            }

            assertEquals(true, item.threadName.startsWith(scope));
        }
    }

    private static class Item {

        private int value;
        private String threadName;
        private boolean exclusive;
    }
}
